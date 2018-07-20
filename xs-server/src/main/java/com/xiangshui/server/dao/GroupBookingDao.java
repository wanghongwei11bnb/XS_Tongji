package com.xiangshui.server.dao;

import com.xiangshui.server.domain.GroupBooking;
import org.springframework.stereotype.Component;

@Component
public class GroupBookingDao extends BaseDynamoDao<GroupBooking> {
    @Override
    public String getTableName() {
        return "group_booking";
    }
}
