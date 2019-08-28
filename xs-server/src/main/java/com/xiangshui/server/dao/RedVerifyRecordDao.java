package com.xiangshui.server.dao;

import com.xiangshui.server.domain.RedVerifyRecord;
import org.springframework.stereotype.Component;

@Component
public class RedVerifyRecordDao extends BaseDynamoDao<RedVerifyRecord> {
    @Override
    public String getTableName() {
        return "red_verify_record";
    }
}
