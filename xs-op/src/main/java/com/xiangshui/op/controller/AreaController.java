package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.op.annotation.AuthPassport;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.CapsuleService;
import com.xiangshui.server.service.CityService;
import com.xiangshui.server.service.S3Service;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;

@Controller
public class AreaController extends BaseController {

    @Autowired
    CityService cityService;
    @Autowired
    AreaService areaService;
    @Autowired
    AreaDao areaDao;
    @Autowired
    S3Service s3Service;

    @Autowired
    CapsuleService capsuleService;

    @AuthPassport(value = "area_manage")
    @GetMapping("/area_manage")
    public String area_manage(HttpServletRequest request) {
        setClient(request);
        return "area_manage";
    }

    @GetMapping("/api/area/search")
    @ResponseBody
    public Result search(Area criteria, Long capsule_id) throws NoSuchFieldException, IllegalAccessException {
        if (criteria == null) criteria = new Area();
        if (capsule_id != null) {
            Capsule capsule = capsuleService.getCapsuleById(capsule_id);
            if (capsule == null) {
                return new Result(-1, "头等舱编号不存在");
            } else {
                criteria.setArea_id(capsule.getArea_id());
            }
        }

        List<Area> areaList = areaService.search(criteria, null);
        if (areaList != null && areaList.size() > 0) {
            areaList.sort(new Comparator<Area>() {
                @Override
                public int compare(Area o1, Area o2) {
                    return o1.getCity().compareTo(o2.getCity());
                }
            });
        }
        return new Result(CodeMsg.SUCCESS).putData("areaList", areaList);

    }


    @GetMapping("/api/area/{area_id}/validateForCreate")
    @ResponseBody
    public Result validateForCreate(@PathVariable("area_id") Integer area_id) {
        Area area = areaDao.getItem(new PrimaryKey("area_id", area_id));
        if (area == null) {
            return new Result(CodeMsg.SUCCESS);
        } else {
            return new Result(-1, "场地编号已存在");
        }
    }


    @GetMapping("/api/area/{area_id}")
    @ResponseBody
    public Result get(@PathVariable("area_id") Integer area_id) {
        Area area = areaDao.getItem(new PrimaryKey("area_id", area_id));
        if (area != null) {
            return new Result(CodeMsg.SUCCESS).putData("area", area);
        } else {
            return new Result(CodeMsg.NO_FOUND);
        }
    }

    @GetMapping("/api/area/{area_id}/types")
    @ResponseBody
    public Result getTypes(@PathVariable("area_id") Integer area_id) {
        Area area = areaDao.getItem(new PrimaryKey("area_id", area_id));
        if (area == null) {
            return new Result(CodeMsg.NO_FOUND);
        } else {
            return new Result(CodeMsg.SUCCESS).putData("types", area.getTypes());
        }
    }


    @PostMapping("/api/area/{area_id:\\d+}/update")
    @ResponseBody
    public Result update(@PathVariable("area_id") Integer area_id, @RequestBody Area criteria) throws Exception {
        if (area_id == null || area_id < 1) {
            return new Result(-1, "场地编号不能小于1");
        }
        criteria.setArea_id(area_id);
        areaService.updateArea(criteria);
        return new Result(CodeMsg.SUCCESS);
    }

    @PostMapping("/api/area/create")
    @ResponseBody
    public Result create(@RequestBody Area criteria) throws Exception {
        areaService.createArea(criteria);
        return new Result(CodeMsg.SUCCESS);
    }

    @PostMapping("/api/area/{area_id:\\d+}/update/types")
    @ResponseBody
    public Result update_types(@PathVariable("area_id") Integer area_id, @RequestBody Area criteria) throws Exception {
        if (area_id == null || area_id < 1) {
            return new Result(-1, "场地编号不能小于1");
        }
        if (criteria == null) {
            return new Result(-1, "参数不能为空");
        }
        criteria.setArea_id(area_id);
        areaService.updateTypes(criteria);
//        areaService.clean_area_cache_notification();
        return new Result(CodeMsg.SUCCESS);
    }

    @PostMapping("/api/clean_area_cache_notification")
    @ResponseBody
    public Result clean_area_cache_notification() {
        areaService.clean_area_cache_notification();
        return new Result(CodeMsg.SUCCESS);
    }
}
