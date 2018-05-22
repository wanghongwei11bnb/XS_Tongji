package com.xiangshui.server.dao;

import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.DeviceRelation;
import com.xiangshui.util.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DeviceRelationDao extends BaseDynamoDao<DeviceRelation> {

    @Override
    public String getTableName() {
        return "device_relation";
    }

}
