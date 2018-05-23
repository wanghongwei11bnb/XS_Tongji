package com.xiangshui.op.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController extends BaseController {
    @GetMapping("/error/no_login")
    public String no_login(HttpServletRequest request) {
        setClient(request);
        return "error/no_login";
    }

    @GetMapping("/error/no_auth")
    public String no_auth(HttpServletRequest request) {
        setClient(request);
        return "error/no_auth";
    }
}
