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

    /**
     * 清除用户钱包余额
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


    public void test() throws Exception {
        cleanWallet("13480943735");
        cleanWallet("18551509366");
    }

    public static void main(String[] args) throws Exception {
        SpringUtils.init();
        SpringUtils.getBean(UserJobs.class).test();
    }

}
