package com.xiangshui.server.dao.mysql;

import com.xiangshui.server.crud.SinglePrimaryCrudTemplate;
import com.xiangshui.server.domain.mysql.Article;
import com.xiangshui.server.domain.mysql.Device;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component()
public class DeviceDao extends SinglePrimaryCrudTemplate<Device, String> {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public String getTableName() {
        return "device";
    }

    @Override
    public String getPrimaryFieldName() {
        return "id";
    }

}
