package com.xiangshui.op.bean;

import java.util.Date;

public class DeviceStatus {
    private Integer area_id;
    private Long capsule_id;
    private String device_id;
    private Date update_time;
    private Integer status;
    private Integer wifi_flag;
    private String status_text;

    public String getStatus_text() {
        return status_text;
    }

    public void setStatus_text(String status_text) {
        this.status_text = status_text;
    }

    public Integer getArea_id() {
        return area_id;
    }

    public void setArea_id(Integer area_id) {
        this.area_id = area_id;
    }

    public Long getCapsule_id() {
        return capsule_id;
    }

    public void setCapsule_id(Long capsule_id) {
        this.capsule_id = capsule_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getWifi_flag() {
        return wifi_flag;
    }

    public void setWifi_flag(Integer wifi_flag) {
        this.wifi_flag = wifi_flag;
    }
}
