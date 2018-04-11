package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.bean.City;
import com.xiangshui.tj.server.relation.CapsuleRelation;
import com.xiangshui.tj.websocket.WebSocketSessionManager;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RelationService {


    @Autowired
    UserDataManager userDataManager;
    @Autowired
    AreaDataManager areaDataManager;
    @Autowired
    CapsuleDataManager capsuleDataManager;
    @Autowired
    BookingDataManager bookingDataManager;
    @Autowired
    AppraiseDataManager appraiseDataManager;
    @Autowired
    WebSocketSessionManager sessionManager;

    public CapsuleRelation getRelation(Capsule capsule) {
        if (capsule == null) {
            return null;
        }
        try {
            CapsuleRelation capsuleRelation = new CapsuleRelation();
            BeanUtils.copyProperties(capsuleRelation, capsule);
            Area area = areaDataManager.getById(capsule.getArea_id());
            if (area != null) {
                capsuleRelation.setAreaObj(area);
                capsuleRelation.setCityObj(City.cityMap.get(area.getCity()));
            }
            return capsuleRelation;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
