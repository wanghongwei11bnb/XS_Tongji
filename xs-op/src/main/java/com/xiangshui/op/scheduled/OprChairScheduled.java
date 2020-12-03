package com.xiangshui.op.scheduled;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.BookingDao;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.util.ListUtils;
import com.xiangshui.util.MapOptions;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class OprChairScheduled {

    @Autowired
    CacheScheduled cacheScheduled;

    @Autowired
    BookingDao bookingDao;

    Set<Integer> areaIdSet = new HashSet<>(Arrays.asList(

    ));

    /**
     * 每日凌晨零点调整座椅
     */
//    @Scheduled(cron = "0 0 0 * * ?")
    public void load() {

        List<Booking> bookingList = bookingDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(
                new ScanFilter("status").eq(1)
        ));

        Set<Long> capsuleIdSet = ListUtils.fieldSet(bookingList, Booking::getCapsule_id);

        for (Capsule capsule : cacheScheduled.capsuleList) {
            try {
                if (!capsuleIdSet.contains(capsule.getCapsule_id())) {
                    log.info(Jsoup.connect("https://www.xiangshuispace.com/api/capsule/opr_chair").method(Connection.Method.POST)
                            .ignoreContentType(true).ignoreHttpErrors(true)
                            .header("User-Uin", "100000")
                            .requestBody(new JSONObject()
                                    .fluentPut("capsule_id", capsule.getCapsule_id())
                                    .fluentPut("opr_flag", 2)// 1 躺，2 坐
                                    .toJSONString()).execute().body());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
