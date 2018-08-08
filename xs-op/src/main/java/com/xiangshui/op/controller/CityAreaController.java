package com.xiangshui.op.controller;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.CityRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.bean.Session;
import com.xiangshui.op.threadLocal.SessionLocal;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.redis.OpPrefix;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.domain.City;
import com.xiangshui.server.service.*;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Controller
public class CityAreaController extends BaseController {

    @Autowired
    CityService cityService;
    @Autowired
    AreaService areaService;
    @Autowired
    AreaDao areaDao;
    @Autowired
    S3Service s3Service;

    @Autowired
    OpUserService opUserService;

    @Autowired
    CapsuleService capsuleService;
    @Autowired
    RedisService redisService;


    public boolean checkCity(HttpServletRequest request, String city) {
        Session session = SessionLocal.get();
        if (session == null || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(city)) {
            return false;
        }
        Set<String> citySet = opUserService.getCitySet(session.getUsername());
        if (citySet == null || !citySet.contains(city)) {
            return false;
        }
        return true;
    }

    @CityRequired
    @GetMapping("/city/{city}/area_manage")
    public String area_manage(HttpServletRequest request, HttpServletResponse response, @PathVariable("city") String city) throws ServletException, IOException {
        if (!checkCity(request, city)) {
            request.getRequestDispatcher("/error/no_auth").forward(request, response);
            return null;
        }
        setClient(request);
        City cityObj = cityService.getByCityName(city);
        request.setAttribute("city_code", cityObj.getCode());
        return "city_area_manage";
    }

    @GetMapping("/api/city/{city}/area/search")
    @ResponseBody
    public Result search(HttpServletRequest request, @PathVariable("city") String city) throws NoSuchFieldException, IllegalAccessException {
        if (!checkCity(request, city)) {
            return new Result(CodeMsg.OPAUTH_FAIL);
        }
        Area criteria = new Area();
        criteria.setCity(city);
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


    @PostMapping("/api/city/{city}/area/create")
    @ResponseBody
    public Result create(HttpServletRequest request, @PathVariable("city") String city, @RequestBody Area criteria) throws Exception {
        if (!checkCity(request, city)) {
            return new Result(CodeMsg.OPAUTH_FAIL);
        }
        areaService.createArea(criteria);
        return new Result(CodeMsg.SUCCESS);
    }


    @PostMapping("/api/city/{city}/area/{area_id:\\d+}/update")
    @ResponseBody
    public Result update(HttpServletRequest request, @PathVariable("city") String city, @PathVariable("area_id") Integer area_id, @RequestBody Area criteria) throws Exception {
        if (!checkCity(request, city)) {
            return new Result(CodeMsg.OPAUTH_FAIL);
        }
        if (area_id == null || area_id < 1) {
            return new Result(-1, "场地编号不能小于1");
        }
        criteria.setArea_id(area_id);
        areaService.updateArea(criteria);
        return new Result(CodeMsg.SUCCESS);
    }


    @PostMapping("/api/city/{city}/area/{area_id:\\d+}/update/types")
    @ResponseBody
    public Result update_types(HttpServletRequest request, @PathVariable("city") String city, @PathVariable("area_id") Integer area_id, @RequestBody Area criteria) throws Exception {
        if (!checkCity(request, city)) {
            return new Result(CodeMsg.OPAUTH_FAIL);
        }
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

    @GetMapping("/api/city/{city}/area/{area_id:\\d+}/booking/search")
    @ResponseBody
    public Result booking_search(HttpServletRequest request, HttpServletResponse response, @PathVariable("city") String city, @PathVariable("area_id") Integer area_id, Date create_date_start, Date create_date_end) throws Exception {
        if (!checkCity(request, city)) {
            return new Result(CodeMsg.OPAUTH_FAIL);
        }
        Booking criteria = new Booking();
        criteria.setArea_id(area_id);
        return bookingController.search(request, response, null, null, null, criteria, create_date_start, create_date_end, null, false);
    }

}
