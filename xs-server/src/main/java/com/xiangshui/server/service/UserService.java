package com.xiangshui.server.service;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.xiangshui.server.bean.UserSearch;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.relation.BookingRelation;
import com.xiangshui.server.relation.UserInfoRelation;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.Option;
import com.xiangshui.util.web.result.CodeMsg;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RedisService redisService;

    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    UserWalletDao userWalletDao;
    @Autowired
    UserRegisterDao userRegisterDao;
    @Autowired
    UserFaceDao userFaceDao;
    @Autowired
    WalletRecordDao walletRecordDao;


    public UserInfo getUserInfoByUin(int uin) {
        return userInfoDao.getItem(new PrimaryKey("uin", uin));
    }

    public UserWallet getUserWalletByUin(int uin) {
        return userWalletDao.getItem(new PrimaryKey("uin", uin));
    }

    public UserRegister getUserRegisterByUin(int uin) {
        return userRegisterDao.getItem(new PrimaryKey("uin", uin));
    }


    public UserInfo getUserInfoByPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return null;
        }
        List<UserInfo> userInfoList = userInfoDao.scan(new ScanSpec().withScanFilters(new ScanFilter("phone").eq(phone)));
        if (userInfoList != null && userInfoList.size() > 0) {
            return userInfoList.get(0);
        } else {
            return null;
        }
    }


    public void matchUserInfoForBooking(BookingRelation bookingRelation) {
        if (bookingRelation == null || bookingRelation.getUin() == null) return;
        bookingRelation.set_userInfo(getUserInfoByUin(bookingRelation.getUin()));
    }


    public List<UserInfo> getUserInfoList(Integer[] uins, String[] attributes) {
        return userInfoDao.batchGetItem("uin", uins, attributes);
    }

    public Set<Integer> getUinSet(List<Booking> bookingList) {
        if (bookingList == null) return null;
        Set<Integer> uinSet = new HashSet<Integer>();
        for (Booking booking : bookingList) {
            if (booking != null && booking.getUin() != null) {
                uinSet.add(booking.getUin());
            }
        }
        return uinSet;
    }

    public List<UserInfo> getUserInfoList(List<Booking> bookingList, final String[] attributes) {
        if (bookingList == null) return null;
        final Set<Integer> uinSet = getUinSet(bookingList);
        if (uinSet == null || uinSet.size() == 0) return null;
        return ServiceUtils.division(uinSet.toArray(new Integer[0]), 100, new CallBackForResult<Integer[], List<UserInfo>>() {
            public List<UserInfo> run(Integer[] object) {
                return getUserInfoList(object, attributes);
            }
        }, new Integer[0]);
    }


//    enum BookingType {
//        SLEEP_BOOKING_TYPE     = 0; //扫码睡觉订单
//        DEPOSIT_BOOKING_TYPE   = 1; //押金充值订单
//        CHARGE_BOOKING_TYPE    = 2; //钱包充值订单
//        DEPOSIT_OP_TYPE        = 3; //op押金修改
//        CHARGE_OP_TYPE         = 4; //op钱包修改
//        DEPOSIT_REFUND_WX_TYPE = 5; //押金微信退款
//        DEPOSIT_REFUND_ALI_TYPE= 6; //押金支付宝退款
//
//    }


    public void updateUserBalance(int uin, int disparity, String subject, String op_username) throws Exception {
        UserInfo userInfo = getUserInfoByUin(uin);
        UserWallet userWallet = getUserWalletByUin(uin);
        if (userInfo == null || userWallet == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }

        if (userWallet.getBalance() == null) {
            userWallet.setBalance(0);
        }
        if (userWallet.getBonus() == null) {
            userWallet.setBonus(0);
        }
        if (userWallet.getCharge() == null) {
            userWallet.setCharge(0);
        }
        userWallet.setBalance(userWallet.getBalance() + disparity);
        if (disparity >= 0) {
            userWallet.setBonus(userWallet.getBonus() + disparity);
        } else {
            userWallet.setCharge(userWallet.getCharge() + disparity);
        }
        userWalletDao.updateItem(new PrimaryKey("uin", uin), userWallet, new String[]{"balance", "bonus", "charge"});
        WalletRecord walletRecord = new WalletRecord();
        walletRecord.setOut_trade_no(UUID.randomUUID().toString().replaceAll("-", ""));
        walletRecord.setUin(userInfo.getUin());
        walletRecord.setPhone(userInfo.getPhone());
        walletRecord.setPrice(disparity);
        walletRecord.setType(4);
        walletRecord.setOperator(op_username);
        walletRecord.setSubject(subject);
        walletRecord.setCreate_time(System.currentTimeMillis() / 1000);
        walletRecordDao.putItem(walletRecord);
    }

    /**
     * 修改用户钱包余额
     *
     * @param uin
     * @param amount
     */
    public void updateBalance(int uin, int type, int amount, String op_username) throws Exception {
        UserWallet userWallet = userWalletDao.getItem(new PrimaryKey("uin", uin));
        if (userWallet == null) throw new XiangShuiException(CodeMsg.NO_FOUND);
        if (userWallet.getBalance() == null) userWallet.setBalance(0);
        if (userWallet.getBonus() == null) userWallet.setBonus(0);
        if (userWallet.getCharge() == null) userWallet.setCharge(0);

        WalletRecord walletRecord = new WalletRecord();
        walletRecord.setOut_trade_no(UUID.randomUUID().toString().replaceAll("-", ""));
        walletRecord.setUin(uin);
        walletRecord.setPrice(amount);
        walletRecord.setType(4);
        walletRecord.setOperator(op_username);
        walletRecord.setCreate_time(System.currentTimeMillis() / 1000);

        // 1：活动赠送，2：系统补偿，3：系统扣除
        switch (type) {
            case 1:
                walletRecord.setSubject("活动赠送");
                userWallet.setBonus(userWallet.getBonus() + amount);
                break;
            case 2:
                walletRecord.setSubject("系统补偿");
                userWallet.setBonus(userWallet.getBonus() + amount);
                break;
            case 3:
                walletRecord.setSubject("系统扣除");
                if (userWallet.getCharge() >= amount) {
                    userWallet.setCharge(userWallet.getCharge() - amount);
                } else if (userWallet.getCharge() + userWallet.getBonus() >= amount) {
                    int temp = amount - userWallet.getCharge();
                    userWallet.setCharge(0);
                    userWallet.setBonus(userWallet.getBonus() - temp);
                } else {
                    throw new XiangShuiException("账户余额不够扣除！");
                }
                break;
            default:
                break;
        }
        userWallet.setBalance(userWallet.getCharge() + userWallet.getBonus());
        userWalletDao.updateItem(new PrimaryKey("uin", uin), userWallet, new String[]{"balance", "bonus", "charge"});
        walletRecordDao.putItem(walletRecord);
    }


}
