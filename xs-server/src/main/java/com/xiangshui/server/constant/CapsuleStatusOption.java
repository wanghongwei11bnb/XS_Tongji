package com.xiangshui.server.constant;

import com.xiangshui.util.Option;

import java.util.List;

public class CapsuleStatusOption extends Option<Integer> {
    public CapsuleStatusOption(Integer value, String text) {
        super(value, text);
    }

    public static final CapsuleStatusOption free = new CapsuleStatusOption(1, "空闲");
    public static final CapsuleStatusOption employ = new CapsuleStatusOption(2, "占用");

    public static final List<Option> options = getOptions(CapsuleStatusOption.class);
}