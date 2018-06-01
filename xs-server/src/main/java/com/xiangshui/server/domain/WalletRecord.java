package com.xiangshui.server.domain;

public class WalletRecord {

    private String out_trade_no;
    private Long create_time;
    private Integer price;
    private String subject;
    private Integer type;
    private Integer uin;
    private String phone;
    private Integer charge_id;
    private String operator;
    private String bank_type;
    private Integer pay_type;


    public String getOut_trade_no() {
        return out_trade_no;
    }

    public WalletRecord setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
        return this;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public WalletRecord setCreate_time(Long create_time) {
        this.create_time = create_time;
        return this;
    }

    public Integer getPrice() {
        return price;
    }

    public WalletRecord setPrice(Integer price) {
        this.price = price;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public WalletRecord setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public WalletRecord setType(Integer type) {
        this.type = type;
        return this;
    }

    public Integer getUin() {
        return uin;
    }

    public WalletRecord setUin(Integer uin) {
        this.uin = uin;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public WalletRecord setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Integer getCharge_id() {
        return charge_id;
    }

    public WalletRecord setCharge_id(Integer charge_id) {
        this.charge_id = charge_id;
        return this;
    }

    public String getOperator() {
        return operator;
    }

    public WalletRecord setOperator(String operator) {
        this.operator = operator;
        return this;
    }

    public String getBank_type() {
        return bank_type;
    }

    public WalletRecord setBank_type(String bank_type) {
        this.bank_type = bank_type;
        return this;
    }

    public Integer getPay_type() {
        return pay_type;
    }

    public WalletRecord setPay_type(Integer pay_type) {
        this.pay_type = pay_type;
        return this;
    }
}
