package com.xiangshui.server.dao;

import com.xiangshui.server.domain.AllVerify;
import org.springframework.stereotype.Component;

@Component
public class AllVerifyDao extends BaseDynamoDao<AllVerify> {
    @Override
    public String getTableName() {
        return "all_verify";
    }
}
