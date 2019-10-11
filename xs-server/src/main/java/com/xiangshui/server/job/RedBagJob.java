package com.xiangshui.server.job;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.RedBag;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.service.*;
import com.xiangshui.util.spring.SpringUtils;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class RedBagJob {


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


    public void test(String phone) throws Exception {

        if (StringUtil.isBlank(phone)) return;

        UserInfo userInfo = userService.getUserInfoByPhone(phone);

        if (userInfo == null) {
            throw new Exception("userInfo is null");
        }
        RedBag redBag = new RedBag()
                .setId(System.currentTimeMillis() / 1000)
                .setCreate_time(System.currentTimeMillis() / 1000)
                .setUin(userInfo.getUin())
                .setType(2)
                .setPrice_title("雨露均沾奖")

                .setMin_price(2000).setCash(500)

//                .setMin_price(5000).setCash(1500)

//                .setMin_price(9900).setCash(3000)

                ;

        redBagDao.putItem(redBag);
        System.out.println(JSON.toJSONString(redBag));
    }


    public static void main(String[] args) throws Exception {

        SpringUtils.init();
        RedBagJob redBagJob = SpringUtils.getBean(RedBagJob.class);
//        redBagJob.test("15524986545");
//        redBagJob.test("13686003589");
//        redBagJob.test("13501231224");
//        redBagJob.test("13141329896");
//        redBagJob.test("15283349076");
//        redBagJob.test("18100807500");


    }
}
