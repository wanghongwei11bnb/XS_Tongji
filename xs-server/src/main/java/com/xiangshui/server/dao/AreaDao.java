package com.xiangshui.server.dao;

import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.UserWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AreaDao extends BaseDynamoDao<Area> {




    @Override
    public String getTableName() {
        return "area";
    }


}
