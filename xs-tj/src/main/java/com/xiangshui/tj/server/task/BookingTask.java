package com.xiangshui.tj.server.task;

import com.xiangshui.tj.server.bean.AreaTj;
import com.xiangshui.tj.server.bean.BookingTj;
import com.xiangshui.tj.server.bean.CapsuleTj;
import com.xiangshui.tj.server.service.AreaDataManager;
import com.xiangshui.tj.server.service.BookingDataManager;
import com.xiangshui.tj.server.service.CapsuleDataManager;
import com.xiangshui.tj.websocket.message.SendMessage;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class BookingTask extends AbstractTask<BookingTask.Result> {

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
    public void reduceBooking(BookingTj booking, Result result) {

    }

    @Override
    public void reduceCapsule(CapsuleTj capsule, Result result) {

    }

    @Override
    public void reduceArea(AreaTj area, Result result) {

    }

    public SendMessage toSendMessage(Result result) {
        return null;
    }

    public static class Result {
        public Date date;
        public int[] data = new int[24];
    }
}
