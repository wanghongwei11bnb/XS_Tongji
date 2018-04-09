package com.xiangshui.tj.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.User;
import com.xiangshui.tj.server.redis.RedisService;
import com.xiangshui.tj.server.redis.SendMessagePrefix;
import com.xiangshui.tj.server.service.AreaDataManager;
import com.xiangshui.tj.server.service.BookingDataManager;
import com.xiangshui.tj.server.service.CapsuleDataManager;
import com.xiangshui.tj.server.service.UserDataManager;
import com.xiangshui.tj.server.task.GeneralTask;
import com.xiangshui.tj.server.task.UsageRateForHourTask;
import com.xiangshui.tj.web.result.CodeMsg;
import com.xiangshui.tj.web.result.Result;
import com.xiangshui.tj.websocket.WebSocketSessionManager;
import com.xiangshui.tj.websocket.message.GeneralMessage;
import com.xiangshui.tj.websocket.message.UsageRateMessage;
import com.xiangshui.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/tj")
public class TestController {

    @Autowired
    RedisService redisService;
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

    @GetMapping("home")
    public String index(HttpServletRequest request) {
        request.setAttribute("DateUtils", DateUtils.class);
        request.setAttribute("sizeSession", sessionManager.sizeSession());
        request.setAttribute("usageRateMessage", JSONObject.toJSON(redisService.get(SendMessagePrefix.cache, UsageRateMessage.class.getSimpleName(), UsageRateMessage.class)));
        request.setAttribute("generalMessage", JSONObject.toJSON(redisService.get(SendMessagePrefix.cache, GeneralMessage.class.getSimpleName(), GeneralMessage.class)));


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
    public Result get(HttpServletRequest request, @PathVariable("type") String type, @PathVariable("id") String id) {

        if ("user".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("user", userDataManager.getById(Integer.valueOf(id)));
        }


        if ("area".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("area", areaDataManager.getById(Integer.valueOf(id)));
        }


        if ("capsule".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("capsule", capsuleDataManager.getById(Long.valueOf(id)));
        }


        if ("booking".equals(type)) {
            return new Result(CodeMsg.SUCCESS).putData("booking", bookingDataManager.getById(Long.valueOf(id)));
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
