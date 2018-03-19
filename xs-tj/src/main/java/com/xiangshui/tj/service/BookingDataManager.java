package com.xiangshui.tj.service;

import com.xiangshui.tj.bean.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingDataManager extends DataManager<Long, Booking> {
    @Override
    Long getId(Booking booking) {
        if (booking == null) {
            return null;
        }
        return booking.getBooking_id();
    }
}
