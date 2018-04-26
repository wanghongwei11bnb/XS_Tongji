package com.xiangshui.server.relation;

import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.domain.UserInfo;

public class BookingRelation extends Booking {
    private UserInfo userInfoObj;
    private Capsule capsuleObj;
    private Area areaObj;

    public UserInfo getUserInfoObj() {
        return userInfoObj;
    }

    public void setUserInfoObj(UserInfo userInfoObj) {
        this.userInfoObj = userInfoObj;
    }

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
