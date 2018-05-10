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
import com.xiangshui.util.CallBackForResult;
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

import java.awt.geom.Area;
import java.util.*;
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


    private volatile long lastBookingCapsuleId;
    private final Timer planPushBookingTimer = new Timer("planPushBookingTimer");
    private volatile TimerTask planPushBookingTask;


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


        planBooking();
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
                            dataReceiver.receive(booking.getStatus() == 1 ? ReceiveEvent.BOOKING_START : ReceiveEvent.BOOKING_END, booking);
                            lastBookingCapsuleId = booking.getCapsule_id();
                            if (booking.getStatus() == 1) {
                                planBooking();
                            }
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


    public void planBooking() {
        if (planPushBookingTask != null) {
            planPushBookingTask.cancel();
        }
        Date now = new Date();
        long delay = 0;
        if (11 <= now.getHours() && now.getHours() <= 14) {//午高峰
            delay = (long) (Math.random() * 1000 * 30 + 1000 * 10);
        } else if (17 <= now.getHours() && now.getHours() <= 20) {//晚高峰
            delay = (long) (Math.random() * 1000 * 30 + 1000 * 10);
        } else if ((22 <= now.getHours() && now.getHours() <= 23) || (0 <= now.getHours() && now.getHours() <= 6)) {//凌晨深夜
            delay = (long) (Math.random() * 1000 * 60 * 30 + 1000 * 60 * 30);
        } else {//其他时段
            delay = (long) (Math.random() * 1000 * 60 + 1000 * 60);
        }
//        delay = (long) (Math.random() * 1000 * 5 + 1000 * 5);

        planPushBookingTask = new TimerTask() {
            @Override
            public void run() {
                bookingDataManager.random(lastBookingCapsuleId, new CallBackForResult<BookingTj, Boolean>() {
                    @Override
                    public Boolean run(BookingTj booking) {
                        CapsuleTj capsule = capsuleDataManager.getById(booking.getCapsule_id());
                        if (capsule == null || capsule.getIs_downline() == 1) {
                            return false;
                        }
                        AreaTj area = areaDataManager.getById(capsule.getArea_id());
                        if (area == null || area.getStatus() == -1 || area.getTitle().indexOf("待运营") > -1 || area.getTitle().indexOf("已下线") > -1) {
                            return false;
                        }
                        BookingTj bookingCp = new BookingTj();
                        BeanUtils.copyProperties(booking, bookingCp);
                        bookingCp.setCreate_time(now.getTime() / 1000);
                        PushBookingMessage pushBookingMessage = new PushBookingMessage();
                        pushBookingMessage.setBooking(bookingCp);
                        pushBookingMessage.setArea(area);
                        pushBookingMessage.setCapsule(capsule);
                        UserTj user = userDataManager.getById(bookingCp.getUin());
                        if (user != null) {
                            bookingCp.setNick_name(user.getNick_name());
                            bookingCp.setPhone(user.getPhone());
                        }
                        sessionManager.sendMessage(pushBookingMessage);
                        lastBookingCapsuleId = bookingCp.getCapsule_id();
                        planBooking();
                        return true;
                    }
                });

            }
        };
        planPushBookingTimer.schedule(planPushBookingTask, delay);
        log.debug("计划{}秒后推送订单", delay / 1000);
    }


}
