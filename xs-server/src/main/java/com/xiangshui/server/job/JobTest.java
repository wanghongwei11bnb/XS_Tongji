package com.xiangshui.server.job;

import com.alibaba.fastjson.JSON;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.service.*;
import com.xiangshui.util.spring.SpringUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class JobTest {


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


    public void test() throws IOException {

        FileUtils.writeStringToFile(new File("/Users/whw/Downloads/test.txt"),JSON.toJSONString(areaDao.scan()));

    }

    public static void main(String[] args) throws IOException {
        SpringUtils.init();
        SpringUtils.getBean(JobTest.class).test();
    }
}
