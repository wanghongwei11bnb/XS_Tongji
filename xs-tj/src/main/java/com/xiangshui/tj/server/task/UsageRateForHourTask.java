package com.xiangshui.tj.server.task;

import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.service.AreaDataManager;
import com.xiangshui.tj.server.service.BookingDataManager;
import com.xiangshui.tj.server.service.CapsuleDataManager;
import com.xiangshui.tj.websocket.message.SendMessage;
import com.xiangshui.tj.websocket.message.UsageRateMessage;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 1.统计过去24小时内舱的使用率
 * 2.统计过去24小时内累计舱使用率
 */
@Component
public class UsageRateForHourTask extends AbstractTask<UsageRateForHourTask.Result> {


    public SendMessage toSendMessage(Result result) {
        List<Object[]> data = new ArrayList();
        Set<Long> cisSet = new HashSet<Long>();
        for (long key : result.usageCumuMap.keySet()) {
            for (long cid : result.usageCumuMap.get(key)) {
                cisSet.add(cid);
            }
            data.add(new Object[]{
                    key,
                    NumberUtils.toFixed(result.usageCumuMap.get(key).size() * 1f / result.countCapsule, 2, true),
                    NumberUtils.toFixed(cisSet.size() * 1f / result.countCapsule, 2, true)
            });
        }
        UsageRateMessage message = new UsageRateMessage();
        message.setData(data);
        return message;
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
//        result.countCapsule = capsuleDataManager.size();
    }

    @Override
    public void handDataManager(BookingDataManager bookingDataManager, Result result) {

    }

    @Override
    public void reduce(Booking booking, Result result) {
        Capsule capsule = capsuleDataManager.getById(booking.getCapsule_id());
        if (capsule == null) {
            return;
        }
        Area area = areaDataManager.getById(capsule.getArea_id());
        if (area != null && area.getStatus() != -1) {

            long start_time = booking.getCreate_time();
            long end_time = booking.getEnd_time();
            if (start_time <= 0) {
                return;
            }
            start_time *= 1000;
            if (end_time <= 0) {
                end_time = result.end_hour.getTime();
            } else {
                end_time *= 1000;
            }
            if (end_time < result.start_hour.getTime()) {
                return;
            }

            for (long ts : result.usageCumuMap.keySet()) {

                if (end_time < ts || start_time >= ts + 1000 * 60 * 60) {
                    continue;
                }
                result.usageCumuMap.get(ts).add(booking.getCapsule_id());
            }
        }
    }

    @Override
    public void reduce(Capsule capsule, Result result) {
        Area area = areaDataManager.getById(capsule.getArea_id());
        if (area != null && area.getStatus() != -1) {
            result.countCapsule++;
        }

    }

    @Override
    public void reduce(Area area, Result result) {

    }


    public static class Result {

        public int countCapsule;

        public Date now;

        public Date start_hour;
        public Date end_hour;
        public Map<Long, Set<Long>> usageCumuMap;

        public Result() {
            usageCumuMap = new TreeMap<Long, Set<Long>>();
            now = new Date();
            end_hour = DateUtils.copyDateEndHour(now);
            start_hour = new Date(end_hour.getTime() - 1000 * 60 * 60 * 24);
            long index_hour_time = start_hour.getTime();
            do {
                usageCumuMap.put(index_hour_time, new HashSet<Long>());
                index_hour_time += 1000 * 60 * 60;
            } while (index_hour_time <= end_hour.getTime());
        }
    }
}
