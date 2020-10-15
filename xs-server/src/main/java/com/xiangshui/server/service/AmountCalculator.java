package com.xiangshui.server.service;

import com.alibaba.fastjson.JSON;
import com.xiangshui.server.bean.AmountReckonParam;
import com.xiangshui.server.bean.AmountReckonResult;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.util.DateUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmountCalculator {

    public static final Logger log = LoggerFactory.getLogger(AmountCalculator.class);


    /**
     * 计算价格
     */
    public static AmountReckonResult reckon(AmountReckonParam param) {
        if (param == null) throw new XiangShuiException("参数不能为空!");
        if (param.getStart_time() == null) throw new XiangShuiException("开始时间不能为空!");
        if (param.getEnd_time() == null) throw new XiangShuiException("结束时间不能为空!");
        if (param.getEnd_time().compareTo(param.getStart_time()) <= 0)
            throw new XiangShuiException("结束时间要大于开始时间!");
        if (param.getUnit_price() < 0) throw new XiangShuiException("每分钟价格不能为空!");
        if (param.getPeak_hour_binary() > 0) {
            if (param.getPeak_price() < 0) {
                throw new XiangShuiException("高峰时段每分钟价格不能为空!");
            }
        }

        AmountReckonResult result = new AmountReckonResult()
                .setStart_time(param.getStart_time().toDate().getTime() / 1000).setEnd_time(param.getEnd_time().toDate().getTime() / 1000)
                .setUse_month_card(param.getUse_month_card())
                .setTotal_amount(0).setCoupon_amount(0).setLvdi_amount(0).setMileage_amount(0);

        param.setStart_time(param.getStart_time().withSecondOfMinute(0).withMillisOfSecond(0));
        param.setEnd_time(param.getEnd_time().withSecondOfMinute(0).withMillisOfSecond(0));

        log.debug("开始时间={},结束时间={},使用时常{}分钟", DateUtils.format(param.getStart_time().toDate()), DateUtils.format(param.getEnd_time().toDate()), (param.getEnd_time().getMillis() - param.getStart_time().getMillis()) / 1000 / 60);

        if (param.getUse_month_card()) {
            result.setUse_month_card(true);
            log.debug("月卡减免45分钟");
            param.setStart_time(param.getStart_time().plusMinutes(45));
            if (param.getStart_time().compareTo(param.getEnd_time()) >= 0) {
                log.debug("月卡45分钟内，没有产生费用");
                return result;
            }
            log.debug("使用时常还剩{}分钟", (param.getEnd_time().getMillis() - param.getStart_time().getMillis()) / 1000 / 60);
        }

        reckonForWrapDay(param, result);

        while (param.getStart_time().compareTo(param.getEnd_time()) < 0) {
            if (param.getWrap_night()) {
                int hour = param.getStart_time().getHourOfDay();
                if (!(8 <= hour && hour < 22)) {
                    reckonForWrapNight(param, result);
                    continue;
                }
            }
            reckonForHour(param, result);
        }

        log.debug("最终价格{}元", result.getTotal_amount() / 100f);

        return result;
    }

    /**
     * 全天
     */
    public static void reckonForWrapDay(AmountReckonParam param, AmountReckonResult result) {
        if (!param.getWrap_day()) return;
        while (param.getStart_time().plusDays(1).compareTo(param.getEnd_time()) < 0) {
            param.setStart_time(param.getStart_time().plusDays(1));
            log.debug("全天价格{}元", param.getWrap_day_price() / 100f);
            result.setTotal_amount(result.getTotal_amount() + param.getWrap_day_price());
            log.debug("start_time={},end_time={}", DateUtils.format(param.getStart_time().toDate()), DateUtils.format(param.getEnd_time().toDate()));
        }
    }

    /**
     * 包夜
     */
    public static void reckonForWrapNight(AmountReckonParam param, AmountReckonResult result) {
        log.debug("夜晚计费");
        DateTime start_time = param.getStart_time();
        DateTime end_time = param.getStart_time().plusDays(1).withHourOfDay(8).withMinuteOfHour(0);
        if (end_time.compareTo(param.getEnd_time()) >= 0) {
            end_time = param.getEnd_time();
        }

        AmountReckonParam newParam = new AmountReckonParam()
                .setStart_time(start_time).setEnd_time(end_time)
                .setUnit_price(param.getUnit_price())
                .setPeak_price(param.getPeak_price()).setPeak_hour_binary(param.getPeak_hour_binary());
        AmountReckonResult newResult = reckon(newParam);


        param.setStart_time(end_time);

        if (newResult.getTotal_amount() > param.getWrap_night_price()) {
            log.debug("包夜费用{}元", param.getWrap_night_price() / 100f);
            result.setTotal_amount(result.getTotal_amount() + param.getWrap_night_price());
        } else {
            log.debug("未包夜费用{}元", newResult.getTotal_amount() / 100f);
            result.setTotal_amount(result.getTotal_amount() + newResult.getTotal_amount());
        }
    }

    /**
     * 高峰时段
     */
    public static void reckonForHour(AmountReckonParam param, AmountReckonResult result) {
        int hour = param.getStart_time().getHourOfDay();
        log.debug("时段{}点", hour);
        int minute = param.getStart_time().getMinuteOfHour();
        int ms;
        if (param.getStart_time().plusHours(1).withMinuteOfHour(0).compareTo(param.getEnd_time()) < 0) {
            ms = 60 - minute;
            param.setStart_time(param.getStart_time().plusHours(1).withMinuteOfHour(0));
        } else {
            ms = param.getEnd_time().getMinuteOfHour() - minute;
            param.setStart_time(param.getEnd_time());
        }
        if (param.getPeak_hour_binary() > 0 && PeakHourTools.checkHour(param.getPeak_hour_binary(), hour)) {
            log.debug("高峰时段价格{}元", param.getPeak_price() * ms / 100f);
            result.setTotal_amount(result.getTotal_amount() + param.getPeak_price() * ms);
        } else {
            log.debug("非高峰时段价格{}元", param.getUnit_price() * ms / 100f);
            result.setTotal_amount(result.getTotal_amount() + param.getUnit_price() * ms);
        }

    }


//    public static DateTime getEndTimeForNight(DateTime start_time, DateTime end_time) {
//
//    }

    public static void main(String[] args) throws Exception {


        System.out.println(JSON.toJSONString(reckon(new AmountReckonParam()
                        .setStart_time(new DateTime(1567262839l * 1000)).setEnd_time(new DateTime(1567293775l * 1000))
                        .setUse_month_card(true)
                        .setUnit_price(20)
                        .setWrap_day(true).setWrap_day_price(9900)
                        .setWrap_night(true).setWrap_night_price(1500)
                        .setPeak_price(33).setPeak_hour_binary(PeakHourTools.appendHour(0, 11, 12, 13, 14, 15))
        )));


    }
}
