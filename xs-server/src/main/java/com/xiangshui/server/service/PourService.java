package com.xiangshui.server.service;

import com.xiangshui.server.crud.Conditions;
import com.xiangshui.server.crud.Example;
import com.xiangshui.server.dao.mysql.PourBookingDao;
import com.xiangshui.server.domain.mysql.qingsu.PourBooking;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.util.UUID;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import com.xiangshui.util.weixin.FluentMap;
import com.xiangshui.util.weixin.PayUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

@Component
public class PourService {

    @Autowired
    PourBookingDao pourBookingDao;

    public static final long start_time = new LocalDate(2020, 1, 1).toDate().getTime();

    public Long makeBookingId() {
        return System.currentTimeMillis() - start_time;
    }

    public List<PourBooking> getBookingList(String phone, int... statusArray) {
        Example example = new Example();
        example.getConditions()
                .eq("phone", phone)
                .in("status", Arrays.asList(statusArray));
        return pourBookingDao.selectByExample(example);
    }


    public void createBookingForUser(String phone, String hear_phone) {

        Conditions conditions = new Conditions();
        conditions.eq("phone", phone);


    }

    public void verifyBookingForOp(PourBooking booking) {
        if (booking == null) throw new XiangShuiException("缺少参数");
        if (booking.getUin() == null) throw new XiangShuiException("缺少用户编号");
        if (StringUtils.isBlank(booking.getPhone())) throw new XiangShuiException("缺少用户手机号码");
        if (booking.getStart_time() == null || booking.getEnd_time() == null || booking.getTalk_time() == null)
            throw new XiangShuiException("缺少通话时间");
        if (booking.getFinal_price() == null) throw new XiangShuiException("缺少费用");
        if (booking.getStatus() == null) throw new XiangShuiException("缺少状态");
    }

    public void createBookingForOp(PourBooking booking) throws NoSuchFieldException, IllegalAccessException {
        verifyBookingForOp(booking);
        booking.setId(makeBookingId());
        booking.setTotal_price(booking.getFinal_price());
        booking.setDiscount_price(0);
        booking.setStatus(booking.getFinal_price() > 0 ? 3 : 4);
        booking.setCreate_time(System.currentTimeMillis() / 1000);
        if (pourBookingDao.insertSelective(booking, null) == 0) throw new XiangShuiException("操作失败");
    }

    public void updateBookingForOp(long id, long start_time, long talk_time, int final_price) throws NoSuchFieldException, IllegalAccessException {
        PourBooking booking = pourBookingDao.selectByPrimaryKey(id, null);
        if (booking == null) throw new XiangShuiException(CodeMsg.NO_FOUND);
        if (new Integer(4).equals(booking.getStatus())) throw new XiangShuiException("已完成的订单不能再次修改");
        if (final_price < 0) throw new XiangShuiException("金额必须>=0");
        booking.setStart_time(start_time);
        booking.setTalk_time(talk_time);
        booking.setFinal_price(final_price);
        booking.setStatus(final_price > 0 ? 3 : 4);
        if (pourBookingDao.updateByPrimaryKey(booking, new String[]{
                "start_time",
                "talk_time",
                "final_price",
                "status",
        }) == 0) throw new XiangShuiException("操作失败");
    }


    public TreeMap<String, String> unifiedorder(PourBooking booking, String openid) throws IOException {
        TreeMap<String, String> result = PayUtils.unifiedorder(new FluentMap()
                        .fluentPut("openid", openid)
                        .fluentPut("appid", "wxb472bc985496fb30")
                        .fluentPut("mch_id", "1483025282")
                        .fluentPut("trade_type", "JSAPI")
                        .fluentPut("total_fee", booking.getFinal_price() + "")
                        .fluentPut("spbill_create_ip", "223.71.0.162")
                        .fluentPut("out_trade_no", "pour_" + booking.getId() + "_" + UUID.get(8))
                        .fluentPut("body", "享+头等舱-订单支付")
                        .fluentPut("notify_url", "https://www.xiangshuispace.com/jpi/pour/booking/notify_url")
                , "MB9pL3BqYeJ5rzkI1RufmNCHd7KJGUfF");


        FluentMap paramMap = new FluentMap();
        paramMap
                .fluentPut("timeStamp", System.currentTimeMillis() / 1000 + "")
                .fluentPut("nonceStr", UUID.get())
                .fluentPut("signType", "MD5")
                .fluentPut("appId", "wxb472bc985496fb30")
                .fluentPut("package", "prepay_id=" + result.get("prepay_id"))
        ;

        String paySign = PayUtils.makeSign(paramMap, "MB9pL3BqYeJ5rzkI1RufmNCHd7KJGUfF");
        paramMap.fluentPut("paySign", paySign);
        return paramMap;
    }

}
