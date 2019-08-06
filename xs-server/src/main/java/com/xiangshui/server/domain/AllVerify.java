package com.xiangshui.server.domain;

public class AllVerify {
    private String verify_code;
    private Integer type;
    private Integer red_envelope;
    private Integer cash;
    private Integer min_price;
    private Long start_time;
    private Long end_time;
    private Integer ban_old;

    public Integer getBan_old() {
        return ban_old;
    }

    public AllVerify setBan_old(Integer ban_old) {
        this.ban_old = ban_old;
        return this;
    }

    public String getVerify_code() {
        return verify_code;
    }

    public AllVerify setVerify_code(String verify_code) {
        this.verify_code = verify_code;
        return this;
    }

    public Long getEnd_time() {
        return end_time;
    }

    public AllVerify setEnd_time(Long end_time) {
        this.end_time = end_time;
        return this;
    }

    public Long getStart_time() {
        return start_time;
    }

    public AllVerify setStart_time(Long start_time) {
        this.start_time = start_time;
        return this;
    }

    public Integer getRed_envelope() {
        return red_envelope;
    }

    public AllVerify setRed_envelope(Integer red_envelope) {
        this.red_envelope = red_envelope;
        return this;
    }

    public Integer getCash() {
        return cash;
    }

    public AllVerify setCash(Integer cash) {
        this.cash = cash;
        return this;
    }

    public Integer getMin_price() {
        return min_price;
    }

    public AllVerify setMin_price(Integer min_price) {
        this.min_price = min_price;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public AllVerify setType(Integer type) {
        this.type = type;
        return this;
    }
}
