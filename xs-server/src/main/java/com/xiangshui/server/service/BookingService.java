package com.xiangshui.server.service;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BookingDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.fragment.CapsuleType;
import com.xiangshui.server.domain.fragment.RushHour;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.relation.BookingRelation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingService {

    @Autowired
    BookingDao bookingDao;

    @Autowired
    AreaService areaService;

    @Autowired
    CapsuleService capsuleService;

    public Booking getBookingById(long booking_id) {
        return bookingDao.getItem(new PrimaryKey("booking_id", booking_id));
    }

    public Booking getBookingById(long booking_id, String[] fields) {
        return bookingDao.getItem(new GetItemSpec().withPrimaryKey(new PrimaryKey("booking_id", booking_id)).withAttributesToGet(fields));
    }


    public List<Booking> getBookingListByCity(String city) {
        if (StringUtils.isBlank(city)) {
            return null;
        }
        List<Integer> areaIdList = areaService.getAreaIdListByCity(city);
        if (areaIdList == null || areaIdList.size() == 0) {
            return null;
        }
        List<Booking> bookingList = bookingDao.batchGetItem("area_id", areaIdList.toArray());
        return bookingList;
    }


    public BookingRelation toRelation(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingRelation bookingRelation = new BookingRelation();
        BeanUtils.copyProperties(booking, bookingRelation);
        return bookingRelation;
    }


    public List<BookingRelation> toRelation(List<Booking> bookingList) {
        if (bookingList == null || bookingList.size() == 0) {
            return null;
        }
        List<BookingRelation> bookingRelationList = new ArrayList<BookingRelation>(bookingList.size());
        for (Booking booking : bookingList) {
            bookingRelationList.add(toRelation(booking));
        }
        return bookingRelationList;
    }


    public int checkPrice(int area_id, long create_time, long end_time) {
        Area area = areaService.getAreaById(area_id);
        if (area == null) {
            throw new XiangShuiException("场地信息未查到");
        }
        List<RushHour> rushHourList = area.getRushHours();
        List<CapsuleType> typeList = area.getTypes();

        if (typeList == null || typeList.size() == 0) {
            throw new XiangShuiException("场地信息未查到types");
        }
        CapsuleType type = null;
        for (CapsuleType typeItem : typeList) {
            if (typeItem.getType_id() == 1) {
                type = typeItem;
                break;
            }
        }
        if (type == null) {
            throw new XiangShuiException("场地信息未查到types");
        }

        Integer price = type.getPrice();
        Integer rush_hour_price = type.getRush_hour_price();
        Integer day_max_price = type.getDay_max_price();
        if (price == null || price < 0) {
            throw new XiangShuiException("场地价格信息有误");
        }
        if (day_max_price == null) {
            day_max_price = Integer.MAX_VALUE;
        }
        if (rush_hour_price == null) {
            rush_hour_price = price;
        }

        return 0;
    }


}
