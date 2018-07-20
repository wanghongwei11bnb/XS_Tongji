package com.xiangshui.server.domain;

public class GroupBooking {


    private Long booking_id;
    private Integer create_date;
    private Long create_time;
    private Long group_id;
    private Integer price;
    private String req_from;
    private Integer status;
    private Integer uin;
    private Long update_time;
    private Integer group_status;

    public Long getBooking_id() {
        return booking_id;
    }

    public GroupBooking setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
        return this;
    }

    public Integer getCreate_date() {
        return create_date;
    }

    public GroupBooking setCreate_date(Integer create_date) {
        this.create_date = create_date;
        return this;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public GroupBooking setCreate_time(Long create_time) {
        this.create_time = create_time;
        return this;
    }

    public Long getGroup_id() {
        return group_id;
    }

    public GroupBooking setGroup_id(Long group_id) {
        this.group_id = group_id;
        return this;
    }

    public Integer getPrice() {
        return price;
    }

    public GroupBooking setPrice(Integer price) {
        this.price = price;
        return this;
    }

    public String getReq_from() {
        return req_from;
    }

    public GroupBooking setReq_from(String req_from) {
        this.req_from = req_from;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public GroupBooking setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Integer getUin() {
        return uin;
    }

    public GroupBooking setUin(Integer uin) {
        this.uin = uin;
        return this;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public GroupBooking setUpdate_time(Long update_time) {
        this.update_time = update_time;
        return this;
    }

    public Integer getGroup_status() {
        return group_status;
    }

    public GroupBooking setGroup_status(Integer group_status) {
        this.group_status = group_status;
        return this;
    }
}
