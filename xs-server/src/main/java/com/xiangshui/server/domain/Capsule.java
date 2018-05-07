package com.xiangshui.server.domain;

public class Capsule {

    private Long capsule_id;
    private Long create_time;
    private Long update_time;
    private Integer status;
    private Integer type;
    private Integer area_id;

    private String device_id;

    private Integer is_downline;

    public Integer getIs_downline() {
        return is_downline;
    }

    public void setIs_downline(Integer is_downline) {
        this.is_downline = is_downline;
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

    public Long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Long update_time) {
        this.update_time = update_time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getArea_id() {
        return area_id;
    }

    public void setArea_id(Integer area_id) {
        this.area_id = area_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }


}
