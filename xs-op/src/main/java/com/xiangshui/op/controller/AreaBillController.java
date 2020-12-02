package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.scheduled.AreaBillScheduled;
import com.xiangshui.op.threadLocal.UsernameLocal;
import com.xiangshui.server.constant.AreaStatusOption;
import com.xiangshui.server.dao.AreaBillDao;
import com.xiangshui.server.dao.AreaContractDao;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BaseDynamoDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.AreaBill;
import com.xiangshui.server.domain.AreaContract;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.service.*;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.Option;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Controller
public class AreaBillController extends BaseController {

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
    AreaBillDao areaBillDao;

    @Menu("分成对账单管理")
    @AuthRequired(AuthRequired.area_bill)
    @GetMapping("/area_bill_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "area_bill_manage";
    }


    @GetMapping("/api/area_bill/search")
    @AuthRequired(AuthRequired.area_bill)
    @ResponseBody
    public Result search(HttpServletRequest request, HttpServletResponse response, AreaBill criteria, Boolean download) throws NoSuchFieldException, IllegalAccessException, IOException {
        if (download == null) download = false;
        if (criteria == null) {
            criteria = new AreaBill();
        }
        List<ScanFilter> scanFilterList = areaBillDao.makeScanFilterList(criteria, new String[]{
                "area_id",
                "year",
                "month",
                "status",
        });
        if (scanFilterList == null) {
            scanFilterList = new ArrayList<>();
        }

        ScanSpec scanSpec = new ScanSpec();
        if (scanFilterList.size() > 0) {
            scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        }
        if (download) {
            scanSpec.withMaxResultSize(BaseDynamoDao.maxDownloadSize);
        }
        List<AreaBill> areaBillList = areaBillDao.scan(scanSpec);
        areaBillList = capsuleAuthorityTools.filterAreaBill(areaBillList);
        List<AreaContract> areaContractList = null;
        List<Area> areaList = null;
        if (areaBillList != null && areaBillList.size() > 0) {

            areaBillList.sort((o1, o2) -> (o2.getYear() * 100 + o2.getMonth()) - (o1.getYear() * 100 + o1.getMonth()));

            Set<Integer> areaIdSet = new HashSet<>();
            areaBillList.forEach(areaBill -> areaIdSet.add(areaBill.getArea_id()));
            if (areaIdSet.size() > 0) {
                areaList = ServiceUtils.division(areaIdSet.toArray(new Integer[areaIdSet.size()]), 100, new CallBackForResult<Integer[], List<Area>>() {
                    @Override
                    public List<Area> run(Integer[] object) {
                        return areaDao.batchGetItem("area_id", object);
                    }
                }, new Integer[0]);

                areaContractList = ServiceUtils.division(areaIdSet.toArray(new Integer[areaIdSet.size()]), 100, new CallBackForResult<Integer[], List<AreaContract>>() {
                    @Override
                    public List<AreaContract> run(Integer[] integers) {
                        return areaContractDao.batchGetItem("area_id", integers);
                    }
                }, new Integer[0]);
            }
        }

        if (download) {
            Map<Integer, Area> areaMap = new HashMap<>();
            if (areaList != null) {
                areaList.forEach(area -> areaMap.put(area.getArea_id(), area));
            }
            Map<Integer, AreaContract> areaContractMap = new HashMap<>();
            if (areaContractList != null) {
                areaContractList.forEach(areaContract -> areaContractMap.put(areaContract.getArea_id(), areaContract));
            }

            ExcelUtils.export(Arrays.asList(
                    new ExcelUtils.Column<AreaBill>("账单月份") {
                        @Override
                        public String render(AreaBill areaBill) {
                            return areaBill.getYear() + "年" + areaBill.getMonth() + "月";
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("场地编号") {
                        @Override
                        public String render(AreaBill areaBill) {
                            return String.valueOf(areaBill.getArea_id());
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("场地名称") {
                        @Override
                        public String render(AreaBill areaBill) {
                            Area area = areaMap.get(areaBill.getArea_id());
                            return area != null ? area.getTitle() : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("场地投放城市") {
                        @Override
                        public String render(AreaBill areaBill) {
                            Area area = areaMap.get(areaBill.getArea_id());
                            return area != null ? area.getCity() : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("投放地址") {
                        @Override
                        public String render(AreaBill areaBill) {
                            Area area = areaMap.get(areaBill.getArea_id());
                            return area != null ? area.getAddress() : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("运营状态") {
                        @Override
                        public String render(AreaBill areaBill) {
                            Area area = areaMap.get(areaBill.getArea_id());
                            return area != null ? Option.getActiveText(AreaStatusOption.options, area.getStatus()) : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("销售人员") {
                        @Override
                        public String render(AreaBill areaBill) {
                            AreaContract areaContract = areaContractMap.get(areaBill.getArea_id());
                            return areaContract != null ? areaContract.getSaler() : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("所属公司") {
                        @Override
                        public String render(AreaBill areaBill) {
                            AreaContract areaContract = areaContractMap.get(areaBill.getArea_id());
                            return areaContract != null ? areaContract.getSaler_city() : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("客户公司名称") {
                        @Override
                        public String render(AreaBill areaBill) {
                            AreaContract areaContract = areaContractMap.get(areaBill.getArea_id());
                            return areaContract != null ? areaContract.getCustomer() : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("客户公司邮箱") {
                        @Override
                        public String render(AreaBill areaBill) {
                            AreaContract areaContract = areaContractMap.get(areaBill.getArea_id());
                            return areaContract != null ? areaContract.getCustomer_email() : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("客户公司联系方式") {
                        @Override
                        public String render(AreaBill areaBill) {
                            AreaContract areaContract = areaContractMap.get(areaBill.getArea_id());
                            return areaContract != null ? areaContract.getCustomer_contact() : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("客户银行付款账户") {
                        @Override
                        public String render(AreaBill areaBill) {
                            AreaContract areaContract = areaContractMap.get(areaBill.getArea_id());
                            return areaContract != null ? areaContract.getBank_account_name() : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("客户银行付款帐号") {
                        @Override
                        public String render(AreaBill areaBill) {
                            AreaContract areaContract = areaContractMap.get(areaBill.getArea_id());
                            return areaContract != null ? areaContract.getBank_account() : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("客户银行支行信息") {
                        @Override
                        public String render(AreaBill areaBill) {
                            AreaContract areaContract = areaContractMap.get(areaBill.getArea_id());
                            return areaContract != null ? areaContract.getBank_branch() : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("合同创建时间") {
                        @Override
                        public String render(AreaBill areaBill) {
                            AreaContract areaContract = areaContractMap.get(areaBill.getArea_id());
                            return areaContract != null ? DateUtils.format(areaContract.getCreate_time() * 1000, "yyyy-MM-dd") : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("合同备注") {
                        @Override
                        public String render(AreaBill areaBill) {
                            AreaContract areaContract = areaContractMap.get(areaBill.getArea_id());
                            return areaContract != null ? areaContract.getRemark() : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("订单数量（笔）") {
                        @Override
                        public String render(AreaBill areaBill) {
                            return String.valueOf(areaBill.getBooking_count());
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("收款金额（元）") {
                        @Override
                        public String render(AreaBill areaBill) {
                            return String.valueOf(
                                    (
                                            (areaBill.getPay_price() != null ? areaBill.getPay_price() : 0)
                                                    + (areaBill.getCharge_price() != null ? areaBill.getCharge_price() : 0)
                                                    + (areaBill.getMonth_card_price() != null ? areaBill.getMonth_card_price() : 0)
                                    ) / 100f
                            );
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("分账比例") {
                        @Override
                        public String render(AreaBill areaBill) {
                            AreaContract areaContract = areaContractMap.get(areaBill.getArea_id());
                            return areaContract != null ?
                                    (areaContract.getAccount_ratio() != null ? areaContract.getAccount_ratio() + "%" : null)
                                    : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("分账金额（元）") {
                        @Override
                        public String render(AreaBill areaBill) {
                            return areaBill.getRatio_price() != null ? String.valueOf(areaBill.getRatio_price() / 100f) : null;
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("账单生成时间") {
                        @Override
                        public String render(AreaBill areaBill) {
                            return DateUtils.format(areaBill.getUpdate_time() * 1000, "yyyy-MM-dd HH:mm:ss");
                        }
                    },
                    new ExcelUtils.Column<AreaBill>("状态") {
                        @Override
                        public String render(AreaBill areaBill) {
                            return new Integer(1).equals(areaBill.getStatus()) ? "已付款" : "未付款";
                        }
                    }


            ), areaBillList, response, "areaBillList.xlsx");

            return null;
        }


        return new Result(CodeMsg.SUCCESS)
                .putData("areaBillList", areaBillList)
                .putData("areaList", areaList)
                .putData("areaContractList", areaContractList);
    }

    @GetMapping("/api/area_bill/{bill_id:\\d+}")
    @AuthRequired(AuthRequired.area_bill)
    @ResponseBody
    public Result get(@PathVariable("bill_id") long bill_id) {

        AreaBill areaBill = areaBillDao.getItem(new PrimaryKey("bill_id", bill_id));
        if (areaBill == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        Area area = null;
        AreaContract areaContract = null;
        if (areaBill != null && areaBill.getArea_id() != null) {
            area = areaDao.getItem(new PrimaryKey("area_id", areaBill.getArea_id()));
            areaContract = areaContractDao.getItem(new PrimaryKey("area_id", areaBill.getArea_id()));
        }
        return new Result(CodeMsg.SUCCESS)
                .putData("areaBill", areaBill)
                .putData("area", area)
                .putData("areaContract", areaContract);
    }

    @PostMapping("/api/area_bill/{bill_id:\\d+}/update/status")
    @AuthRequired(AuthRequired.area_bill)
    @ResponseBody
    public Result update_status(@PathVariable("bill_id") long bill_id, AreaBill criteria) throws Exception {
        AreaBill areaBill = areaBillDao.getItem(new PrimaryKey("bill_id", bill_id));
        if (areaBill == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        if (criteria == null) {
            criteria = new AreaBill();
        }
        if (areaBill.getStatus() != null && areaBill.getStatus().equals(1)) {
            return new Result(-1, "已结算的账单不能修改");
        }
        areaBillDao.updateItem(new PrimaryKey("bill_id", bill_id), criteria, new String[]{
                "status",
        });
        return new Result(CodeMsg.SUCCESS);
    }

}
