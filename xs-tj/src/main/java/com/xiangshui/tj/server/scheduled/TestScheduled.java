package com.xiangshui.tj.server.scheduled;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.tj.server.bean.*;
import com.xiangshui.tj.server.constant.ReceiveEvent;
import com.xiangshui.tj.server.dynamedb.DynamoDBService;
import com.xiangshui.tj.server.redis.RedisService;
import com.xiangshui.tj.server.redis.SendMessagePrefix;
import com.xiangshui.tj.server.relation.CapsuleRelation;
import com.xiangshui.tj.server.service.*;
import com.xiangshui.tj.server.task.*;
import com.xiangshui.tj.websocket.WebSocketSessionManager;
import com.xiangshui.tj.websocket.message.PushBookingMessage;
import com.xiangshui.util.CallBack;
import com.xiangshui.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
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
    UserDataManager userDataManager;
    @Autowired
    AreaDataManager areaDataManager;
    @Autowired
    CapsuleDataManager capsuleDataManager;
    @Autowired
    BookingDataManager bookingDataManager;
    @Autowired
    AppraiseDataManager appraiseDataManager;

    @Autowired
    RelationService relationService;

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


    private volatile long planBookingTime;
    private volatile long lastBookingTime;
    private volatile long lastBookingCapsuleId;


    public void loadUser(ScanSpec scanSpec) {
        log.info("start loadUser");
        if (scanSpec == null) {
            scanSpec = new ScanSpec();
        }
        dynamoDBService.loadUser(scanSpec, new CallBack<UserTj>() {
            @Override
            public void run(UserTj object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
            }
        });
    }

    public void loadArea(ScanSpec scanSpec) {
        log.info("start loadArea");
        if (scanSpec == null) {
            scanSpec = new ScanSpec();
        }
        dynamoDBService.loadArea(scanSpec, new CallBack<AreaTj>() {
            public void run(AreaTj object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
            }
        });
    }

    public void loadCapsule(ScanSpec scanSpec) {
        log.info("start loadCapsule");
        if (scanSpec == null) {
            scanSpec = new ScanSpec();
        }
        dynamoDBService.loadCapsule(scanSpec, new CallBack<CapsuleTj>() {
            public void run(CapsuleTj object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
            }
        });
    }

    public void loadBooking(ScanSpec scanSpec) {
        log.info("start loadBooking");
        if (scanSpec == null) {
            scanSpec = new ScanSpec();
        }
        dynamoDBService.loadBooking(scanSpec, new CallBack<BookingTj>() {
            public void run(BookingTj object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
            }
        });
    }

    public void initLoad() {
        dynamoDBService.loadCity();
        loadUser(null);
        loadArea(null);
        loadCapsule(null);
        loadBooking(null);
        dynamoDBService.loadAppraise(new CallBack<AppraiseTj>() {
            public void run(AppraiseTj object) {
                dataReceiver.receive(ReceiveEvent.HISTORY_DATA, object);
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


    @Scheduled(fixedDelay = 1000 * 60 * 10, initialDelay = 1000 * 60 * 10)
    public void updateLoad() {
        long now = System.currentTimeMillis();
        dynamoDBService.loadCity();
        loadUser(new ScanSpec().withScanFilters(new ScanFilter("create_time").gt((now - 1000 * 60 * 60 * 24) / 1000)));
        loadArea(null);
        loadCapsule(new ScanSpec().withScanFilters(new ScanFilter("create_time").gt((now - 1000 * 60 * 60 * 24) / 1000)));
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
                bookingDataManager,
                capsuleDataManager,
                areaDataManager,
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
                            lastBookingTime = System.currentTimeMillis();
                            lastBookingCapsuleId = booking.getCapsule_id();
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

    @Scheduled(fixedDelay = 1000, initialDelay = 1000 * 30)
    public void planPushBooking() {
        Date now = new Date();
        if (now.getTime() >= planBookingTime) {
            if (lastBookingTime >= planBookingTime) {
                planBookingTime = (long) (now.getTime() + (Math.random() * 1000 * 30 + 1000 * 10));
            } else {
                BookingTj booking = bookingDataManager.random(lastBookingCapsuleId);
                if (booking != null) {
                    BookingTj bookingCp = new BookingTj();
                    BeanUtils.copyProperties(booking, bookingCp);
                    bookingCp.setCreate_time(now.getTime() / 1000);
                    PushBookingMessage pushBookingMessage = new PushBookingMessage();
                    pushBookingMessage.setBooking(bookingCp);
                    pushBookingMessage.setArea(areaDataManager.getById(bookingCp.getArea_id()));
                    pushBookingMessage.setCapsule(capsuleDataManager.getById(bookingCp.getCapsule_id()));
                    UserTj user = userDataManager.getById(bookingCp.getUin());
                    if (user != null) {
                        bookingCp.setNick_name(user.getNick_name());
                        bookingCp.setPhone(user.getPhone());
                    }
                    sessionManager.sendMessage(pushBookingMessage);
                    lastBookingTime = now.getTime();
                    lastBookingCapsuleId = bookingCp.getCapsule_id();
                }
            }
        }
    }
}
