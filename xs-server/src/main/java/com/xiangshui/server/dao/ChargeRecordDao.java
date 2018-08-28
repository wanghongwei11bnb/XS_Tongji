package com.xiangshui.server.dao;

import com.xiangshui.server.domain.ChargeRecord;
import org.springframework.stereotype.Component;

@Component
public class ChargeRecordDao extends BaseDynamoDao<ChargeRecord> {
    @Override
    public String getTableName() {
        return "charge_record";
    }
}
