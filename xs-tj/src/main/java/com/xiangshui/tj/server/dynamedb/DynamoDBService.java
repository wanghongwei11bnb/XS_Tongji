package com.xiangshui.tj.server.dynamedb;

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

    public void loadUser(CallBack<UserTj> callBack) {
        loadUser(new ScanSpec(), callBack);
    }

    public void loadUser(ScanSpec scanSpec, CallBack<UserTj> callBack) {
        Table table = dynamoDB.getTable(prefix() + "user_info");
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            try {
                Item item = iter.next();
                UserTj user = JSON.parseObject(item.toJSON(), UserTj.class);
                callBack.run(user);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


    public UserTj getUserByUin(int uin) {
        Table table = dynamoDB.getTable(prefix() + "user_info");
        Item item = table.getItem(new PrimaryKey("uin", uin));
        if (item != null) {
            return JSON.parseObject(item.toJSON(), UserTj.class);
        }
        return null;
    }


    public void loadArea(CallBack<AreaTj> callBack) {
        loadArea(new ScanSpec(), callBack);
    }


    public void loadArea(ScanSpec scanSpec, CallBack<AreaTj> callBack) {
        Table table = dynamoDB.getTable(prefix() + "area");
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            try {
                Item item = iter.next();
                AreaTj area = JSON.parseObject(item.toJSON(), AreaTj.class);
                callBack.run(area);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


    public void loadCapsule(CallBack<CapsuleTj> callBack) {
        loadCapsule(new ScanSpec(), callBack);
    }

    public void loadCapsule(ScanSpec scanSpec, CallBack<CapsuleTj> callBack) {
        Table table = dynamoDB.getTable(prefix() + "device");
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            try {
                Item item = iter.next();
                CapsuleTj capsule = JSON.parseObject(item.toJSON(), CapsuleTj.class);
                callBack.run(capsule);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


    public void loadBooking(CallBack<BookingTj> callBack) {
        loadBooking(new ScanSpec(), callBack);
    }

    public void loadBooking(ScanSpec scanSpec, CallBack<BookingTj> callBack) {
        Table table = dynamoDB.getTable(prefix() + "booking");
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            try {
                Item item = iter.next();
                BookingTj booking = JSON.parseObject(item.toJSON(), BookingTj.class);
                callBack.run(booking);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


    public void loadAppraise(CallBack<AppraiseTj> callBack) {
        log.info("start loadAppraise");
        Table table = dynamoDB.getTable(prefix() + "booking_appraise");
        ScanSpec scanSpec = new ScanSpec();
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            try {
                Item item = iter.next();
                AppraiseTj appraise = JSON.parseObject(item.toJSON(), AppraiseTj.class);
                callBack.run(appraise);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }


    public void loadCity() {
        log.info("start loadCity");
        Table table = dynamoDB.getTable("city");
        ScanSpec scanSpec = new ScanSpec();
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        Iterator<Item> iter = items.iterator();
        List<CityTj> cityList = new ArrayList();
        while (iter.hasNext()) {
            try {
                Item item = iter.next();
                CityTj city = JSON.parseObject(item.toJSON(), CityTj.class);
                cityList.add(city);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        CityTj.cityList = cityList;
    }


}
