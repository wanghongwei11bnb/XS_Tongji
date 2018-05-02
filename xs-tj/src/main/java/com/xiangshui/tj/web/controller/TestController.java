package com.xiangshui.tj.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.xiangshui.tj.server.bean.AreaTj;
import com.xiangshui.tj.server.bean.CapsuleTj;
import com.xiangshui.tj.server.redis.RedisService;
import com.xiangshui.tj.server.redis.SendMessagePrefix;
import com.xiangshui.tj.server.relation.CapsuleRelation;
import com.xiangshui.tj.server.service.*;
import com.xiangshui.tj.server.task.LongTimeBookingTask;
import com.xiangshui.tj.websocket.WebSocketSessionManager;
import com.xiangshui.tj.websocket.message.GeneralMessage;
import com.xiangshui.tj.websocket.message.UsageRateMessage;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;

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

    @Autowired
    RelationService relationService;

    @GetMapping("home")
    public String index(HttpServletRequest request) {
        request.setAttribute("DateUtils", DateUtils.class);
        request.setAttribute("sizeSession", sessionManager.sizeSession());
        request.setAttribute("usageRateMessage", JSONObject.toJSON(redisService.get(SendMessagePrefix.cache, UsageRateMessage.class.getSimpleName(), UsageRateMessage.class)));
        request.setAttribute("generalMessage", JSONObject.toJSON(redisService.get(SendMessagePrefix.cache, GeneralMessage.class.getSimpleName(), GeneralMessage.class)));


        Set<CapsuleRelation> capsuleSet = new TreeSet<>(new Comparator<CapsuleRelation>() {
            @Override
            public int compare(CapsuleRelation o1, CapsuleRelation o2) {
                String n1 = (o1.getLastBookingTime() != null ? o1.getLastBookingTime().getTime() : 0) + "" + o1.getCapsule_id();
                String n2 = (o2.getLastBookingTime() != null ? o2.getLastBookingTime().getTime() : 0) + "" + o2.getCapsule_id();
                return n1.compareTo(n2);
            }
        });

        capsuleDataManager.foreach(new BiConsumer<Long, CapsuleTj>() {
            @Override
            public void accept(Long aLong, CapsuleTj capsule) {
                if (capsule.getIs_downline() == 1) {
                    return;
                }
                AreaTj area = areaDataManager.getById(capsule.getArea_id());
                if (area != null && area.getStatus() != -1) {

                    capsule.setLastBookingTimeText("未使用过");

                    if (capsule.getLastBookingTime() != null) {
                        Date now = new Date();
                        long t = DateUtils.copyDateEndDate(now).getTime();
                        long ct = DateUtils.copyDateEndDate(capsule.getLastBookingTime()).getTime();
                        long c = (t - ct) / (1000 * 60 * 60 * 24);
                        if (now.getTime() - capsule.getLastBookingTime().getTime() <= 1000 * 60 * 60 * 1) {
                            capsule.setLastBookingTimeText("1小时内");
                        } else if (now.getTime() - capsule.getLastBookingTime().getTime() <= 1000 * 60 * 60 * 12) {
                            capsule.setLastBookingTimeText("12小时内");
                        } else if (c <= 0) {
                            capsule.setLastBookingTimeText("今天");
                        } else if (c <= 1) {
                            capsule.setLastBookingTimeText("昨天");
                        } else {
                            capsule.setLastBookingTimeText(c + "天前");
                        }
                    }
                    capsuleSet.add(relationService.getRelation(capsule));
                }
            }
        });

        request.setAttribute("orderCapsuleSet", capsuleSet);
        if (LongTimeBookingTask.Result.lastResult != null) {
            request.setAttribute("runingBookings", LongTimeBookingTask.Result.lastResult.map.values());
        }

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
            CapsuleTj capsule = capsuleDataManager.getById(Long.valueOf(id));
            AreaTj area = null;
            if (capsule != null) {
                area = areaDataManager.getById(capsule.getArea_id());
            }
            return new Result(CodeMsg.SUCCESS).putData("capsule", capsule).putData("area", area);
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
