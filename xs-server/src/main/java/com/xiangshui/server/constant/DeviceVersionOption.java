package com.xiangshui.server.constant;

import com.xiangshui.util.Option;

import java.util.List;

public class DeviceVersionOption extends Option<Integer> {
    public DeviceVersionOption(Integer value, String text) {
        super(value, text);
    }

    public static final DeviceVersionOption v200 = new DeviceVersionOption(200, "200第二代产品");
    public static final DeviceVersionOption v300 = new DeviceVersionOption(300, "300第三代产品");
    public static final DeviceVersionOption v301 = new DeviceVersionOption(301, "301第三代人体感应");
    public static final DeviceVersionOption v311 = new DeviceVersionOption(311, "311第三代人体感应，按摩沙发");

    public static final List<Option> options = getOptions(DeviceVersionOption.class);
}
