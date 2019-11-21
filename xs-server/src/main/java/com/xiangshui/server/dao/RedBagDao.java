package com.xiangshui.server.dao;

import com.xiangshui.server.domain.RedBag;
import org.springframework.stereotype.Component;

@Component
public class RedBagDao extends BaseDynamoDao<RedBag>{
    @Override
    public String getTableName() {
        return "red_bag";
    }
}
