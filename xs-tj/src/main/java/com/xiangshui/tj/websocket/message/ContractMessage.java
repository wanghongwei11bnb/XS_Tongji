package com.xiangshui.tj.websocket.message;

import com.xiangshui.tj.server.bean.*;

import java.util.List;

public class ContractMessage extends SendMessage {
    private List<CityTj> cityList;

    public List<CityTj> getCityList() {
        return cityList;
    }

    public void setCityList(List<CityTj> cityList) {
        this.cityList = cityList;
    }
}
