package com.xiangshui.tj.scheduled;

import com.xiangshui.tj.bean.Booking;
import com.xiangshui.tj.websocket.WebSocketSessionManager;
import com.xiangshui.tj.websocket.message.PushBookingCommitMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.util.Timer;
import java.util.TimerTask;

@Component
public class SchedulingConfig implements ApplicationRunner {

    @Autowired
    WebSocketSessionManager sessionManager;

    public void sendMessageTest() {
        PushBookingCommitMessage message = new PushBookingCommitMessage();
        Booking booking = new Booking();
        booking.setBooking_id(123123);
        message.setBooking(booking);
        sessionManager.sendMessage(message);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessageTest();
            }
        }, 1000 * 3, 1000 * 3);
    }
}
