package com.xiangshui.tj.server.task;

import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.service.AreaDataManager;
import com.xiangshui.tj.server.service.BookingDataManager;
import com.xiangshui.tj.server.service.CapsuleDataManager;
import com.xiangshui.tj.websocket.message.SendMessage;
import com.xiangshui.util.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Component
public class CumulativeBookingTodayTask extends Task<CumulativeBookingTodayTask.Result> {


    public Result createResult() {
        return new Result();
    }

    public void handDataManager(AreaDataManager areaDataManager, Result result) {

    }

    public void handDataManager(CapsuleDataManager capsuleDataManager, Result result) {

    }

    public void handDataManager(BookingDataManager bookingDataManager, Result result) {

    }


    public SendMessage toSendMessage(Result result) {
        return null;
    }

    public void reduce(Booking booking, Result result) {

        long create_time = booking.getCreate_time();
        if (create_time <= 0) {
            return;
        }
        create_time *= 1000;
        if (!(result.start_hour.getTime() <= create_time && create_time <= result.end_hour.getTime())) {
            return;
        }

        Date create_hour = DateUtils.copyDateEndHour(new Date(create_time));
        long create_hour_time = create_hour.getTime() + 1000 * 60 * 60;

        if (result.data.containsKey(create_hour_time)) {
            result.data.put(create_hour_time, result.data.get(create_hour_time) + 1);
        }
    }

    public void reduce(Capsule capsule, Result result) {

    }

    public void reduce(Area area, Result result) {

    }

    public static class Result {

        public Date now;

        public Date start_hour;
        public Date end_hour;

        public Map<Long, Integer> data;

        public Result() {
            now = new Date();
            start_hour = DateUtils.copyDateEndDate(now);
            end_hour = DateUtils.copyDateEndHour(now);
            end_hour.setTime(end_hour.getTime() + 1000 * 60 * 60);
            data = new TreeMap<Long, Integer>();
            Date index_hour = (Date) start_hour.clone();
            do {
                data.put(index_hour.getTime(), 0);
                index_hour.setTime(index_hour.getTime() + 1000 * 60 * 60);
            } while (index_hour.getTime() <= end_hour.getTime());
        }
    }
}
