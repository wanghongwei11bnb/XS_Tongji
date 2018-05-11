package com.xiangshui.server.relation;

import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Capsule;

public class CapsuleRelation extends Capsule {
    private Area _area;

    public Area get_area() {
        return _area;
    }

    public void set_area(Area _area) {
        this._area = _area;
    }
}
