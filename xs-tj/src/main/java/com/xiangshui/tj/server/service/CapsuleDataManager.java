package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.Capsule;
import org.springframework.stereotype.Component;

@Component
public class CapsuleDataManager extends DataManager<Long, Capsule> {
    @Override
    Long getId(Capsule capsule) {
        if (capsule == null) {
            return null;
        }
        return capsule.getCapsule_id();
    }
}
