package com.xiangshui.server.dao;

import com.xiangshui.server.domain.Activity;
import com.xiangshui.server.domain.ActivityPush;
import org.springframework.stereotype.Component;

@Component
public class ActivityPushDao extends BaseDynamoDao<ActivityPush> {


    @Override
    public String getTableName() {
        return "activity_push";
    }


}
