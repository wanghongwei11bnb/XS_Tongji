package com.xiangshui.server.dao;

import com.xiangshui.server.domain.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserInfoDao extends BaseDynamoDao<UserInfo> {


    private static final Logger log = LoggerFactory.getLogger(UserInfoDao.class);


    @Override
    public String getTableName() {
        return "user_info";
    }


}
