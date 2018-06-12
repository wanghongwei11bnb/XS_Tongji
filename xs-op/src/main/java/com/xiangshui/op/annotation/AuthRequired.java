package com.xiangshui.op.annotation;


import java.lang.annotation.*;


@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRequired {
    String value();
    String auth_booking_show_phone = "订单——查看手机号";
    String auth_booking_download = "订单——下载";
}
