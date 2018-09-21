package com.xiangshui.op.count;

import com.alibaba.fastjson.JSONObject;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import org.joda.time.LocalDate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class CountBookingForCountAtDayUnit extends CountProcessor<Booking> {


    private LocalDate localDateStart;
    private LocalDate localDateEnd;


    private Map<Long, Integer> countMap = new TreeMap<>();


    public void initDateRange(Date create_date_start, Date create_date_end) {
        if (create_date_start == null) {
            throw new XiangShuiException("开始日期不能为空");
        }
        if (create_date_end == null) {
            throw new XiangShuiException("结束日期不能为空");
        }
        localDateStart = new LocalDate(create_date_start);
        localDateEnd = new LocalDate(create_date_end);
        if (localDateStart.compareTo(localDateEnd) > 0) {
            throw new XiangShuiException("开始日期不能大于结束日期");
        }
        if ((localDateEnd.toDate().getTime() - localDateStart.toDate().getTime()) / (1000 * 60 * 60 * 24) >= 7) {
            throw new XiangShuiException("时间范围不能大于7天");
        }
    }

    @Override
    public CountResult count(List<Booking> data) {
        return super.count(data);
    }

    @Override
    protected void reduce(Booking booking, CountResult countResult) {
        if (booking != null && booking.getCreate_time() != null
                && countMap.containsKey(new LocalDate(booking.getCreate_time() * 1000).toDate().getTime())) {
            countMap.put(new LocalDate(booking.getCreate_time() * 1000).toDate().getTime(), countMap.get(new LocalDate(booking.getCreate_time() * 1000).toDate().getTime()) + 1);
        }
    }

    @Override
    protected void handStart(List<Booking> data, CountResult countResult) {
        do {
            countMap.put(localDateStart.toDate().getTime(), 0);
            localDateStart = localDateStart.plusDays(1);
        } while (localDateStart.compareTo(localDateEnd) <= 0);
    }

    @Override
    protected void handEnd(List<Booking> data, CountResult countResult) {

        countResult.setType("bar");
        countResult.setData(new JSONObject()
                .fluentPut("labels", ((CallBackForResult<Map<Long, Integer>, List<String>>) countMap -> {
                    List<String> labels = new ArrayList<>();
                    countMap.keySet().forEach(aLong -> {
                        labels.add(new LocalDate(aLong).toString("yyyy-MM-dd"));
                    });
                    return labels;
                }).run(countMap))
                .fluentPut("datasets", new Object[]{
                        new JSONObject()
                                .fluentPut("label", "下单数量")
                                .fluentPut("backgroundColor", "#007bff")
                                .fluentPut("borderColor", "#007bff")
                                .fluentPut("borderWidth", 1)
                                .fluentPut("data", countMap.values())
                }));
        countResult.setOptions(new JSONObject()
                .fluentPut("responsive", true)
                .fluentPut("legend", new JSONObject().fluentPut("position", "top"))
                .fluentPut("title", new JSONObject().fluentPut("display", true).fluentPut("text", "统计每天下单数量"))
        );

    }

    @Override
    public void countForDownload(List<Booking> data,HttpServletResponse response) throws IOException {
        this.count(data);
        ExcelUtils.export(Arrays.asList(
                new ExcelUtils.Column<Long>("日期") {
                    @Override
                    public String render(Long aLong) {
                        return DateUtils.format(aLong, "yyyy-MM-dd");
                    }
                },
                new ExcelUtils.Column<Long>("下单数量") {
                    @Override
                    public String render(Long aLong) {
                        return String.valueOf(countMap.get(aLong));
                    }
                }
        ), new ArrayList<>(countMap.keySet()), response, "统计每天下单数量.xlsx");

    }
}
