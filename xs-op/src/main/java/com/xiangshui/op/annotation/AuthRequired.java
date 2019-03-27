package com.xiangshui.op.annotation;


import java.lang.annotation.*;


@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRequired {


    String[] value();

    String auth_op_auth = "OP权限管理";

    String auth_article = "新闻帖子管理";

    String auth_booking_show_coupon = "订单——查看优惠金额";
    String auth_booking_show_phone = "订单——查看手机号";
    String auth_booking_bill_show_month_card = "订单——下载账单显示月卡";
    String auth_booking_download = "订单——下载";
    String auth_month_card_download = "月卡——下载";

    String area_contract = "客户分成管理";
    String area_contract_saler = "客户分成管理——业务员";
    String area_contract_verify = "客户分成管理——审核员";
    String area_contract_operate = "客户分成管理——运营";

    String area_bill = "分成对账单管理";
    String group = "拼团管理";
    String auth_f1 = "F1";

    String auth_minitou_investor = "迷你投——投资人";
    String auth_minitou_op = "迷你投——OP";

}
