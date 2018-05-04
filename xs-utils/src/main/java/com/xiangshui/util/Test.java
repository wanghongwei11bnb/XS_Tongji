package com.xiangshui.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import com.xiangshui.util.test.HouseEs;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jsoup.Jsoup;

import java.net.InetAddress;
import java.util.Date;

public class Test {


    public static TransportClient esClient() throws Exception {
        Settings settings = Settings.builder().put("cluster.name", "msz").put("client.transport.sniff", true).build();
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        return client;
    }


    public static void testInsert() throws Exception {

        HouseEs houseEs = new HouseEs();
        houseEs.setTitle("java入门大法");
        houseEs.setCreateTime(new Date());
        houseEs.setId(123);

        IndexResponse response = esClient().prepareIndex("house", "house", houseEs.getId() + "").setSource(
                JSON.toJSONString(houseEs)
                , XContentType.JSON).get();

        System.out.println(JSON.toJSONString(response));
    }

    public static void testSelect() throws Exception {

        GetResponse response = esClient().prepareGet("house", "house", "123").get();

        System.out.println(JSON.toJSONString(response));
    }


    public static void create() {

    }


    public static void main(String[] args) throws Exception {
//        testInsert();
//        testSelect();


//        String result = Jsoup.connect("http://op.xiangshuispace.com/op/capsule_area/list")
//                .cookie("remember_token", "wanghongwei@xiangshuispace.com|b5874b49077477facb40917543f258f61e758ab0e34fe04bed4faedf945382d1837d2886e45416564c9ae3a7421fee927c78816b68a76bc1559837610523d7ab")
////                .cookie("session", ".eJw1j9FqwjAARX-l5HnIbPWlIKyjrlhIOiVakiESY9YkbVppKtqI_77gtqfLvXAPnDs4fDfMSmFB_HUHwfAbRxCDY7lzPPxo6XqxAI8X8NkIZkXQdFWg2mDoAsa5sDYYpLLBmVViAvaP_YsH9sJKEA_9RfimTiD-I6JyNSchnBNHIp8OpnxGcS5RuRwRThzRVBf4vUauiopyOYVuOVKM6iJNRuR_EDea6GQGcfJKwlwTXEWeEVGzdUWGlN8aaPIaZpuapidJDFI03RhSwhA5PiXlaoRZXhd4p2i2CqmWiuD6SvB2StPcEJzciF7foNk-jQ9n0RvWinb4t7lY0T-NwJW1leza6irU2035YuVF2TPjYsI7Ax4_rppzZw.DcWqcQ.EXAX_xm1Z6P3FKCKqPEdvpSdGyY")
//                .execute().body();
//        System.out.println(result);

        System.out.println(MD5.getMD5("11bnb_opsc" + "1q2w3e4r5t").toLowerCase());
        System.out.println("2e7e677e06366527a7eedfc3b82852ce");
//        2e7e677e06366527a7eedfc3b82852ce

    }
}
