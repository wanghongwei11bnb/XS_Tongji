package com.xiangshui.server.dao;

import com.xiangshui.server.domain.AreaBill;
import org.springframework.stereotype.Component;

@Component
public class AreaBillDao extends BaseDynamoDao<AreaBill> {
    @Override
    public String getTableName() {
        return "area_bill";
    }
}
