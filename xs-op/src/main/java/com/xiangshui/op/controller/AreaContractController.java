package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.bean.AreaBillResult;
import com.xiangshui.op.scheduled.AreaBillScheduled;
import com.xiangshui.op.scheduled.CacheScheduled;
import com.xiangshui.op.scheduled.CountCapsuleScheduled;
import com.xiangshui.op.threadLocal.UsernameLocal;
import com.xiangshui.server.constant.AreaContractStatusOption;
import com.xiangshui.server.dao.AreaContractDao;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BaseDynamoDao;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.service.*;
import com.xiangshui.op.tool.ExcelTools;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.Option;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Controller
public class AreaContractController extends BaseController {
    @Autowired
    ExcelTools excelTools;
    @Autowired
    CityService cityService;
    @Autowired
    AreaService areaService;
    @Autowired
    AreaDao areaDao;
    @Autowired
    S3Service s3Service;

    @Autowired
    AreaContractService areaContractService;

    @Autowired
    AreaContractDao areaContractDao;

    @Autowired
    CapsuleService capsuleService;

    @Autowired
    AreaBillScheduled areaBillScheduled;

    @Autowired
    CountCapsuleScheduled countCapsuleScheduled;

    @Autowired
    CacheScheduled cacheScheduled;

    @Menu("客户分成管理")
    @AuthRequired(AuthRequired.area_contract)
    @GetMapping("/area_contract_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "area_contract_manage";
    }


