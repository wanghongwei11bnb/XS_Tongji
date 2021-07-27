package com.xiangshui.server.domain;

public class Booking {
    private Long booking_id;
    private Integer area_id;
    private String calculate_rule;
    private Long capsule_id;
    private Integer create_date;
    private Long create_time;
    private Long end_time;
    private Integer coupon_cash;
    private Integer final_price;

    private Integer status;
    private Integer pay_type;
    private Integer uin;


    private Long update_time;
    private String bank_type;


    private Integer use_balance;
    private Integer use_pay;


    private Integer from_bonus;

    private Integer from_charge;

    private Integer from_guohang;

    private String guohang_confirm_id;

    private String guohang_order_sn;

    private Integer test_flag;

    private Integer month_card_flag;

    private String req_from;

    private Integer appraise_flag;

    private Integer by_op;

    private Integer f0;
    private Integer f1;


    private Integer monthCardPrice;

    public Integer getMonthCardPrice() {
        return monthCardPrice;
    }

    public Booking setMonthCardPrice(Integer monthCardPrice) {
        this.monthCardPrice = monthCardPrice;
        return this;
    }

    public Integer getFrom_guohang() {
        return from_guohang;
    }

    public Booking setFrom_guohang(Integer from_guohang) {
        this.from_guohang = from_guohang;
        return this;
    }

    public String getGuohang_confirm_id() {
        return guohang_confirm_id;
    }

    public Booking setGuohang_confirm_id(String guohang_confirm_id) {
        this.guohang_confirm_id = guohang_confirm_id;
        return this;
    }

    public String getGuohang_order_sn() {
        return guohang_order_sn;
    }

    public Booking setGuohang_order_sn(String guohang_order_sn) {
        this.guohang_order_sn = guohang_order_sn;
        return this;
    }

    public Integer getCoupon_cash() {
        return coupon_cash;
    }

    public Booking setCoupon_cash(Integer coupon_cash) {
        this.coupon_cash = coupon_cash;
        return this;
    }

    public Long getBooking_id() {
        return booking_id;
    }

    public Booking setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
        return this;
    }

    public Integer getArea_id() {
        return area_id;
    }

    public Booking setArea_id(Integer area_id) {
        this.area_id = area_id;
        return this;
    }

    public String getCalculate_rule() {
        return calculate_rule;
    }

    public Booking setCalculate_rule(String calculate_rule) {
        this.calculate_rule = calculate_rule;
        return this;
    }

    public Long getCapsule_id() {
        return capsule_id;
    }

    public Booking setCapsule_id(Long capsule_id) {
        this.capsule_id = capsule_id;
        return this;
    }

    public Integer getCreate_date() {
        return create_date;
    }

    public Booking setCreate_date(Integer create_date) {
        this.create_date = create_date;
        return this;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public Booking setCreate_time(Long create_time) {
        this.create_time = create_time;
        return this;
    }

    public Long getEnd_time() {
        return end_time;
    }

    public Booking setEnd_time(Long end_time) {
        this.end_time = end_time;
        return this;
    }

    public Integer getFinal_price() {
        return final_price;
    }

    public Booking setFinal_price(Integer final_price) {
        this.final_price = final_price;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public Booking setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Integer getPay_type() {
        return pay_type;
    }

    public Booking setPay_type(Integer pay_type) {
        this.pay_type = pay_type;
        return this;
    }

    public Integer getUin() {
        return uin;
    }

    public Booking setUin(Integer uin) {
        this.uin = uin;
        return this;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public Booking setUpdate_time(Long update_time) {
        this.update_time = update_time;
        return this;
    }

    public String getBank_type() {
        return bank_type;
    }

    public Booking setBank_type(String bank_type) {
        this.bank_type = bank_type;
        return this;
    }

    public Integer getUse_balance() {
        return use_balance;
    }

    public Booking setUse_balance(Integer use_balance) {
        this.use_balance = use_balance;
        return this;
    }

    public Integer getUse_pay() {
        return use_pay;
    }

    public Booking setUse_pay(Integer use_pay) {
        this.use_pay = use_pay;
        return this;
    }

    public Integer getFrom_bonus() {
        return from_bonus;
    }

    public Booking setFrom_bonus(Integer from_bonus) {
        this.from_bonus = from_bonus;
        return this;
    }

    public Integer getFrom_charge() {
        return from_charge;
    }

    public Booking setFrom_charge(Integer from_charge) {
        this.from_charge = from_charge;
        return this;
    }

    public Integer getTest_flag() {
        return test_flag;
    }

    public Booking setTest_flag(Integer test_flag) {
        this.test_flag = test_flag;
        return this;
    }

    public Integer getMonth_card_flag() {
        return month_card_flag;
    }

    public Booking setMonth_card_flag(Integer month_card_flag) {
        this.month_card_flag = month_card_flag;
        return this;
    }

    public String getReq_from() {
        return req_from;
    }

    public Booking setReq_from(String req_from) {
        this.req_from = req_from;
        return this;
    }

    public Integer getAppraise_flag() {
        return appraise_flag;
    }

    public Booking setAppraise_flag(Integer appraise_flag) {
        this.appraise_flag = appraise_flag;
        return this;
    }

    public Integer getBy_op() {
        return by_op;
    }

    public Booking setBy_op(Integer by_op) {
        this.by_op = by_op;
        return this;
    }

    public Integer getF0() {
        return f0;
    }

    public Booking setF0(Integer f0) {
        this.f0 = f0;
        return this;
    }

    public Integer getF1() {
        return f1;
    }

    public Booking setF1(Integer f1) {
        this.f1 = f1;
        return this;
    }
}
