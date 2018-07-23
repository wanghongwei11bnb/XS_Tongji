package com.xiangshui.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController extends BaseController {


    @GetMapping(value = {"/", "/index.html"})
    public String index() {
        return "index";
    }

    @GetMapping("/about.html")
    public String about() {
        return "about";
    }

}
