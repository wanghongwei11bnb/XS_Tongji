package com.xiangshui.server.bean;

import org.joda.time.DateTime;

import java.util.List;

public class AmountReckonParam {

    private DateTime start_time;
    private DateTime end_time;


    private int unit_price;


    private int peak_hour_binary;
    private int peak_price;


    private boolean wrap_night;
    private int wrap_night_price;

    private boolean wrap_day;
    private int wrap_day_price;

    private boolean use_month_card;

    public DateTime getStart_time() {
        return start_time;
    }

    public AmountReckonParam setStart_time(DateTime start_time) {
        this.start_time = start_time;
        return this;
    }

    public DateTime getEnd_time() {
        return end_time;
    }

    public AmountReckonParam setEnd_time(DateTime end_time) {
        this.end_time = end_time;
        return this;
    }

    public int getUnit_price() {
        return unit_price;
    }

    public AmountReckonParam setUnit_price(int unit_price) {
        this.unit_price = unit_price;
        return this;
    }

    public int getPeak_hour_binary() {
        return peak_hour_binary;
    }

    public AmountReckonParam setPeak_hour_binary(int peak_hour_binary) {
        this.peak_hour_binary = peak_hour_binary;
        return this;
    }

    public int getPeak_price() {
        return peak_price;
    }

    public AmountReckonParam setPeak_price(int peak_price) {
        this.peak_price = peak_price;
        return this;
    }

    public boolean getWrap_night() {
        return wrap_night;
    }

    public AmountReckonParam setWrap_night(boolean wrap_night) {
        this.wrap_night = wrap_night;
        return this;
    }

    public int getWrap_night_price() {
        return wrap_night_price;
    }

    public AmountReckonParam setWrap_night_price(int wrap_night_price) {
        this.wrap_night_price = wrap_night_price;
        return this;
    }

    public boolean getWrap_day() {
        return wrap_day;
    }

    public AmountReckonParam setWrap_day(boolean wrap_day) {
        this.wrap_day = wrap_day;
        return this;
    }

    public int getWrap_day_price() {
        return wrap_day_price;
    }

    public AmountReckonParam setWrap_day_price(int wrap_day_price) {
        this.wrap_day_price = wrap_day_price;
        return this;
    }

    public boolean getUse_month_card() {
        return use_month_card;
    }

    public AmountReckonParam setUse_month_card(boolean use_month_card) {
        this.use_month_card = use_month_card;
        return this;
    }
}
