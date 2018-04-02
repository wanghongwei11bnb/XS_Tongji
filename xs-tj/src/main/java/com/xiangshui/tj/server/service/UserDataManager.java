package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.User;
import com.xiangshui.tj.server.dynamedb.DynamoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDataManager extends DataManager<Integer, User> {

    @Autowired
    DynamoDBService dynamoDBService;

    @Override
    Integer getId(User user) {
        if (user == null) {
            return null;
        }
        return user.getUin();
    }


    @Override
    public User getById(Integer id) {
        User user = super.getById(id);
        if (user != null) {
            return null;
        }
        user = dynamoDBService.getUserByUin(id);
        if (user != null) {
            save(user);
            return user;
        }
        return null;
    }
}
