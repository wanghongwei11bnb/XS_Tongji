package com.xiangshui.op.constant;

import com.xiangshui.util.Option;

import java.util.List;

public class CashRecordTypeOption extends Option<Integer> {
    public CashRecordTypeOption(Integer value, String text) {
        super(value, text);
    }

    public static final CashRecordTypeOption qianbao_chongzhi = new CashRecordTypeOption(1, "钱包充值");
    public static final CashRecordTypeOption dingdan_zhifu = new CashRecordTypeOption(2, "订单支付");
    public static final CashRecordTypeOption yajin_zhifu = new CashRecordTypeOption(3, "押金支付");
    public static final CashRecordTypeOption yueka_zhifu = new CashRecordTypeOption(4, "月卡购买");
    public static final CashRecordTypeOption pintuan_zhifu = new CashRecordTypeOption(5, "拼团支付");
    public static final CashRecordTypeOption pintuan_tuikuan = new CashRecordTypeOption(6, "拼团退款");
    public static final CashRecordTypeOption yajin_tuikuan = new CashRecordTypeOption(7, "押金退款");

    public static final List<Option> optionList = getOptions(CashRecordTypeOption.class);

}
