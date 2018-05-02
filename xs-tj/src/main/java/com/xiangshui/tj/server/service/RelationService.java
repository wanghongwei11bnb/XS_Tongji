package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.AreaTj;
import com.xiangshui.tj.server.bean.CapsuleTj;
import com.xiangshui.tj.server.bean.CityTj;
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

    public CapsuleRelation getRelation(CapsuleTj capsule) {
        if (capsule == null) {
            return null;
        }
        try {
            CapsuleRelation capsuleRelation = new CapsuleRelation();
            BeanUtils.copyProperties(capsuleRelation, capsule);
            AreaTj area = areaDataManager.getById(capsule.getArea_id());
            if (area != null) {
                capsuleRelation.setAreaObj(area);
                capsuleRelation.setCityObj(CityTj.cityMap.get(area.getCity()));
            }
            return capsuleRelation;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
