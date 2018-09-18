package com.xiangshui.op.count;

import com.alibaba.fastjson.JSONObject;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.util.CallBackForResult;
import org.joda.time.LocalTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CountBookingForCountMonthCardAtHourUnit extends CountProcessor<Booking> {


    private Map<Integer, Integer> countMap = new HashMap<>();
    private Map<Integer, Integer> countMapForMonthCard = new TreeMap<>();


    @Override
    protected void reduce(Booking booking, CountResult countResult) {
        if (booking != null && booking.getCreate_time() != null) {
            LocalTime localTime = new LocalTime(booking.getCreate_time() * 1000);
            if (countMap.containsKey(localTime.getHourOfDay())) {
                if (new Integer(1).equals(booking.getMonth_card_flag())) {
                    countMapForMonthCard.put(localTime.getHourOfDay(), countMapForMonthCard.get(localTime.getHourOfDay()) + 1);
                } else {
                    countMap.put(localTime.getHourOfDay(), countMap.get(localTime.getHourOfDay()) + 1);
                }

            }
        }
    }

    @Override
    protected void handStart(List<Booking> data, CountResult countResult) {
        for (int i = 0; i <= 23; i++) {
            countMap.put(i, 0);
            countMapForMonthCard.put(i, 0);
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
                                .fluentPut("label", "非月卡下单数量")
                                .fluentPut("backgroundColor", "#007bff")
                                .fluentPut("borderColor", "#007bff")
                                .fluentPut("borderWidth", 1)
                                .fluentPut("data", countMap.values()),

                        new JSONObject()
                                .fluentPut("label", "月卡下单数量")
                                .fluentPut("backgroundColor", "#28a745")
                                .fluentPut("borderColor", "#28a745")
                                .fluentPut("borderWidth", 1)
                                .fluentPut("data", countMapForMonthCard.values()),
                }));
        countResult.setOptions(new JSONObject()
                .fluentPut("responsive", true)
                .fluentPut("scales", new JSONObject()
                        .fluentPut("xAxes", new Object[]{new JSONObject().fluentPut("stacked", true)})
                        .fluentPut("yAxes", new Object[]{new JSONObject().fluentPut("stacked", true)})
                )
                .fluentPut("title", new JSONObject().fluentPut("display", true).fluentPut("text", "分时段统计月卡下单数量"))
        );
    }
}
