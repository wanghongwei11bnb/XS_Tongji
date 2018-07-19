package com.xiangshui.server.domain;

import org.springframework.data.annotation.Id;

public class City {
    @Id
    private String city;
    private Integer code;
    private String province;

    private Integer visible;

    private Double lng;
    private Double lat;

    private String region;

    public String getRegion() {
        return region;
    }

    public City setRegion(String region) {
        this.region = region;
        return this;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }
}
