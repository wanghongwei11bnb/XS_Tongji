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
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.domain.fragment.CapsuleType;
import com.xiangshui.server.domain.fragment.RushHour;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.relation.BookingRelation;
import com.xiangshui.util.CallBack;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.spring.SpringUtils;
import com.xiangshui.util.web.result.CodeMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.awt.print.Book;
import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class BookingService {

    @Value("${isdebug}")
    boolean debug;

    @Autowired
    BookingDao bookingDao;

    @Autowired
    DeviceRelationDao deviceRelationDao;

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
    RedisService redisService;

    @Autowired
    AreaService areaService;

    @Autowired
    UserService userService;
    @Autowired
    DeviceService deviceService;

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

    public synchronized long createBookingId() {
        for (; ; ) {
            long booking_id = System.currentTimeMillis();
            if (bookingDao.getItem(new PrimaryKey("booking_id", booking_id)) == null) {
                return booking_id;
            }
        }
    }


    public void submitBooking(long capsule_id, int uin, String app) throws IOException {
        UserInfo userInfo = userInfoDao.getItem(new PrimaryKey("uin", uin));
        if (userInfo == null) throw new XiangShuiException(CodeMsg.NO_FOUND);
        UserWallet userWallet = userWalletDao.getItem(new PrimaryKey("uin", uin));
        if (userWallet == null) throw new XiangShuiException(CodeMsg.NO_FOUND);
        UserRegister userRegister = userRegisterDao.getItem(new PrimaryKey("uin", uin));
        if (userRegister == null) throw new XiangShuiException(CodeMsg.NO_FOUND);
        //黑名单
        if (new Integer(1).equals(userInfo.getBlock())) throw new XiangShuiException("黑名单用户");
        Capsule capsule = capsuleDao.getItem(new PrimaryKey("capsule_id", capsule_id));
        if (capsule == null) throw new XiangShuiException(CodeMsg.NO_FOUND);
        Area area = areaDao.getItem(new PrimaryKey("area_id", capsule.getArea_id()));
        if (area == null) throw new XiangShuiException(CodeMsg.NO_FOUND);
        //实名认证
        if (new Integer(1).equals(area.getIs_external())
                && !"wx-wx".equals(userRegister.getRegister_from())
                && !new Integer(1).equals(userInfo.getId_verified())) {
            throw new XiangShuiException("未实名认证");
        }
        //押金
        if ("wx-wx".equals(userRegister.getRegister_from()) || (
                new Integer(1).equals(area.getNeed_deposit())
                        && (userWallet.getDeposit() == null || userWallet.getDeposit() < 9900)
        )) {
            throw new XiangShuiException("押金不足");
        }

        //未完成的订单


        List<Booking> bookingList = bookingDao.indexQuery(
                "uin-index",
                new QuerySpec()
                        .withHashKey("uin", uin)
                        .withQueryFilters(new QueryFilter("status").ne(4))
                        .withMaxResultSize(1),
                null,
                (o1, o2) -> o2.getCreate_time().compareTo(o1.getCreate_time())
        );

        log.info(bookingList.size() + "");

        if (bookingList.size() > 0) throw new XiangShuiException("有未完成的订单");
        //太空舱被占用
        if (new Integer(2).equals(capsule.getStatus())) throw new XiangShuiException("太空舱被占用");


        //上海：快牛金科：场地编号：3100063  后台修改成上午11：45之后才可以扫码开舱
        if (area.getArea_id().equals(3100063) && Integer.valueOf(DateUtils.format("hhmm")) < 1245) {
            throw new XiangShuiException("非营业时间");
        }
        if (new Integer(1).equals(area.getIs_time_limit()) && Integer.valueOf(DateUtils.format("hh")) < 6) {
            throw new XiangShuiException("非营业时间");
        }

        //todo:判断设备的wifi是否连接

        //这个capsule_id是大门
        if (capsule.getType() >= 99) {
            throw new XiangShuiException("这个capsule_id是大门");
        }

        //获取type
        CapsuleType capsuleType = null;
        if (area.getTypes() != null && area.getTypes().size() > 0) {
            for (int i = 0; i < area.getTypes().size(); i++) {
                if (area.getTypes().get(i).getType_id().equals(capsule.getType())) {
                    capsuleType = area.getTypes().get(i);
                    break;
                }
            }
        }

        if (capsuleType == null) {
            log.error("找不到 CapsuleType");
            throw new XiangShuiException("下单失败");
        }


        Date now = new Date();
        Booking booking = new Booking()
                .setBooking_id(createBookingId()).setStatus(1)
                .setUin(uin).setArea_id(area.getArea_id()).setCapsule_id(capsule_id)
                .setCalculate_rule(capsuleType.getPrice_rule_text())
                .setMonth_card_flag(userWallet.getMonth_card_flag())
                .setCreate_time(now.getTime() / 1000).setCreate_date(Integer.valueOf(DateUtils.format(now, "yyyyMMdd"))).setUpdate_time(now.getTime() / 1000)
                .setReq_from(app)
                .setFinal_price(0);

        Set<Long> capsuleIdSetOfV1 = new HashSet<>();
        for (String s : IOUtils.readLines(System.class.getResourceAsStream("/capsule_id_v1.txt"), "UTF-8")) {
            if (StringUtils.isNotBlank(s) && s.matches("^\\d+$")) {
                capsuleIdSetOfV1.add(Long.valueOf(s));
            }
        }
        //二代舱
        if (!capsuleIdSetOfV1.contains(capsule_id)) {
            //开灯
            deviceService.openLamp(capsule.getDevice_id());
        }

        //开舱


        log.info(JSON.toJSONString(booking));

        //写入booking
//        bookingDao.putItem(booking);
        //绑定device_id与booking_id, 方便后面根据device_id直接找到订单号
        DeviceRelation deviceRelation = new DeviceRelation().setDevice_id(capsule.getDevice_id()).setBooking_id(booking.getBooking_id());
        deviceRelationDao.putItem(deviceRelation);

        redisService.run(jedis -> {
            /*
             con.zadd('time_out',  time.time(),uin)
             con.zadd('time_out_new', time.time(),str(uin)+"&"+str(cap_id))
             con.zadd('pay_time', time.time(), uin)
             con.sadd('capsule_time',cap_id)
             */
            jedis.zadd("time_out", System.currentTimeMillis() / 1000, uin + "");
            jedis.zadd("time_out_new", System.currentTimeMillis() / 1000, uin + "&" + capsule_id);
            jedis.zadd("pay_time", System.currentTimeMillis() / 1000, uin + "");
            jedis.sadd("capsule_time", capsule_id + "");
        });

        //检查店铺限时


    }


    public static void main(String[] args) throws IOException {
        SpringUtils.init();
        SpringUtils.getBean(BookingService.class).submitBooking(1100017002, 1339281935, "");
    }


}
