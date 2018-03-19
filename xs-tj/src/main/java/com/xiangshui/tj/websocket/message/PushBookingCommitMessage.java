package com.xiangshui.tj.websocket.message;

import com.xiangshui.tj.bean.Booking;

public class PushBookingCommitMessage extends SendMessage {
    private Booking booking;

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
