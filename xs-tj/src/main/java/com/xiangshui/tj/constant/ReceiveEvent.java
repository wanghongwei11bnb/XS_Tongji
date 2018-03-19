package com.xiangshui.tj.constant;

import com.xiangshui.util.ClassUtils;

public class ReceiveEvent {

    public static final int HISTORY_DATA = 1;

    //    BOOKING
    public static final int BOOKING_COMMIT = 10;
    public static final int BOOKING_PROPAY = 11;
    public static final int BOOKING_FINISH = 12;

    //    CAPSULE
    public static final int CAPSULE_ADD = 20;
    public static final int CAPSULE_UPDATE = 21;
    public static final int CAPSULE_DEL = 22;

    //    AREA
    public static final int AREA_ADD = 30;
    public static final int AREA_UPDATE = 31;
    public static final int AREA_DEL = 32;

    public static void main(String[] args) {
        System.out.println(ClassUtils.getStaticJSON(ReceiveEvent.class));
    }

}
