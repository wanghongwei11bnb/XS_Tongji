package com.xiangshui.server.dao.mysql;

import com.xiangshui.server.crud.SinglePrimaryCrudTemplate;
import com.xiangshui.server.domain.mysql.HomeMedia;
import com.xiangshui.server.domain.mysql.PrizeQuota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PrizeQuotaDao extends SinglePrimaryCrudTemplate<PrizeQuota, Integer> {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public PrizeQuotaDao() {
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
        return "prize_quota";
    }
}
