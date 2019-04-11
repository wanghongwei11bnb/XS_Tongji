package com.xiangshui.server.dao;

import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.CashInfo;
import com.xiangshui.util.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CashInfoDao extends BaseDynamoDao<CashInfo> {


    private static final Logger log = LoggerFactory.getLogger(CashInfoDao.class);


    @Override
    public String getTableName() {
        return  "cash_info";
    }

    public void test() {

    }

    public static void main(String[] args) {
        SpringUtils.init();
        SpringUtils.getBean(CashInfoDao.class).test();
    }

}
