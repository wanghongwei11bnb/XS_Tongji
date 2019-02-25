package com.xiangshui.server.domain;

public class DepositRecord {
    private String out_trade_no;
    private Long create_time;
    private Integer price;
    private String subject;
    private Integer uin;

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public DepositRecord setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
        return this;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public DepositRecord setCreate_time(Long create_time) {
        this.create_time = create_time;
        return this;
    }

    public Integer getPrice() {
        return price;
    }

    public DepositRecord setPrice(Integer price) {
        this.price = price;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public DepositRecord setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Integer getUin() {
        return uin;
    }

    public DepositRecord setUin(Integer uin) {
        this.uin = uin;
        return this;
    }
}
