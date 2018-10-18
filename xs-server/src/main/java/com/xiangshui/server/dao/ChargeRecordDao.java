package com.xiangshui.server.dao;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.domain.ChargeRecord;
import com.xiangshui.util.spring.SpringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChargeRecordDao extends BaseDynamoDao<ChargeRecord> {
    @Override
    public String getTableName() {
        return "charge_record";
    }


    public Map<Long, ChargeRecord> mapperBillBookingIdMap(List<ChargeRecord> chargeRecordList) {
        Map<Long, ChargeRecord> chargeRecordMap = new HashMap<>();
        if (chargeRecordList != null && chargeRecordList.size() > 0) {
            chargeRecordList.forEach(chargeRecord -> {
                if (chargeRecord != null && chargeRecord.getBill_area_id() != null && chargeRecord.getBill_booking_id() != null) {
                    chargeRecordMap.put(chargeRecord.getBill_booking_id(), chargeRecord);
                }
            });
        }
        return chargeRecordMap;
    }

    public static void main(String[] args) {
        SpringUtils.init();
        System.out.println(JSON.toJSONString(SpringUtils.getBean(ChargeRecordDao.class).scan(new ScanSpec().withScanFilters(
                new ScanFilter("subject").in("享+-月卡充值", "享+-钱包充值"),
                new ScanFilter("uin").eq(820332557)
        ))));
    }
}
