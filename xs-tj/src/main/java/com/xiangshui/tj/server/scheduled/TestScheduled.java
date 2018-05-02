package com.xiangshui.tj.server.scheduled;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.function.BiConsumer;

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
    @Autowired
    CountBookingForDaysTask countBookingForDaysTask;


    public void initLoad() {
        log.info("start loadUser");
        dynamoDBService.loadUser(new CallBack<UserTj>() {
            public void run(UserTj object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
            }
        });

        log.info("start reloadCity");
        dynamoDBService.loadCity();

        log.info("start loadArea");
        dynamoDBService.loadArea(new CallBack<AreaTj>() {
            public void run(AreaTj object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);

            }
        });


        log.info("start loadArea");
        dynamoDBService.loadCapsule(new CallBack<CapsuleTj>() {
            public void run(CapsuleTj object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
            }
        });

        log.info("start loadBooking");
        dynamoDBService.loadBooking(new CallBack<BookingTj>() {
            public void run(BookingTj object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
            }
        });

        log.info("start loadAppraise");
        dynamoDBService.loadAppraise(new CallBack<AppraiseTj>() {
            public void run(AppraiseTj object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
            }
        });


        CityTj.cityMap.forEach(new BiConsumer<String, CityTj>() {
            @Override
            public void accept(String s, CityTj city) {
                try {
                    String string = Jsoup.connect("http://api.map.baidu.com/geocoder/v2/?address=" + city.getProvince() + city.getCity() + "&output=json&ak=" + "71UPECanchHaS66O2KsxPBSetZkCV7wW").execute().body();
                    JSONObject resp = JSONObject.parseObject(string);
                    if (resp.getIntValue("status") == 0) {
                        JSONObject location = resp.getJSONObject("result").getJSONObject("location");
                        city.setLat(location.getFloatValue("lat"));
                        city.setLng(location.getFloatValue("lng"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


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
    }


    @Scheduled(fixedDelay = 1000 * 60 * 5, initialDelay = 1000 * 10)
    public void doTask() {
        dataReceiver.doTask(new AbstractTask[]{
                generalTask,
                usageRateForHourTask,
                cumulativeBookingTask,
                cumulativeTimeTask,
                countBookingForDaysTask
        }, new DataManager[]{
                areaDataManager,
                capsuleDataManager,
                bookingDataManager
        });
    }


    @Scheduled(fixedDelay = 1000, initialDelay = 1000 * 15)
    public void startRedisBooking() {
        redisService.run(new CallBack<Jedis>() {
            public void run(Jedis object) {
                try {
                    String string = object.lpop((debug ? "" : "online_") + "booking");
                    if (StringUtils.isNotBlank(string)) {
                        BookingTj booking = JSON.parseObject(string, BookingTj.class);
                        if (booking != null) {
                            dataReceiver.receive(booking.getStatus() == 1 ? ReceiveEvent.BOOKING_START : ReceiveEvent.BOOKING_END, booking);
                        }
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        });
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 1000 * 15)
    public void startRedisAppraise() {
        redisService.run(new CallBack<Jedis>() {
            public void run(Jedis object) {
                try {
                    String string = object.lpop((debug ? "" : "online_") + "appraise");
                    if (StringUtils.isNotBlank(string)) {
                        AppraiseTj appraise = JSON.parseObject(string, AppraiseTj.class);
                        if (appraise != null) {
                            dataReceiver.receive(ReceiveEvent.APPRAISE, appraise);
                        }
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        });
    }


}
