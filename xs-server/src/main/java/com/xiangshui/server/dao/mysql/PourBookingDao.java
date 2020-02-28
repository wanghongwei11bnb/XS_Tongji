package com.xiangshui.server.dao.mysql;

import com.xiangshui.server.crud.SinglePrimaryCrudTemplate;
import com.xiangshui.server.domain.mysql.qingsu.PourBooking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PourBookingDao extends SinglePrimaryCrudTemplate<PourBooking, Long> {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public String getPrimaryFieldName() {
        return "id";
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    protected String getTableName() {
        return "pour_booking";
    }
}
