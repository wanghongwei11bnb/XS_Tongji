package com.xiangshui.server.dao;

import com.xiangshui.server.domain.Appraise;

public class AppraiseDao extends BaseDynamoDao<Appraise> {
    public String getTableName() {
        return "booking_appraise";
    }
}
