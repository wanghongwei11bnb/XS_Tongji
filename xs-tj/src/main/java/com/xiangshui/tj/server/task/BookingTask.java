package com.xiangshui.tj.server.task;

import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.service.AreaDataManager;
import com.xiangshui.tj.server.service.BookingDataManager;
import com.xiangshui.tj.server.service.CapsuleDataManager;
import com.xiangshui.tj.websocket.message.SendMessage;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class BookingTask extends Task<BookingTask.Result> {

    @Override
    public Result createResult() {
        return new Result();
    }

    @Override
    public void handDataManager(AreaDataManager areaDataManager, Result result) {

    }

    @Override
    public void handDataManager(CapsuleDataManager capsuleDataManager, Result result) {

    }

    @Override
    public void handDataManager(BookingDataManager bookingDataManager, Result result) {

    }

    @Override
    public void reduce(Booking booking, Result result) {

    }

    @Override
    public void reduce(Capsule capsule, Result result) {

    }

    @Override
    public void reduce(Area area, Result result) {

    }

    public SendMessage toSendMessage(Result result) {
        return null;
    }

    public static class Result {
        public Date date;
        public int[] data = new int[24];
    }
}
