package com.xiangshui.server.domain;

public class Booking {
    private Long booking_id;
    private Integer area_id;
    private String calculate_rule;
    private Long capsule_id;
    private Integer create_date;
    private Long create_time;
    private Long end_time;
    private Integer final_price;

    private Integer status;
    private Integer pay_type;
    private Integer uin;


    private Integer update_time;
    private String bank_type;


    private Integer use_balance;
    private Integer use_pay;


    private Integer from_bonus;

    private Integer from_charge;

    private Integer test_flag;

    private Integer month_card_flag;

    private String req_from;

    private Integer appraise_flag;

    private Integer by_op;

    public Integer getBy_op() {
        return by_op;
    }

    public Booking setBy_op(Integer by_op) {
        this.by_op = by_op;
        return this;
    }


    public Integer getMonth_card_flag() {
        return month_card_flag;
    }

    public Booking setMonth_card_flag(Integer month_card_flag) {
        this.month_card_flag = month_card_flag;
        return this;
    }

    public Long getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
    }

    public Integer getArea_id() {
        return area_id;
    }

    public void setArea_id(Integer area_id) {
        this.area_id = area_id;
    }

    public String getCalculate_rule() {
        return calculate_rule;
    }

    public void setCalculate_rule(String calculate_rule) {
        this.calculate_rule = calculate_rule;
    }

    public Long getCapsule_id() {
        return capsule_id;
    }

    public void setCapsule_id(Long capsule_id) {
        this.capsule_id = capsule_id;
    }

    public Integer getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Integer create_date) {
        this.create_date = create_date;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }

    public Long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Long end_time) {
        this.end_time = end_time;
    }

    public Integer getFinal_price() {
        return final_price;
    }

    public void setFinal_price(Integer final_price) {
        this.final_price = final_price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPay_type() {
        return pay_type;
    }

    public void setPay_type(Integer pay_type) {
        this.pay_type = pay_type;
    }

    public Integer getUin() {
        return uin;
    }

    public void setUin(Integer uin) {
        this.uin = uin;
    }

    public Integer getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Integer update_time) {
        this.update_time = update_time;
    }

    public String getBank_type() {
        return bank_type;
    }

    public void setBank_type(String bank_type) {
        this.bank_type = bank_type;
    }

    public Integer getUse_balance() {
        return use_balance;
    }

    public void setUse_balance(Integer use_balance) {
        this.use_balance = use_balance;
    }

    public Integer getUse_pay() {
        return use_pay;
    }

    public void setUse_pay(Integer use_pay) {
        this.use_pay = use_pay;
    }

    public Integer getFrom_bonus() {
        return from_bonus;
    }

    public void setFrom_bonus(Integer from_bonus) {
        this.from_bonus = from_bonus;
    }

    public Integer getFrom_charge() {
        return from_charge;
    }

    public void setFrom_charge(Integer from_charge) {
        this.from_charge = from_charge;
    }

    public Integer getTest_flag() {
        return test_flag;
    }

    public void setTest_flag(Integer test_flag) {
        this.test_flag = test_flag;
    }

    public String getReq_from() {
        return req_from;
    }

    public void setReq_from(String req_from) {
        this.req_from = req_from;
    }

    public Integer getAppraise_flag() {
        return appraise_flag;
    }

    public void setAppraise_flag(Integer appraise_flag) {
        this.appraise_flag = appraise_flag;
    }
}
