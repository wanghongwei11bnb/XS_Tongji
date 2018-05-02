package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.BookingTj;
import org.springframework.stereotype.Component;

@Component
public class BookingDataManager extends DataManager<Long, BookingTj> {
    @Override
    Long getId(BookingTj booking) {
        if (booking == null) {
            return null;
        }
        return booking.getBooking_id();
    }
}
