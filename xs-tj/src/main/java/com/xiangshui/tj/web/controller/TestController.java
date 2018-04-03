package com.xiangshui.tj.web.controller;

import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.User;
import com.xiangshui.tj.server.service.AreaDataManager;
import com.xiangshui.tj.server.service.BookingDataManager;
import com.xiangshui.tj.server.service.CapsuleDataManager;
import com.xiangshui.tj.server.service.UserDataManager;
import com.xiangshui.tj.web.result.CodeMsg;
import com.xiangshui.tj.web.result.Result;
import com.xiangshui.tj.websocket.WebSocketSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/tj")
public class TestController {

    @Autowired
    UserDataManager userDataManager;
    @Autowired
    AreaDataManager areaDataManager;
    @Autowired
    CapsuleDataManager capsuleDataManager;
    @Autowired
    BookingDataManager bookingDataManager;


    @Autowired
    WebSocketSessionManager sessionManager;

    @GetMapping("")
    public String index(HttpServletRequest request) {
        request.setAttribute("sizeSession", sessionManager.sizeSession());
        return "ws";
    }


    @GetMapping("{type:.+}/size")
    @ResponseBody
    public Result size(HttpServletRequest request, @PathVariable("type") String type) {

        if ("user".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("size", userDataManager.size());
        }


        if ("area".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("size", areaDataManager.size());
        }


        if ("capsule".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("size", capsuleDataManager.size());
        }


        if ("booking".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("size", bookingDataManager.size());
        }
        return new Result(CodeMsg.NOT_FOUND);
    }


    @GetMapping("{type:.+}/{id:\\d+}")
    @ResponseBody
    public Result get(HttpServletRequest request, @PathVariable("type") String type, @PathVariable("id") Long id) {

        if ("user".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("user", userDataManager.getById(Math.toIntExact(id)));
        }


        if ("area".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("area", areaDataManager.getById(Math.toIntExact(id)));
        }


        if ("capsule".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("capsule", capsuleDataManager.getById(id));
        }


        if ("booking".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("booking", bookingDataManager.getById(id));
        }
        return new Result(CodeMsg.NOT_FOUND);
    }

    @GetMapping("{type:.+}/map")
    @ResponseBody
    public Result map(HttpServletRequest request, @PathVariable("type") String type) {

        if ("user".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("map", userDataManager.getMap());
        }


        if ("area".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("map", areaDataManager.getMap());
        }


        if ("capsule".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("map", capsuleDataManager.getMap());
        }


        if ("booking".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("map", bookingDataManager.getMap());
        }
        return new Result(CodeMsg.NOT_FOUND);
    }


}
