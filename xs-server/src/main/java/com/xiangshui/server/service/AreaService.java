package com.xiangshui.server.service;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AreaService {

    @Autowired
    AreaDao areaDao;


    public Area getAreaById(int area_id) {
        return areaDao.getItem(new PrimaryKey("area_id", area_id));
    }

    public List<Area> getAreaListByCity(String city) {
        if (StringUtils.isBlank(city)) {
            return new ArrayList<Area>();
        }
        return areaDao.scan(new ScanSpec().withFilterExpression("city = :city").withValueMap(new ValueMap().withString(":city", city)));
    }


    public List<Area> getAreaListByCy(String city) {
        if (StringUtils.isBlank(city)) {
            return new ArrayList<Area>();
        }
        return areaDao.scan(new ScanSpec().withFilterExpression("city = :city").withValueMap(new ValueMap().withString(":city", city)));
    }


    public void update(Integer area_id, JSONObject json) {

        if (areaDao.getItem(new PrimaryKey("area_id", area_id)) == null) {
            throw new RuntimeException("area not found !");
        }

        List<AttributeUpdate> attributeUpdateList = new ArrayList<AttributeUpdate>();

        if (json.containsKey("title")) {
            attributeUpdateList.add(new AttributeUpdate("title").put(json.getString("title")));
        }

        if (json.containsKey("address")) {
            attributeUpdateList.add(new AttributeUpdate("address").put(json.getString("address")));
        }

        if (json.containsKey("area_img")) {
            attributeUpdateList.add(new AttributeUpdate("area_img").put(json.getString("area_img")));
        }
        if (json.containsKey("city")) {
            attributeUpdateList.add(new AttributeUpdate("city").put(json.getString("city")));
        }
        if (json.containsKey("notification")) {
            attributeUpdateList.add(new AttributeUpdate("notification").put(json.getString("notification")));
        }
        if (json.containsKey("contact")) {
            attributeUpdateList.add(new AttributeUpdate("contact").put(json.getString("contact")));
        }
        if (json.containsKey("status")) {
            attributeUpdateList.add(new AttributeUpdate("status").put(json.getIntValue("status")));
        }
        if (json.containsKey("location")) {
            attributeUpdateList.add(new AttributeUpdate("location").put(json.getJSONObject("location")));
        }
        if (json.containsKey("minute_start")) {
            attributeUpdateList.add(new AttributeUpdate("minute_start").put(json.getIntValue("minute_start")));
        }

        if (json.containsKey("imgs")) {
            attributeUpdateList.add(new AttributeUpdate("imgs").put(json.getJSONArray("imgs")));
        }

        if (json.containsKey("rushHours")) {
            attributeUpdateList.add(new AttributeUpdate("rushHours").put(json.getJSONArray("rushHours")));
        }

        areaDao.updateItem(new PrimaryKey("area_id", area_id), attributeUpdateList.toArray(new AttributeUpdate[]{}));
    }
}
