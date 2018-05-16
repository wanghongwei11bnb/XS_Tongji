package com.xiangshui.server.service;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
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


    public Capsule getCapsuleById(long capsule_id, String[] attributes) {
        if (attributes == null || attributes.length == 0) {
            return capsuleDao.getItem(new PrimaryKey("capsule_id", capsule_id));
        } else {
            return capsuleDao.getItem(new GetItemSpec().withPrimaryKey(new PrimaryKey("capsule_id", capsule_id)).withAttributesToGet(attributes));
        }

    }

    public Capsule getCapsuleById(long capsule_id) {
        return getCapsuleById(capsule_id, null);
    }


    public List<Capsule> search(Capsule criteria, String[] attributes) throws NoSuchFieldException, IllegalAccessException {
        if (criteria == null) {
            if (attributes == null || attributes.length == 0) {
                return capsuleDao.scan();
            } else {
                return capsuleDao.scan(new ScanSpec().withAttributesToGet(attributes));
            }
        }
        if (criteria.getCapsule_id() != null) {
            Capsule capsule = getCapsuleById(criteria.getCapsule_id(), attributes);
            List<Capsule> capsuleList = new ArrayList<Capsule>(1);
            if (capsule != null) {
                capsuleList.add(capsule);
            }
            return capsuleList;
        }


        List<ScanFilter> filterList = capsuleDao.makeScanFilterList(criteria, new String[]{
                "area_id",
                "status",
                "type",
                "status",
                "device_id",
                "is_downline",
        });
        ScanSpec scanSpec = new ScanSpec();
        if (filterList != null && filterList.size() > 0) {
            scanSpec.withScanFilters(filterList.toArray(new ScanFilter[0]));
        }
        if (attributes != null && attributes.length > 0) {
            scanSpec.withAttributesToGet(attributes);
        }
        return capsuleDao.scan(scanSpec);
    }





}
