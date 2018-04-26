package com.xiangshui.server.dao;

import com.xiangshui.server.domain.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CityDao extends BaseDynamoDao<City> {


    private static final Logger log = LoggerFactory.getLogger(CityDao.class);


    @Override
    public String getTableName() {
        return "city";
    }


    @Override
    public String getFullTableName() {
        return getTableName();
    }
}
