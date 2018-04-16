package com.xiangshui.tj.server.task;

import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.bean.City;
import com.xiangshui.tj.server.service.AreaDataManager;
import com.xiangshui.tj.server.service.BookingDataManager;
import com.xiangshui.tj.server.service.CapsuleDataManager;
import com.xiangshui.tj.websocket.message.GeneralMessage;
import com.xiangshui.tj.websocket.message.SendMessage;
import com.xiangshui.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

@Component
public class CountBookingForDaysTask extends AbstractTask<CountBookingForDaysTask.Result> {

    private static final Logger log = LoggerFactory.getLogger(CountBookingForDaysTask.class);

    @Override
    public Result createResult() {
        Date now = new Date();
        Result result = new Result();
        result.end_time = DateUtils.copyDateEndDate(new Date(now.getTime() + 1000 * 60 * 60 * 24));
        result.start_time_3 = new Date(result.end_time.getTime() - 1000 * 60 * 60 * 24 * 3);
        result.start_time_7 = new Date(result.end_time.getTime() - 1000 * 60 * 60 * 24 * 7);
        result.countFor3Days = new HashMap<>();
        result.countFor7Days = new HashMap<>();
        capsuleDataManager.foreach(new BiConsumer<Long, Capsule>() {
            @Override
            public void accept(Long aLong, Capsule capsule) {
                result.countFor3Days.put(aLong, 0);
                result.countFor7Days.put(aLong, 0);
            }
        });
        return result;
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
        if (result.start_time_3.getTime() < booking.getCreate_time() * 1000 && booking.getCreate_time() * 1000 < result.end_time.getTime()) {
            if (result.countFor3Days.containsKey(booking.getCapsule_id())) {
                result.countFor3Days.put(booking.getCapsule_id(), result.countFor3Days.get(booking.getCapsule_id()) + 1);
            }
        }
        if (result.start_time_7.getTime() < booking.getCreate_time() * 1000 && booking.getCreate_time() * 1000 < result.end_time.getTime()) {
            if (result.countFor7Days.containsKey(booking.getCapsule_id())) {
                result.countFor7Days.put(booking.getCapsule_id(), result.countFor7Days.get(booking.getCapsule_id()) + 1);
            }
        }
    }

    @Override
    public void reduce(Capsule capsule, Result result) {

    }

    @Override
    public void reduce(Area area, Result result) {

    }

    @Override
    public SendMessage toSendMessage(Result result) {

        capsuleDataManager.foreach(new BiConsumer<Long, Capsule>() {
            @Override
            public void accept(Long aLong, Capsule capsule) {
                if (result.countFor3Days.containsKey(aLong)) {
                    capsule.setCountBookingFor3Day(result.countFor3Days.get(aLong));
                }
                if (result.countFor7Days.containsKey(aLong)) {
                    capsule.setCountBookingFor7Day(result.countFor7Days.get(aLong));
                }
            }
        });


        return null;
    }

    public static class Result {

        public Date start_time_3;
        public Date start_time_7;
        public Date end_time;

        public Map<Long, Integer> countFor3Days;
        public Map<Long, Integer> countFor7Days;


        public Result() {


        }
    }
}
