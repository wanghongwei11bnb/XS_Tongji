package com.xiangshui.op.scheduled;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.bean.AreaBillResult;
import com.xiangshui.server.constant.AreaContractStatusOption;
import com.xiangshui.server.constant.AreaStatusOption;
import com.xiangshui.server.constant.BookingStatusOption;
import com.xiangshui.server.constant.PayTypeOption;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.domain.fragment.RangeRatio;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.service.*;
import com.xiangshui.util.*;
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

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Component
public class AreaBillScheduled implements InitializingBean {

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
    UserInfoDao userInfoDao;

    @Autowired
    ChargeRecordDao chargeRecordDao;

    public Set<Integer> testUinSet = new HashSet<>();
    public Set<String> testPhoneSet = new HashSet<>();


    @Scheduled(cron = "0 0 2 1,5,10,15 * ?")
    public void makeBill() {
        LocalDate localDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        areaContractDao.scan(new ScanSpec().withScanFilters(
                new ScanFilter("status").eq(AreaContractStatusOption.adopt.value)
        ), areaContract -> {
            if (areaContract == null) {
                return;
            }
            if (areaContract.getStatus() == null || !areaContract.getStatus().equals(AreaContractStatusOption.adopt.value)) {
                return;
            }
            try {
                AreaBillResult areaBillResult = reckonAreaBill(areaContract.getArea_id(), localDate.toDate().getTime() / 1000, localDate.plusMonths(1).toDate().getTime() / 1000, false);
                upsetAreaBill(areaBillResult, localDate.getYear(), localDate.getMonthOfYear());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public AreaBillResult reckonAreaBill(int area_id, long time_start, long time_end, boolean skipContract) {
        Date now = new Date();
        AreaBillResult areaBillResult = new AreaBillResult();
        AreaContract areaContract = areaContractService.getByAreaId(area_id);
        areaBillResult.setAreaContract(areaContract);
        if (!skipContract) {

            if (areaContract == null) {
                throw new XiangShuiException("没匹配到场地合同");
            }
            if (!Integer.valueOf(AreaContractStatusOption.adopt.value).equals(areaContract.getStatus())) {
                throw new XiangShuiException("审核未通过");
            }


//            if (areaContract.getAccount_ratio() == null) {
//                throw new XiangShuiException("该场地没有设置分账比例");
//            }
//            if (!(0 < areaContract.getAccount_ratio() && areaContract.getAccount_ratio() <= 100)) {
//                throw new XiangShuiException("该场地分账比例设置有误");
//            }


        }

        List<ChargeRecord> chargeRecordList = chargeRecordDao.scan(new ScanSpec().withScanFilters(
                new ScanFilter("create_time").between(time_start - (60 * 60 * 24 * 31), time_end + (60 * 60 * 24 * 31)),
                new ScanFilter("subject").in(new String[]{"享+-月卡充值", "享+-季卡充值"}),
                new ScanFilter("bill_area_id").eq(area_id),
                new ScanFilter("bill_booking_id").exists()
        ));
        Map<Long, ChargeRecord> chargeRecordMap = new HashMap<>();
        if (chargeRecordList != null && chargeRecordList.size() > 0) {
            chargeRecordList.forEach(chargeRecord -> {
                if (chargeRecord != null && chargeRecord.getBill_area_id() != null && chargeRecord.getBill_booking_id() != null) {
                    chargeRecordMap.put(chargeRecord.getBill_booking_id(), chargeRecord);
                }
            });
        }
        List<Booking> bookingList = bookingDao.scan(new ScanSpec().withScanFilters(
                new ScanFilter("area_id").eq(area_id),
                new ScanFilter("status").eq(BookingStatusOption.pay.value),
                new ScanFilter(Integer.valueOf(DateUtils.format(time_start * 1000, "yyyyDD")) > 201807 ? "create_time" : "update_time").between(time_start, time_end)
        ).withMaxResultSize(Integer.MAX_VALUE));


        List<Booking> activeBookingList = new ArrayList<>();

        if (bookingList != null && bookingList.size() > 0) {
            for (Booking booking : bookingList) {
                if (testUinSet.contains(booking.getUin())) {
                    continue;
                }
                if ((booking.getFinal_price() == null || booking.getFinal_price() == 0)
                        && !chargeRecordMap.containsKey(booking.getBooking_id())) {
                    continue;
                }
                if ((booking.getFrom_charge() != null ? booking.getFrom_charge() : 0) + (booking.getUse_pay() != null ? booking.getUse_pay() : 0) == 0
                        && !chargeRecordMap.containsKey(booking.getBooking_id())) {
                    continue;
                }
                activeBookingList.add(booking);
            }
        }
        int booking_count = 0;
        int final_price = 0;
        int charge_price = 0;
        int pay_price = 0;
        int month_card_price = 0;
        for (Booking booking : activeBookingList) {
            final_price += booking.getFinal_price();
            if (booking.getFrom_charge() != null && booking.getFrom_charge() > 0) {
                charge_price += booking.getFrom_charge();
            }
            if (booking.getUse_pay() != null && booking.getUse_pay() > 0) {
                pay_price += booking.getUse_pay();
            }
            booking_count++;
            if (chargeRecordMap.containsKey(booking.getBooking_id()) && chargeRecordMap.get(booking.getBooking_id()) != null && chargeRecordMap.get(booking.getBooking_id()).getPrice() != null) {
                ChargeRecord chargeRecord = chargeRecordMap.get(booking.getBooking_id());
                if (chargeRecord != null && chargeRecord.getPrice() != null) {
                    month_card_price += chargeRecord.getPrice();
                }
            }
        }


        int count_price = charge_price + pay_price + (new LocalDate(time_start * 1000).withDayOfMonth(1).toDate().getTime() > new LocalDate(2018, 7, 1).toDate().getTime() ? month_card_price : 0);
        Integer account_ratio = areaContractService.checkAccountRatio(areaBillResult.getAreaContract(), count_price);
        if (account_ratio == null) {
            throw new XiangShuiException("该场地没有设置分账比例");
        }
        if (!(0 <= account_ratio && account_ratio <= 100)) {
            throw new XiangShuiException("该场地分账比例设置有误");
        }
        areaBillResult
                .setArea_id(area_id)
                .setTime_start(time_start)
                .setTime_end(time_end)
                .setChargeRecordList(chargeRecordList)
                .setChargeRecordMap(chargeRecordMap)
                .setBookingList(activeBookingList)
                .setBooking_count(booking_count)
                .setFinal_price(final_price)
                .setPay_price(pay_price)
                .setCharge_price(charge_price)
                .setMonth_card_price(month_card_price);
        if (!skipContract) {
            areaBillResult
                    .setAccount_ratio(account_ratio)
                    .setRatio_price(count_price * account_ratio / 100);
        }
        return areaBillResult;
    }


    public List<AreaBillResult> reckonAreaBillList(List<Integer> areaIdList, long time_start, long time_end, boolean skipContract) throws Exception {
        if (areaIdList == null || areaIdList.size() == 0) throw new XiangShuiException("缺少场地编号");
        List<AreaBillResult> resultList = new ArrayList<>();
        for (int area_id : areaIdList) {
            AreaBillResult result = reckonAreaBill(area_id, time_start, time_end, skipContract);
            if (result != null) {
                resultList.add(result);
            }
        }
        return resultList;
    }


    public void upsetAreaBill(AreaBillResult areaBillResult, int year, int month) {
        Date now = new Date();
        long bill_id = areaBillResult.getArea_id() * 1000000l + year * 100 + month;
        AreaBill areaBill = areaBillDao.getItem(new PrimaryKey("bill_id", bill_id));
        if (areaBill != null && Integer.valueOf(1).equals(areaBill.getStatus())) {
            throw new XiangShuiException("账单已计算，不能再次生成啦");
        }
        if (areaBill == null) {
            areaBill = new AreaBill();
            areaBill.setBill_id(bill_id);
            areaBill.setArea_id(areaBillResult.getArea_id());
            areaBill.setCreate_time(now.getTime() / 1000);
        } else {
            areaBill.setStatus(0);
        }
        areaBill.setYear(year);
        areaBill.setMonth(month);
        areaBill.setAccount_ratio(areaBillResult.getAccount_ratio());
        areaBill.setBooking_count(areaBillResult.getBooking_count());
        areaBill.setFinal_price(areaBillResult.getFinal_price());
        areaBill.setCharge_price(areaBillResult.getCharge_price());
        areaBill.setPay_price(areaBillResult.getPay_price());
        areaBill.setMonth_card_price(new LocalDate(year, month, 1).toDate().getTime() > new LocalDate(2018, 7, 1).toDate().getTime() ? areaBillResult.getMonth_card_price() : 0);
        areaBill.setRatio_price(areaBillResult.getRatio_price());
        areaBill.setUpdate_time(now.getTime() / 1000);
        areaBillDao.putItem(areaBill);
    }

    @Override
    public void afterPropertiesSet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (String text : IOUtils.readLines(this.getClass().getResourceAsStream("/test_phone.txt"), "UTF-8")) {
                        if (StringUtils.isNotBlank(text)) {
                            testPhoneSet.add(text.trim());
                        }
                    }

                    List<UserInfo> userInfoList = ServiceUtils.division(testPhoneSet.toArray(new String[testPhoneSet.size()]), 100, phones -> userInfoDao.scan(new ScanSpec()
                            .withScanFilters(
                                    new ScanFilter("phone").in(phones)
                            )), new String[0]);

                    for (UserInfo userInfo : userInfoList) {
                        testUinSet.add(userInfo.getUin());
                    }

//                    test(2018,1);
//                    test(2018,2);
//                    test(2018,3);
//                    test(2018,4);
//                    test(2018,5);
//                    test(2018,6);
//                    test(2018,7);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void test(int year, int month) {
        long start = new LocalDate(year, month, 1).toDate().getTime() / 1000;
        long end = new LocalDate(year, month, 1).plusMonths(1).toDate().getTime() / 1000 - 1;
        ScanSpec scanSpec = new ScanSpec()
                .withMaxResultSize(Integer.MAX_VALUE)
                .withScanFilters(
                        new ScanFilter("create_time").between(start, end),
                        new ScanFilter("status").eq(4),
                        new ScanFilter("final_price").gt(0),
                        new ScanFilter("f1").notExist(),
                        new ScanFilter("f0").notExist()
                );

        List<Booking> bookingList = bookingDao.scan(scanSpec);
        bookingList = ListUtils.filter(bookingList, booking -> booking != null
                && booking.getFinal_price() != null
                && booking.getFinal_price() > 0
                && (booking.getUse_pay() != null ? booking.getUse_pay() : 0) + (booking.getFrom_charge() != null ? booking.getFrom_charge() : 0) > 0
                && !testUinSet.contains(booking.getUin()));

        int count_capsule = ListUtils.fieldSet(bookingList, booking -> booking.getCapsule_id()).size();
        double sum_price = ListUtils.fieldSum(bookingList, booking -> Double.valueOf((booking.getUse_pay() != null ? booking.getUse_pay() : 0) + (booking.getFrom_charge() != null ? booking.getFrom_charge() : 0)));
        log.info("月份={},舱数={},月收入={},月订单数={}", year + "/" + month, count_capsule, sum_price / 100, bookingList.size());
    }

    public static void main(String[] args) throws Exception {
        SpringUtils.init();
    }

}
