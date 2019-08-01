package com.xiangshui.server.dao;

import com.xiangshui.server.domain.DiscountCoupon;
import org.springframework.stereotype.Component;

@Component
public class DiscountCouponDao extends BaseDynamoDao<DiscountCoupon> {

    @Override
    public String getTableName() {
        return "discount_coupon";
    }
}
