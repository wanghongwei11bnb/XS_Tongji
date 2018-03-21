package com.xiangshui.tj.server.dao;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.tj.server.bean.*;
import com.xiangshui.util.CallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class DynamoDBService {

    @Value("${isdebug}")
    private boolean debug;

    private final Logger log = LoggerFactory.getLogger(DynamoDBService.class);

    private AmazonDynamoDB client;
    private DynamoDB dynamoDB;

    private boolean inited;

    public void init() {
        if (!inited) {
            client = AmazonDynamoDBClientBuilder.standard().build();
            dynamoDB = new DynamoDB(client);
            inited = true;
        }
    }

    private String prefix() {
        return debug ? "dev_" : "";
    }

    public void loadArea(CallBack<Area> callBack) {
        Table table = dynamoDB.getTable(prefix() + "area");
        ScanSpec scanSpec = new ScanSpec();
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            try {
                Item item = iter.next();
                Area area = JSON.parseObject(item.toJSON(), Area.class);
                callBack.run(area);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


    public void loadCapsule(CallBack<Capsule> callBack) {
        Table table = dynamoDB.getTable(prefix() + "device");
        ScanSpec scanSpec = new ScanSpec();
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            try {
                Item item = iter.next();
                Capsule capsule = JSON.parseObject(item.toJSON(), Capsule.class);
                callBack.run(capsule);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


    public void loadBooking(CallBack<Booking> callBack) {
        Table table = dynamoDB.getTable(prefix() + "booking");
        ScanSpec scanSpec = new ScanSpec();
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            try {
                Item item = iter.next();
                Booking booking = JSON.parseObject(item.toJSON(), Booking.class);
                callBack.run(booking);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


    public void loadAppraise(CallBack<Appraise> callBack) {
        Table table = dynamoDB.getTable(prefix() + "booking_appraise");
        ScanSpec scanSpec = new ScanSpec();
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            try {
                Item item = iter.next();
                Appraise appraise = JSON.parseObject(item.toJSON(), Appraise.class);
                callBack.run(appraise);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


    public void reloadCity() {
        Table table = dynamoDB.getTable("city");
        ScanSpec scanSpec = new ScanSpec();
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        Iterator<Item> iter = items.iterator();
        List<City> cityList = new ArrayList();
        while (iter.hasNext()) {
            try {
                Item item = iter.next();
                City city = JSON.parseObject(item.toJSON(), City.class);
                cityList.add(city);
            } catch (Exception e) {
                log.error("", e);
            }
        }

        City.cityList = cityList;

    }


}
