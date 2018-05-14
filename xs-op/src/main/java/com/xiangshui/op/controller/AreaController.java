package com.xiangshui.op.controller;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.xspec.L;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.fragment.Location;
import com.xiangshui.server.domain.fragment.RushHour;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.CityService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AreaController extends BaseController {

    @Autowired
    CityService cityService;
    @Autowired
    AreaService areaService;
    @Autowired
    AreaDao areaDao;


    @GetMapping("/area_manage")
    public String area_manage() {
        return "area_manage";
    }

    @GetMapping("/api/area/search")
    @ResponseBody
    public Result search(Area criteria) throws NoSuchFieldException, IllegalAccessException {

        if (criteria.getArea_id() != null) {
            Area area = areaService.getAreaById(criteria.getArea_id());
            if (area == null) {
                return new Result(CodeMsg.SUCCESS);
            } else {
                return new Result(CodeMsg.SUCCESS).putData("areaList", new Area[]{area});
            }
        } else {

            ScanSpec scanSpec = new ScanSpec();
            List<ScanFilter> filterList = areaDao.makeScanFilterList(criteria, new String[]{
                    "city", "status", "is_external",
            });
            scanSpec.withScanFilters(filterList.toArray(new ScanFilter[]{}));
            List<Area> areaList = areaDao.scan(scanSpec);
            return new Result(CodeMsg.SUCCESS).putData("areaList", areaList);
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


    @PostMapping("/api/area/{area_id}/update")
    @ResponseBody
    public Result update(@PathVariable("area_id") Integer area_id, @RequestBody Area criteria) throws Exception {
        if (areaDao.getItem(new PrimaryKey("area_id", area_id)) == null) {
            return new Result(CodeMsg.NO_FOUND);
        }


        areaDao.updateItem(new PrimaryKey("area_id", area_id), criteria, new String[]{
                "title",
//                "city",
                "address",
                "contact",
                "notification",
                "area_img",
                "status",
                "minute_start",
                "imgs",
                "location",
                "rushHours",
                "is_external",
        });
        return new Result(CodeMsg.SUCCESS);
    }

    @PostMapping("/api/area/create")
    @ResponseBody
    public Result create(@RequestBody Area criteria) throws Exception {
        if (criteria.getArea_id() == null || criteria.getArea_id() == 0) {
            return new Result(-1, "场地编号不能为空");

        }
        if (areaDao.getItem(new PrimaryKey("area_id", criteria.getArea_id())) != null) {
            return new Result(-1, "场地编号已存在");
        }

        if (StringUtils.isBlank(criteria.getTitle())) return new Result(-1, "标题不能为空");
        if (StringUtils.isBlank(criteria.getCity())) return new Result(-1, "城市不能为空");
        if (StringUtils.isBlank(criteria.getAddress())) return new Result(-1, "地址不能为空");
        if (StringUtils.isBlank(criteria.getContact())) return new Result(-1, "联系方式不能为空");
        if (StringUtils.isBlank(criteria.getNotification())) return new Result(-1, "提醒文案不能为空");
        if (criteria.getMinute_start() == null || criteria.getMinute_start() <= 0) return new Result(-1, "最少时长不能小于1");
        if (criteria.getRushHours() != null && criteria.getRushHours().size() > 0) {
            for (RushHour rushHour : criteria.getRushHours()) {
                if (rushHour == null || rushHour.getStart_time() <= 0 || rushHour.getEnd_time() <= 0) {
                    return new Result(-1, "高峰时段输入有误");
                }
            }
        }

        String string = Jsoup.connect("http://api.map.baidu.com/geocoder/v2/?address=" + criteria.getCity() + " " + criteria.getAddress() + "&output=json&ak=" + "71UPECanchHaS66O2KsxPBSetZkCV7wW").execute().body();
        JSONObject resp = JSONObject.parseObject(string);
        if (resp.getIntValue("status") == 0) {
            JSONObject location = resp.getJSONObject("result").getJSONObject("location");

            float lat = location.getFloatValue("lat");
            float lng = location.getFloatValue("lng");

            if (lat > 0 && lng > 0) {
                Location location1 = new Location();
                location1.setLatitude((int) (lat * 1000000));
                location1.setLongitude((int) (lng * 1000000));
                criteria.setLocation(location1);
                areaDao.putItem(criteria);
                return new Result(CodeMsg.SUCCESS);
            } else {
                return new Result(-1, "获取经纬度失败，请修改地址重试");
            }
        } else {
            return new Result(-1, "获取经纬度失败，请修改地址重试");
        }


    }

    @PostMapping("/api/area/{area_id}/update/types")
    @ResponseBody
    public Result update_types(@PathVariable("area_id") Integer area_id, @RequestBody Area area) throws Exception {
        if (areaDao.getItem(new PrimaryKey("area_id", area_id)) == null) {
            return new Result(CodeMsg.NO_FOUND);
        }

        if (area.getTypes() == null || area.getTypes().size() == 0) {
            return new Result(-1, "头等舱类型不能为空");
        }

        areaDao.updateItem(new PrimaryKey("area_id", area_id), area, new String[]{
                "types",
        });
        return new Result(CodeMsg.SUCCESS);
    }
}
