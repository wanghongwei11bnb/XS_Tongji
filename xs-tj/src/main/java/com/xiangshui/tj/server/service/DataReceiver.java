package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.Appraise;
import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.constant.ReceiveEvent;
import com.xiangshui.tj.server.redis.RedisService;
import com.xiangshui.tj.server.redis.SendMessagePrefix;
import com.xiangshui.tj.server.task.*;
import com.xiangshui.tj.websocket.WebSocketSessionManager;
import com.xiangshui.tj.websocket.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataReceiver {


    @Autowired
    BaseTask baseTask;
    @Autowired
    BookingTask bookingTask;
    @Autowired
    UsageRateForHourTask usageRateForHourTask;


    @Autowired
    CumulativeBookingTask cumulativeBookingTask;
    @Autowired
    CumulativeTimeTask cumulativeTimeTask;


    @Autowired
    AreaDataManager areaDataManager;
    @Autowired
    CapsuleDataManager capsuleDataManager;
    @Autowired
    BookingDataManager bookingDataManager;
    @Autowired
    AppraiseDataManager appraiseDataManager;

    @Autowired
    WebSocketSessionManager sessionManager;
    @Autowired
    RedisService redisService;

    private static final Logger log = LoggerFactory.getLogger(DataReceiver.class);

    public void receive(int event, Area area) {
        areaDataManager.save(area);
    }

    public void receive(int event, Capsule capsule) {
        capsuleDataManager.save(capsule);
    }

    public void receive(int event, Booking booking) {
        bookingDataManager.save(booking);
        if (event == ReceiveEvent.BOOKING_START) {
            PushBookingMessage pushBookingMessage = new PushBookingMessage();
            pushBookingMessage.setBooking(booking);
            pushBookingMessage.setArea(areaDataManager.getById(booking.getArea_id()));
            pushBookingMessage.setCapsule(capsuleDataManager.getById(booking.getCapsule_id()));
            sessionManager.sendMessage(pushBookingMessage);
        }
        if (event != ReceiveEvent.HISTORY_DATA) {
            sendUsageRateMessage();
        }

    }

    public void receive(int event, Appraise appraise) {
        appraiseDataManager.save(appraise);
        PushAppraiseMessage message = new PushAppraiseMessage();
        message.setAppraise(appraise);
        sessionManager.sendMessage(message);
    }

    public void sendUsageRateMessage() {
        BaseTask.Result baseResult = baseTask.tongji();
        UsageRateForHourTask.Result hourResult = usageRateForHourTask.tongji();
        List<Object[]> data = new ArrayList();
        for (long key : hourResult.usageNumMap.keySet()) {
            data.add(new Object[]{key, hourResult.usageNumMap.get(key) * 1f / baseResult.countCapsule});
        }
        UsageRateMessage message = new UsageRateMessage();
        message.setData(data);
        redisService.set(SendMessagePrefix.cache, message.getClass().getSimpleName(), message);
        sessionManager.sendMessage(message);
    }

    public void sendCumulativeBookingMessage() {
        CumulativeBookingTask.Result result = cumulativeBookingTask.tongji();
        List<Object[]> data = new ArrayList();
        int cumulative = 0;
        for (long key : result.data.keySet()) {
            cumulative += result.data.get(key);
            data.add(new Object[]{key, cumulative});
        }
        CumulativeBookingMessage message = new CumulativeBookingMessage();
        message.setData(data);
        redisService.set(SendMessagePrefix.cache, message.getClass().getSimpleName(), message);
        sessionManager.sendMessage(message);
    }

    public void sendCumulativeTimeMessage() {
        CumulativeTimeTask.Result result = cumulativeTimeTask.tongji();
        List<Object[]> data = new ArrayList();
        long cumulative = 0;
        for (long key : result.data.keySet()) {
            cumulative += result.data.get(key);
            data.add(new Object[]{key, cumulative});
        }
        CumulativeTimeMessage message = new CumulativeTimeMessage();
        message.setData(data);
        redisService.set(SendMessagePrefix.cache, message.getClass().getSimpleName(), message);
        sessionManager.sendMessage(message);
    }

}
