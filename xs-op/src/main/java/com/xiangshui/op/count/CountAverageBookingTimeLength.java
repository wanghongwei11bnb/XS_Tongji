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

public class CountAverageBookingTimeLength extends CountProcessor<Booking> {

    private int total = 0;
    private long time = 0;

    @Override
    public CountResult count(List<Booking> data) {
        return super.count(data);
    }

    @Override
    protected void reduce(Booking booking, CountResult countResult) {
        if (booking == null || booking.getCreate_time() == null) {
            return;
        }
        total++;
        time += (booking.getEnd_time() != null ? booking.getEnd_time() : System.currentTimeMillis() / 1000) - booking.getCreate_time();
    }

    @Override
    protected void handStart(List<Booking> data, CountResult countResult) {

    }

    @Override
    protected void handEnd(List<Booking> data, CountResult countResult) {
        if (total == 0) {
            throw new XiangShuiException("没有订单");
        }
        countResult.setType("bar");
        countResult.setData(new JSONObject()
                .fluentPut("labels", new String[]{"平均订单时长（分钟）"})
                .fluentPut("datasets", new Object[]{
                        new JSONObject()
                                .fluentPut("label", "平均订单时长（分钟）")
                                .fluentPut("backgroundColor", "#007bff")
                                .fluentPut("borderColor", "#007bff")
                                .fluentPut("borderWidth", 1)
                                .fluentPut("data", new int[]{(int) (time / total / 60)})
                }));
        countResult.setOptions(new JSONObject()
                .fluentPut("responsive", true)
                .fluentPut("legend", new JSONObject().fluentPut("position", "top"))
                .fluentPut("title", new JSONObject().fluentPut("display", true).fluentPut("text", "统计平均订单时长"))
        );

    }

    @Override
    public void countForDownload(List<Booking> data, HttpServletResponse response) throws IOException {
        throw new XiangShuiException("本项目不支持下载");
    }
}
