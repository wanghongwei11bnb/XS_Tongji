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
public class CumulativeTimeTask extends Task<CumulativeTimeTask.Result> {


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
        long end_time = booking.getEnd_time();
        if (start_time <= 0) {
            return;
        }
        start_time *= 1000;
        if (end_time <= 0) {
            end_time = result.now.getTime();
        } else {
            end_time *= 1000;
        }
        if (end_time < result.start_date.getTime() || start_time > result.end_date.getTime()) {
            return;
        }

        for (long index_time : result.data.keySet()) {

            long max_start_time = start_time > index_time ? start_time : index_time;
            long min_end_time = end_time < index_time + 1000 * 60 * 60 * 24 ? end_time : index_time + 1000 * 60 * 60 * 24;

            long time = min_end_time - max_start_time;
            if (time > 0) {
                result.data.put(index_time, result.data.get(index_time) + time);
            }
        }


    }

    public void reduce(Capsule capsule, Result result) {

    }

    public void reduce(Area area, Result result) {

    }

    public static class Result {

        public Date now;
        public Date now_date;

        public Date start_date;
        public Date end_date;

        public Map<Long, Long> data;

        public Result() {
            now = new Date();
            end_date = DateUtils.copyDateEndDate(now);
            now_date = DateUtils.copyDateEndDate(now);
            start_date = DateUtils.copyDateEndDate(now);
            start_date.setTime(start_date.getTime() - 1000 * 60 * 60 * 24 * 7);
            data = new TreeMap<Long, Long>();
            Date index_date = (Date) start_date.clone();
            do {
                data.put(index_date.getTime(), 0l);
                index_date.setTime(index_date.getTime() + 1000 * 60 * 60 * 24);
            } while (index_date.getTime() <= end_date.getTime());
        }
    }
}
