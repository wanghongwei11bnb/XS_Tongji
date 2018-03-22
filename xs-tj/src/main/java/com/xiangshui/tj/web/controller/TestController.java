package com.xiangshui.tj.web.controller;

import com.xiangshui.tj.websocket.WebSocketSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/test")
public class TestController {


    @Autowired
    WebSocketSessionManager sessionManager;

    @GetMapping("ws")
    public String index(HttpServletRequest request) {
        request.setAttribute("sizeSession", sessionManager.sizeSession());
        return "ws";
    }


}