    @AuthRequired(AuthRequired.area_contract)
    @GetMapping("/api/area_contract/search")
    @ResponseBody
    public Result search(HttpServletRequest request, HttpServletResponse response, AreaContract criteria, Boolean download) throws NoSuchFieldException, IllegalAccessException, IOException {
        Set<String> authSet = opUserService.getAuthSet(UsernameLocal.get());
        if (!(authSet.contains(AuthRequired.area_contract_saler) || authSet.contains(AuthRequired.area_contract_verify))) {
            return new Result(CodeMsg.AUTH_FAIL);
        }
        if (download == null) {
            download = false;
        }
        if (criteria == null) {
            criteria = new AreaContract();
        }
        if (!authSet.contains(AuthRequired.area_contract_verify)) {
            Op op = opUserService.getOpByUsername(UsernameLocal.get(), null);
            if (StringUtils.isBlank(op.getFullname()) || StringUtils.isBlank(op.getCity())) {
                return new Result(-1, "您还没有设置姓名或城市");
            }
            criteria.setSaler(op.getFullname());
            criteria.setSaler_city(op.getCity());
        }
        List<ScanFilter> scanFilterList = areaContractDao.makeScanFilterList(criteria, new String[]{
                "area_id",
                "saler",
                "saler_city",
                "customer_email",
                "customer_contact",
                "status",
        });

        if (scanFilterList == null) {
            scanFilterList = new ArrayList<>();
        }
        if (StringUtils.isNotBlank(criteria.getCustomer())) {
            scanFilterList.add(new ScanFilter("customer").contains(criteria.getCustomer()));
        }
        ScanSpec scanSpec = new ScanSpec();
        scanSpec.setMaxResultSize(BaseDynamoDao.maxDownloadSize);
        if (scanFilterList.size() > 0) {
            scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        }
        List<AreaContract> areaContractList = areaContractDao.scan(scanSpec);
        areaContractList = capsuleAuthorityTools.filterAreaContract(areaContractList);
        List<Area> areaList = null;
        if (areaContractList != null && areaContractList.size() > 0) {
            areaContractList.sort((o1, o2) -> {
//                if (new Integer(-1).equals(cacheScheduled.areaMapOptions.get(o1.getArea_id()).getStatus())) {
//                    return 1;
//                }
//                if (new Integer(-1).equals(cacheScheduled.areaMapOptions.get(o2.getArea_id()).getStatus())) {
//                    return -1;
//                }
                int result = (o2.getStatus() == 1 ? o2.getStatus() * (-100) - 1 : o2.getStatus() * 100) - (o1.getStatus() == 1 ? o1.getStatus() * (-100) - 1 : o1.getStatus() * 100);
                if (result != 0) {
                    return result;
                } else {
                    return (int) (o2.getCreate_time() - o1.getCreate_time());
                }
            });

            Set<Integer> areaIdSet = new HashSet<>();
            areaContractList.forEach(areaContract -> areaIdSet.add(areaContract.getArea_id()));
            if (areaIdSet.size() > 0) {
                areaList = ServiceUtils.division(areaIdSet.toArray(new Integer[0]), 100, object -> areaDao.batchGetItem("area_id", object, null), new Integer[0]);
            }
        }


        if (download) {
            Map<Integer, Area> areaMap = new HashMap<>();
            if (areaList != null && areaList.size() > 0) {
                areaList.forEach(area -> areaMap.put(area.getArea_id(), area));
            }

            List<ExcelUtils.Column<AreaContract>> columnList = new ArrayList<>();
            columnList.add(new ExcelUtils.Column<AreaContract>("场地编号") {
                @Override
                public String render(AreaContract areaContract) {
                    return String.valueOf(areaContract.getArea_id());
                }
            });
            columnList.add(new ExcelUtils.Column<AreaContract>("场地名称") {
                @Override
                public String render(AreaContract areaContract) {
                    return areaMap.containsKey(areaContract.getArea_id()) ? areaMap.get(areaContract.getArea_id()).getTitle() : "";
                }
            });
            columnList.add(new ExcelUtils.Column<AreaContract>("投放城市") {
                @Override
                public String render(AreaContract areaContract) {
                    return areaMap.containsKey(areaContract.getArea_id()) ? areaMap.get(areaContract.getArea_id()).getCity() : "";
                }
            });
            columnList.add(new ExcelUtils.Column<AreaContract>("地址") {
                @Override
                public String render(AreaContract areaContract) {
                    return areaMap.containsKey(areaContract.getArea_id()) ? areaMap.get(areaContract.getArea_id()).getAddress() : "";
                }
            });
            columnList.add(new ExcelUtils.Column<AreaContract>("投放日期") {
                @Override
                public String render(AreaContract areaContract) {
                    return countCapsuleScheduled.areaCreateTimeMap.containsKey(areaContract.getArea_id()) ? DateUtils.format(countCapsuleScheduled.areaCreateTimeMap.get(areaContract.getArea_id()) * 1000, "yyyy-MM-dd") : null;
                }
            });
            columnList.add(new ExcelUtils.Column<AreaContract>("投放数量（台）") {
                @Override
                public String render(AreaContract areaContract) {
                    return countCapsuleScheduled.countGroupArea.containsKey(areaContract.getArea_id()) ? String.valueOf(countCapsuleScheduled.countGroupArea.get(areaContract.getArea_id())) : null;
                }
            });
            columnList.add(new ExcelUtils.Column<AreaContract>("销售") {
                @Override
                public String render(AreaContract areaContract) {
                    return areaContract.getSaler();
                }
            });
            columnList.add(new ExcelUtils.Column<AreaContract>("所属公司") {
                @Override
                public String render(AreaContract areaContract) {
                    return areaContract.getSaler_city();
                }
            });
            columnList.add(new ExcelUtils.Column<AreaContract>("客户公司名称") {
                @Override
                public String render(AreaContract areaContract) {
                    return areaContract.getCustomer();
                }
            });
            columnList.add(new ExcelUtils.Column<AreaContract>("客户银行账号名称") {
                @Override
                public String render(AreaContract areaContract) {
                    return areaContract.getBank_account_name();
                }
            });
            columnList.add(new ExcelUtils.Column<AreaContract>("客户银行账号") {
                @Override
                public String render(AreaContract areaContract) {
                    return areaContract.getBank_account();
                }
            });
            columnList.add(new ExcelUtils.Column<AreaContract>("客户银行账号开户支行") {
                @Override
                public String render(AreaContract areaContract) {
                    return areaContract.getBank_branch();
                }
            });
            columnList.add(new ExcelUtils.Column<AreaContract>("分账比例") {
                @Override
                public String render(AreaContract areaContract) {
                    return areaContract.getAccount_ratio() != null ? areaContract.getAccount_ratio() + "%" : "";
                }
            });
            columnList.add(new ExcelUtils.Column<AreaContract>("审核状态") {
                @Override
                public String render(AreaContract areaContract) {
                    return Option.getActiveText(AreaContractStatusOption.options, areaContract.getStatus());
                }
            });
            columnList.add(new ExcelUtils.Column<AreaContract>("备注") {
                @Override
                public String render(AreaContract areaContract) {
                    return areaContract.getRemark();
                }
            });
            ExcelUtils.export(columnList, areaContractList, response, "areaContractList.xlsx");
            return null;
        } else {
            return new Result(CodeMsg.SUCCESS)
                    .putData("areaContractList", areaContractList)
                    .putData("areaList", areaList)
                    .putData("countGroupArea", countCapsuleScheduled.countGroupArea)
                    .putData("areaCreateTimeMap", countCapsuleScheduled.areaCreateTimeMap);
        }
    }

    @AuthRequired(AuthRequired.area_contract)
    @GetMapping("/api/area_contract/{area_id:\\d+}")
    @ResponseBody
    public Result getByAreaId(@PathVariable("area_id") int area_id) {
        AreaContract areaContract = areaContractDao.getItem(new PrimaryKey("area_id", area_id));
        if (areaContract == null) {
            return new Result(CodeMsg.NO_FOUND);
        } else {
            return new Result(CodeMsg.SUCCESS)
                    .putData("areaContract", areaContract)
                    .putData("area", areaService.getAreaById(areaContract.getArea_id()));
        }
    }


