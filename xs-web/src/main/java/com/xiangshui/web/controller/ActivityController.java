package com.xiangshui.web.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.server.crud.Conditions;
import com.xiangshui.server.crud.Example;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.domain.mysql.PrizeQuota;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.util.spring.SpringUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Slf4j
@Controller
public class ActivityController extends BaseController {
    @GetMapping("/jpi/swiperItem/search")
    @ResponseBody
    public Result swiper_item_search() {
        Example example = new Example().setOrderByClause("sort_num desc,create_time desc,id desc");
        example.getConditions().eq("status", 1).eq("app", "ali");
        List<SwiperItem> swiperItemList = swiperItemDao.selectByExample(example);
        return new Result(CodeMsg.SUCCESS).putData("swiperItemList", swiperItemList);
    }


    @PostMapping("/jpi/booking/{booking_id:\\d+}/checkPrizeQuota")
    @ResponseBody
    public Result checkPrizeQuota(@PathVariable("booking_id") Long booking_id) throws Exception {
        Date now = new Date();
        int price = 1000;
        if (now.getTime() > PrizeQuota.ActivityEnum.act_201912.expire_time.toDate().getTime()) {
            log.info("活动已结束");
            return new Result(CodeMsg.SUCCESS);
        }

        Booking booking = bookingDao.getItem(new PrimaryKey("booking_id", booking_id));
        if (booking == null) return new Result(CodeMsg.SUCCESS);

        Integer area_id = booking.getArea_id();

        //指定场地才可参加活动
        if (!new HashSet<>(Arrays.asList(new Integer[]{
                2601018,
                2601023,
                2601002,
                2601012,
                2601004,
                2601016,
                2601022,
                2601006,
                2601003,
                2601005,
                2601019,
                2601014,
                2601008,
                2601013,
                2601020,
                2601021,
                3403020,
                3403026,
                3403027,
                3403005,
                2501039,
                2501001,
                2501016,
                2501018,
                1100010,
                1200003,
                1100004,
                1401001,
                1100108,
                1100127,
                1100045,
                1200005,
                1200004,
                1100103,
                3001001,
                2201007,
                3100004,
                3100006,
                3100018,
                3100029,
                3100052,
        })).contains(area_id)) {
            log.info("该场地不参与活动 {}", area_id);
            return new Result(CodeMsg.SUCCESS);
        }

        if (new Integer(1).equals(booking.getStatus())) throw new XiangShuiException(-1, "订单未结束");

        int uin = booking.getUin();
        UserInfo userInfo = userInfoDao.getItem(new PrimaryKey("uin", uin));
        if (userInfo == null) throw new XiangShuiException(CodeMsg.NO_FOUND);

        UserWallet userWallet = userWalletDao.getItem(new PrimaryKey("uin", uin));
        if (userWallet == null) throw new XiangShuiException(CodeMsg.NO_FOUND);


        PrizeQuota quota = prizeQuotaDao.selectOne(new Conditions()
                        .eq("activity", PrizeQuota.ActivityEnum.act_201912.name())
                        .eq("uin", uin)
                , null, null);

        if (quota != null) {
            log.info("已经领取过了");
            return new Result(CodeMsg.SUCCESS);
        }

        quota = new PrizeQuota();
        quota.setCreate_time(now.getTime() / 1000);
        quota.setActivity(PrizeQuota.ActivityEnum.act_201912.name());
        quota.setUin(booking.getUin());
        quota.setBooking_id(booking_id);

        quota.setPrize_type(PrizeQuota.PrizeTypeEnum.balance.name());
        quota.setPrice(price);

        quota.setReceive_status(1);

        prizeQuotaDao.insertSelective(quota, null);

        Integer balance = userWallet.getBalance();
        if (balance == null) balance = 0;
        Integer bonus = userWallet.getBonus();
        if (bonus == null) bonus = 0;

        balance += 1000;
        bonus += 1000;
        userWallet.setBalance(balance);
        userWallet.setBonus(bonus);
        userWalletDao.updateItem(new PrimaryKey("uin", uin), userWallet, new String[]{
                "balance",
                "bonus",
        });

        WalletRecord record = new WalletRecord().setOut_trade_no(UUID.randomUUID().toString()).setUin(uin).setPhone(userInfo.getPhone())
                .setPrice(1000).setSubject("活动赠送").setCreate_time(now.getTime() / 1000);
        walletRecordDao.putItem(record);

        return new Result(CodeMsg.SUCCESS).putData("quota", quota);

    }


    public static void main(String[] args) throws Exception {
        SpringUtils.init();
        SpringUtils.getBean(ActivityController.class).checkPrizeQuota(2003313121l);
    }


}
