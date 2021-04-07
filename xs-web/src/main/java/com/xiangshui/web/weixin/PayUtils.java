package com.xiangshui.web.weixin;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.TreeMap;

@Slf4j
public class PayUtils {

    /**
     * 生成签名
     */
    public static String makeSign(TreeMap<String, String> params, String key) {
        log.info(makeXml(params));
        StringBuilder stringBuilder = new StringBuilder();
        for (String k : params.keySet()) {
            if (StringUtils.isBlank(k) || "sign".equals(k) || "key".equals(k)) continue;
            String v = params.get(k);
            if (StringUtils.isBlank(v)) continue;
            stringBuilder.append(k).append("=").append(v).append("&");
        }
        stringBuilder.append("key=").append(key);
        log.info(stringBuilder.toString());

        log.info(MD5.getMD5(stringBuilder.toString()).toUpperCase());
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
//        if (!makeSign(resp, key).equals(resp.get("sign"))) {
//            throw new RuntimeException("签名错误");
//        }
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


    public static TreeMap<String, String> execute(String api, TreeMap<String, String> params, String key, InputStream keyStoreFileInputStream, String keyStorePassword) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try {
            keyStore.load(keyStoreFileInputStream, keyStorePassword.toCharArray());
        } finally {
            keyStoreFileInputStream.close();
        }
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, keyStorePassword.toCharArray()).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        try {
            HttpPost httpPost = new HttpPost(api);
            params.put("nonce_str", UUID.get());
            String sign = makeSign(params, key);
            params.put("sign", sign);
            String xml = makeXml(params);
            httpPost.setEntity(new StringEntity(xml, Charset.forName("utf-8")));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                String body = null;
                if (entity != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String text;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((text = bufferedReader.readLine()) != null) {
                        stringBuilder.append(text);
                    }
                    body = stringBuilder.toString();
                    log.info(body);
                }
                EntityUtils.consume(entity);
                TreeMap<String, String> resp = parseXml(body);
//                if (!makeSign(resp, key).equals(resp.get("sign"))) {
//                    throw new RuntimeException("签名错误");
//                }
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
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }

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
     * 申请退款
     */
    public static TreeMap<String, String> refund(TreeMap<String, String> params, String key, InputStream keyStoreFileInputStream, String keyStorePassword) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return execute("https://api.mch.weixin.qq.com/secapi/pay/refund", params, key, keyStoreFileInputStream, keyStorePassword);
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


    /**
     * 企业付款
     */
    public static TreeMap<String, String> mch_pay(TreeMap<String, String> params, String key, InputStream keyStoreFileInputStream, String keyStorePassword) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return execute("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", params, key, keyStoreFileInputStream, keyStorePassword);
    }

    /**
     * 查询企业付款
     */
    public static TreeMap<String, String> mch_pay_query(TreeMap<String, String> params, String key, InputStream keyStoreFileInputStream, String keyStorePassword) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return execute("https://api.mch.weixin.qq.com/mmpaymkttransfers/gettransferinfo", params, key, keyStoreFileInputStream, keyStorePassword);
    }


    public static void main(String[] args) throws Exception {


//        log.info(JSON.toJSONString(unifiedorder(new FluentMap()
//                        .fluentPut("appid", "wxb472bc985496fb30")
//                        .fluentPut("mch_id", "1483025282")
//                        .fluentPut("trade_type", "NATIVE")
//                        .fluentPut("total_fee", "1")
//                        .fluentPut("spbill_create_ip", "223.71.0.162")
//                        .fluentPut("out_trade_no", "test11111")
//                        .fluentPut("body", "测试")
//                        .fluentPut("notify_url", "https://www.xiangshuispace.com/test1111")
//                , "MB9pL3BqYeJ5rzkI1RufmNCHd7KJGUfF")));

//        log.info(JSON.toJSONString(orderquery(new FluentMap()
//                        .fluentPut("appid", "wxb472bc985496fb30")
//                        .fluentPut("mch_id", "1483025282")
//                        .fluentPut("out_trade_no", "test111")
//                , "MB9pL3BqYeJ5rzkI1RufmNCHd7KJGUfF")));


//        FluentMap map = new FluentMap()
//                .fluentPut("appid", "wxb472bc985496fb30")
//                .fluentPut("mch_id", "1483025282")
//                .fluentPut("out_trade_no", "RP_2467464857_B7C4")
//                .fluentPut("out_refund_no", "RP_2467464857_B7C4_refund")
//                .fluentPut("total_fee", "2")
//                .fluentPut("refund_fee", "2");
//        log.info(JSON.toJSONString(refund( map, "MB9pL3BqYeJ5rzkI1RufmNCHd7KJGUfF"
//                , new Object().getClass().getResourceAsStream("/cert/apiclient_cert.p12"), "1483025282")));


        FluentMap map = new FluentMap()
                .fluentPut("mch_appid", "wx83ae522d07ea0185")
                .fluentPut("mchid", "1483025282")
                .fluentPut("partner_trade_no", "test11111213sdf")
                .fluentPut("openid", "o_Tzq5duLT2ER3hHQnHq25vVJytA")
                .fluentPut("check_name", "NO_CHECK")
                .fluentPut("amount", "30")
                .fluentPut("desc", "测试");
        log.info(JSON.toJSONString(mch_pay(map, "MB9pL3BqYeJ5rzkI1RufmNCHd7KJGUfF"
                , new Object().getClass().getResourceAsStream("/cert/apiclient_cert.p12"), "1483025282")));


    }

}
