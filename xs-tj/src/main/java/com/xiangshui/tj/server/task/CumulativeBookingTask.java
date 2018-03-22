package com.xiangshui.tj.server.task;

import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.service.AreaDataManager;
import com.xiangshui.tj.server.service.BookingDataManager;
import com.xiangshui.tj.server.service.CapsuleDataManager;
import com.xiangshui.util.DateUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Component
public class CumulativeBookingTask extends Task<CumulativeBookingTask.Result> {


    public Result createResult() {
        return new Result();
    }

    public void handDataManager(AreaDataManager areaDataManager, Result result) {

    }

    public void handDataManager(CapsuleDataManager capsuleDataManager, Result result) {

    }

    public void handDataManager(BookingDataManager bookingDataManager, Result result) {

    }

    @Override
    public boolean reduce_for_booking() {
        return true;
    }

    public void reduce(Booking booking, Result result) {

        long start_time = booking.getCreate_time();
        if (start_time <= 0) {
            return;
        }
        start_time = DateUtils.copyDateEndDate(new Date(start_time * 1000)).getTime();
        if (!(result.start_date.getTime() <= start_time && start_time <= result.end_date.getTime())) {
            return;
        }

        result.data.put(start_time, result.data.get(start_time) + 1);

    }

    public void reduce(Capsule capsule, Result result) {

    }

    public void reduce(Area area, Result result) {

    }

    public static class Result {

        public Date now;

        public Date start_date;
        public Date end_date;

        public Map<Long, Integer> data;

        public Result() {
            now = new Date();
            end_date = DateUtils.copyDateEndDate(now);
            start_date = DateUtils.copyDateEndDate(now);
            start_date.setTime(end_date.getTime() - 1000 * 60 * 60 * 24 * 7);
            data = new TreeMap<Long, Integer>();
            Date index_date = (Date) start_date.clone();
            do {
                data.put(index_date.getTime(), 0);
                index_date.setTime(index_date.getTime() + 1000 * 60 * 60 * 24);
            } while (index_date.getTime() <= end_date.getTime());
        }
    }
}
