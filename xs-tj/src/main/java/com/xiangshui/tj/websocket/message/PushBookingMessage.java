package com.xiangshui.tj.websocket.message;


import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.bean.User;

public class PushBookingMessage extends SendMessage {
    private Booking booking;

    private Capsule capsule;
    private Area area;

    public Capsule getCapsule() {
        return capsule;
    }

    public void setCapsule(Capsule capsule) {
        this.capsule = capsule;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

}
