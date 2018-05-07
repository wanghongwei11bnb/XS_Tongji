package com.xiangshui.server.domain.fragment;

public class CapsuleType {
    private Integer type_id;
    private Integer size;
    private Integer day_max_price;
    private Integer price;
    private Integer rush_hour_price;
    private String price_rule_text;
    private String typeDesc;
    private String typeTitle;


    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getRush_hour_price() {
        return rush_hour_price;
    }

    public void setRush_hour_price(Integer rush_hour_price) {
        this.rush_hour_price = rush_hour_price;
    }

    public Integer getType_id() {
        return type_id;
    }

    public void setType_id(Integer type_id) {
        this.type_id = type_id;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getDay_max_price() {
        return day_max_price;
    }

    public void setDay_max_price(Integer day_max_price) {
        this.day_max_price = day_max_price;
    }

    public String getPrice_rule_text() {
        return price_rule_text;
    }

    public void setPrice_rule_text(String price_rule_text) {
        this.price_rule_text = price_rule_text;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }
}
