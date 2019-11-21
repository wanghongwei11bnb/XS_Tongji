package com.xiangshui.server.domain;

public class DiscountCoupon {
    private Long coupon_id;
    private Integer cash;
    private Long create_time;
    private Long update_time;
    private Long validity_time;
    private String introduce;
    private Integer min_price;
    private Integer status;
    private Integer type;
    private Integer uin;

    public Long getCoupon_id() {
        return coupon_id;
    }

    public DiscountCoupon setCoupon_id(Long coupon_id) {
        this.coupon_id = coupon_id;
        return this;
    }

    public Integer getCash() {
        return cash;
    }

    public DiscountCoupon setCash(Integer cash) {
        this.cash = cash;
        return this;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public DiscountCoupon setCreate_time(Long create_time) {
        this.create_time = create_time;
        return this;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public DiscountCoupon setUpdate_time(Long update_time) {
        this.update_time = update_time;
        return this;
    }

    public Long getValidity_time() {
        return validity_time;
    }

    public DiscountCoupon setValidity_time(Long validity_time) {
        this.validity_time = validity_time;
        return this;
    }

    public String getIntroduce() {
        return introduce;
    }

    public DiscountCoupon setIntroduce(String introduce) {
        this.introduce = introduce;
        return this;
    }

    public Integer getMin_price() {
        return min_price;
    }

    public DiscountCoupon setMin_price(Integer min_price) {
        this.min_price = min_price;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public DiscountCoupon setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public DiscountCoupon setType(Integer type) {
        this.type = type;
        return this;
    }

    public Integer getUin() {
        return uin;
    }

    public DiscountCoupon setUin(Integer uin) {
        this.uin = uin;
        return this;
    }
}
