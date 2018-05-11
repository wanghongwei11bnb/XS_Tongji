package com.xiangshui.op.controller;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.CityService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
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
    public Result search(String city, Integer area_type_id, Integer status, Integer area_id) {
        ScanSpec scanSpec = new ScanSpec();
        List<ScanFilter> filterList = new ArrayList<ScanFilter>();
        if (StringUtils.isNotBlank(city)) {
            filterList.add(new ScanFilter("city").eq(city));
        }
        if (area_type_id != null) {
            filterList.add(new ScanFilter("area_type_id").eq(area_type_id));
        }
        if (status != null) {
            filterList.add(new ScanFilter("status").eq(status));
        }
        if (area_id != null) {
            filterList.add(new ScanFilter("area_id").eq(area_id));
        }
        scanSpec.withScanFilters(filterList.toArray(new ScanFilter[]{}));
        scanSpec.setMaxResultSize(500);
        List<Area> areaList = areaDao.scan(scanSpec);
        return new Result(CodeMsg.SUCCESS).putData("list", areaList);
    }


    @GetMapping("/api/area/{area_id}")
    @ResponseBody
    public Result area(@PathVariable("area_id") Integer area_id) {
        Area area = areaDao.getItem(new PrimaryKey("area_id", area_id));
        if (area != null) {
            return new Result(CodeMsg.SUCCESS).putData("area", area);
        } else {
            return new Result(CodeMsg.NO_FOUND);
        }
    }


    @PostMapping("/api/area/{area_id}/update")
    @ResponseBody
    public Result update(@PathVariable("area_id") Integer area_id, @RequestBody Area area) throws Exception {
        if (areaDao.getItem(new PrimaryKey("area_id", area_id)) == null) {
            return new Result(CodeMsg.NO_FOUND);
        }


        areaDao.updateItem(new PrimaryKey("area_id", area_id), area, new String[]{
                "title",
                "city",
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
