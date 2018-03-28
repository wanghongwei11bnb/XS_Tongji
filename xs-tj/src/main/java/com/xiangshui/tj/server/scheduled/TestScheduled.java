package com.xiangshui.tj.server.scheduled;

import com.alibaba.fastjson.JSON;
import com.xiangshui.tj.server.bean.*;
import com.xiangshui.tj.server.constant.ReceiveEvent;
import com.xiangshui.tj.server.dynamedb.DynamoDBService;
import com.xiangshui.tj.server.redis.RedisService;
import com.xiangshui.tj.server.redis.SendMessagePrefix;
import com.xiangshui.tj.server.service.*;
import com.xiangshui.tj.server.task.*;
import com.xiangshui.tj.websocket.WebSocketSessionManager;
import com.xiangshui.util.CallBack;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.*;

@Component
public class TestScheduled implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(TestScheduled.class);


    @Value("${isdebug}")
    boolean debug;

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
    GeneralTask generalTask;
    @Autowired
    UsageRateForHourTask usageRateForHourTask;
    @Autowired
    CumulativeBookingTask cumulativeBookingTask;
    @Autowired
    CumulativeTimeTask cumulativeTimeTask;


    public void initLoad() {
        log.info("start loadUser");
        dynamoDBService.loadUser(new CallBack<User>() {
            public void run(User object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
            }
        });

        log.info("start reloadCity");
        dynamoDBService.reloadCity();

        log.info("start loadArea");
        final Map<Integer, City> cityMap = new TreeMap<Integer, City>();
        dynamoDBService.loadArea(new CallBack<Area>() {
            public void run(Area object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
                City city = City.getByCity(object.getCity());
                if (city != null && !cityMap.containsKey(city.getCode())) {
                    city = new City();
                    city.setCity(object.getCity());
                    city.setCode(object.getArea_id() / 1000);
                    if (City.getByCity(city.getCity()) != null) {
                        city.setProvince(City.getByCity(city.getCity()).getProvince());
                    }
                    cityMap.put(city.getCode(), city);
                }
            }
        });

        City.cityMap = cityMap;


        log.info("start loadArea");
        dynamoDBService.loadCapsule(new CallBack<Capsule>() {
            public void run(Capsule object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
            }
        });

        log.info("start loadBooking");
        dynamoDBService.loadBooking(new CallBack<Booking>() {
            public void run(Booking object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
            }
        });

        log.info("start loadAppraise");
        dynamoDBService.loadAppraise(new CallBack<Appraise>() {
            public void run(Appraise object) {
                appraiseDataManager.save(object);
            }
        });
    }


    public void startRedisBooking() {
        redisService.run(new CallBack<Jedis>() {
            public void run(Jedis object) {
                try {
                    List<String> stringList = object.blpop(0, (debug ? "" : "online_") + "booking");
                    if (stringList != null && stringList.size() > 1 && StringUtils.isNotBlank(stringList.get(1))) {
                        Booking booking = JSON.parseObject(stringList.get(1), Booking.class);
                        if (booking != null) {
                            dataReceiver.receive(booking.getStatus() == 1 ? ReceiveEvent.BOOKING_START : ReceiveEvent.BOOKING_END, booking);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        startRedisBooking();

    }

    public void startRedisAppraise() {
        redisService.run(new CallBack<Jedis>() {
            public void run(Jedis object) {
                try {
                    List<String> stringList = object.blpop(0, (debug ? "" : "online_") + "appraise");
                    if (stringList != null && stringList.size() > 1 && StringUtils.isNotBlank(stringList.get(1))) {
                        Appraise appraise = JSON.parseObject(stringList.get(1), Appraise.class);
                        if (appraise != null) {
                            dataReceiver.receive(ReceiveEvent.APPRAISE, appraise);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        startRedisAppraise();
    }


    public void afterPropertiesSet() throws Exception {


        SendMessagePrefix.debug = debug;
        log.info("init dynamoDBService");
        dynamoDBService.init();
        log.info("init redisService");
        redisService.init();
        log.info("start initLoad");
        initLoad();
        log.info("end initLoad");
        redisService.run(new CallBack<Jedis>() {
            public void run(Jedis object) {
                object.del((debug ? "" : "online_") + "booking");
            }
        });
        redisService.run(new CallBack<Jedis>() {
            public void run(Jedis object) {
                object.del((debug ? "" : "online_") + "appraise");
            }
        });
        new Thread(new Runnable() {
            public void run() {
                startRedisBooking();
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                startRedisAppraise();
            }
        }).start();
    }


    @Scheduled(fixedDelay = 1000 * 30, initialDelay = 1000 * 10)
    public void doTask() {
        dataReceiver.doTask(new Task[]{
                generalTask,
                usageRateForHourTask,
                cumulativeBookingTask,
                cumulativeTimeTask
        }, new DataManager[]{
                areaDataManager,
                capsuleDataManager,
                bookingDataManager
        });
    }

}
