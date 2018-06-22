package com.xiangshui.server.dao;

import com.xiangshui.server.domain.AreaContract;
import org.springframework.stereotype.Component;

@Component
public class AreaContractDao extends BaseDynamoDao<AreaContract> {
    @Override
    public String getTableName() {
        return "area_contract";
    }
}
