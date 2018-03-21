package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.constant.ReceiveEvent;
import com.xiangshui.tj.server.task.BaseTask;
import com.xiangshui.tj.server.task.BookingTask;
import com.xiangshui.tj.server.task.CumulativeBookingTask;
import com.xiangshui.tj.server.task.UsageRateForHourTask;
import com.xiangshui.tj.websocket.WebSocketSessionManager;
import com.xiangshui.tj.websocket.message.CumulativeBookingMessage;
import com.xiangshui.tj.websocket.message.PushBookingMessage;
import com.xiangshui.tj.websocket.message.UsageRateMessage;
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
    AreaDataManager areaDataManager;
    @Autowired
    CapsuleDataManager capsuleDataManager;
    @Autowired
    BookingDataManager bookingDataManager;

    @Autowired
    WebSocketSessionManager sessionManager;

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


    public void sendUsageRateMessage() {
        BaseTask.Result baseResult = baseTask.tongji();
        UsageRateForHourTask.Result hourResult = usageRateForHourTask.tongji();
        List<Object[]> data = new ArrayList();
        for (long key : hourResult.usageNumMap.keySet()) {
            data.add(new Object[]{key + "", hourResult.usageNumMap.get(key) * 1f / baseResult.countCapsule});
        }
        UsageRateMessage message = new UsageRateMessage();
        message.setData(data);
        sessionManager.sendMessage(message);
    }

    public void sendCumulativeBookingMessage() {
        CumulativeBookingTask.Result result = cumulativeBookingTask.tongji();
        List<Object[]> data = new ArrayList();
        int cumulative = 0;
        for (long key : result.data.keySet()) {
            cumulative += result.data.get(key);
            data.add(new Object[]{key + "", cumulative});
        }
        CumulativeBookingMessage message = new CumulativeBookingMessage();
        message.setData(data);
        sessionManager.sendMessage(message);
    }

}
