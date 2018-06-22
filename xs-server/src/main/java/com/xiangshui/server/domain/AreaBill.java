package com.xiangshui.server.domain;

import java.util.Date;

public class AreaBill {
    /**
     * ${area_id}_${month}
     */
    private String bill_id;

    private Integer area_id;
    /**
     * yyyyMM
     */
    private Integer month;

    private Integer account_ratio;

    private Integer booking_count;

    private Integer final_price;

    private Integer pay_price;

    private Integer charge_price;

    private Integer ratio_price;

    private Integer status;

    private Long create_time;

    private Long update_time;

    public Long getCreate_time() {
        return create_time;
    }

    public AreaBill setCreate_time(Long create_time) {
        this.create_time = create_time;
        return this;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public AreaBill setUpdate_time(Long update_time) {
        this.update_time = update_time;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public AreaBill setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getBill_id() {
        return bill_id;
    }

    public AreaBill setBill_id(String bill_id) {
        this.bill_id = bill_id;
        return this;
    }

    public Integer getArea_id() {
        return area_id;
    }

    public AreaBill setArea_id(Integer area_id) {
        this.area_id = area_id;
        return this;
    }

    public Integer getMonth() {
        return month;
    }

    public AreaBill setMonth(Integer month) {
        this.month = month;
        return this;
    }

    public Integer getAccount_ratio() {
        return account_ratio;
    }

    public AreaBill setAccount_ratio(Integer account_ratio) {
        this.account_ratio = account_ratio;
        return this;
    }

    public Integer getBooking_count() {
        return booking_count;
    }

    public AreaBill setBooking_count(Integer booking_count) {
        this.booking_count = booking_count;
        return this;
    }

    public Integer getFinal_price() {
        return final_price;
    }

    public AreaBill setFinal_price(Integer final_price) {
        this.final_price = final_price;
        return this;
    }

    public Integer getPay_price() {
        return pay_price;
    }

    public AreaBill setPay_price(Integer pay_price) {
        this.pay_price = pay_price;
        return this;
    }

    public Integer getCharge_price() {
        return charge_price;
    }

    public AreaBill setCharge_price(Integer charge_price) {
        this.charge_price = charge_price;
        return this;
    }

    public Integer getRatio_price() {
        return ratio_price;
    }

    public AreaBill setRatio_price(Integer ratio_price) {
        this.ratio_price = ratio_price;
        return this;
    }
}
