package com.xiangshui.tj.server.bean;

public class Booking {
    private long booking_id;
    private long status;
    private long pay_type;

    private int uin;
    private long create_time;
    private long end_time;
    private int update_time;
    private long capsule_id;
    private int area_id;
    private String area_title;
    private String reserve_token;
    private String calculate_rule;
    private String bank_type;
    private String phone;
    private int final_price;
    private int start_date;
    private int end_date;
    private int create_date;
    private int balance;
    private int need_charge;
    private int use_balance;
    private int use_pay;
    private int gate_capsule_id;

    public long getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(long booking_id) {
        this.booking_id = booking_id;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public long getPay_type() {
        return pay_type;
    }

    public void setPay_type(long pay_type) {
        this.pay_type = pay_type;
    }

    public int getUin() {
        return uin;
    }

    public void setUin(int uin) {
        this.uin = uin;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public int getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(int update_time) {
        this.update_time = update_time;
    }

    public long getCapsule_id() {
        return capsule_id;
    }

    public void setCapsule_id(long capsule_id) {
        this.capsule_id = capsule_id;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public String getArea_title() {
        return area_title;
    }

    public void setArea_title(String area_title) {
        this.area_title = area_title;
    }

    public String getReserve_token() {
        return reserve_token;
    }

    public void setReserve_token(String reserve_token) {
        this.reserve_token = reserve_token;
    }

    public String getCalculate_rule() {
        return calculate_rule;
    }

    public void setCalculate_rule(String calculate_rule) {
        this.calculate_rule = calculate_rule;
    }

    public String getBank_type() {
        return bank_type;
    }

    public void setBank_type(String bank_type) {
        this.bank_type = bank_type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getFinal_price() {
        return final_price;
    }

    public void setFinal_price(int final_price) {
        this.final_price = final_price;
    }

    public int getStart_date() {
        return start_date;
    }

    public void setStart_date(int start_date) {
        this.start_date = start_date;
    }

    public int getEnd_date() {
        return end_date;
    }

    public void setEnd_date(int end_date) {
        this.end_date = end_date;
    }

    public int getCreate_date() {
        return create_date;
    }

    public void setCreate_date(int create_date) {
        this.create_date = create_date;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getNeed_charge() {
        return need_charge;
    }

    public void setNeed_charge(int need_charge) {
        this.need_charge = need_charge;
    }

    public int getUse_balance() {
        return use_balance;
    }

    public void setUse_balance(int use_balance) {
        this.use_balance = use_balance;
    }

    public int getUse_pay() {
        return use_pay;
    }

    public void setUse_pay(int use_pay) {
        this.use_pay = use_pay;
    }

    public int getGate_capsule_id() {
        return gate_capsule_id;
    }

    public void setGate_capsule_id(int gate_capsule_id) {
        this.gate_capsule_id = gate_capsule_id;
    }
}
