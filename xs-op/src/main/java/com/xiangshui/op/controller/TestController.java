package com.xiangshui.op.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class TestController extends BaseController {

    @GetMapping("/test")
    public String area_manage(HttpServletRequest request) {
        setClient(request);
        return "test";
    }

}
