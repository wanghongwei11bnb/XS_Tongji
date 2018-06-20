package com.xiangshui.server.dao;

import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.MonthCardRecode;
import com.xiangshui.util.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MonthCardRecodeDao extends BaseDynamoDao<MonthCardRecode> {


    private static final Logger log = LoggerFactory.getLogger(MonthCardRecodeDao.class);


    @Override
    public String getTableName() {
        return "month_card_recode";
    }

    public void test() {

    }

}
