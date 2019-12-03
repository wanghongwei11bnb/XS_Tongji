package com.xiangshui.server.job;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.dao.mysql.BookingMysqlDao;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.service.*;
import com.xiangshui.util.spring.SpringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class BookingDataCopy {


    @Autowired
    DiscountCouponDao discountCouponDao;

    @Autowired
    CityService cityService;
    @Autowired
    RedBagDao redBagDao;
    @Autowired
    AreaService areaService;
    @Autowired
    AreaDao areaDao;
    @Autowired
    S3Service s3Service;

    @Autowired
    AreaContractService areaContractService;

    @Autowired
    AreaContractDao areaContractDao;

    @Autowired
    CapsuleService capsuleService;


    @Autowired
    AreaBillDao areaBillDao;

    @Autowired
    OpUserService opUserService;
    @Autowired
    ChargeRecordDao chargeRecordDao;
    @Autowired
    CashInfoDao cashInfoDao;
    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    UserService userService;
    @Autowired
    MonthCardService monthCardService;

    @Autowired
    BookingDao bookingDao;
    @Autowired
    BookingMysqlDao bookingMysqlDao;


    public void test(int year,int month) throws Exception {

        long start = new LocalDate(year, month, 1).toDate().getTime() / 1000;
        long end = new LocalDate(year, month, 1).plusMonths(1).toDate().getTime() / 1000;

        List<Booking> bookingList = bookingDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(
                new ScanFilter("create_time").between(start, end)
        ));

        for (Booking booking : bookingList) {
            bookingMysqlDao.insertSelective(booking, null);
        }


    }

    public static void main(String[] args) throws Exception {
        SpringUtils.init();
        SpringUtils.getBean(BookingDataCopy.class).test(2019,10);
    }

}
