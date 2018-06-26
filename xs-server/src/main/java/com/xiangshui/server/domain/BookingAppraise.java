package com.xiangshui.server.domain;

import java.util.List;

public class BookingAppraise {

    private Long booking_id;
    private List<String> appraise;

    private Integer area_id;

    private Long createtime;
    private Integer score;
    private Integer uin;
    private String suggest;

    public Long getBooking_id() {
        return booking_id;
    }

    public BookingAppraise setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
        return this;
    }

    public List<String> getAppraise() {
        return appraise;
    }

    public BookingAppraise setAppraise(List<String> appraise) {
        this.appraise = appraise;
        return this;
    }

    public Integer getArea_id() {
        return area_id;
    }

    public BookingAppraise setArea_id(Integer area_id) {
        this.area_id = area_id;
        return this;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public BookingAppraise setCreatetime(Long createtime) {
        this.createtime = createtime;
        return this;
    }

    public Integer getScore() {
        return score;
    }

    public BookingAppraise setScore(Integer score) {
        this.score = score;
        return this;
    }

    public Integer getUin() {
        return uin;
    }

    public BookingAppraise setUin(Integer uin) {
        this.uin = uin;
        return this;
    }

    public String getSuggest() {
        return suggest;
    }

    public BookingAppraise setSuggest(String suggest) {
        this.suggest = suggest;
        return this;
    }
}
