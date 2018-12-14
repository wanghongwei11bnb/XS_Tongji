package com.xiangshui.server.domain;

public class MinitouBill {

    private Long bill_id;
    private Integer year;
    private Integer month;
    private Long capsule_id;
    private Integer final_price;
    private Integer account_ratio;
    private Integer ratio_price;
    private Integer rent_price;
    private Integer other_price;
    private Integer net_price;


    public Long getBill_id() {
        return bill_id;
    }

    public MinitouBill setBill_id(Long bill_id) {
        this.bill_id = bill_id;
        return this;
    }

    public Integer getYear() {
        return year;
    }

    public MinitouBill setYear(Integer year) {
        this.year = year;
        return this;
    }

    public Integer getMonth() {
        return month;
    }

    public MinitouBill setMonth(Integer month) {
        this.month = month;
        return this;
    }

    public Long getCapsule_id() {
        return capsule_id;
    }

    public MinitouBill setCapsule_id(Long capsule_id) {
        this.capsule_id = capsule_id;
        return this;
    }

    public Integer getFinal_price() {
        return final_price;
    }

    public MinitouBill setFinal_price(Integer final_price) {
        this.final_price = final_price;
        return this;
    }

    public Integer getAccount_ratio() {
        return account_ratio;
    }

    public MinitouBill setAccount_ratio(Integer account_ratio) {
        this.account_ratio = account_ratio;
        return this;
    }

    public Integer getRatio_price() {
        return ratio_price;
    }

    public MinitouBill setRatio_price(Integer ratio_price) {
        this.ratio_price = ratio_price;
        return this;
    }

    public Integer getRent_price() {
        return rent_price;
    }

    public MinitouBill setRent_price(Integer rent_price) {
        this.rent_price = rent_price;
        return this;
    }

    public Integer getOther_price() {
        return other_price;
    }

    public MinitouBill setOther_price(Integer other_price) {
        this.other_price = other_price;
        return this;
    }

    public Integer getNet_price() {
        return net_price;
    }

    public MinitouBill setNet_price(Integer net_price) {
        this.net_price = net_price;
        return this;
    }
}
