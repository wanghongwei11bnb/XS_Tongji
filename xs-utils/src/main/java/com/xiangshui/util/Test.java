package com.xiangshui.util;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) throws Exception {

//        DateTime dateTime = new DateTime(2016, 7, 27, 10, 45);
//        log.debug(dateTime.toString("yyyy-MM-dd HH:mm:ss"));


//        LocalDate localDate = LocalDate.now().withDayOfMonth(1).minusDays(1);
//        log.debug(localDate.getYear()+"");
//        log.debug(localDate.getMonthOfYear()+"");

        int year = 2018;
        int month = 6;
        long start = new LocalDate(year, month, 1).toDate().getTime() / 1000;
        long end = new LocalDate(year, month + 1, 1).toDate().getTime() / 1000;
        log.debug(start + "");
        log.debug(end + "");
    }
}
