package com.xiangshui.server.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.xiangshui.server.Test;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.domain.fragment.CapsuleType;
import com.xiangshui.server.domain.fragment.RushHour;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.relation.BookingRelation;
import com.xiangshui.util.spring.SpringUtils;
import com.xiangshui.util.web.result.CodeMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.print.Book;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
public class BookingService {

    @Value("${isdebug}")
    boolean debug;

    @Autowired
    BookingDao bookingDao;

    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    UserRegisterDao userRegisterDao;
    @Autowired
    UserWalletDao userWalletDao;
    @Autowired
    CapsuleDao capsuleDao;
    @Autowired
    AreaDao areaDao;

    @Autowired
    AreaService areaService;

    @Autowired
    UserService userService;

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


    public int checkPrice(long booking_id, Long end_time) throws IOException {
        Booking booking = getBookingById(booking_id);
        if (booking == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }
        UserRegister userRegister = userService.getUserRegisterByUin(booking.getUin());

        if (userRegister == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }
        if (StringUtils.isBlank(userRegister.getLast_access_token())) {
            throw new XiangShuiException("未找到 access_token");
        }

        String body = Jsoup.connect(
                (debug ? "http://dev.xiangshuispace.com:18083" : "https://www.xiangshuispace.com")
                        + "/api/booking/checkprice"
        ).method(Connection.Method.POST).header("User-Uin", String.valueOf(booking.getUin())).header("Client-Token", userRegister.getLast_access_token()).header("Content-Type", "application/json")
                .requestBody(new JSONObject().fluentPut("booking_id", booking_id).fluentPut("end_time", end_time).toJSONString()).execute().body();
        JSONObject resp = JSONObject.parseObject(body);
        if (resp.getIntValue("ret") == 0) {
            return resp.getIntValue("price");
        } else {
            throw new XiangShuiException(resp.getString("err"));
        }

    }


//    public List<UserInfo> getUserInfoListByBookingList(List<Booking> bookingList){
//
//    }


    public void createBooking(Integer uin, Long capsule_id) {

        UserInfo userInfo = userInfoDao.getItem(new PrimaryKey("uin", uin));
        UserWallet userWallet = userWalletDao.getItem(new PrimaryKey("uin", uin));
        UserRegister userRegister = userRegisterDao.getItem(new PrimaryKey("uin", uin));
        Capsule capsule = capsuleDao.getItem(new PrimaryKey("capsule_id", capsule_id));
        Area area = areaDao.getItem(new PrimaryKey("area_id", capsule.getArea_id()));


        List<Booking> bookingList = bookingDao.indexQuery("uin-index", new QuerySpec()
                .withKeyConditionExpression("uin = :v_uin")
                .withValueMap(new ValueMap()
                        .withInt(":v_uin", uin)
                )
                .withMaxResultSize(1)
        );
        log.info(JSON.toJSONString(bookingList));
    }


    public static void main(String[] args) {
        SpringUtils.init();
        SpringUtils.getBean(BookingService.class).createBooking(1339281935, 1100017002l);
    }


}
