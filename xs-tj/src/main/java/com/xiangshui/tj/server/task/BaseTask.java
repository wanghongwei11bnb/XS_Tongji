package com.xiangshui.tj.server.task;

import com.xiangshui.tj.server.bean.AreaTj;
import com.xiangshui.tj.server.bean.BookingTj;
import com.xiangshui.tj.server.bean.CapsuleTj;
import com.xiangshui.tj.server.service.AreaDataManager;
import com.xiangshui.tj.server.service.BookingDataManager;
import com.xiangshui.tj.server.service.CapsuleDataManager;
import com.xiangshui.tj.websocket.message.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BaseTask extends AbstractTask<BaseTask.Result> {


    private static final Logger log = LoggerFactory.getLogger(BaseTask.class);


    @Override
    public Result createResult() {
        return new Result();
    }

    @Override
    public void handDataManager(AreaDataManager areaDataManager, Result result) {
        result.countArea = areaDataManager.size();
    }

    @Override
    public void handDataManager(CapsuleDataManager capsuleDataManager, Result result) {
        result.countCapsule = capsuleDataManager.size();
    }

    @Override
    public void handDataManager(BookingDataManager bookingDataManager, Result result) {
        result.countBooking = bookingDataManager.size();
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
        public int countArea;
        public int countCapsule;
        public int countBooking;
    }
}
