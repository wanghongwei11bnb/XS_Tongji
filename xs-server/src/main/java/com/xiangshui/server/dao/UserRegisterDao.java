package com.xiangshui.server.dao;

import com.xiangshui.server.domain.UserRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserRegisterDao extends BaseDynamoDao<UserRegister> {




    @Override
    public String getTableName() {
        return "user_register";
    }


}
