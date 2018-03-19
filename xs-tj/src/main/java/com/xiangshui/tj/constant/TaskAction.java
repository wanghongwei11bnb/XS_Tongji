package com.xiangshui.tj.constant;

import com.alibaba.fastjson.JSON;
import com.xiangshui.util.ClassUtils;

public class TaskAction {
    //    BOOKING
    public static final byte BOOKING_COMMIT = 10;
    public static final byte BOOKING_PROPAY = 11;
    public static final byte BOOKING_FINISH = 12;

//    CAPSULE
    public static final byte CAPSULE_ADD = 20;
    public static final byte CAPSULE_UPDATE = 21;
    public static final byte CAPSULE_DEL = 22;

//    AREA
    public static final byte AREA_ADD = 30;
    public static final byte AREA_UPDATE = 31;
    public static final byte AREA_DEL = 32;

    public static void main(String[] args) {
        System.out.println(ClassUtils.getStaticJSON(TaskAction.class));
    }

}
