package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.CapsuleTj;
import org.springframework.stereotype.Component;

@Component
public class CapsuleDataManager extends DataManager<Long, CapsuleTj> {
    @Override
    Long getId(CapsuleTj capsule) {
        if (capsule == null) {
            return null;
        }
        return capsule.getCapsule_id();
    }


}
