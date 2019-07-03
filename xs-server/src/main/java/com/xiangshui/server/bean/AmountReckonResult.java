package com.xiangshui.server.bean;

public class AmountReckonResult {
    private Integer total_amount;
    private Integer coupon_amount;
    private Integer mileage_amount;
    private Integer lvdi_amount;
    private Integer month_card_minutes;
    private Boolean use_month_card;

    private Long start_time;
    private Long end_time;

    private Integer unit_price;
    private Integer peak_price;

    private Integer peak_minutes;
    private Integer normal_minutes;


    public Integer getUnit_price() {
        return unit_price;
    }

    public AmountReckonResult setUnit_price(Integer unit_price) {
        this.unit_price = unit_price;
        return this;
    }

    public Integer getPeak_price() {
        return peak_price;
    }

    public AmountReckonResult setPeak_price(Integer peak_price) {
        this.peak_price = peak_price;
        return this;
    }

    public Integer getPeak_minutes() {
        return peak_minutes;
    }

    public AmountReckonResult setPeak_minutes(Integer peak_minutes) {
        this.peak_minutes = peak_minutes;
        return this;
    }

    public Integer getNormal_minutes() {
        return normal_minutes;
    }

    public AmountReckonResult setNormal_minutes(Integer normal_minutes) {
        this.normal_minutes = normal_minutes;
        return this;
    }

    public Integer getTotal_amount() {
        return total_amount;
    }

    public AmountReckonResult setTotal_amount(Integer total_amount) {
        this.total_amount = total_amount;
        return this;
    }

    public Integer getCoupon_amount() {
        return coupon_amount;
    }

    public AmountReckonResult setCoupon_amount(Integer coupon_amount) {
        this.coupon_amount = coupon_amount;
        return this;
    }

    public Integer getMileage_amount() {
        return mileage_amount;
    }

    public AmountReckonResult setMileage_amount(Integer mileage_amount) {
        this.mileage_amount = mileage_amount;
        return this;
    }

    public Integer getLvdi_amount() {
        return lvdi_amount;
    }

    public AmountReckonResult setLvdi_amount(Integer lvdi_amount) {
        this.lvdi_amount = lvdi_amount;
        return this;
    }

    public Integer getMonth_card_minutes() {
        return month_card_minutes;
    }

    public AmountReckonResult setMonth_card_minutes(Integer month_card_minutes) {
        this.month_card_minutes = month_card_minutes;
        return this;
    }

    public Boolean getUse_month_card() {
        return use_month_card;
    }

    public AmountReckonResult setUse_month_card(Boolean use_month_card) {
        this.use_month_card = use_month_card;
        return this;
    }

    public Long getStart_time() {
        return start_time;
    }

    public AmountReckonResult setStart_time(Long start_time) {
        this.start_time = start_time;
        return this;
    }

    public Long getEnd_time() {
        return end_time;
    }

    public AmountReckonResult setEnd_time(Long end_time) {
        this.end_time = end_time;
        return this;
    }
}
