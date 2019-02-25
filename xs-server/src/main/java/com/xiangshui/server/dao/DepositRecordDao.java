package com.xiangshui.server.dao;

import com.xiangshui.server.domain.AllVerify;
import com.xiangshui.server.domain.DepositRecord;
import org.springframework.stereotype.Component;

@Component
public class DepositRecordDao extends BaseDynamoDao<DepositRecord> {
    @Override
    public String getTableName() {
        return "deposit_record";
    }
}
