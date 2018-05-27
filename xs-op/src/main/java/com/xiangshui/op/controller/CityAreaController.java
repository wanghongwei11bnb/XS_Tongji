package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.CityRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.bean.Session;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.redis.OpPrefix;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Capsule;
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
        Session session = (Session) request.getAttribute("session");
        if (session == null || StringUtils.isBlank(session.getUsername()) || StringUtils.isBlank(city)) {
            return false;
        }
        Set<String> citySet = opUserService.getCitySet(session.getUsername());
        if (citySet == null || !citySet.contains(city)) {
            return false;
        }
        return true;
    }

    @Menu(value = "{city}场地管理")
    @CityRequired
    @GetMapping("/city/{city}/area_manage")
    public String area_manage(HttpServletRequest request, HttpServletResponse response, @PathVariable("city") String city) throws ServletException, IOException {
        if (!checkCity(request, city)) {
            request.getRequestDispatcher("/error/no_auth").forward(request, response);
            return null;
        }
        setClient(request);
        request.setAttribute("city", city);
        return "city_area_manage";
    }

    @GetMapping("/api/city/{city}/area/search")
    @ResponseBody
    public Result search(HttpServletRequest request, @PathVariable("city") String city, Area criteria, Long capsule_id) throws NoSuchFieldException, IllegalAccessException {
        ScanSpec scanSpec = new ScanSpec();

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


    @GetMapping("/api/city/{city}/area/{area_id}/validateForCreate")
    @ResponseBody
    public Result validateForCreate(@PathVariable("area_id") Integer area_id) {
        Area area = areaDao.getItem(new PrimaryKey("area_id", area_id));
        if (area == null) {
            return new Result(CodeMsg.SUCCESS);
        } else {
            return new Result(-1, "场地编号已存在");
        }
    }


    @GetMapping("/api/city/{city}/area/{area_id}")
    @ResponseBody
    public Result get(@PathVariable("area_id") Integer area_id) {
        Area area = areaDao.getItem(new PrimaryKey("area_id", area_id));
        if (area != null) {
            return new Result(CodeMsg.SUCCESS).putData("area", area);
        } else {
            return new Result(CodeMsg.NO_FOUND);
        }
    }

    @GetMapping("/api/city/{city}/area/{area_id}/types")
    @ResponseBody
    public Result getTypes(@PathVariable("area_id") Integer area_id) {
        Area area = areaDao.getItem(new PrimaryKey("area_id", area_id));
        if (area == null) {
            return new Result(CodeMsg.NO_FOUND);
        } else {
            return new Result(CodeMsg.SUCCESS).putData("types", area.getTypes());
        }
    }


    @PostMapping("/api/city/{city}/area/{area_id:\\d+}/update")
    @ResponseBody
    public Result update(@PathVariable("area_id") Integer area_id, @RequestBody Area criteria) throws Exception {
        if (area_id == null || area_id < 1) {
            return new Result(-1, "场地编号不能小于1");
        }
        criteria.setArea_id(area_id);
        areaService.updateArea(criteria);
        return new Result(CodeMsg.SUCCESS);
    }

    @PostMapping("/api/city/{city}/area/create")
    @ResponseBody
    public Result create(@RequestBody Area criteria) throws Exception {
        areaService.createArea(criteria);
        return new Result(CodeMsg.SUCCESS);
    }

    @PostMapping("/api/city/{city}/area/{area_id:\\d+}/update/types")
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

    @PostMapping("/api/city/{city}/clean_area_cache_notification")
    @ResponseBody
    public Result clean_area_cache_notification() {
        areaService.clean_area_cache_notification();
        return new Result(CodeMsg.SUCCESS);
    }
}
