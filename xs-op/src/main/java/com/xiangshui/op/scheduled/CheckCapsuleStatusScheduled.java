package com.xiangshui.op.scheduled;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.BookingDao;
import com.xiangshui.server.dao.CapsuleDao;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.util.ListUtils;
import com.xiangshui.util.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class CheckCapsuleStatusScheduled {

    @Autowired
    CapsuleDao capsuleDao;
    @Autowired
    BookingDao bookingDao;

    /**
     * 每天凌晨6点系统自动检查设备状态，如有问题更正。
     */
    @Scheduled(cron = "0 0 6 * * ?")
    public Set<Capsule> checkAndUpdate() {
        Set<Capsule> capsuleSet = this.check();
        capsuleSet.forEach(capsule -> {
            try {
                capsule.setStatus(1);
                capsuleDao.updateItem(new PrimaryKey("capsule_id", capsule.getCapsule_id()), capsule, new String[]{
                        "status"
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return capsuleSet;
    }

    public Set<Capsule> check() {
        Set<Capsule> result = new HashSet<>();
        Set<Long> capsuleIdSet = ListUtils.fieldSet(bookingDao.scan(new ScanSpec().withScanFilters(new ScanFilter("status").eq(1))), Booking::getCapsule_id);
        capsuleDao.scan(new ScanSpec().withScanFilters(new ScanFilter("status").eq(2))).forEach(capsule -> {
            if (!capsuleIdSet.contains(capsule.getCapsule_id())) {
                result.add(capsule);
            }
        });
        return result;
    }

    public static void main(String[] args) {
        SpringUtils.init();
        SpringUtils.getBean(CheckCapsuleStatusScheduled.class).check();
    }
}
