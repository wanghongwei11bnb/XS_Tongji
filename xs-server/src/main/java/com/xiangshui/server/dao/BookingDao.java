package com.xiangshui.server.dao;

import com.xiangshui.server.domain.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BookingDao extends BaseDynamoDao<Booking> {


    private static final Logger log = LoggerFactory.getLogger(BookingDao.class);


    @Override
    public String getTableName() {
        return  "booking";
    }


}
