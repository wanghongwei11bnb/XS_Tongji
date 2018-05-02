package com.xiangshui.tj.server.bean;

import java.util.Date;

public class CapsuleTj {

    private long capsule_id;
    private int create_time;
    private Date create_time_date;
    private int update_time;
    private Date update_time_date;
    private int status;
    private int type;
    private int area_id;

    private String device_id;
    private String device_id_new;


    private Date lastUseTime;
    private Date lastBookingTime;
    private String lastBookingTimeText;

    private int countBookingFor3Day;
    private int countBookingFor7Day;

    private int is_downline;

    public int getIs_downline() {
        return is_downline;
    }

    public void setIs_downline(int is_downline) {
        this.is_downline = is_downline;
    }

    public String getLastBookingTimeText() {
        return lastBookingTimeText;
    }

    public void setLastBookingTimeText(String lastBookingTimeText) {
        this.lastBookingTimeText = lastBookingTimeText;
    }

    public int getCountBookingFor3Day() {
        return countBookingFor3Day;
    }

    public void setCountBookingFor3Day(int countBookingFor3Day) {
        this.countBookingFor3Day = countBookingFor3Day;
    }

    public int getCountBookingFor7Day() {
        return countBookingFor7Day;
    }

    public void setCountBookingFor7Day(int countBookingFor7Day) {
        this.countBookingFor7Day = countBookingFor7Day;
    }

    public Date getLastUseTime() {
        return lastUseTime;
    }

    public void setLastUseTime(Date lastUseTime) {
        this.lastUseTime = lastUseTime;
    }

    public Date getLastBookingTime() {
        return lastBookingTime;
    }

    public void setLastBookingTime(Date lastBookingTime) {
        this.lastBookingTime = lastBookingTime;
    }

    public long getCapsule_id() {
        return capsule_id;
    }

    public void setCapsule_id(long capsule_id) {
        this.capsule_id = capsule_id;
    }

    public int getCreate_time() {
        return create_time;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    public int getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(int update_time) {
        this.update_time = update_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_id_new() {
        return device_id_new;
    }

    public void setDevice_id_new(String device_id_new) {
        this.device_id_new = device_id_new;
    }

    public Date getCreate_time_date() {
        return create_time_date;
    }

    public void setCreate_time_date(Date create_time_date) {
        this.create_time_date = create_time_date;
    }

    public Date getUpdate_time_date() {
        return update_time_date;
    }

    public void setUpdate_time_date(Date update_time_date) {
        this.update_time_date = update_time_date;
    }
}
