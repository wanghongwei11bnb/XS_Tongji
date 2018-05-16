package com.xiangshui.server.service;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.server.dao.CapsuleDao;
import com.xiangshui.server.domain.Capsule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CapsuleService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CapsuleDao capsuleDao;
    @Autowired
    AreaService areaService;


    public Capsule getCapsuleById(long capsule_id) {
        return capsuleDao.getItem(new PrimaryKey("capsule_id", capsule_id));
    }


    public List<Capsule> search(Capsule criteria, String[] attributes) {
        if (criteria == null) {
            return capsuleDao.scan();
        }
        if (criteria.getCapsule_id() != null) {
            Capsule capsule = getCapsuleById(criteria.getCapsule_id());
            List<Capsule> capsuleList = new ArrayList<Capsule>(1);
            if (capsule != null) {
                capsuleList.add(capsule);
            }
            return capsuleList;
        }


        return null;
    }


}
