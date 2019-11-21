package com.xiangshui.server.domain;

public class RedBag {
    private Long id;
    private Integer uin;
    private Integer type;
    private Integer status;
    private String price_title;
    private Long booking_id;
    private Integer cash;
    private Integer price;
    private Integer min_price;
    private Long create_time;
    private Long receive_time;

    public Long getId() {
        return id;
    }

    public RedBag setId(Long id) {
        this.id = id;
        return this;
    }

    public Integer getUin() {
        return uin;
    }

    public RedBag setUin(Integer uin) {
        this.uin = uin;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public RedBag setType(Integer type) {
        this.type = type;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public RedBag setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getPrice_title() {
        return price_title;
    }

    public RedBag setPrice_title(String price_title) {
        this.price_title = price_title;
        return this;
    }

    public Long getBooking_id() {
        return booking_id;
    }

    public RedBag setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
        return this;
    }

    public Integer getCash() {
        return cash;
    }

    public RedBag setCash(Integer cash) {
        this.cash = cash;
        return this;
    }

    public Integer getPrice() {
        return price;
    }

    public RedBag setPrice(Integer price) {
        this.price = price;
        return this;
    }

    public Integer getMin_price() {
        return min_price;
    }

    public RedBag setMin_price(Integer min_price) {
        this.min_price = min_price;
        return this;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public RedBag setCreate_time(Long create_time) {
        this.create_time = create_time;
        return this;
    }

    public Long getReceive_time() {
        return receive_time;
    }

    public RedBag setReceive_time(Long receive_time) {
        this.receive_time = receive_time;
        return this;
    }
}
