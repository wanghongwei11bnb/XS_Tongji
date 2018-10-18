package com.xiangshui.op.bean;

import com.xiangshui.server.domain.AreaBill;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.ChargeRecord;

import java.util.List;
import java.util.Map;

public class AreaBillResult {


    private Integer area_id;

    private Long time_start;

    private Long time_end;

    private List<Booking> bookingList;

    private List<ChargeRecord> chargeRecordList;

    private Map<Long, ChargeRecord> chargeRecordMap;

    private Integer booking_count;

    private Integer final_price;

    private Integer pay_price;

    private Integer charge_price;

    private Integer month_card_price;

    private Integer ratio_price;

    private Integer account_ratio;


    public Integer getArea_id() {
        return area_id;
    }

    public AreaBillResult setArea_id(Integer area_id) {
        this.area_id = area_id;
        return this;
    }

    public Long getTime_start() {
        return time_start;
    }

    public AreaBillResult setTime_start(Long time_start) {
        this.time_start = time_start;
        return this;
    }

    public Long getTime_end() {
        return time_end;
    }

    public AreaBillResult setTime_end(Long time_end) {
        this.time_end = time_end;
        return this;
    }

    public List<Booking> getBookingList() {
        return bookingList;
    }

    public AreaBillResult setBookingList(List<Booking> bookingList) {
        this.bookingList = bookingList;
        return this;
    }

    public List<ChargeRecord> getChargeRecordList() {
        return chargeRecordList;
    }

    public AreaBillResult setChargeRecordList(List<ChargeRecord> chargeRecordList) {
        this.chargeRecordList = chargeRecordList;
        return this;
    }

    public Map<Long, ChargeRecord> getChargeRecordMap() {
        return chargeRecordMap;
    }

    public AreaBillResult setChargeRecordMap(Map<Long, ChargeRecord> chargeRecordMap) {
        this.chargeRecordMap = chargeRecordMap;
        return this;
    }

    public Integer getBooking_count() {
        return booking_count;
    }

    public AreaBillResult setBooking_count(Integer booking_count) {
        this.booking_count = booking_count;
        return this;
    }

    public Integer getFinal_price() {
        return final_price;
    }

    public AreaBillResult setFinal_price(Integer final_price) {
        this.final_price = final_price;
        return this;
    }

    public Integer getPay_price() {
        return pay_price;
    }

    public AreaBillResult setPay_price(Integer pay_price) {
        this.pay_price = pay_price;
        return this;
    }

    public Integer getCharge_price() {
        return charge_price;
    }

    public AreaBillResult setCharge_price(Integer charge_price) {
        this.charge_price = charge_price;
        return this;
    }

    public Integer getMonth_card_price() {
        return month_card_price;
    }

    public AreaBillResult setMonth_card_price(Integer month_card_price) {
        this.month_card_price = month_card_price;
        return this;
    }

    public Integer getRatio_price() {
        return ratio_price;
    }

    public AreaBillResult setRatio_price(Integer ratio_price) {
        this.ratio_price = ratio_price;
        return this;
    }

    public Integer getAccount_ratio() {
        return account_ratio;
    }

    public AreaBillResult setAccount_ratio(Integer account_ratio) {
        this.account_ratio = account_ratio;
        return this;
    }
}
