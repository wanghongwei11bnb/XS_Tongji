package com.xiangshui.server.dao;

import com.xiangshui.server.domain.GroupInfo;
import org.springframework.stereotype.Component;

@Component
public class GroupInfoDao extends BaseDynamoDao<GroupInfo> {
    @Override
    public String getTableName() {
        return "group_info";
    }
}
