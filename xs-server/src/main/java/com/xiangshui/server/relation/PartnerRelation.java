package com.xiangshui.server.relation;

import com.xiangshui.server.domain.*;
import com.xiangshui.server.domain.mysql.Partner;

import java.util.List;

public class PartnerRelation extends Partner {

    private List<Area> _areaList;

    public List<Area> get_areaList() {
        return _areaList;
    }

    public void set_areaList(List<Area> _areaList) {
        this._areaList = _areaList;
    }
}
