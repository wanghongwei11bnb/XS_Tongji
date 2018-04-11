package com.xiangshui.tj.server.relation;

import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.bean.City;

public class CapsuleRelation extends Capsule {
    private Area areaObj;
    private City cityObj;

    public Area getAreaObj() {
        return areaObj;
    }

    public void setAreaObj(Area areaObj) {
        this.areaObj = areaObj;
    }

    public City getCityObj() {
        return cityObj;
    }

    public void setCityObj(City cityObj) {
        this.cityObj = cityObj;
    }
}
