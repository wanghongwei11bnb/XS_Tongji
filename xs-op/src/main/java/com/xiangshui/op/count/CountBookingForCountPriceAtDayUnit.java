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

public class CountBookingForCountPriceAtDayUnit extends CountProcessor<Booking> {


    private LocalDate localDateStart;
    private LocalDate localDateEnd;


    private Map<Long, Integer> countMapForFinal = new TreeMap<>();
    private Map<Long, Integer> countMapForBonus = new TreeMap<>();
    private Map<Long, Integer> countMapForCharge = new TreeMap<>();
    private Map<Long, Integer> countMapForPay = new TreeMap<>();


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
                && countMapForFinal.containsKey(new LocalDate(booking.getCreate_time() * 1000).toDate().getTime())) {
            countMapForFinal.put(new LocalDate(booking.getCreate_time() * 1000).toDate().getTime(), countMapForFinal.get(new LocalDate(booking.getCreate_time() * 1000).toDate().getTime()) + (booking.getFinal_price() != null ? booking.getFinal_price() : 0));
            countMapForPay.put(new LocalDate(booking.getCreate_time() * 1000).toDate().getTime(), countMapForPay.get(new LocalDate(booking.getCreate_time() * 1000).toDate().getTime()) + (booking.getUse_pay() != null ? booking.getUse_pay() : 0));
            countMapForCharge.put(new LocalDate(booking.getCreate_time() * 1000).toDate().getTime(), countMapForCharge.get(new LocalDate(booking.getCreate_time() * 1000).toDate().getTime()) + (booking.getFrom_charge() != null ? booking.getFrom_charge() : 0));
            countMapForBonus.put(new LocalDate(booking.getCreate_time() * 1000).toDate().getTime(), countMapForBonus.get(new LocalDate(booking.getCreate_time() * 1000).toDate().getTime()) + (booking.getFrom_bonus() != null ? booking.getFrom_bonus() : 0));
        }
    }

    @Override
    protected void handStart(List<Booking> data, CountResult countResult) {
        do {
            countMapForFinal.put(localDateStart.toDate().getTime(), 0);
            countMapForPay.put(localDateStart.toDate().getTime(), 0);
            countMapForCharge.put(localDateStart.toDate().getTime(), 0);
            countMapForBonus.put(localDateStart.toDate().getTime(), 0);
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
                }).run(countMapForFinal))
                .fluentPut("datasets", new Object[]{
                        new JSONObject()
                                .fluentPut("label", "订单总金额")
                                .fluentPut("backgroundColor", "#007bff")
                                .fluentPut("borderColor", "#007bff")
                                .fluentPut("borderWidth", 1)
                                .fluentPut("data", ((CallBackForResult<Map<Long, Integer>, List<Float>>) countMap -> {
                            List<Float> labels = new ArrayList<>();
                            countMap.values().forEach(price -> {
                                labels.add(price / 100f);
                            });
                            return labels;
                        }).run(countMapForFinal)),
                        new JSONObject()
                                .fluentPut("label", "非会员付费金额")
                                .fluentPut("backgroundColor", "#28a745")
                                .fluentPut("borderColor", "#28a745")
                                .fluentPut("borderWidth", 1)
                                .fluentPut("data", ((CallBackForResult<Map<Long, Integer>, List<Float>>) countMap -> {
                            List<Float> labels = new ArrayList<>();
                            countMap.values().forEach(price -> {
                                labels.add(price / 100f);
                            });
                            return labels;
                        }).run(countMapForPay)),
                        new JSONObject()
                                .fluentPut("label", "充值部分")
                                .fluentPut("backgroundColor", "#17a2b8")
                                .fluentPut("borderColor", "#17a2b8")
                                .fluentPut("borderWidth", 1)
                                .fluentPut("data", ((CallBackForResult<Map<Long, Integer>, List<Float>>) countMap -> {
                            List<Float> labels = new ArrayList<>();
                            countMap.values().forEach(price -> {
                                labels.add(price / 100f);
                            });
                            return labels;
                        }).run(countMapForCharge)),
                        new JSONObject()
                                .fluentPut("label", "赠送部分")
                                .fluentPut("backgroundColor", "#6c757d")
                                .fluentPut("borderColor", "#6c757d")
                                .fluentPut("borderWidth", 1)
                                .fluentPut("data", ((CallBackForResult<Map<Long, Integer>, List<Float>>) countMap -> {
                            List<Float> labels = new ArrayList<>();
                            countMap.values().forEach(price -> {
                                labels.add(price / 100f);
                            });
                            return labels;
                        }).run(countMapForBonus)),

                }));
        countResult.setOptions(new JSONObject()
                .fluentPut("responsive", true)
                .fluentPut("legend", new JSONObject().fluentPut("position", "top"))
                .fluentPut("title", new JSONObject().fluentPut("display", true).fluentPut("text", "统计每天订单金额"))
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
                new ExcelUtils.Column<Long>("订单总金额") {
                    @Override
                    public String render(Long aLong) {
                        return String.valueOf(countMapForFinal.get(aLong) / 100f);
                    }
                },
                new ExcelUtils.Column<Long>("非会员付费金额") {
                    @Override
                    public String render(Long aLong) {
                        return String.valueOf(countMapForPay.get(aLong) / 100f);
                    }
                },
                new ExcelUtils.Column<Long>("充值部分") {
                    @Override
                    public String render(Long aLong) {
                        return String.valueOf(countMapForCharge.get(aLong) / 100f);
                    }
                },
                new ExcelUtils.Column<Long>("赠送部分") {
                    @Override
                    public String render(Long aLong) {
                        return String.valueOf(countMapForBonus.get(aLong) / 100f);
                    }
                }
        ), new ArrayList<>(countMapForFinal.keySet()), response, "统计每天订单金额.xlsx");
    }
}
