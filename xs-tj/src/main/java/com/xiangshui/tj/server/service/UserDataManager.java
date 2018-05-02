package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.UserTj;
import com.xiangshui.tj.server.dynamedb.DynamoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDataManager extends DataManager<Integer, UserTj> {

    @Autowired
    DynamoDBService dynamoDBService;

    @Override
    Integer getId(UserTj user) {
        if (user == null) {
            return null;
        }
        return user.getUin();
    }


    @Override
    public UserTj getById(Integer id) {
        UserTj user = super.getById(id);
        if (user == null) {
            user = dynamoDBService.getUserByUin(id);
            if (user != null) {
                save(user);
            }
        }
        return user;
    }
}
