package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.dao.CityDao;
import com.xiangshui.server.dao.redis.CityKeyPrefix;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.City;
import com.xiangshui.server.service.CityService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CityController extends BaseController {

    @Autowired
    CityService cityService;
    @Autowired
    CityDao cityDao;
    @Autowired
    RedisService redisService;

    @Menu(value = "城市管理", sort = 901)
    @GetMapping("/city_manage")
    public String city_manage(HttpServletRequest request) {
        setClient(request);
        return "city_manage";
    }

    @GetMapping("/api/cityList")
    @ResponseBody
    public Result cityList() {
        return new Result(CodeMsg.SUCCESS).putData("cityList", cityService.getCityList());
    }

    @GetMapping("/api/activeCityList")
    @ResponseBody
    public Result activeCityList() {
        return new Result(CodeMsg.SUCCESS).putData("cityList", cityService.getActiveCityList());
    }

    @PostMapping("/api/city/create")
    @ResponseBody
    public Result create(@RequestBody City city) {
        if (city == null) {
            return new Result(-1, "参数不全");
        }
        if (city.getCode() == null) {
            return new Result(-1, "请输入城市编号");
        }
        if (city.getCode() < 1000 || city.getCode() > 4200) {
            return new Result(-1, "城市编号必须在1000到4200之间");
        }

        if (StringUtils.isBlank(city.getCity())) {
            return new Result(-1, "请输入城市名称");
        }
        if (StringUtils.isBlank(city.getProvince())) {
            return new Result(-1, "请输入城市省份");
        }

        if (cityDao.getItem(new PrimaryKey("city", city.getCity())) != null) {
            return new Result(-1, "城市名称已存在");
        }

        List<City> cityList = cityDao.scan(new ScanSpec().withScanFilters(new ScanFilter("code").eq(city.getCode())));
        if (cityList != null && cityList.size() > 0) {
            return new Result(-1, "城市编号已存在");
        }
        city.setVisible(1);
        cityDao.putItem(city);
        redisService.del(CityKeyPrefix.list_all);
        return new Result(CodeMsg.SUCCESS);
    }

}
