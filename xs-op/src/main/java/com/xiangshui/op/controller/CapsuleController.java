package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.constant.CapsuleStatusOption;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.CapsuleDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.relation.CapsuleRelation;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.CapsuleService;
import com.xiangshui.server.service.CityService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    @Autowired
    CapsuleService capsuleService;


    @GetMapping("/api/capsule/search")
    @ResponseBody
    public Result search(Capsule criteria) throws NoSuchFieldException, IllegalAccessException {
        return new Result(CodeMsg.SUCCESS).putData("capsuleList", capsuleService.search(criteria, null));
    }


    @GetMapping("/api/capsule/{capsule_id:\\d+}")
    @ResponseBody
    public Result get(@PathVariable("capsule_id") Long capsule_id) {

        Capsule capsule = capsuleService.getCapsuleById(capsule_id);
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

    @GetMapping("/api/capsule/{capsule_id:\\d+}/validateDeviceIdForSave/{device_id}")
    @ResponseBody
    public Result validateDeviceIdForSave(@PathVariable("capsule_id") Long capsule_id, @PathVariable("device_id") String device_id) {
        List<Capsule> capsuleList = capsuleDao.scan(new ScanSpec().withScanFilters(new ScanFilter("device_id").eq(device_id), new ScanFilter("capsule_id").ne(capsule_id)));
        if (capsuleList == null || capsuleList.size() == 0) {
            return new Result(CodeMsg.SUCCESS);
        } else {
            Set<Integer> areaIdSet = new HashSet<>();
            for (Capsule capsule : capsuleList) {
                areaIdSet.add(capsule.getArea_id());
            }
            List<Area> areaList = areaDao.batchGetItem("area_id", areaIdSet.toArray(), null);
            return new Result(-1, "硬件设备ID已占用").putData("capsuleList", capsuleList).putData("areaList", areaList);
        }
    }

    @PostMapping("/api/capsule/{capsule_id:\\d+}/update")
    @ResponseBody
    public Result update(@PathVariable("capsule_id") Long capsule_id, @RequestBody Capsule criteria) throws Exception {
        criteria.setCapsule_id(capsule_id);
        capsuleService.updateCapsule(criteria);
        return new Result(CodeMsg.SUCCESS);
    }

    @PostMapping("/api/capsule/create")
    @ResponseBody
    public Result create(@RequestBody Capsule criteria) throws Exception {
        capsuleService.createCapsule(criteria);
        return new Result(CodeMsg.SUCCESS);
    }

}
