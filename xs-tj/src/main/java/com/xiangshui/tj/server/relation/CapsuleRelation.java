package com.xiangshui.tj.server.relation;

import com.xiangshui.tj.server.bean.AreaTj;
import com.xiangshui.tj.server.bean.CapsuleTj;
import com.xiangshui.tj.server.bean.CityTj;

public class CapsuleRelation extends CapsuleTj {
    private AreaTj areaObj;
    private CityTj cityObj;

    public AreaTj getAreaObj() {
        return areaObj;
    }

    public void setAreaObj(AreaTj areaObj) {
        this.areaObj = areaObj;
    }

    public CityTj getCityObj() {
        return cityObj;
    }

    public void setCityObj(CityTj cityObj) {
        this.cityObj = cityObj;
    }
}
