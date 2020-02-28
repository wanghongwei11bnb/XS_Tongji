package com.xiangshui.util.weixin;

import com.alibaba.fastjson.JSON;
import com.xiangshui.util.MD5;
import com.xiangshui.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.TreeMap;

@Slf4j
public class PayUtils {

    /**
     * 生成签名
     */
    public static String makeSign(TreeMap<String, String> params, String key) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String k : params.keySet()) {
            if (StringUtils.isBlank(k) || "sign".equals(k) || "key".equals(k)) continue;
            String v = params.get(k);
            if (StringUtils.isBlank(v)) continue;
            stringBuilder.append(k).append("=").append(v).append("&");
        }
        stringBuilder.append("key=").append(key);
        return MD5.getMD5(stringBuilder.toString()).toUpperCase();
    }

    /**
     * 生成XML
     */
    public static String makeXml(TreeMap<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<xml>");
        for (String k : params.keySet()) {
            if (StringUtils.isBlank(k) || "key".equals(k)) continue;
            String v = params.get(k);
            if (StringUtils.isBlank(v)) continue;
            stringBuilder.append("<").append(k).append(">");
            stringBuilder.append(v);
            stringBuilder.append("</").append(k).append(">");
        }
        stringBuilder.append("</xml>");
        return stringBuilder.toString();
    }


    public static TreeMap<String, String> parseXml(Document document) {
        TreeMap<String, String> map = new TreeMap<>();
        for (Element element : document.select("xml > *")) {
            String k = element.tagName();
            if (StringUtils.isBlank(k)) continue;
            String v = element.text();
            if (StringUtils.isBlank(v)) continue;
            map.put(k, v);
        }
        return map;
    }

    public static TreeMap<String, String> parseXml(String xml) {
        return parseXml(Jsoup.parse(xml));
    }

    /**
     * 调用API接口
     *
     * @param api    接口
     * @param params 参数
     * @param key    秘钥
     * @throws IOException
     */
    public static TreeMap<String, String> execute(String api, TreeMap<String, String> params, String key) throws IOException {
        params.put("nonce_str", UUID.get());
        String sign = makeSign(params, key);
        params.put("sign", sign);
        String xml = makeXml(params);
        String body = Jsoup.connect(api).method(Connection.Method.POST).requestBody(xml).execute().body();
        TreeMap<String, String> resp = parseXml(body);
        if (!makeSign(resp, key).equals(resp.get("sign"))) {
            throw new RuntimeException("签名错误");
        }
        if (!"SUCCESS".equals(resp.get("return_code"))) {
            String return_msg = resp.get("return_msg");
            throw new RuntimeException(return_msg);
        }
        if (!"SUCCESS".equals(resp.get("result_code"))) {
            String err_code = resp.get("err_code");
            String err_code_des = resp.get("err_code_des");
            throw new RuntimeException(String.format("err_code=%s,err_code_des=%s", err_code, err_code_des));
        }
        return resp;
    }


    /**
     * 统一下单
     */
    public static TreeMap<String, String> unifiedorder(TreeMap<String, String> params, String key) throws IOException {
        return execute("https://api.mch.weixin.qq.com/pay/unifiedorder", params, key);
    }


    /**
     * 查询订单
     */
    public static TreeMap<String, String> orderquery(TreeMap<String, String> params, String key) throws IOException {
        return execute("https://api.mch.weixin.qq.com/pay/orderquery", params, key);
    }

    /**
     * 查询退款
     */
    public static TreeMap<String, String> refundquery(TreeMap<String, String> params, String key) throws IOException {
        return execute("https://api.mch.weixin.qq.com/pay/refundquery", params, key);
    }


    /**
     * 关闭订单
     */
    public static TreeMap<String, String> closeorder(TreeMap<String, String> params, String key) throws IOException {
        return execute("https://api.mch.weixin.qq.com/pay/closeorder", params, key);
    }

    public static void main(String[] args) throws Exception {


        log.info(JSON.toJSONString(unifiedorder(new FluentMap()
                        .fluentPut("appid", "wxb472bc985496fb30")
                        .fluentPut("mch_id", "1483025282")
                        .fluentPut("trade_type", "JSAPI")
                        .fluentPut("total_fee", "1")
                        .fluentPut("spbill_create_ip", "223.71.0.162")
                        .fluentPut("out_trade_no", "test11111")
                        .fluentPut("body", "测试")
                        .fluentPut("notify_url", "https://www.xiangshuispace.com/test1111")
                , "MB9pL3BqYeJ5rzkI1RufmNCHd7KJGUfF")));

//        log.info(JSON.toJSONString(orderquery(new FluentMap()
//                        .fluentPut("appid", "wxb472bc985496fb30")
//                        .fluentPut("mch_id", "1483025282")
//                        .fluentPut("out_trade_no", "test111")
//                , "MB9pL3BqYeJ5rzkI1RufmNCHd7KJGUfF")));

    }

}
