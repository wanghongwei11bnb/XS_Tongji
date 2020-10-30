package com.xiangshui.server.constant;

import com.xiangshui.util.Option;

import java.util.List;

public class CapsuleBelongStatusOption extends Option<Integer> {
    public CapsuleBelongStatusOption(Integer value, String text, String color) {
        super(value, text, color);
    }

    public static final CapsuleBelongStatusOption normal = new CapsuleBelongStatusOption(0, "正常运营", "success");
    public static final CapsuleBelongStatusOption give = new CapsuleBelongStatusOption(1, "赠予场地", "danger");
    public static final CapsuleBelongStatusOption bad = new CapsuleBelongStatusOption(2, "已销毁", "danger");


    public static final List<Option> options = getOptions(CapsuleBelongStatusOption.class);
}
