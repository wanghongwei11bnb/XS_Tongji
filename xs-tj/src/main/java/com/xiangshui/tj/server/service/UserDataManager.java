package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.User;
import org.springframework.stereotype.Component;

@Component
public class UserDataManager extends DataManager<Integer, User> {
    @Override
    Integer getId(User user) {
        if (user == null) {
            return null;
        }
        return user.getUin();
    }
}
