package com.xiangshui.tj.websocket.message;


import com.xiangshui.tj.server.bean.AreaTj;
import com.xiangshui.tj.server.bean.BookingTj;
import com.xiangshui.tj.server.bean.CapsuleTj;

public class PushBookingMessage extends SendMessage {
    private BookingTj booking;

    private CapsuleTj capsule;
    private AreaTj area;

    public CapsuleTj getCapsule() {
        return capsule;
    }

    public void setCapsule(CapsuleTj capsule) {
        this.capsule = capsule;
    }

    public AreaTj getArea() {
        return area;
    }

    public void setArea(AreaTj area) {
        this.area = area;
    }

    public BookingTj getBooking() {
        return booking;
    }

    public void setBooking(BookingTj booking) {
        this.booking = booking;
    }

}
