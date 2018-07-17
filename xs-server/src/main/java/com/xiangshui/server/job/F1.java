package com.xiangshui.server.job;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BookingDao;
import com.xiangshui.server.dao.CapsuleDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.service.BookingService;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.spring.SpringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

@Component
public class F1 {


    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    AreaDao areaDao;
    @Autowired
    BookingDao bookingDao;
    @Autowired
    CapsuleDao capsuleDao;


    public void doWork(int year, int month, InputStream inputStream, String sheetName, int final_price, int plusRatio) throws IOException {

        long start = new LocalDate(year, month, 1).toDate().getTime() / 1000;
        long end = new LocalDate(year, month, 1).plusMonths(1).toDate().getTime() / 1000 - 1;

        List<List<String>> data = ExcelUtils.read(inputStream, sheetName);
        data.forEach(strings -> {
            log.debug("{} {}", strings.get(0));
            try {
                int uin = Integer.valueOf(strings.get(0));
                int area_id = Integer.valueOf(strings.get(1));
                UserInfo userInfo = userInfoDao.getItem(new PrimaryKey("uin", uin));
                if (userInfo == null) {
                    return;
                }
                Area area = areaDao.getItem(new PrimaryKey("area_id", area_id));
                if (area == null) {
                    return;
                }

                List<Booking> bookingList = bookingDao.scan(new ScanSpec().withScanFilters(
                        new ScanFilter("uin").eq(uin),
                        new ScanFilter("area_id").eq(area_id),
                        new ScanFilter("create_time").between(start, end),
                        new ScanFilter("f1").eq(1)
                ));
                if ((uin % 100 > plusRatio && bookingList.size() >= 1)
                        || (uin % 100 <= plusRatio && bookingList.size() >= 2)) {
                    return;
                }

                List<Capsule> capsuleList = capsuleDao.scan(new ScanSpec().withScanFilters(new ScanFilter("area_id").eq(area_id)));
                if (capsuleList.size() == 0) {
                    return;
                }
                for (int i = 0; i < capsuleList.size(); i++) {
                    Capsule capsule = capsuleList.get(i);


                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        SpringUtils.init();
        SpringUtils.getBean(F1.class).doWork(2018, 4, new FileInputStream(new File("/Users/whw/Documents/4-6月订单副本.xlsx")), "4月用户候选", 2070, 10);
    }


}
