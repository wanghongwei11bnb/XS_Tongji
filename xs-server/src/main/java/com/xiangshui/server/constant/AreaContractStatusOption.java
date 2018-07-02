package com.xiangshui.server.constant;

import com.xiangshui.util.Option;

import java.util.List;

public class AreaContractStatusOption extends Option<Integer> {

    public AreaContractStatusOption(Integer value, String text) {
        super(value, text);
    }

    public static final AreaContractStatusOption normal = new AreaContractStatusOption(0, "待审核");
    public static final AreaContractStatusOption adopt = new AreaContractStatusOption(1, "审核通过");
    public static final AreaContractStatusOption refuse = new AreaContractStatusOption(-1, "审核未通过");
    public static final AreaContractStatusOption discard = new AreaContractStatusOption(-2, "废弃");

    public static final List<Option> options = getOptions(AreaContractStatusOption.class);
}
