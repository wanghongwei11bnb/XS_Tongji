package com.xiangshui.tj.websocket.message;

import com.xiangshui.tj.server.bean.*;

import java.util.List;

public class ContractMessage extends SendMessage {
    private List<City> cityList;

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }
}
