package com.xiangshui.server.service;

import com.alibaba.fastjson.JSON;
import com.xiangshui.server.dao.DiscountCouponDao;
import com.xiangshui.server.domain.DiscountCoupon;
import com.xiangshui.util.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Slf4j
@Service
public class DiscountCouponService {

    @Autowired
    DiscountCouponDao discountCouponDao;

    /**
     * 发放优惠券
     * @param uin
     * @param min_price
     * @param cash
     * @param introduce
     * @param amount
     */
    private void giveFullDiscountCoupon(int uin, int min_price, int cash, String introduce, int amount) {
        for (int i = amount; i > 0; i--) {
            Date now = new Date();
            long coupon_id = now.getTime() / 1000;
            coupon_id += new Random().nextInt(100000);
            DiscountCoupon coupon = new DiscountCoupon();
            coupon.setCoupon_id(coupon_id);
            coupon.setCreate_time(now.getTime() / 1000);
            coupon.setMin_price(min_price);
            coupon.setCash(cash);
            coupon.setUin(uin);
            coupon.setStatus(0);
            coupon.setType(1);
            coupon.setValidity_time(now.getTime() / 1000 + 60 * 60 * 24 * 30);
            if (StringUtils.isNotBlank(introduce)) {
                coupon.setIntroduce(introduce);
            } else {
                coupon.setIntroduce("满" + min_price / 100 + "元可用");
            }
            log.info(JSON.toJSONString(coupon));
            discountCouponDao.putItem(coupon);
        }
    }

    /**
     * 发放优惠券
     * @param uin
     * @param charge_id
     */
    public void giveFullDiscountCoupon(int uin, int charge_id) {
        switch (charge_id) {
            case 1://20
                giveFullDiscountCoupon(uin, 1000, 200, null, 4);
                giveFullDiscountCoupon(uin, 2000, 500, null, 2);
                return;
            case 2://50
                giveFullDiscountCoupon(uin, 1000, 200, null, 5);
                giveFullDiscountCoupon(uin, 2000, 500, null, 3);
                giveFullDiscountCoupon(uin, 5000, 1500, null, 1);
                return;
            case 3://100
                giveFullDiscountCoupon(uin, 1000, 200, null, 5);
                giveFullDiscountCoupon(uin, 2000, 500, null, 3);
                giveFullDiscountCoupon(uin, 5000, 1500, null, 1);
                giveFullDiscountCoupon(uin, 9900, 3000, null, 1);
                return;
            case 4://200
                giveFullDiscountCoupon(uin, 1000, 200, null, 5);
                giveFullDiscountCoupon(uin, 2000, 500, null, 3);
                giveFullDiscountCoupon(uin, 5000, 1500, null, 1);
                giveFullDiscountCoupon(uin, 9900, 3000, null, 2);
                return;
            default:
                return;
        }
    }


    public static void main(String[] args) throws Exception {
        SpringUtils.init();
//        SpringUtils.getBean(DiscountCouponService.class).giveFullDiscountCoupon(1654970047, 4);
        SpringUtils.getBean(DiscountCouponService.class).giveFullDiscountCoupon(783202018,1000,200,null,1);

    }


}
