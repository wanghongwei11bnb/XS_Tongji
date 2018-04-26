package com.xiangshui.server.relation;

import com.xiangshui.server.domain.*;
import com.xiangshui.server.domain.mysql.Partner;

import java.util.List;

public class PartnerRelation extends Partner {

    private List<Area> areaList;

    public List<Area> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<Area> areaList) {
        this.areaList = areaList;
    }
}
