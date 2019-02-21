package com.xiangshui.op.bean;

public class CashRecord {

    private Integer id;
    private Integer type;
    private Long cash_time;
    private Integer cash_amount;
    private Integer uin;
    private String phone;
    private Integer area_id;
    private String area_title;
    private String area_city;
    private Long capsule_id;
    private Long booking_id;
    private Integer group_status;
    private String saler;
    private String operator;


    public Integer getId() {
        return id;
    }

    public CashRecord setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public CashRecord setType(Integer type) {
        this.type = type;
        return this;
    }

    public Long getCash_time() {
        return cash_time;
    }

    public CashRecord setCash_time(Long cash_time) {
        this.cash_time = cash_time;
        return this;
    }

    public Integer getCash_amount() {
        return cash_amount;
    }

    public CashRecord setCash_amount(Integer cash_amount) {
        this.cash_amount = cash_amount;
        return this;
    }

    public Integer getUin() {
        return uin;
    }

    public CashRecord setUin(Integer uin) {
        this.uin = uin;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public CashRecord setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Integer getArea_id() {
        return area_id;
    }

    public CashRecord setArea_id(Integer area_id) {
        this.area_id = area_id;
        return this;
    }

    public String getArea_title() {
        return area_title;
    }

    public CashRecord setArea_title(String area_title) {
        this.area_title = area_title;
        return this;
    }

    public String getArea_city() {
        return area_city;
    }

    public CashRecord setArea_city(String area_city) {
        this.area_city = area_city;
        return this;
    }

    public Long getCapsule_id() {
        return capsule_id;
    }

    public CashRecord setCapsule_id(Long capsule_id) {
        this.capsule_id = capsule_id;
        return this;
    }

    public Long getBooking_id() {
        return booking_id;
    }

    public CashRecord setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
        return this;
    }

    public Integer getGroup_status() {
        return group_status;
    }

    public CashRecord setGroup_status(Integer group_status) {
        this.group_status = group_status;
        return this;
    }

    public String getSaler() {
        return saler;
    }

    public CashRecord setSaler(String saler) {
        this.saler = saler;
        return this;
    }

    public String getOperator() {
        return operator;
    }

    public CashRecord setOperator(String operator) {
        this.operator = operator;
        return this;
    }
}
