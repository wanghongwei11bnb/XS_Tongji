package com.xiangshui.server.service;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BookingDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
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
}
