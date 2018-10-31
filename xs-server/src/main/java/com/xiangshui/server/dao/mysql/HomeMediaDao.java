package com.xiangshui.server.dao.mysql;

import com.xiangshui.server.crud.SinglePrimaryCrudTemplate;
import com.xiangshui.server.domain.mysql.HomeMedia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class HomeMediaDao extends SinglePrimaryCrudTemplate<HomeMedia, Integer> {
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
        return "home_media";
    }
}
