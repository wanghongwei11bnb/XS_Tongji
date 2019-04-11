package com.xiangshui.server.domain;

public class CashInfo {

    private Long cash_id;
    private Integer cash_num;
    private Long create_time;
    private Integer type;
    private Integer uin;
    private Long booking_id;

    public Long getBooking_id() {
        return booking_id;
    }

    public CashInfo setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
        return this;
    }

    public Long getCash_id() {
        return cash_id;
    }

    public CashInfo setCash_id(Long cash_id) {
        this.cash_id = cash_id;
        return this;
    }

    public Integer getCash_num() {
        return cash_num;
    }

    public CashInfo setCash_num(Integer cash_num) {
        this.cash_num = cash_num;
        return this;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public CashInfo setCreate_time(Long create_time) {
        this.create_time = create_time;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public CashInfo setType(Integer type) {
        this.type = type;
        return this;
    }

    public Integer getUin() {
        return uin;
    }

    public CashInfo setUin(Integer uin) {
        this.uin = uin;
        return this;
    }
}