    @AuthRequired(AuthRequired.area_contract_saler)
    @PostMapping("/api/area_contract/create/forSaler")
    @ResponseBody
    public Result createForSaler(@RequestBody AreaContract criteria) {
        Date now = new Date();
        String saler_username = UsernameLocal.get();
        Op op = opUserService.getOpByUsername(saler_username, null);
        if (StringUtils.isBlank(op.getFullname()) || StringUtils.isBlank(op.getCity())) {
            return new Result(-1, "请设置您的姓名及城市");
        }

        if (criteria == null) throw new XiangShuiException("参数不能为空");
        if (criteria.getArea_id() == null) throw new XiangShuiException("场地编号不能为空");
        AreaContract areaContract = areaContractService.getByAreaId(criteria.getArea_id());
        if (areaContract != null) throw new XiangShuiException("场地重复创建，请核实");
        Area area = areaService.getAreaById(criteria.getArea_id());
        if (area == null) throw new XiangShuiException("场地不存在");

        areaContractService.validateCustomer(criteria);

        criteria.setStatus(AreaContractStatusOption.normal.value);

        criteria.setSaler(op.getFullname());
        criteria.setSaler_city(op.getCity());

        criteria.setCreate_time(now.getTime() / 1000);
        criteria.setUpdate_time(now.getTime() / 1000);

        areaContractDao.putItem(criteria);

        return new Result(CodeMsg.SUCCESS);
    }


    @AuthRequired(AuthRequired.area_contract_operate)
    @PostMapping("/api/area_contract/create/forOperate")
    @ResponseBody
    public Result createForOperate(@RequestBody AreaContract criteria) {
        Date now = new Date();

        if (criteria == null) throw new XiangShuiException("参数不能为空");
        if (criteria.getArea_id() == null) throw new XiangShuiException("场地编号不能为空");
        AreaContract areaContract = areaContractService.getByAreaId(criteria.getArea_id());
        if (areaContract != null) throw new XiangShuiException("场地重复创建，请核实");
        Area area = areaService.getAreaById(criteria.getArea_id());
        if (area == null) throw new XiangShuiException("场地不存在");

        criteria.setStatus(AreaContractStatusOption.normal.value);

        if (StringUtils.isBlank(criteria.getSaler()) || StringUtils.isBlank(criteria.getSaler_city())) {
            throw new XiangShuiException("销售不能为空");
        }

        criteria.setCreate_time(now.getTime() / 1000);
        criteria.setUpdate_time(now.getTime() / 1000);

        areaContractDao.putItem(criteria);

        return new Result(CodeMsg.SUCCESS);
    }

    @AuthRequired(AuthRequired.area_contract_operate)
    @PostMapping("/api/area_contract/create/forVerify")
    @ResponseBody
    public Result createForVerify(@RequestBody AreaContract criteria) {
        Date now = new Date();
        if (criteria == null) throw new XiangShuiException("参数不能为空");
        if (criteria.getArea_id() == null) throw new XiangShuiException("场地编号不能为空");
        AreaContract areaContract = areaContractService.getByAreaId(criteria.getArea_id());
        if (areaContract != null) throw new XiangShuiException("场地重复创建，请核实");
        Area area = areaService.getAreaById(criteria.getArea_id());
        if (area == null) throw new XiangShuiException("场地不存在");
        criteria.setStatus(AreaContractStatusOption.normal.value);
        if (StringUtils.isBlank(criteria.getSaler()) || StringUtils.isBlank(criteria.getSaler_city())) {
            throw new XiangShuiException("销售不能为空");
        }
        criteria.setCreate_time(now.getTime() / 1000);
        criteria.setUpdate_time(now.getTime() / 1000);
        areaContractDao.putItem(criteria);
        return new Result(CodeMsg.SUCCESS);
    }

