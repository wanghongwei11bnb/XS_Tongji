package com.xiangshui.util;

public class NumberUtils {


    public static double toFixed(double d, int n, boolean round) {
        int n10 = (int) Math.pow(10, n);
        double dn10 = d * n10;
        double zn = Math.floor(dn10);
        if (round && dn10 % 1 >= 0.5) {
            zn++;
        }
        return zn / n10;
    }


}
