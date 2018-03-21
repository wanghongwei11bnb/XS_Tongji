package com.xiangshui.tj.server.bean;

import java.util.List;

public class City {
    private String city;
    private int code;
    private String province;

    public static List<City> cityList;

    public static City getByCity(String cityName) {
        if (cityList == null) {
            return null;
        }

        for (City city : cityList) {
            if (city.getCity().equals(cityName)) {
                return city;
            }
        }
        return null;
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
}
