package com.xiangshui.op.annotation;


import java.lang.annotation.*;


@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRequired {


    String[] value();

    String auth_op_auth = "OP权限管理";

    String auth_article = "新闻帖子管理";

    String auth_coupon = "优惠券管理";


    String auth_booking = "订单管理";
    String auth_booking_all = "订单管理（全国）";
    String auth_booking_show_coupon = "订单——查看优惠金额";
    String auth_booking_show_f = "订单——显示F标签";
    String auth_booking_show_phone = "订单——查看手机号";
    String auth_booking_bill_show_month_card = "订单——下载账单显示月卡";
    String auth_booking_download = "订单——下载";
    String auth_month_card_download = "月卡——下载";

    String area_contract = "客户分成管理";
    String area_contract_saler = "客户分成管理——业务员";
    String area_contract_verify = "客户分成管理——审核员";
    String area_contract_operate = "客户分成管理——运营";

    String area_bill = "分成对账单管理";
    String area_bill_operate = "分成对账单管理——操作权限";
    String group = "拼团管理";
    String auth_f1 = "F1";

    String auth_minitou_investor = "迷你投——投资人";
    String auth_minitou_op = "迷你投——OP";

    String auth_my_area_summary = "我的场地——查看单日汇总";


    String auth_red_bag = "周年活动";

    String auth_check_capsule_status="检查设备状态";


    String auth_device_operate="硬件设备远程操控";



}
