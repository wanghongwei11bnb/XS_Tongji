package com.xiangshui.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.service.AESUtils;
import com.xiangshui.util.MD5;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.Base64;
import java.util.TreeMap;

@Controller
@Slf4j
public class GuoHangController extends BaseController {

    @Value("${guohang.key}")
    String key;
    @Value("${guohang.appid}")
    String appid;
    @Value("${guohang.aeskey}")
    String aeskey;

    @GetMapping("/jpi/booking/{booking_id:\\d+}/create_guohang_link")
    @ResponseBody
    public Result create_guohang_link(@PathVariable("booking_id") Long booking_id) throws Exception {
        Booking booking = bookingDao.getItem(new PrimaryKey("booking_id", booking_id));
        if (booking == null) return new Result(CodeMsg.NO_FOUND);
        Capsule capsule = capsuleDao.getItem(new PrimaryKey("capsule_id", booking.getCapsule_id()));
        if (capsule == null) return new Result(CodeMsg.NO_FOUND);
        Area area = areaDao.getItem(new PrimaryKey("area_id", booking.getArea_id()));
        if (area == null) return new Result(CodeMsg.NO_FOUND);

        if (new Integer(1).equals(booking.getStatus())) return new Result(-1, "订单未结束");
//        if (new Integer(4).equals(booking.getStatus())) return new Result(-1, "订单已支付");
        if (booking.getFinal_price() == null || booking.getFinal_price() <= 0) return new Result(-1, "订单没有金额");

        JSONObject json = new JSONObject()
                .fluentPut("appid", appid)
                .fluentPut("store_code", String.format("%s,%s,%s,%s", booking.getArea_id(), area.getTitle(), area.getCity(), area.getAddress()))
                .fluentPut("confirm_id", "b_" + booking_id + "_" + System.currentTimeMillis() % 1000)
                .fluentPut("amount", booking.getFinal_price());
        String jsonString = json.toJSONString();
        String base64 = Base64.getEncoder().encodeToString(jsonString.getBytes());
        String encryptStr = AESUtils.encrypt(base64, key);
        return new Result(CodeMsg.SUCCESS).putData("link", "https://pays.jifen360.com/china_air/third_index.html?param=" + encryptStr);
    }


    @PostMapping("/jpi/guohang/notify_url")
    @ResponseBody
    public void notify_url(@RequestBody String body) throws Exception {
        body = URLDecoder.decode(body);
        if (body.endsWith("=")) body = body.substring(0, body.length() - 1);
        log.info(body);
        JSONObject json = JSONObject.parseObject(body);
        if (new Integer(0).equals(json.getInteger("status")) && json.containsKey("body")) {
            JSONObject bodyJson = json.getJSONObject("body");
            TreeMap<String, String> treeMap = new TreeMap<>();
            for (String k : bodyJson.keySet()) {
                if (StringUtils.isBlank(k) || "sign".endsWith(k)) continue;
                String v = bodyJson.getString(k);
                if (StringUtils.isBlank(v)) continue;
                treeMap.put(k, v);
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String k : treeMap.keySet()) {
                if (StringUtils.isBlank(k) || "sign".endsWith(k)) continue;
                String v = treeMap.get(k);
                if (StringUtils.isBlank(v)) continue;
                stringBuilder.append(k).append(v);
            }
            stringBuilder.append(aeskey);
            String kvStr = stringBuilder.toString();
            log.info(kvStr);
            String sign = MD5.getMD5(kvStr).toUpperCase();
            log.info(sign);
            if (sign.endsWith(bodyJson.getString("sign"))) {
                //验证签名成功之后的逻辑
                String confirm_id = treeMap.get("confirm_id");
                String money = treeMap.get("money");
                String order_sn = treeMap.get("order_sn");
                String order_time = treeMap.get("order_time");
                String store_code = treeMap.get("store_code");
                if (confirm_id.startsWith("b_")) {
                    Integer price = (int) (Float.valueOf(money) * 100);
                    if (price <= 0) return;
                    Long booking_id = Long.valueOf(confirm_id.split("_")[1]);
                    Booking booking = bookingDao.getItem(new PrimaryKey("booking_id", booking_id));
                    if (booking == null || new Integer(1).equals(booking.getStatus()) || new Integer(4).equals(booking.getStatus()))
                        return;
                    booking.setGuohang_order_sn(order_sn);
                    booking.setGuohang_confirm_id(confirm_id);
                    booking.setFrom_guohang(price);
                    if (booking.getFinal_price() == null || price >= booking.getFinal_price()) {
                        booking.setStatus(4);
                        bookingDao.updateItem(new PrimaryKey("booking_id", booking_id), booking, new String[]{
                                "from_guohang",
                                "guohang_confirm_id",
                                "guohang_order_sn",
                                "status",
                        });
                    }
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {

    }


}
