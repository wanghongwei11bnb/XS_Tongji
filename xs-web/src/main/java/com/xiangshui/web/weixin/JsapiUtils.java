package com.xiangshui.web.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

import java.io.IOException;

@Slf4j
public class JsapiUtils {

    public static JSONObject get_access_token(String appid, String secret) throws IOException {
        JSONObject result = JSON.parseObject(Jsoup.connect("https://api.weixin.qq.com/cgi-bin/token")
                .data("grant_type", "client_credential")
                .data("appid", appid)
                .data("secret", secret)
                .ignoreContentType(true).ignoreHttpErrors(true)
                .execute().body());

        if (result.containsKey("errcode")) {
            throw new RuntimeException(result.toJSONString());
        }
        return result;
    }


    public static JSONObject get_jsapi_ticket(String access_token) throws IOException {
        JSONObject result = JSON.parseObject(Jsoup.connect("https://api.weixin.qq.com/cgi-bin/ticket/getticket")
                .data("access_token", access_token)
                .data("type", "jsapi")
                .ignoreContentType(true).ignoreHttpErrors(true)
                .execute().body());

        if (result.getIntValue("errcode") != 0) {
            throw new RuntimeException(result.toJSONString());
        }
        return result;
    }

    public static String makeSign(String jsapi_ticket, String url, long timestamp, String nonceStr) throws Exception {
        FluentMap map = new FluentMap()
                .fluentPut("jsapi_ticket", jsapi_ticket)
                .fluentPut("url", url)
                .fluentPut("noncestr", nonceStr)
                .fluentPut("timestamp", String.valueOf(timestamp));
        StringBuilder stringBuilder = new StringBuilder();
        for (String k : map.keySet()) {
            String v = map.get(k);
            if (StringUtils.isBlank(v)) continue;
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            stringBuilder.append(k).append("=").append(v);
        }
        log.info(stringBuilder.toString());
        return SHA1.shaEncode(stringBuilder.toString());

    }



}
