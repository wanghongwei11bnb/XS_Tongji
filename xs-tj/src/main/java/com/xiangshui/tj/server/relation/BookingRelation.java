package com.xiangshui.tj.server.relation;

import com.xiangshui.tj.server.bean.AreaTj;
import com.xiangshui.tj.server.bean.BookingTj;
import com.xiangshui.tj.server.bean.CapsuleTj;

public class BookingRelation extends BookingTj {
    private CapsuleTj capsuleObj;
    private AreaTj areaObj;

    public CapsuleTj getCapsuleObj() {
        return capsuleObj;
    }

    public void setCapsuleObj(CapsuleTj capsuleObj) {
        this.capsuleObj = capsuleObj;
    }

    public AreaTj getAreaObj() {
        return areaObj;
    }

    public void setAreaObj(AreaTj areaObj) {
        this.areaObj = areaObj;
    }
}
