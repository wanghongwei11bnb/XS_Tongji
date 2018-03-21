package com.xiangshui.tj.websocket.message;

import com.xiangshui.tj.server.bean.*;

import java.util.List;

public class ContractMessage extends SendMessage {
    private List<City> cityList;
    private List<Appraise> appraiseList;

    private List<Object[]> usageRateData;

    public List<Object[]> getUsageRateData() {
        return usageRateData;
    }

    public void setUsageRateData(List<Object[]> usageRateData) {
        this.usageRateData = usageRateData;
    }

    public List<Appraise> getAppraiseList() {
        return appraiseList;
    }

    public void setAppraiseList(List<Appraise> appraiseList) {
        this.appraiseList = appraiseList;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }
}
