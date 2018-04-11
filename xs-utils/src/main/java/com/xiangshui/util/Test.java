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

        GetResponse response = esClient().prepareGet("house","house","123").get();

        System.out.println(JSON.toJSONString(response));
    }



    public static void create(){

    }




    public static void main(String[] args) throws Exception {
//        testInsert();
        testSelect();
    }
}
