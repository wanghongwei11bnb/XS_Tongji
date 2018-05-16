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
import java.util.Date;
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

    @GetMapping("/api/capsule/{capsule_id:\\d+}/validateForCreate")
    @ResponseBody
    public Result validateForCreate(@PathVariable("capsule_id") Long capsule_id) {
        Capsule capsule = capsuleDao.getItem(new PrimaryKey("capsule_id", capsule_id));
        if (capsule == null) {
            return new Result(CodeMsg.SUCCESS);
        } else {
            return new Result(-1, "头等舱编号已存在");
        }
    }

    @PostMapping("/api/capsule/{capsule_id:\\d+}/update")
    @ResponseBody
    public Result update(@PathVariable("capsule_id") Long capsule_id, @RequestBody Capsule criteria) throws Exception {
        Capsule capsule = capsuleDao.getItem(new PrimaryKey("capsule_id", capsule_id));
        if (capsule == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        if (criteria.getStatus() == null) {
            return new Result(-1, "设备状态不能为空");
        }
        if (StringUtils.isBlank(criteria.getDevice_id())) {
            return new Result(-1, "设备ID不能为空");
        }
        criteria.setCapsule_id(capsule_id);
        criteria.setUpdate_time(System.currentTimeMillis() / 1000);
        capsuleDao.updateItem(new PrimaryKey("capsule_id", capsule_id), criteria, new String[]{
                "update_time",
                "status",
                "device_id",
                "is_downline",
        });
        return new Result(CodeMsg.SUCCESS);
    }

    @PostMapping("/api/capsule/create")
    @ResponseBody
    public Result create(@RequestBody Capsule criteria) throws Exception {
        if (criteria.getCapsule_id() == null || criteria.getCapsule_id() == 0) {
            return new Result(-1, "头等舱编号不能为空");
        }
        if (criteria.getArea_id() == null || criteria.getArea_id() == 0) {
            return new Result(-1, "场地编号不能为空");
        }
        if (criteria.getStatus() == null) {
            return new Result(-1, "设备状态不能为空");
        }
        if (StringUtils.isBlank(criteria.getDevice_id())) {
            return new Result(-1, "设备ID不能为空");
        }
        if (capsuleDao.getItem(new PrimaryKey("capsule_id", criteria.getCapsule_id())) != null) {
            return new Result(-1, "头等舱编号已存在");
        }
        Date now = new Date();
        criteria.setCreate_time(now.getTime() / 1000);
        criteria.setUpdate_time(now.getTime() / 1000);
        criteria.setIs_downline(0);
        criteria.setType(1);
        capsuleDao.putItem(criteria);
        return new Result(CodeMsg.SUCCESS);
    }

}
