package com.xiangshui.tj.task;

import com.xiangshui.tj.bean.Area;
import com.xiangshui.tj.bean.Booking;
import com.xiangshui.tj.bean.Capsule;
import com.xiangshui.tj.constant.ReceiveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BaseTask implements Task {

    private static final Logger log = LoggerFactory.getLogger(BaseTask.class);

    private final int[] countBookingForHour = new int[24];



    @Override
    public void reduce(int event, Booking booking) {

        if (booking == null) {
            return;
        }
        switch (event) {
            case ReceiveEvent.HISTORY_DATA:


                break;

            default:
                break;
        }

    }

    @Override
    public void reduce(int event, Capsule capsule) {

    }

    @Override
    public void reduce(int event, Area area) {

    }
}
