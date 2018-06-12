package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.op.annotation.AreaRequired;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.threadLocal.UsernameLocal;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.service.*;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Controller
public class MyAreaController extends BaseController {

    @Autowired
    CityService cityService;
    @Autowired
    AreaService areaService;
    @Autowired
    AreaDao areaDao;
    @Autowired
    S3Service s3Service;

    @Autowired
    UserService userService;
    @Autowired
    OpUserService opUserService;

    @Autowired
    CapsuleService capsuleService;

    @GetMapping("/main_area_manage")
    public String area_manage(HttpServletRequest request) {
        setClient(request);
        return "main_area_manage";
    }

    @GetMapping("/api/main_area/search")
    @ResponseBody
    public Result search(HttpServletRequest request) {
        String op_username = UsernameLocal.get();
        Set<Integer> areaSet = opUserService.getAreaSet(op_username);
        List<Area> areaList = areaService.getAreaListByIds(areaSet.toArray(new Integer[areaSet.size()]));
        if (areaList != null && areaList.size() > 0) {
            areaList.sort(Comparator.comparing(Area::getCity));
        }
        return new Result(CodeMsg.SUCCESS).putData("areaList", areaList);
    }


    @GetMapping("/api/main_area/{area_id}")
    @ResponseBody
    public Result get(@PathVariable("area_id") Integer area_id) {
        Area area = areaDao.getItem(new PrimaryKey("area_id", area_id));
        if (area != null) {
            return new Result(CodeMsg.SUCCESS).putData("area", area);
        } else {
            return new Result(CodeMsg.NO_FOUND);
        }
    }

    @GetMapping("/api/main_area/{area_id}/types")
    @ResponseBody
    public Result getTypes(@PathVariable("area_id") Integer area_id) {
        Area area = areaDao.getItem(new PrimaryKey("area_id", area_id));
        if (area == null) {
            return new Result(CodeMsg.NO_FOUND);
        } else {
            return new Result(CodeMsg.SUCCESS).putData("types", area.getTypes());
        }
    }


    @PostMapping("/api/main_area/{area_id:\\d+}/update")
    @ResponseBody
    public Result update(@PathVariable("area_id") Integer area_id, @RequestBody Area criteria) throws Exception {
        if (area_id == null || area_id < 1) {
            return new Result(-1, "场地编号不能小于1");
        }
        criteria.setArea_id(area_id);
        areaService.updateArea(criteria);
        return new Result(CodeMsg.SUCCESS);
    }

    @PostMapping("/api/main_area/{area_id:\\d+}/update/types")
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
        return new Result(CodeMsg.SUCCESS);
    }


    @Autowired
    BookingController bookingController;

    @GetMapping("/api/main_area/{area_id:\\d+}/booking/search")
    @ResponseBody
    public Result booking_search(HttpServletRequest request, HttpServletResponse response, @PathVariable("area_id") Integer area_id, Date create_date_start, Date create_date_end) throws Exception {
        Booking criteria = new Booking();
        criteria.setArea_id(area_id);
        return bookingController.search(request, response, null, null, null, criteria, create_date_start, create_date_end, false);
    }
}
