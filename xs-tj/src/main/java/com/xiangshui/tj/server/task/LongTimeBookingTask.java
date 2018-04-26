package com.xiangshui.tj.server.task;

import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.relation.BookingRelation;
import com.xiangshui.tj.server.service.AreaDataManager;
import com.xiangshui.tj.server.service.BookingDataManager;
import com.xiangshui.tj.server.service.CapsuleDataManager;
import com.xiangshui.tj.websocket.message.SendMessage;
import com.xiangshui.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

@Component
public class LongTimeBookingTask extends AbstractTask<LongTimeBookingTask.Result> {

    private static final Logger log = LoggerFactory.getLogger(LongTimeBookingTask.class);

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
    public void reduceBooking(Booking booking, Result result) {
        if (booking.getStatus() == 1) {
            BookingRelation bookingRelation = new BookingRelation();
            BeanUtils.copyProperties(booking, bookingRelation);
            bookingRelation.setAreaObj(areaDataManager.getById(booking.getArea_id()));
            bookingRelation.setCapsuleObj(capsuleDataManager.getById(booking.getCapsule_id()));

            long c = result.now_s - booking.getCreate_time();

            long h = c / (60 * 60);

            long m = (c % (60 * 60)) / 60;

            long s = c % 60;

            booking.setTimeLengthText(
                    (h > 0 ? h + "小时" : "") +
                            (h > 0 || c > 0 ? c + "分钟" : "") + s + "秒"
            );
            result.map.put("" + booking.getCreate_time() + booking.getBooking_id(), bookingRelation);
        }

    }

    @Override
    public void reduceCapsule(Capsule capsule, Result result) {

    }

    @Override
    public void reduceArea(Area area, Result result) {

    }

    @Override
    public SendMessage toSendMessage(Result result) {
        Result.lastResult = result;
        return null;
    }

    public static class Result {

        public static Result lastResult;

        public Map<String, BookingRelation> map;

        public long now_s;

        public Result() {
            map = new TreeMap<>();
            now_s = System.currentTimeMillis() / 1000;
        }
    }
}