    @AuthRequired(AuthRequired.area_contract_saler)
    @PostMapping("/api/area_contract/{area_id:\\d+}/update/forSaler")
    @ResponseBody
    public Result updateForSaler(@PathVariable("area_id") int area_id, @RequestBody AreaContract criteria) throws Exception {

        Date now = new Date();
        String saler_username = UsernameLocal.get();
        Op op = opUserService.getOpByUsername(saler_username, null);
        if (StringUtils.isBlank(op.getFullname()) || StringUtils.isBlank(op.getCity())) {
            return new Result(-1, "请设置您的姓名及城市");
        }

        if (criteria == null) {
            criteria = new AreaContract();
        }
        criteria.setArea_id(area_id);
        AreaContract areaContract = areaContractService.getByAreaId(criteria.getArea_id());
        if (areaContract == null) throw new XiangShuiException(CodeMsg.NO_FOUND);

        if (!(op.getFullname().equals(areaContract.getSaler()) && op.getCity().equals(areaContract.getSaler_city()))) {
            throw new XiangShuiException("没有权限，只能修改自己的场地");
        }

        if (areaContract.getStatus() != null && areaContract.getStatus().equals(AreaContractStatusOption.adopt.value)) {
            throw new XiangShuiException("已审核通过，不能再次修改啦");
        }

        areaContractService.validateCustomer(criteria);

        criteria.setUpdate_time(now.getTime() / 1000);
        areaContractDao.updateItem(new PrimaryKey("area_id", criteria.getArea_id()), criteria, new String[]{
                "customer",
                "customer_email",
                "customer_contact",
                "account_ratio",
                "bank_account_name",
                "bank_account",
                "bank_branch",
                "remark",
        });
        return new Result(CodeMsg.SUCCESS);
    }


    @AuthRequired(AuthRequired.area_contract_verify)
    @PostMapping("/api/area_contract/{area_id:\\d+}/update/verify")
    @ResponseBody
    public Result updateVerify(@PathVariable("area_id") int area_id, @RequestBody AreaContract criteria) throws Exception {
        if (criteria == null || criteria.getStatus() == null) {
            throw new XiangShuiException("参数不能为空");
        }
        criteria.setArea_id(area_id);
        AreaContract areaContract = areaContractService.getByAreaId(criteria.getArea_id());
        if (areaContract == null) throw new XiangShuiException(CodeMsg.NO_FOUND);

//        areaContractService.validateCustomer(criteria);

        areaContractDao.updateItem(new PrimaryKey("area_id", criteria.getArea_id()), criteria, new String[]{
                "saler",
                "saler_city",
                "customer",
                "customer_email",
                "customer_contact",
                "account_ratio",
                "range_ratio_list",
                "bank_account_name",
                "bank_account",
                "bank_branch",
                "remark",
                "status",
        });
        return new Result(CodeMsg.SUCCESS);
    }


    @AuthRequired(AuthRequired.area_contract_verify)
    @PostMapping("/api/area_contract/{area_id:\\d+}/reckon")
    @ResponseBody
    public Result reckon(@PathVariable("area_id") Integer area_id, Integer year, Integer month) {
        if (year == null) {
            throw new XiangShuiException("年份不能为空");
        }
        if (month == null) {
            throw new XiangShuiException("月份不能为空");
        }
        LocalDate localDate = new LocalDate(year, month, 1);
        AreaBillResult areaBillResult = areaBillScheduled.reckonAreaBill(area_id, localDate.toDate().getTime() / 1000, localDate.plusMonths(1).toDate().getTime() / 1000, false);
        areaBillScheduled.upsetAreaBill(areaBillResult, year, month);
        return new Result(CodeMsg.SUCCESS);
    }

    @AuthRequired(AuthRequired.area_contract_verify)
    @GetMapping("/api/area_contract/{area_id:\\d+}/reckon/download")
    @ResponseBody
    public Result reckon_download(HttpServletRequest request, HttpServletResponse response,
                                  @PathVariable("area_id") Integer area_id, Integer year, Integer month) throws IOException {
        String op_username = UsernameLocal.get();
        boolean auth_booking_show_phone = opUserService.getAuthSet(op_username).contains(AuthRequired.auth_booking_show_phone);
        boolean auth_booking_bill_show_month_card = opUserService.getAuthSet(op_username).contains(AuthRequired.auth_booking_bill_show_month_card);
        if (year == null) {
            throw new XiangShuiException("年份不能为空");
        }
        if (month == null) {
            throw new XiangShuiException("月份不能为空");
        }
        LocalDate localDate = new LocalDate(year, month, 1);
        AreaBillResult areaBillResult = areaBillScheduled.reckonAreaBill(area_id, localDate.toDate().getTime() / 1000, localDate.plusMonths(1).toDate().getTime() / 1000, true);
        List<Booking> bookingList = areaBillResult.getBookingList();
        if (bookingList == null) {
            bookingList = new ArrayList<>();
        }
        Collections.sort(bookingList, (o1, o2) -> -(int) (o1.getCreate_time() - o2.getCreate_time()));
        excelTools.exportBookingList(bookingList, (auth_booking_show_phone ? ExcelTools.EXPORT_PHONE : 0) | (auth_booking_bill_show_month_card ? ExcelTools.EXPORT_MONTH_CARD_BILL : 0), areaBillResult.getChargeRecordMap(), response, "booking.xlsx");
        return null;
    }

}
