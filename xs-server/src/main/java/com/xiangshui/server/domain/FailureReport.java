package com.xiangshui.server.domain;

import java.util.List;

public class FailureReport {
    private Long capsule_id;
    private Long create_time;
    private Long booking_id;

    private String app_version;
    private Integer area_id;
    private String client_type;
    private String client_version;
    private String description;
    private String op_description;
    private List<String> imgs;
    private String phone;
    private String req_from;
    private List<String> tags;
    private Integer uin;

    private Integer op_status;

    public String getOp_description() {
        return op_description;
    }

    public void setOp_description(String op_description) {
        this.op_description = op_description;
    }

    public Integer getOp_status() {
        return op_status;
    }

    public void setOp_status(Integer op_status) {
        this.op_status = op_status;
    }

    public Long getCapsule_id() {
        return capsule_id;
    }

    public void setCapsule_id(Long capsule_id) {
        this.capsule_id = capsule_id;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }

    public Long getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public Integer getArea_id() {
        return area_id;
    }

    public void setArea_id(Integer area_id) {
        this.area_id = area_id;
    }

    public String getClient_type() {
        return client_type;
    }

    public void setClient_type(String client_type) {
        this.client_type = client_type;
    }

    public String getClient_version() {
        return client_version;
    }

    public void setClient_version(String client_version) {
        this.client_version = client_version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReq_from() {
        return req_from;
    }

    public void setReq_from(String req_from) {
        this.req_from = req_from;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getUin() {
        return uin;
    }

    public void setUin(Integer uin) {
        this.uin = uin;
    }
}
