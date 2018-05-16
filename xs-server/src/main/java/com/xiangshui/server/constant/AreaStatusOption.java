package com.xiangshui.server.constant;

import com.xiangshui.util.Option;

import java.util.List;

public class AreaStatusOption extends Option<Integer> {

    public AreaStatusOption(Integer value, String text) {
        super(value, text);
    }

    public static final AreaStatusOption normal = new AreaStatusOption(0, "正常");
    public static final AreaStatusOption offline = new AreaStatusOption(-1, "已下线");
    public static final AreaStatusOption stay = new AreaStatusOption(-2, "待运营");

    public static final List<Option> options = getOptions(AreaStatusOption.class);
}
