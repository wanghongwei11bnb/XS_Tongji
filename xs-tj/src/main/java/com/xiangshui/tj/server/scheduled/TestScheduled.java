package com.xiangshui.tj.server.scheduled;

import com.alibaba.fastjson.JSON;
import com.xiangshui.tj.server.bean.*;
import com.xiangshui.tj.server.constant.ReceiveEvent;
import com.xiangshui.tj.server.dao.DynamoDBService;
import com.xiangshui.tj.server.redis.RedisService;
import com.xiangshui.tj.server.service.*;
import com.xiangshui.tj.server.task.BaseTask;
import com.xiangshui.tj.server.task.BookingTask;
import com.xiangshui.tj.server.task.UsageRateForHourTask;
import com.xiangshui.tj.websocket.WebSocketSessionManager;
import com.xiangshui.tj.websocket.message.PushAppraiseMessage;
import com.xiangshui.tj.websocket.message.PushBookingMessage;
import com.xiangshui.util.CallBack;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.*;

@Component
public class TestScheduled implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(TestScheduled.class);


    public static boolean inited;


    @Autowired
    WebSocketSessionManager sessionManager;

    @Autowired
    DynamoDBService dynamoDBService;
    @Autowired
    RedisService redisService;

    @Autowired
    DataReceiver dataReceiver;


    @Autowired
    AreaDataManager areaDataManager;
    @Autowired
    CapsuleDataManager capsuleDataManager;
    @Autowired
    BookingDataManager bookingDataManager;
    @Autowired
    AppraiseDataManager appraiseDataManager;


    @Autowired
    BaseTask baseTask;
    @Autowired
    BookingTask bookingTask;
    @Autowired
    UsageRateForHourTask usageRateForHourTask;


    public void initLoad() {
        dynamoDBService.reloadCity();
        final Map<String, City> cityMap = new HashMap();
        dynamoDBService.loadArea(new CallBack<Area>() {
            public void run(Area object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
                if (!cityMap.containsKey(object.getCity())) {
                    City city = new City();
                    city.setCity(object.getCity());
                    city.setCode(object.getArea_id() / 10000);
                    if (City.getByCity(city.getCity()) != null) {
                        city.setProvince(City.getByCity(city.getCity()).getProvince());
                    }
                    cityMap.put(city.getCity(), city);
                }
            }
        });
        City.cityList = new ArrayList(cityMap.values());
        dynamoDBService.loadCapsule(new CallBack<Capsule>() {
            public void run(Capsule object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
            }
        });
        dynamoDBService.loadBooking(new CallBack<Booking>() {
            public void run(Booking object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
            }
        });
        dynamoDBService.loadAppraise(new CallBack<Appraise>() {
            public void run(Appraise object) {
                appraiseDataManager.save(object);
            }
        });


    }


    public void startRedis() {
        new Thread(new Runnable() {
            public void run() {
                redisService.run(new CallBack<Jedis>() {
                    public void run(Jedis object) {
                        try {
                            List<String> stringList = object.blpop(0, "booking");
                            if (stringList != null && stringList.size() > 1 && StringUtils.isNotBlank(stringList.get(1))) {
                                Booking booking = JSON.parseObject(stringList.get(1), Booking.class);
                                if (booking != null) {
                                    dataReceiver.receive(booking.getStatus() <= 2 ? ReceiveEvent.BOOKING_START : ReceiveEvent.BOOKING_END, booking);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                startRedis();
            }
        }).start();
    }

    public void afterPropertiesSet() throws Exception {
        dynamoDBService.init();
        redisService.init();
        initLoad();
        redisService.run(new CallBack<Jedis>() {
            public void run(Jedis object) {
                object.del("booking");
            }
        });
        startRedis();
    }


    //    @Scheduled(fixedDelay = 1000 * 5)
    public void testSendBooking() {
        Booking booking = bookingDataManager.getById((Long) bookingDataManager.getMap().keySet().toArray()[(int) Math.floor(Math.random() * bookingDataManager.size())]);
        PushBookingMessage message = new PushBookingMessage();
        message.setBooking(booking);
        message.setCapsule(capsuleDataManager.getById(booking.getCapsule_id()));
        message.setArea(areaDataManager.getById(booking.getArea_id()));
        sessionManager.sendMessage(message);
    }


    //    @Scheduled(fixedDelay = 1000 * 5)
    public void testSendAppraise() {
        Appraise appraise = appraiseDataManager.getById((Long) appraiseDataManager.getMap().keySet().toArray()[(int) Math.floor(Math.random() * appraiseDataManager.size())]);
        PushAppraiseMessage message = new PushAppraiseMessage();
        message.setAppraise(appraise);
        sessionManager.sendMessage(message);
    }


    @Scheduled(fixedDelay = 1000 * 10)
    public void testSendUsageRate() {
        dataReceiver.sendUsageRateMessage();
    }

    @Scheduled(fixedDelay = 1000 * 10)
    public void testCumulativeBooking() {
        dataReceiver.sendCumulativeBookingMessage();
    }

}
