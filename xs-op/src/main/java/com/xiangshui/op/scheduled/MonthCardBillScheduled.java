package com.xiangshui.op.scheduled;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.constant.AreaContractStatusOption;
import com.xiangshui.server.constant.BookingStatusOption;
import com.xiangshui.server.constant.PayTypeOption;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.service.AreaContractService;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.BookingService;
import com.xiangshui.server.service.UserService;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.Option;
import com.xiangshui.util.spring.SpringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Component
public class MonthCardBillScheduled {

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
    ChargeRecordDao chargeRecordDao;
    @Autowired
    GroupInfoDao groupInfoDao;


    /**
     * 每月1日凌晨1点匹配上月月卡与订单
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    public void makeMonthCardBillTask() {
        LocalDate localDate = new LocalDate().plusMonths(-1);
        makeMonthCardBillTask(localDate.getYear(), localDate.getMonthOfYear());
    }

    public void makeMonthCardBillTask(int year, int month) {
        log.debug("makeMonthCardBillTask({},{}):start", year, month);
        LocalDate startDate = new LocalDate(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).plusDays(-1);
        long create_time_start = startDate.toDate().getTime() / 1000;
        long create_time_end = endDate.plusDays(1).toDate().getTime() / 1000;
        List<Booking> bookingList = bookingDao.scan(new ScanSpec().withScanFilters(
                new ScanFilter("create_time").between(create_time_start, create_time_end),
                new ScanFilter("f1").notExist()
        ));
//        List<Booking> bookingListForYongyou = bookingDao.scan(new ScanSpec().withScanFilters(
//                new ScanFilter("create_time").between(create_time_start, create_time_end),
//                new ScanFilter("area_id").eq(1100117)
//        ).withMaxResultSize(100000));
        List<ChargeRecord> chargeRecordList = chargeRecordDao.scan(new ScanSpec().withScanFilters(
                new ScanFilter("create_time").between(create_time_start, create_time_end),
                new ScanFilter("subject").in("享+-月卡充值", "享+-季卡充值"),
                new ScanFilter("status").eq(1)
        ).withMaxResultSize(100000));
        chargeRecordList.forEach(chargeRecord -> {
            try {
                if (!dealBillBookingId(chargeRecord, bookingList)) {
//                    dealBillBookingId(chargeRecord, bookingListForYongyou);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //以下是处理拼团的逻辑，现在已下线
//        List<GroupInfo> groupInfoList = groupInfoDao.scan(new ScanSpec().withScanFilters(
//                new ScanFilter("group_status").eq(2),
//                new ScanFilter("create_time").between(create_time_start, create_time_end)
//        ));
//        groupInfoList.forEach(groupInfo -> {
//            List<String> out_trade_no_list = groupInfo.getOut_trade_no_list();
//            if (out_trade_no_list != null) {
//                out_trade_no_list.forEach(out_trade_no -> {
//                    if (StringUtils.isNotBlank(out_trade_no)) {
//                        ChargeRecord chargeRecord = chargeRecordDao.getItem(new PrimaryKey("out_trade_no", out_trade_no));
//                        try {
//                            dealBillBookingId(chargeRecord, bookingList);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
        log.debug("makeMonthCardBillTask({},{}):finish", year, month);
    }

    public boolean dealBillBookingId(ChargeRecord chargeRecord, List<Booking> bookingList) throws Exception {
        boolean success = false;
        if (chargeRecord == null || chargeRecord.getUin() == null) {
            return success;
        }
        List<Booking> activeBookingList = new ArrayList<>();
        if (bookingList != null) {
            bookingList.forEach(booking -> {
                if (chargeRecord.getUin().equals(booking.getUin())) {
                    activeBookingList.add(booking);
                }
            });
        }

        if (activeBookingList.size() > 0) {
            activeBookingList.sort((o1, o2) -> (int) (o1.getCreate_time() - o2.getCreate_time()));
            Booking booking = activeBookingList.get(0);
            chargeRecord.setBill_booking_id(booking.getBooking_id());
            chargeRecord.setBill_area_id(booking.getArea_id());
            success = true;
        } else {
            chargeRecord.setBill_area_id(null);
            chargeRecord.setBill_booking_id(null);
        }
        log.debug("dealBillBookingId:{}", chargeRecord.getOut_trade_no());
        chargeRecordDao.updateItem(new PrimaryKey("out_trade_no", chargeRecord.getOut_trade_no()), chargeRecord, new String[]{
                "bill_booking_id",
                "bill_area_id",
        });
        return success;
    }

    public static void main(String[] args) {
        SpringUtils.init();
        SpringUtils.getBean(MonthCardBillScheduled.class).makeMonthCardBillTask(2019, 7);
    }
}
