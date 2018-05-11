package com.xiangshui.server.relation;

import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.domain.UserInfo;

public class BookingRelation extends Booking {
    private UserInfo _userInfo;
    private Capsule _capsule;
    private Area _area;

    public UserInfo get_userInfo() {
        return _userInfo;
    }

    public void set_userInfo(UserInfo _userInfo) {
        this._userInfo = _userInfo;
    }

    public Capsule get_capsule() {
        return _capsule;
    }

    public void set_capsule(Capsule _capsule) {
        this._capsule = _capsule;
    }

    public Area get_area() {
        return _area;
    }

    public void set_area(Area _area) {
        this._area = _area;
    }
}
