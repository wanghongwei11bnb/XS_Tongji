package com.xiangshui.op.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController extends BaseController{


    @GetMapping("")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "index";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request) {
        setClient(request);
        return "home";
    }


}
