package com.xiangshui.server.job;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.dao.UserRegisterDao;
import com.xiangshui.server.dao.UserWalletDao;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.domain.UserWallet;
import com.xiangshui.server.service.DiscountCouponService;
import com.xiangshui.util.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UserJobs {

    @Autowired
    UserWalletDao userWalletDao;
    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    UserRegisterDao userRegisterDao;

    @Autowired
    DiscountCouponService discountCouponService;


    public UserInfo getUserInfo(int uin) {
        return userInfoDao.getItem(new PrimaryKey("uin", uin));
    }

    public UserInfo getUserInfo(String phone) {
        List<UserInfo> userInfoList = userInfoDao.scan(new ScanSpec().withScanFilters(new ScanFilter("phone").eq(phone)));
        if (userInfoList.size() > 0) {
            return userInfoList.get(0);
        }
        return null;
    }


    public UserWallet getUserWallet(int uin) {
        return userWalletDao.getItem(new PrimaryKey("uin", uin));
    }

    public UserWallet getUserWallet(String phone) {
        UserInfo userInfo = getUserInfo(phone);
        if (userInfo == null) return null;
        return getUserWallet(userInfo.getUin());
    }


    /**
     * 清除用户钱包余额
     *
     * @param phone
     * @throws Exception
     */
    public void cleanWallet(String phone) throws Exception {
        UserInfo userInfo = getUserInfo(phone);
        if (userInfo == null) {
            log.error("userInfo is null " + phone);
            return;
        }
        cleanWallet(userInfo.getUin());
    }

    /**
     * 清除用户钱包余额
     *
     * @param uin
     * @throws Exception
     */
    public void cleanWallet(int uin) throws Exception {
        UserWallet userWallet = userWalletDao.getItem(new PrimaryKey("uin", uin));
        if (userWallet == null) {
            log.error("userWallet is null " + uin);
            return;
        }
        userWallet.setBonus(0);
        userWallet.setCharge(0);
        userWallet.setBalance(0);
        log.info("清除用户钱包 " + JSON.toJSONString(userWallet));
        userWalletDao.updateItem(new PrimaryKey("uin", uin), userWallet, new String[]{
                "bonus",
                "charge",
                "balance",
        });
    }


    /**
     * 修改用户钱包余额
     *
     * @throws Exception
     */
    public void changeWallet(UserWallet userWallet, int price, String subject) throws Exception {
        if (userWallet == null) {
            log.error("userWallet is null");
            return;
        }

        log.info(JSON.toJSONString(userWallet));

        int charge = userWallet.getCharge() != null ? userWallet.getCharge() : 0;
        int bonus = userWallet.getBonus() != null ? userWallet.getBonus() : 0;
        if (price > 0) {
            bonus += price;
        } else if (price < 0) {
            charge += price;
            if (charge < 0) {
                bonus += charge;
                charge = 0;
            }
        }
        int balance = charge + bonus;
        userWallet.setBalance(balance);
        userWallet.setCharge(charge);
        userWallet.setBonus(bonus);

        log.info(JSON.toJSONString(userWallet));


        userWalletDao.updateItem(new PrimaryKey("uin", userWallet.getUin()), userWallet, new String[]{
                "balance",
                "charge",
                "bonus",
        });

    }


    /**
     * 清除用户押金
     *
     * @throws Exception
     */
    public void cleanDeposit(int uin) throws Exception {
        UserWallet userWallet = userWalletDao.getItem(new PrimaryKey("uin", uin));
        if (userWallet == null) {
            log.error("userWallet is null " + uin);
            return;
        }
        userWallet.setDeposit(0);
        log.info("清除用户押金 " + JSON.toJSONString(userWallet));
        userWalletDao.updateItem(new PrimaryKey("uin", uin), userWallet, new String[]{
                "deposit",
        });
    }


    /**
     * 清除用户押金
     *
     * @param phone
     * @throws Exception
     */
    public void cleanDeposit(String phone) throws Exception {
        UserInfo userInfo = getUserInfo(phone);
        if (userInfo == null) {
            log.error("userInfo is null " + phone);
            return;
        }
        cleanDeposit(userInfo.getUin());
    }


    public void test() throws Exception {
//        cleanDeposit("91563241580");

//        changeWallet(getUserWallet("17327873623"), -810, "账户扣除");

//        UserInfo userInfo = getUserInfo("13552395202");
//        log.info(JSON.toJSONString(userInfo));
//        discountCouponService.giveFullDiscountCoupon(userInfo.getUin(), 1);
//        discountCouponService.giveFullDiscountCoupon(userInfo.getUin(), 4);




//        cleanDeposit("18522268105");
//        cleanDeposit("13702022496");
//        cleanDeposit("15822135546");
//        cleanDeposit("18611502193");
//        cleanDeposit(235464285);
//        cleanDeposit("13001088142");
        cleanDeposit("15711051513");



    }

    public static void main(String[] args) throws Exception {
        SpringUtils.init();
        SpringUtils.getBean(UserJobs.class).test();

        log.info("finish");
    }

}
