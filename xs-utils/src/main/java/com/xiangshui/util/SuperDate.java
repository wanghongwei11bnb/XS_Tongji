package com.xiangshui.util;

import java.util.Date;

public class SuperDate extends Date {


    public SuperDate() {
    }

    public SuperDate(long date) {
        super(date);
    }

    public SuperDate(int year, int month, int date) {
        super(year - 1900, month - 1, date);
    }

    public SuperDate(int year, int month, int date, int hrs, int min) {
        super(year - 1900, month - 1, date, hrs, min);
    }

    public SuperDate(int year, int month, int date, int hrs, int min, int sec) {
        super(year - 1900, month - 1, date, hrs, min, sec);
    }

    public SuperDate(String s) {
        super(s);
    }







}
