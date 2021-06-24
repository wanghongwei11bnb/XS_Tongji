package com.xiangshui.server.dao;

import com.xiangshui.server.domain.Activity;
import org.springframework.stereotype.Component;

@Component
public class ActivityDao extends BaseDynamoDao<Activity> {


    @Override
    public String getTableName() {
        return "activity";
    }


}
