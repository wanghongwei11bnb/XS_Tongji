package com.xiangshui.op.controller;

import com.xiangshui.server.service.CityService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CityController extends BaseController{

    @Autowired
    CityService cityService;

    @GetMapping("/city_manage")
    public String city_manage() {
        return "city_manage";
    }

    @GetMapping("/api/cityList")
    @ResponseBody
    public Result cityList() {
        return new Result(CodeMsg.SUCCESS).putData("cityList", cityService.getCityList());
    }
}