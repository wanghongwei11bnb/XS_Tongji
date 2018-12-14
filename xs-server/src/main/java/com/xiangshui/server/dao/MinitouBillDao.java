package com.xiangshui.server.dao;

import com.xiangshui.server.domain.MinitouBill;
import org.springframework.stereotype.Component;

@Component
public class MinitouBillDao extends BaseDynamoDao<MinitouBill> {
    @Override
    public String getTableName() {
        return "minitou_bill";
    }
}
