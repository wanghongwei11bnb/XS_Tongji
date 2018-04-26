package com.xiangshui.server.dao;

import com.xiangshui.server.domain.UserFace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserFaceDao extends BaseDynamoDao<UserFace> {

    @Override
    public String getTableName() {
        return "user_face";
    }
}
