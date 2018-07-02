package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.scheduled.AreaBillScheduled;
import com.xiangshui.op.threadLocal.UsernameLocal;
import com.xiangshui.server.dao.AreaContractDao;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BaseDynamoDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.AreaContract;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.server.exception.XiangShuiException;
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

    @Autowired
    AreaBillScheduled areaBillScheduled;

    @Menu("客户分成对账(开发中)")
    @AuthRequired(AuthRequired.area_contract)
    @GetMapping("/area_contract_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "area_contract_manage";
    }


    @AuthRequired(AuthRequired.area_contract)
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
        String saler_username = UsernameLocal.get();
        Op op = opUserService.getOpByUsername(saler_username, null);
        if (StringUtils.isBlank(op.getFullname()) || StringUtils.isBlank(op.getCity())) {
            return new Result(-1, "请设置您的姓名及城市");
        }
        criteria.setSaler(op.getFullname());
        criteria.setSaler_city(op.getCity());
        areaContractService.createForSaler(criteria, UsernameLocal.get());
        return new Result(CodeMsg.SUCCESS);
    }

    @AuthRequired(AuthRequired.area_contract_saler)
    @PostMapping("/api/area_contract/{area_id:\\d+}/update/forSaler")
    @ResponseBody
    public Result updateForSaler(@PathVariable("area_id") int area_id, @RequestBody AreaContract criteria) throws Exception {
        if (criteria == null) {
            criteria = new AreaContract();
        }
        criteria.setArea_id(area_id);
        String saler_username = UsernameLocal.get();
        Op op = opUserService.getOpByUsername(saler_username, null);
        if (StringUtils.isBlank(op.getFullname()) || StringUtils.isBlank(op.getCity())) {
            return new Result(-1, "请设置您的姓名及城市");
        }
        return new Result(CodeMsg.SUCCESS);
    }


    @AuthRequired(AuthRequired.area_contract_verify)
    @PostMapping("/api/area_contract/{area_id:\\d+}/update/verify")
    @ResponseBody
    public Result updateVerify(@PathVariable("area_id") int area_id, @RequestBody AreaContract criteria) throws Exception {
        if (criteria == null) {
            criteria = new AreaContract();
        }
        criteria.setArea_id(area_id);
        areaContractDao.updateItem(new PrimaryKey("area_id", criteria.getArea_id()), criteria, new String[]{"status"});
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


}
