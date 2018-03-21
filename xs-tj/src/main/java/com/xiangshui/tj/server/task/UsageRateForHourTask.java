package com.xiangshui.tj.server.task;

import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.service.AreaDataManager;
import com.xiangshui.tj.server.service.BookingDataManager;
import com.xiangshui.tj.server.service.CapsuleDataManager;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Component
public class UsageRateForHourTask extends Task<UsageRateForHourTask.Result> {


    @Override
    public boolean reduce_for_booking() {
        return true;
    }

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
        long create_time = booking.getCreate_time();
        long end_time = booking.getEnd_time();
        if (create_time <= 0) {
            return;
        }

        Date start_date = new Date(create_time * 1000);

        Date end_date;
        if (end_time <= 0) {
            end_date = result.end_date;
        } else {
            end_date = new Date(end_time * 1000);
        }


        if (end_date.getTime() < result.start_date.getTime()) {
            return;
        }

        for (long ts : result.usageNumMap.keySet()) {
            if (start_date.getTime() - 1000 * 60 * 30 <= ts && ts <= end_date.getTime() + 1000 * 60 * 30) {
                append(result, ts);
            }
        }
    }

    @Override
    public void reduce(Capsule capsule, Result result) {

    }

    @Override
    public void reduce(Area area, Result result) {

    }


    private void append(Result result, long k) {
        if (result.usageNumMap.containsKey(k)) {
            result.usageNumMap.put(k, result.usageNumMap.get(k) + 1);
        }
    }

    public static class Result {

        public Date start_date;
        public Date end_date;
        //key:yyyyMMddhh,value:订单数
        public Map<Long, Integer> usageNumMap = new TreeMap();

        public Result() {

            Date now = new Date();
            end_date = (Date) now.clone();
            try {
                Date indexDate = (Date) end_date.clone();
                for (int i = 0; i < 25; i++) {
                    usageNumMap.put(indexDate.getTime(), 0);
                    indexDate.setTime(indexDate.getTime() - (1000 * 60 * 60));
                }
                start_date = indexDate;
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
