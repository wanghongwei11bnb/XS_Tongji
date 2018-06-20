package com.xiangshui.server.domain;

public class MonthCardRecode {

    private Long card_no;
    private String city;
    private Long end_time;
    private Long left_seconds;
    private String out_trade_no;
    private String trade_no;
    private Integer uin;
    private Long update_time;


    public Long getCard_no() {
        return card_no;
    }

    public MonthCardRecode setCard_no(Long card_no) {
        this.card_no = card_no;
        return this;
    }

    public String getCity() {
        return city;
    }

    public MonthCardRecode setCity(String city) {
        this.city = city;
        return this;
    }

    public Long getEnd_time() {
        return end_time;
    }

    public MonthCardRecode setEnd_time(Long end_time) {
        this.end_time = end_time;
        return this;
    }

    public Long getLeft_seconds() {
        return left_seconds;
    }

    public MonthCardRecode setLeft_seconds(Long left_seconds) {
        this.left_seconds = left_seconds;
        return this;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public MonthCardRecode setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
        return this;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public MonthCardRecode setTrade_no(String trade_no) {
        this.trade_no = trade_no;
        return this;
    }

    public Integer getUin() {
        return uin;
    }

    public MonthCardRecode setUin(Integer uin) {
        this.uin = uin;
        return this;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public MonthCardRecode setUpdate_time(Long update_time) {
        this.update_time = update_time;
        return this;
    }
}
