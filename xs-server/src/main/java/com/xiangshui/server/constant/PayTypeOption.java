package com.xiangshui.server.constant;

import com.xiangshui.util.Option;

import java.util.List;

public class PayTypeOption extends Option<Integer> {

    public PayTypeOption(Integer value, String text) {
        super(value, text);
    }

    public static final PayTypeOption mp = new PayTypeOption(5, "公众号支付");
    public static final PayTypeOption web_alipay = new PayTypeOption(7, "支付宝移动页面支付");
    public static final PayTypeOption xcx = new PayTypeOption(9, "微信小程序支付");
    public static final PayTypeOption new_user = new PayTypeOption(30, "新用户注册赠送");
    public static final PayTypeOption wechat = new PayTypeOption(1, "微信支付");
    public static final PayTypeOption alipay = new PayTypeOption(2, "支付宝支付");
    public static final PayTypeOption wallet = new PayTypeOption(20, "钱包余额支付");
    public static final PayTypeOption guohang = new PayTypeOption(50, "国航里程支付");

    public static final List<Option> options = getOptions(PayTypeOption.class);
}
