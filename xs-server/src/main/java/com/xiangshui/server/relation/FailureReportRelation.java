package com.xiangshui.server.relation;

import com.xiangshui.server.domain.*;

public class FailureReportRelation extends FailureReport {

    private Area _area;
    private Booking _booking;
    private Capsule _capsule;
    private UserInfo _userInfo;

    public Area get_area() {
        return _area;
    }

    public void set_area(Area _area) {
        this._area = _area;
    }

    public Booking get_booking() {
        return _booking;
    }

    public void set_booking(Booking _booking) {
        this._booking = _booking;
    }

    public Capsule get_capsule() {
        return _capsule;
    }

    public void set_capsule(Capsule _capsule) {
        this._capsule = _capsule;
    }

    public UserInfo get_userInfo() {
        return _userInfo;
    }

    public void set_userInfo(UserInfo _userInfo) {
        this._userInfo = _userInfo;
    }
}
