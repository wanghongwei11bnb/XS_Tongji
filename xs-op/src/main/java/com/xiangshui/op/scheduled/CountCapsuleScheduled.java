package com.xiangshui.op.scheduled;

import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.service.AreaContractService;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.BookingService;
import com.xiangshui.server.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CountCapsuleScheduled {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Autowired
    AreaService areaService;

    @Autowired
    AreaBillDao areaBillDao;
    @Autowired
    AreaContractDao areaContractDao;
    @Autowired
    AreaContractService areaContractService;

    @Autowired
    BookingService bookingService;
    @Autowired
    BookingDao bookingDao;

    @Autowired
    UserService userService;

    @Autowired
    GroupInfoDao groupInfoDao;

    @Autowired
    ChargeRecordDao chargeRecordDao;
    @Autowired
    CapsuleDao capsuleDao;

    public volatile Map<Integer, Integer> countGroupArea = new HashMap<>();
    public volatile Map<Integer, Long> areaCreateTimeMap = new HashMap<>();

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void task() {
        update();
    }

    public void update() {
        Map<Integer, Integer> countGroupArea = new HashMap<>();
        Map<Integer, Long> areaCreateTimeMap = new HashMap<>();
        List<Capsule> capsuleList = capsuleDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE));
        capsuleList.forEach(capsule -> {
            if (capsule == null || capsule.getArea_id() == null) {
                return;
            }
            if (capsule.getCreate_time() != null) {
                if (areaCreateTimeMap.containsKey(capsule.getArea_id())) {
                    if (capsule.getCreate_time() < areaCreateTimeMap.get(capsule.getArea_id())) {
                        areaCreateTimeMap.put(capsule.getArea_id(), capsule.getCreate_time());
                    }
                } else {
                    areaCreateTimeMap.put(capsule.getArea_id(), capsule.getCreate_time());
                }
            }
            if (new Integer(1).equals(capsule.getIs_downline())) {
                return;
            }
            if (StringUtils.isBlank(capsule.getDevice_id()) || capsule.getDevice_id().trim().length() != 24) {
                return;
            }
            if (countGroupArea.containsKey(capsule.getArea_id())) {
                countGroupArea.put(capsule.getArea_id(), countGroupArea.get(capsule.getArea_id()) + 1);
            } else {
                countGroupArea.put(capsule.getArea_id(), 1);
            }
        });
        this.countGroupArea = countGroupArea;
        this.areaCreateTimeMap = areaCreateTimeMap;
    }


}
