package com.xiangshui.server.service;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.relation.BookingRelation;
import com.xiangshui.server.relation.CapsuleRelation;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AreaService {

    @Autowired
    AreaDao areaDao;


    public Area getAreaById(int area_id) {
        return areaDao.getItem(new PrimaryKey("area_id", area_id));
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

    public List<Integer> getAreaIdListByCity(String city) {
        if (StringUtils.isBlank(city)) {
            return null;
        }

        List<Area> areaList = getAreaListByCity(city, new String[]{"area_id"});
        if (areaList == null || areaList.size() == 0) {
            return null;
        }

        List<Integer> areaIdList = new ArrayList<Integer>(areaList.size());
        for (Area area : areaList) {
            areaIdList.add(area.getArea_id());
        }
        return areaIdList;
    }

    public List<Area> getAreaListByCity(String city, String[] attributes) {
        if (StringUtils.isBlank(city)) {
            return null;
        }
        return areaDao.scan(new ScanSpec().withScanFilters(new ScanFilter("city").eq(city)).withAttributesToGet(attributes));
    }

    public List<Area> getAreaListByIds(Integer... areaIds) {
        return areaDao.batchGetItem("area_id", areaIds);
    }

    public Map<Integer, Area> getAreaMapByIds(Integer[] areaIds) {
        List<Area> areaList = getAreaListByIds(areaIds);
        if (areaList == null || areaList.size() == 0) {
            return null;
        }
        Map<Integer, Area> areaMap = new HashMap<Integer, Area>(areaList.size());
        for (Area area : areaList) {
            areaMap.put(area.getArea_id(), area);
        }
        return areaMap;
    }


    public void matchAreaForCapsule(CapsuleRelation capsuleRelation) {
        if (capsuleRelation == null || capsuleRelation.getArea_id() == null) {
            return;
        }
        capsuleRelation.set_area(getAreaById(capsuleRelation.getArea_id()));
    }

    public void matchAreaForCapsule(List<CapsuleRelation> capsuleRelationList) {
        if (capsuleRelationList == null || capsuleRelationList.size() == 0) {
            return;
        }
        Set<Integer> areaIdSet = new HashSet<Integer>();
        for (CapsuleRelation capsuleRelation : capsuleRelationList) {
            if (capsuleRelation.getArea_id() != null) {
                areaIdSet.add(capsuleRelation.getArea_id());
            }
        }
        Map<Integer, Area> areaMap = getAreaMapByIds(areaIdSet.toArray(new Integer[0]));
        for (CapsuleRelation capsuleRelation : capsuleRelationList) {
            if (areaMap.containsKey(capsuleRelation.getArea_id())) {
                capsuleRelation.set_area(areaMap.get(capsuleRelation.getArea_id()));
            }
        }
    }

    public void matchAreaForBooking(BookingRelation bookingRelation) {
        if (bookingRelation == null) {
            return;
        }
        bookingRelation.set_area(getAreaById(bookingRelation.getArea_id()));
    }

    public void matchAreaForBooking(List<BookingRelation> bookingRelationList) {
        if (bookingRelationList == null || bookingRelationList.size() == 0) {
            return;
        }
        Set<Integer> areaIdSet = new HashSet<Integer>();
        for (BookingRelation bookingRelation : bookingRelationList) {
            if (bookingRelation.getArea_id() != null) {
                areaIdSet.add(bookingRelation.getArea_id());
            }
        }
        Map<Integer, Area> areaMap = getAreaMapByIds(areaIdSet.toArray(new Integer[0]));
        for (BookingRelation bookingRelation : bookingRelationList) {
            if (areaMap.containsKey(bookingRelation.getArea_id())) {
                bookingRelation.set_area(areaMap.get(bookingRelation.getArea_id()));
            }
        }
    }
}
