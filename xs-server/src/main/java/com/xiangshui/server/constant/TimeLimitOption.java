package com.xiangshui.server.constant;

import com.xiangshui.util.Option;

import java.util.List;

public class TimeLimitOption extends Option<Integer> {
    public TimeLimitOption(Integer value, String text) {
        super(value, text);
    }

    public static final TimeLimitOption unlimit = new TimeLimitOption(0, "不限时");
    public static final TimeLimitOption currency = new TimeLimitOption(1, "通用限时");
//    public static final TimeLimitOption special = new TimeLimitOption(2, "特殊限时");
    public static final List<Option> options = getOptions(TimeLimitOption.class);
}
