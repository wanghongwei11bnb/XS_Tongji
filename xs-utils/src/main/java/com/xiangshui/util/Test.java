package com.xiangshui.util;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) throws Exception {

//        log.info(new LocalDate(2020 + 20, 1, 1).toDate().getTime() / 1000 + "");
//
//        log.info(Integer.MAX_VALUE + "");
//        log.info(DateUtils.format(2147483647000l, "yyyy-MM-dd"));
//
//

        log.info(DateUtils.format(new LocalDate().minusDays(1).toDate(),"E"));
        new AtomicInteger().getAndAdd(1);
    }
}
