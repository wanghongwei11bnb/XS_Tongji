package com.xiangshui.op.count;

import com.alibaba.fastjson.JSONObject;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.util.CallBackForResult;
import org.joda.time.LocalTime;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CountBookingForCountAtHourUnit extends CountProcessor<Booking> {


    private Map<Integer, Integer> countMap = new HashMap<>();


    @Override
    protected void reduce(Booking booking, CountResult countResult) {
        if (booking != null && booking.getCreate_time() != null) {
            LocalTime localTime = new LocalTime(booking.getCreate_time() * 1000);
            if (countMap.containsKey(localTime.getHourOfDay())) {
                countMap.put(localTime.getHourOfDay(), countMap.get(localTime.getHourOfDay()) + 1);
            }
        }
    }

    @Override
    protected void handStart(List<Booking> data, CountResult countResult) {
        for (int i = 0; i <= 23; i++) {
            countMap.put(i, 0);
        }
    }

    @Override
    protected void handEnd(List<Booking> data, CountResult countResult) {
        countResult.setType("bar");
        countResult.setData(new JSONObject()
                .fluentPut("labels", ((CallBackForResult<Map<Integer, Integer>, String[]>) countMap -> {
                    String[] labels = new String[countMap.size()];
                    countMap.keySet().forEach(integer -> labels[integer] = integer + "点");
                    return labels;
                }).run(countMap))
                .fluentPut("datasets", new Object[]{
                        new JSONObject()
                                .fluentPut("label", "下单数量")
                                .fluentPut("backgroundColor", "blue")
                                .fluentPut("borderColor", "blue")
                                .fluentPut("borderWidth", 1)
                                .fluentPut("data", countMap.values())
                }));
        countResult.setOptions(new JSONObject()
                .fluentPut("responsive", true)
                .fluentPut("legend", new JSONObject().fluentPut("position", "top"))
                .fluentPut("title", new JSONObject().fluentPut("display", true).fluentPut("text", "分时段统计下单数量"))
        );
    }
}
