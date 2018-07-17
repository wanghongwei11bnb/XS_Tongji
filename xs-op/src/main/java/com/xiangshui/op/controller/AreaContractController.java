package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.scheduled.AreaBillScheduled;
import com.xiangshui.op.threadLocal.UsernameLocal;
import com.xiangshui.server.constant.AreaContractStatusOption;
import com.xiangshui.server.constant.AreaStatusOption;
import com.xiangshui.server.constant.BookingStatusOption;
import com.xiangshui.server.constant.PayTypeOption;
import com.xiangshui.server.dao.AreaContractDao;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BaseDynamoDao;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.service.*;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.Option;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Controller
public class AreaContractController extends BaseController {

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
        List<Area> areaList = null;
        if (areaContractList != null && areaContractList.size() > 0) {
            areaContractList.sort((o1, o2) -> {
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
                areaList = ServiceUtils.division(areaIdSet.toArray(new Integer[0]), 100, new CallBackForResult<Integer[], List<Area>>() {
                    public List<Area> run(Integer[] object) {
                        return areaDao.batchGetItem("area_id", object, null);
                    }
                }, new Integer[0]);
            }
        }


        if (download) {
            Map<Integer, Area> areaMap = new HashMap<>();
            if (areaList != null && areaList.size() > 0) {
                areaList.forEach(new Consumer<Area>() {
                    @Override
                    public void accept(Area area) {
                        areaMap.put(area.getArea_id(), area);
                    }
                });
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
            XSSFWorkbook workbook = ExcelUtils.export(columnList, areaContractList);
            response.addHeader("Content-Disposition", "attachment;filename=" + new String("areaContractList.xlsx".getBytes()));
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            workbook.close();
            return null;
        } else {
            return new Result(CodeMsg.SUCCESS)
                    .putData("areaContractList", areaContractList)
                    .putData("areaList", areaList);
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
        areaBillScheduled.makeBill(area_id, year, month);
        return new Result(CodeMsg.SUCCESS);
    }

    @AuthRequired(AuthRequired.area_contract_verify)
    @GetMapping("/api/area_contract/{area_id:\\d+}/reckon/download")
    @ResponseBody
    public Result reckon_download(HttpServletRequest request, HttpServletResponse response,
                                  @PathVariable("area_id") Integer area_id, Integer year, Integer month) throws IOException {
        if (year == null) {
            throw new XiangShuiException("年份不能为空");
        }
        if (month == null) {
            throw new XiangShuiException("月份不能为空");
        }
        List<Booking> bookingList = areaBillScheduled.billBookingList(area_id, year, month);

        if (bookingList == null) {
            bookingList = new ArrayList<>();
        }
        List<Area> areaList = null;
        if (bookingList != null && bookingList.size() > 0) {
            areaList = areaService.getAreaListByBooking(bookingList, new String[]{"area_id", "title", "city", "address", "status"});
            Collections.sort(bookingList, new Comparator<Booking>() {
                @Override
                public int compare(Booking o1, Booking o2) {
                    return -(int) (o1.getCreate_time() - o2.getCreate_time());
                }
            });

        }


        Map<Integer, Area> areaMap = new HashMap<>();
        Map<Integer, UserInfo> userInfoMap = new HashMap<>();

        if (areaList != null && areaList.size() > 0) {
            areaList.forEach(new Consumer<Area>() {
                @Override
                public void accept(Area area) {
                    if (area != null) {
                        areaMap.put(area.getArea_id(), area);
                    }
                }
            });
        }

        List<List<String>> data = new ArrayList<>();

        List<String> headRow = new ArrayList<>();
        headRow.add("订单编号");
        headRow.add("创建时间");
        headRow.add("结束时间");
        headRow.add("订单状态");
        headRow.add("订单总金额");
        headRow.add("实际充值金额");
        headRow.add("系统赠送金额");
        headRow.add("实际付款金额");
        headRow.add("支付方式");
        headRow.add("头等舱编号");
        headRow.add("场地编号");
        headRow.add("场地名称");
        headRow.add("城市");
        headRow.add("地址");
        headRow.add("用户UIN");
        headRow.add("用户手机号");
        headRow.add("订单来源");
        data.add(headRow);
        if (bookingList != null && bookingList.size() > 0) {
            bookingList.forEach(new Consumer<Booking>() {
                @Override
                public void accept(Booking booking) {
                    if (booking == null) {
                        return;
                    }
                    Area area = areaMap.get(booking.getArea_id());
                    if (area == null) {
                        return;
                    }
                    if (AreaStatusOption.offline.value.equals(area.getStatus())) {
                        return;
                    }
                    List<String> row = new ArrayList<>();
                    row.add(String.valueOf(booking.getBooking_id()));
                    row.add((booking.getCreate_time() != null && booking.getCreate_time() > 0 ?
                            DateUtils.format(booking.getCreate_time() * 1000, "yyyy-MM-dd HH:mm")
                            : ""));
                    row.add("" + (booking.getEnd_time() != null && booking.getEnd_time() > 0 ?
                            DateUtils.format(booking.getEnd_time() * 1000, "yyyy-MM-dd HH:mm")
                            : null));
                    row.add("" + Option.getActiveText(BookingStatusOption.options, booking.getStatus()));
                    row.add(booking.getFinal_price() != null ? String.valueOf(booking.getFinal_price() / 100f) : "");

                    row.add(booking.getFrom_charge() != null ? String.valueOf(booking.getFrom_charge() / 100f) : "");
                    row.add(booking.getFrom_bonus() != null ? String.valueOf(booking.getFrom_bonus() / 100f) : "");

                    row.add(booking.getUse_pay() != null ? booking.getUse_pay() / 100f + "" : "");
                    row.add("" + Option.getActiveText(PayTypeOption.options, booking.getPay_type()));
                    row.add("" + booking.getCapsule_id());
                    row.add("" + booking.getArea_id());
                    row.add("" + (areaMap.containsKey(booking.getArea_id()) ?
                            areaMap.get(booking.getArea_id()).getTitle()
                            : null));
                    row.add("" + (areaMap.containsKey(booking.getArea_id()) ?
                            areaMap.get(booking.getArea_id()).getCity()
                            : null));
                    row.add("" + (areaMap.containsKey(booking.getArea_id()) ?
                            areaMap.get(booking.getArea_id()).getAddress()
                            : null));
                    row.add("" + booking.getUin());
                    row.add("" + (userInfoMap.containsKey(booking.getUin()) ?
                            userInfoMap.get(booking.getUin()).getPhone()
                            : null));
                    row.add(booking.getReq_from());
                    data.add(row);
                }
            });
        }

        XSSFWorkbook workbook = ExcelUtils.export(data);
        response.addHeader("Content-Disposition", "attachment;filename=" + new String("booking.xlsx".getBytes()));
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
        return null;


    }


}
