package com.xiangshui.server.service;

import com.xiangshui.server.exception.XiangShuiException;

public class PeakHourTools {

    public static int appendHour(int binary, int... hours) {
        if (hours != null && hours.length > 0) {
            for (int hour : hours) {
                if (!(0 <= hour && hour <= 23)) {
                    throw new XiangShuiException("hour在0到23之间！");
                }
                binary = binary | ((int) Math.pow(2, hour));
            }
        }
        return binary;
    }

    public static boolean checkHour(int binary, int hour) {
        if (!(0 <= hour && hour <= 23)) {
            throw new XiangShuiException("hour在0到23之间！");
        }
        int hour_binary = (int) Math.pow(2, hour);
        return (binary & hour_binary) == hour_binary;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(Integer.MAX_VALUE);

        int binary = appendHour(0, 11, 12, 13, 14);

        System.out.println(binary);

        System.out.println(checkHour(binary, 11));

    }

}
