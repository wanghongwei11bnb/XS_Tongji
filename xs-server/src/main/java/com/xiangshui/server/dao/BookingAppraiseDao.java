package com.xiangshui.server.dao;

import com.xiangshui.server.domain.BookingAppraise;
import org.springframework.stereotype.Component;

@Component
public class BookingAppraiseDao extends BaseDynamoDao<BookingAppraise> {
    @Override
    public String getTableName() {
        return "booking_appraise";
    }
}
