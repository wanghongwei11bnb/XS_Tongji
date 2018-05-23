package com.xiangshui.server.constant;

import com.xiangshui.util.Option;

import java.util.List;

public class BookingStatusOption extends Option<Integer> {

    public BookingStatusOption(Integer value, String text) {
        super(value, text);
    }

    public static final BookingStatusOption runing = new BookingStatusOption(1, "进行中");
    public static final BookingStatusOption unpay2 = new BookingStatusOption(2, "待支付");
    public static final BookingStatusOption unpay3 = new BookingStatusOption(3, "待支付（支付中）");
    public static final BookingStatusOption pay = new BookingStatusOption(4, "已支付");

    public static final List<Option> options = getOptions(BookingStatusOption.class);
}
