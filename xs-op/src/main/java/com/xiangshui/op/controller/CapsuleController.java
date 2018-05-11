package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.CapsuleDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.relation.CapsuleRelation;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.CityService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CapsuleController extends BaseController {

    @Autowired
    CityService cityService;
    @Autowired
    AreaService areaService;
    @Autowired
    AreaDao areaDao;
    @Autowired
    CapsuleDao capsuleDao;


    @GetMapping("/api/capsule/search")
    @ResponseBody
    public Result search(Integer area_id) {
        ScanSpec scanSpec = new ScanSpec();
        List<ScanFilter> filterList = new ArrayList<ScanFilter>();

        if (area_id != null) {
            filterList.add(new ScanFilter("area_id").eq(area_id));
        }
        scanSpec.withScanFilters(filterList.toArray(new ScanFilter[]{}));
        scanSpec.setMaxResultSize(500);
        List<Capsule> capsuleList = capsuleDao.scan(scanSpec);
        return new Result(CodeMsg.SUCCESS).putData("list", capsuleList);
    }


    @GetMapping("/api/capsule/{capsule_id:\\d+}")
    @ResponseBody
    public Result get(@PathVariable("capsule_id") Long capsule_id) {
        Capsule capsule = capsuleDao.getItem(new PrimaryKey("capsule_id", capsule_id));
        if (capsule == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        Area area = areaDao.getItem(new PrimaryKey("area_id", capsule.getArea_id()));
        if (area != null) {
            CapsuleRelation capsuleRelation = new CapsuleRelation();
            BeanUtils.copyProperties(capsule, capsuleRelation);
            capsuleRelation.set_area(area);
            return new Result(CodeMsg.SUCCESS).putData("capsule", capsuleRelation);
        } else {
            return new Result(CodeMsg.SUCCESS).putData("capsule", capsule);
        }
    }


}
