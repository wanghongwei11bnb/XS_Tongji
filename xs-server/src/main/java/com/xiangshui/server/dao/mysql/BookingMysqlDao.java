package com.xiangshui.server.dao.mysql;

import com.xiangshui.server.crud.SinglePrimaryCrudTemplate;
import com.xiangshui.server.domain.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookingMysqlDao extends SinglePrimaryCrudTemplate<Booking,Long> {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public String getPrimaryFieldName() {
        return "booking_id";
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    protected String getTableName() {
        return "booking";
    }
}
