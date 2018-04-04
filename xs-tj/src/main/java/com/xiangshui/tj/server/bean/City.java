package com.xiangshui.tj.server.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class City {
    private String city;
    private int code;
    private String province;

    private int countArea;
    private int countCapsule;
    private int countBooking;

    private double lng;
    private double lat;

    public static List<City> cityList;

    public static Map<Integer, City> cityMap = new HashMap<>();


    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getCountArea() {
        return countArea;
    }

    public void setCountArea(int countArea) {
        this.countArea = countArea;
    }

    public int getCountCapsule() {
        return countCapsule;
    }

    public void setCountCapsule(int countCapsule) {
        this.countCapsule = countCapsule;
    }

    public int getCountBooking() {
        return countBooking;
    }

    public void setCountBooking(int countBooking) {
        this.countBooking = countBooking;
    }
}
