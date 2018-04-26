package com.xiangshui.tj.server.relation;

import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.Capsule;

public class BookingRelation extends Booking {
    private Capsule capsuleObj;
    private Area areaObj;

    public Capsule getCapsuleObj() {
        return capsuleObj;
    }

    public void setCapsuleObj(Capsule capsuleObj) {
        this.capsuleObj = capsuleObj;
    }

    public Area getAreaObj() {
        return areaObj;
    }

    public void setAreaObj(Area areaObj) {
        this.areaObj = areaObj;
    }
}
