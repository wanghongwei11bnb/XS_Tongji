package com.xiangshui.server.domain;

public class ChargeRecord {

    private String out_trade_no;
    private Long create_time;
    private Long update_time;
    private Long booking_id;
    private Integer price;
    private String subject;
    private Integer type;
    private Integer uin;
    private Integer pay_type;
    private String phone;
    private Integer status;
    private String city;
    private String trade_no;
    private String bank_type;

    private Long bill_booking_id;
    private Integer bill_area_id;

    public Integer getBill_area_id() {
        return bill_area_id;
    }

    public ChargeRecord setBill_area_id(Integer bill_area_id) {
        this.bill_area_id = bill_area_id;
        return this;
    }

    public Long getBill_booking_id() {
        return bill_booking_id;
    }

    public ChargeRecord setBill_booking_id(Long bill_booking_id) {
        this.bill_booking_id = bill_booking_id;
        return this;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public ChargeRecord setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
        return this;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public ChargeRecord setCreate_time(Long create_time) {
        this.create_time = create_time;
        return this;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public ChargeRecord setUpdate_time(Long update_time) {
        this.update_time = update_time;
        return this;
    }

    public Long getBooking_id() {
        return booking_id;
    }

    public ChargeRecord setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
        return this;
    }

    public Integer getPrice() {
        return price;
    }

    public ChargeRecord setPrice(Integer price) {
        this.price = price;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public ChargeRecord setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public ChargeRecord setType(Integer type) {
        this.type = type;
        return this;
    }

    public Integer getUin() {
        return uin;
    }

    public ChargeRecord setUin(Integer uin) {
        this.uin = uin;
        return this;
    }

    public Integer getPay_type() {
        return pay_type;
    }

    public ChargeRecord setPay_type(Integer pay_type) {
        this.pay_type = pay_type;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public ChargeRecord setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public ChargeRecord setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getCity() {
        return city;
    }

    public ChargeRecord setCity(String city) {
        this.city = city;
        return this;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public ChargeRecord setTrade_no(String trade_no) {
        this.trade_no = trade_no;
        return this;
    }

    public String getBank_type() {
        return bank_type;
    }

    public ChargeRecord setBank_type(String bank_type) {
        this.bank_type = bank_type;
        return this;
    }
}
