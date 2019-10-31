package com.xiangshui.server.dao.mysql;

import com.xiangshui.server.crud.SinglePrimaryCrudTemplate;
import com.xiangshui.server.domain.SwiperItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SwiperItemDao extends SinglePrimaryCrudTemplate<SwiperItem, Integer> {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public SwiperItemDao() {
        this.primaryAutoIncr = true;
    }

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
        return "swiper_item";
    }
}
