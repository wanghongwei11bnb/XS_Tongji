package com.xiangshui.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wanghongwei on 15/10/23.
 */
public class DateUtils {

    public static final Logger log = LoggerFactory.getLogger(DateUtils.class);
    // dateFormat
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String format(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static String format(Date date, SimpleDateFormat dateFormat) {
        return dateFormat.format(date);
    }

    public static String format(Date date) {
        return simpleDateFormat.format(date);
    }

    public static String format(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    public static String format() {
        return simpleDateFormat.format(new Date());
    }


    public static Date createDate(String format, String text) throws Exception {
        return new SimpleDateFormat(format).parse(text);
    }


    public static Date createDate(int year, int month, int date) {
        return new Date(year - 1900, month - 1, date);
    }

    public static Date createDate(int year, int month, int date, int hrs, int min) {
        return new Date(year - 1900, month - 1, date, hrs, min);
    }


    public static Date createDate(int year, int month, int date, int hrs, int min, int sec) {
        return new Date(year - 1900, month - 1, date, hrs, min, sec);
    }

    public static Date copyDateEndHour(Date date) {
        return new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), 0, 0);
    }

    public static Date copyDateEndDate(Date date) {
        return new Date(date.getYear(), date.getMonth(), date.getDate(), 0, 0, 0);
    }

    public static Date copyDateEndMonth(Date date) {
        return new Date(date.getYear(), date.getMonth(), 1, 0, 0, 0);
    }

    public static Date copyDateEndMinutes(Date date) {
        return new Date(date.getYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), 0);
    }


    public static String format(long ts, String format) {
        return new SimpleDateFormat(format).format(new Date(ts));
    }


    public static void main(String[] args) throws Exception {


        log.debug(format(new Date(2018 - 1900, 1 - 1, 1, 23, 45, 59)));

        log.debug(new Date().getTime() + "");

    }
}
