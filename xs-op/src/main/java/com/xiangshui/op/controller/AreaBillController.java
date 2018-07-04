package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.scheduled.AreaBillScheduled;
import com.xiangshui.op.threadLocal.UsernameLocal;
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
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @Menu("分成对账单管理（开发中）")
    @AuthRequired(AuthRequired.area_bill)
    @GetMapping("/area_bill_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "area_bill_manage";
    }


    @GetMapping("/api/area_bill/search")
    @ResponseBody
    public Result search(AreaBill criteria) throws NoSuchFieldException, IllegalAccessException {
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
        List<AreaBill> areaBillList = areaBillDao.scan(scanSpec);
        List<AreaContract> areaContractList = null;
        List<Area> areaList = null;
        if (areaBillList != null && areaBillList.size() > 0) {
            Set<Integer> areaIdSet = new HashSet<>();
            areaBillList.forEach(new Consumer<AreaBill>() {
                @Override
                public void accept(AreaBill areaBill) {
                    areaIdSet.add(areaBill.getArea_id());
                }
            });
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
        return new Result(CodeMsg.SUCCESS)
                .putData("areaBillList", areaBillList)
                .putData("areaList", areaList)
                .putData("areaContractList", areaContractList);
    }

    @GetMapping("/api/area_bill/{bill_id:\\d+}")
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
