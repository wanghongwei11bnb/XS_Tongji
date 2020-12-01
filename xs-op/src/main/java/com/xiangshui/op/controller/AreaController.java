package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.scheduled.CountCapsuleScheduled;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.AreaContract;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.CapsuleService;
import com.xiangshui.server.service.CityService;
import com.xiangshui.server.service.S3Service;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
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

    @Autowired
    AreaContractController areaContractController;

    @Autowired
    CountCapsuleScheduled countCapsuleScheduled;

    @Menu(value = "场地管理")
    @AuthRequired("场地管理（全国）")
    @GetMapping("/area_manage")
    public String area_manage(HttpServletRequest request) {
        setClient(request);
        return "area_manage";
    }

    @GetMapping("/api/area/search")
    @ResponseBody
    public Result search(Area criteria, Long capsule_id, String device_id) throws NoSuchFieldException, IllegalAccessException {
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
        return new Result(CodeMsg.SUCCESS)
                .putData("areaList", capsuleAuthorityTools.filterArea(areaList))
                .putData("countGroupArea", countCapsuleScheduled.countGroupArea).putData("cityList", cityService.getCityList());
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
            capsuleAuthorityTools.authForException(area);
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
            capsuleAuthorityTools.authForException(area);
            return new Result(CodeMsg.SUCCESS).putData("types", area.getTypes());
        }
    }


    @PostMapping("/api/area/{area_id:\\d+}/update")
    @ResponseBody
    public Result update(@PathVariable("area_id") Integer area_id, @RequestBody Area criteria) throws Exception {
        if (area_id == null || area_id < 1) {
            return new Result(-1, "场地编号不能小于1");
        }
        Area area = areaDao.getItem(new PrimaryKey("area_id", area_id));
        capsuleAuthorityTools.authForException(area);
        criteria.setArea_id(area_id);
        areaService.updateArea(criteria);
        return new Result(CodeMsg.SUCCESS);
    }

    @PostMapping("/api/area/create")
    @ResponseBody
    public Result create(@RequestBody Area criteria, String saler, String saler_city) throws Exception {
        if (StringUtils.isBlank(saler) || StringUtils.isBlank(saler_city)) {
            return new Result(-1, "请选择销售");
        }
        capsuleAuthorityTools.authForException(criteria);
        areaService.createArea(criteria);
        AreaContract areaContract = new AreaContract();
        areaContract.setArea_id(criteria.getArea_id());
        areaContract.setSaler(saler);
        areaContract.setSaler_city(saler_city);
        areaContractController.createForOperate(areaContract);
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
        Area area = areaDao.getItem(new PrimaryKey("area_id", area_id));
        capsuleAuthorityTools.authForException(area);
        areaService.updateTypes(criteria);
        return new Result(CodeMsg.SUCCESS);
    }

    @PostMapping("/api/clean_area_cache_notification")
    @ResponseBody
    public Result clean_area_cache_notification() {
        areaService.clean_area_cache_notification();
        return new Result(CodeMsg.SUCCESS);
    }


    @Autowired
    BookingController bookingController;

    @GetMapping("/api/area/{area_id:\\d+}/booking/search")
    @ResponseBody
    public Result booking_search(HttpServletRequest request, HttpServletResponse response, @PathVariable("area_id") Integer area_id, Long capsule_id, Date create_date_start, Date create_date_end) throws Exception {
        Booking criteria = new Booking();
        criteria.setArea_id(area_id);
        criteria.setCapsule_id(capsule_id);
        capsuleAuthorityTools.authForException(criteria);
        return bookingController.search(request, response, null, null, null, criteria, create_date_start, create_date_end, null, false, null, null);
    }
}
