package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.BookingTj;
import com.xiangshui.util.CallBackForResult;
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


    public synchronized BookingTj random(long lastBookingCapsuleId, CallBackForResult<BookingTj, Boolean> filter) {
        int n = (int) (Math.random() * 100 + 100);
        int i = 0;
        for (long booking_id : map.keySet()) {
            if (++i < n) {
                continue;
            }
            BookingTj bookingTj = map.get(booking_id);
            if (bookingTj.getCapsule_id() != lastBookingCapsuleId) {
                if (filter.run(bookingTj)) {
                    return bookingTj;
                }
            }
        }
        return null;
    }


}
