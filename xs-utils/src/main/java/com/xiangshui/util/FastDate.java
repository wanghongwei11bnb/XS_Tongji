package com.xiangshui.util;

import java.util.Date;

public class FastDate extends Date {

    public FastDate(int year, int month) {
        super(year - 1900, month - 1, 1);
    }

    public FastDate(int year, int month, int date) {
        super(year - 1900, month - 1, date);
    }

    public FastDate(int year, int month, int date, int hrs, int min) {
        super(year - 1900, month - 1, date, hrs, min);
    }

    public FastDate(int year, int month, int date, int hrs, int min, int sec) {
        super(year - 1900, month - 1, date, hrs, min, sec);
    }





}
