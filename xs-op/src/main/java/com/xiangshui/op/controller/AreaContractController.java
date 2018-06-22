package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.dao.AreaContractDao;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BaseDynamoDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.AreaContract;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.service.*;
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

    @Menu("场地合同管理")
    @GetMapping("/area_contract_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "area_contract_manage";
    }


    @GetMapping("/api/area_contract/search")
    @ResponseBody
    public Result search(HttpServletRequest request, HttpServletResponse response, AreaContract criteria, Boolean download) throws NoSuchFieldException, IllegalAccessException {
        if (download == null) {
            download = false;
        }
        if (criteria == null) {
            criteria = new AreaContract();
        }

        List<ScanFilter> scanFilterList = areaContractDao.makeScanFilterList(criteria, new String[]{
                "area_id",
                "saler_city",
                "customer_email",
                "customer_contact",
                "status",
        });

        if (scanFilterList == null) {
            scanFilterList = new ArrayList<>();
        }
        if (StringUtils.isNotBlank(criteria.getSaler())) {
            scanFilterList.add(new ScanFilter("saler").contains(criteria.getSaler()));
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
            Set<Integer> areaIdSet = new HashSet<>();
            areaContractList.forEach(areaContract -> areaIdSet.add(areaContract.getArea_id()));
            if (areaIdSet.size() > 0) {
                areaList = areaService.getAreaListByIds(areaIdSet.toArray(new Integer[areaIdSet.size()]));
            }
        }
        return new Result(CodeMsg.SUCCESS)
                .putData("areaContractList", areaContractList)
                .putData("areaList", areaList);
    }


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

    @PostMapping("/api/area_contract/create")
    @ResponseBody
    public Result create(@RequestBody AreaContract criteria) {
        areaContractService.create(criteria);
        return new Result(CodeMsg.SUCCESS);
    }

    @PostMapping("/api/area_contract/{area_id:\\d+}/update")
    @ResponseBody
    public Result update(@PathVariable("area_id") int area_id, @RequestBody AreaContract criteria) throws Exception {
        if (criteria == null) {
            criteria = new AreaContract();
        }
        criteria.setArea_id(area_id);
        areaContractService.update(criteria);
        return new Result(CodeMsg.SUCCESS);
    }

}
