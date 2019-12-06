package com.xiangshui.server.domain;

public class DeviceRelation {
    private String device_id;
    private Long booking_id;

    public String getDevice_id() {
        return device_id;
    }

    public DeviceRelation setDevice_id(String device_id) {
        this.device_id = device_id;
        return this;
    }

    public Long getBooking_id() {
        return booking_id;
    }

    public DeviceRelation setBooking_id(Long booking_id) {
        this.booking_id = booking_id;
        return this;
    }
}
