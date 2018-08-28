package com.xiangshui.server.domain;

import java.util.Date;

public class AreaBill {

    private Long bill_id;

    private Integer area_id;

    private Integer year;

    private Integer month;

    private Integer account_ratio;

    private Integer booking_count;

    private Integer final_price;

    private Integer pay_price;

    private Integer charge_price;

    private Integer month_card_price;

    private Integer ratio_price;

    private Integer status;

    private Long create_time;

    private Long update_time;

    public Integer getMonth_card_price() {

        return month_card_price;
    }

    public AreaBill setMonth_card_price(Integer month_card_price) {
        this.month_card_price = month_card_price;
        return this;
    }

    public Long getBill_id() {
        return bill_id;
    }

    public void setBill_id(Long bill_id) {
        this.bill_id = bill_id;
    }

    public Integer getArea_id() {
        return area_id;
    }

    public void setArea_id(Integer area_id) {
        this.area_id = area_id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getAccount_ratio() {
        return account_ratio;
    }

    public void setAccount_ratio(Integer account_ratio) {
        this.account_ratio = account_ratio;
    }

    public Integer getBooking_count() {
        return booking_count;
    }

    public void setBooking_count(Integer booking_count) {
        this.booking_count = booking_count;
    }

    public Integer getFinal_price() {
        return final_price;
    }

    public void setFinal_price(Integer final_price) {
        this.final_price = final_price;
    }

    public Integer getPay_price() {
        return pay_price;
    }

    public void setPay_price(Integer pay_price) {
        this.pay_price = pay_price;
    }

    public Integer getCharge_price() {
        return charge_price;
    }

    public void setCharge_price(Integer charge_price) {
        this.charge_price = charge_price;
    }

    public Integer getRatio_price() {
        return ratio_price;
    }

    public void setRatio_price(Integer ratio_price) {
        this.ratio_price = ratio_price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Long update_time) {
        this.update_time = update_time;
    }
}
