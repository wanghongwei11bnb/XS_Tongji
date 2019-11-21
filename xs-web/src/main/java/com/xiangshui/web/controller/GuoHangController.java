package com.xiangshui.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.service.AESUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@Controller
public class GuoHangController extends BaseController {


    @GetMapping("/jpi/booking/{booking_id:\\d+}/create_guohang_link")
    @ResponseBody
    public Result create_guohang_link(@PathVariable("booking_id") Long booking_id) throws Exception {
        Booking booking = bookingDao.getItem(new PrimaryKey("booking_id", booking_id));
        if (booking == null) return new Result(CodeMsg.NO_FOUND);
        Capsule capsule = capsuleDao.getItem(new PrimaryKey("capsule_id", booking.getCapsule_id()));
        if (capsule == null) return new Result(CodeMsg.NO_FOUND);
        Area area = areaDao.getItem(new PrimaryKey("area_id", booking.getArea_id()));
        if (area == null) return new Result(CodeMsg.NO_FOUND);

        String key = "v0Xw6cqlDO1cOehG";
        JSONObject json = new JSONObject()
                .fluentPut("appid", "ovQLFBjxHucSOqPrL8")
                .fluentPut("store_code", String.format("%s,%s,%s,%s", area.getArea_id(), area.getTitle(), area.getCity(), area.getAddress()))
                .fluentPut("confirm_id", booking.getBooking_id())
//                .fluentPut("amount", 100)
                ;
        String jsonString = json.toJSONString();
        String base64 = Base64.getEncoder().encodeToString(jsonString.getBytes());
        String encryptStr = AESUtils.encrypt(base64, key);
        return new Result(CodeMsg.SUCCESS).putData("link", "https://pays.jifen360.com/china_air/third_index.html?param=" + encryptStr);
    }


    @PostMapping("/jpi/guohang/notify_url")
    @ResponseBody
    public void notify_url(@RequestBody String body) {
        log.info(body);
    }


}
