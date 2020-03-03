package com.xiangshui.server.yunpian;

import com.alibaba.fastjson.JSONObject;
import com.xiangshui.server.exception.XiangShuiException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

@Slf4j
public class YunpianUtils {


    public static void single_send(String apikey, String mobile, String text) throws IOException {
        String body = Jsoup.connect("https://sms.yunpian.com/v2/sms/single_send.json").method(Connection.Method.POST).ignoreHttpErrors(true).ignoreContentType(true)
                .data("apikey", apikey)
                .data("mobile", mobile)
                .data("text", text)
                .execute().body();
        log.info(body);
        JSONObject resp = JSONObject.parseObject(body);
        if (resp.getIntValue("code") != 0) {
            throw new XiangShuiException(resp.getString("msg"));
        }
    }

    public static void main(String[] args) throws Exception {
        String body = Jsoup.connect("https://sms.yunpian.com/v2/sms/single_send.json").method(Connection.Method.POST).ignoreHttpErrors(true).ignoreContentType(true)
                .data("apikey", "5572ac618e9981b633416e39c35902ee")
                .data("mobile", "13501231224")
                .data("text", String.format("【共享头等舱】您有一个享+倾诉的实时订单，订单计时%s分，尚未支付费用%s元。请登录“共享头等舱”小程序，订单历史中完成支付。", 20, 19.8))
                .execute().body();
        log.info(body);
    }
}
