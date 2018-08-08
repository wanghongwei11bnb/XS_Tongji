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
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.spring.SpringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
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


    public List<Booking> filterBookingList(List<Booking> bookingList, CallBackForResult<Booking, Boolean> callBackForResult) {
        List<Booking> bookingListNew = new ArrayList<>();
        bookingList.forEach(new Consumer<Booking>() {
            @Override
            public void accept(Booking booking) {
                if (callBackForResult.run(booking)) {
                    bookingListNew.add(booking);
                }
            }
        });
        return bookingListNew;
    }

    public Area getAreaByAreaId(List<Area> areaList, int area_id) {
        for (int i = 0; i < areaList.size(); i++) {
            if (areaList.get(i).getArea_id() == area_id) {
                return areaList.get(i);
            }
        }
        return null;
    }

    public List<Capsule> getCapsuleListByAreaId(List<Capsule> capsuleList, int area_id) {
        List<Capsule> capsuleListNew = new ArrayList<>();
        for (int i = 0; i < capsuleList.size(); i++) {
            if (capsuleList.get(i).getArea_id() == area_id) {
                capsuleListNew.add(capsuleList.get(i));
            }
        }
        return capsuleListNew;
    }


    public void doWork(int year, int month, InputStream inputStream, String sheetName, int final_price, int plusRatio) throws IOException {


        List<Area> areaList = areaDao.scan();
        List<Capsule> capsuleList = capsuleDao.scan();


        List<Booking> bookingList = bookingDao.scan(new ScanSpec().withScanFilters(
                new ScanFilter("create_time").lt(new LocalDate(year, month + 1, 1).toDate().getTime() / 1000)
                , new ScanFilter("end_time").gt(new LocalDate(year, month, 1).toDate().getTime() / 1000)
        ).withMaxResultSize(Integer.MAX_VALUE));

        StringBuilder errorLog = new StringBuilder();

        long start = new LocalDate(year, month, 1).toDate().getTime() / 1000;
        long end = new LocalDate(year, month, 1).plusMonths(1).toDate().getTime() / 1000 - 1;

        List<List<String>> data = ExcelUtils.read(inputStream, sheetName);
        data.forEach((List<String> strings) -> {
            try {
                int uin = Integer.valueOf(strings.get(0));
                int area_id = Integer.valueOf(strings.get(1));
                log.debug("{} {}", uin, area_id);

                UserInfo userInfo = userInfoDao.getItem(new PrimaryKey("uin", uin));
                if (userInfo == null) {
                    return;
                }
                if (userInfo.getCreate_time() >= end) {
                    return;
                }
                Area area = getAreaByAreaId(areaList, area_id);
                if (area == null) {
                    return;
                }

                List<Booking> bookingListForF1 = filterBookingList(bookingList, new CallBackForResult<Booking, Boolean>() {
                    @Override
                    public Boolean run(Booking booking) {
                        if (
                                booking != null
                                        && booking.getUin() == uin
                                        && booking.getArea_id() == area_id
                                        && new Integer(1).equals(booking.getF1())
                                        && start <= booking.getCreate_time() && booking.getCreate_time() <= end
                                ) {
                            return true;
                        }
                        return false;
                    }
                });

                if ((uin % 100 > plusRatio && bookingListForF1.size() >= 1)
                        || (uin % 100 <= plusRatio && bookingListForF1.size() >= 2)) {
                    return;
                }

                List<Capsule> capsuleListForArea = getCapsuleListByAreaId(capsuleList, area_id);
                if (capsuleListForArea == null || capsuleListForArea.size() == 0) {
                    return;
                }

                long user_create_time = userInfo.getCreate_time() * 1000;

                LocalDate userCreateDate = LocalDate.fromDateFields(new Date(user_create_time));
                if (userCreateDate.isBefore(new LocalDate(year, month, 1))) {
                    userCreateDate = new LocalDate(year, month, 1);
                }
                LocalDate bookingDate = userCreateDate.plusDays(1);

                Booking booking = new Booking();
                booking.setUin(uin);
                booking.setArea_id(area_id);
                booking.setF1(1);
                booking.setStatus(4);
                booking.setFrom_charge(0);
                booking.setCalculate_rule(area.getTypes().get(0).getPrice_rule_text());
                booking.setReq_from("wx-app");

                while (bookingDate.getYear() == year && bookingDate.getMonthOfYear() == month) {

                    LocalTime bookingCreateTime = new LocalTime(8, 0).plusMillis((int) (Math.random() * (1000 * 60 * 30)));
                    LocalTime bookingEndTime = bookingCreateTime.plusMillis((int) ((1000 * 60 * 50) + Math.random() * (1000 * 60 * 20)));

                    do {
                        int c = 1000 * 60 * 30;
                        bookingCreateTime = bookingCreateTime.plusMillis(c);
                        bookingEndTime = bookingEndTime.plusMillis(c);
                        long create_time = new DateTime(
                                bookingDate.getYear(), bookingDate.getMonthOfYear(), bookingDate.getDayOfMonth(),
                                bookingCreateTime.getHourOfDay(), bookingCreateTime.getMinuteOfHour(), bookingCreateTime.getSecondOfMinute()
                        ).toDate().getTime() / 1000;
                        long end_time = new DateTime(
                                bookingDate.getYear(), bookingDate.getMonthOfYear(), bookingDate.getDayOfMonth(),
                                bookingEndTime.getHourOfDay(), bookingEndTime.getMinuteOfHour(), bookingEndTime.getSecondOfMinute()
                        ).toDate().getTime() / 1000;
                        List<Booking> bookingListForUin = filterBookingList(bookingList, new CallBackForResult<Booking, Boolean>() {
                            @Override
                            public Boolean run(Booking booking) {
                                if (
                                        booking != null
                                                && booking.getUin() == uin
                                                && booking.getCreate_time() < end_time
                                                && booking.getEnd_time() > create_time
                                        ) {
                                    return true;
                                }
                                return false;
                            }
                        });

                        if (bookingListForUin != null && bookingListForUin.size() > 0) {
                            continue;
                        }
                        for (int i = 0; i < capsuleListForArea.size(); i++) {
                            Capsule capsule = capsuleListForArea.get(i);

                            List<Booking> bookingListForCapsule = filterBookingList(bookingList, new CallBackForResult<Booking, Boolean>() {
                                @Override
                                public Boolean run(Booking booking) {
                                    if (
                                            booking != null
                                                    && booking.getArea_id() == area_id
                                                    && booking.getCapsule_id().equals(capsule.getCapsule_id())
                                                    && booking.getCreate_time() < end_time
                                                    && booking.getEnd_time() > create_time
                                            ) {
                                        return true;
                                    }
                                    return false;
                                }
                            });
                            if (bookingListForCapsule != null && bookingListForCapsule.size() > 0) {
                                continue;
                            }
                            booking.setCapsule_id(capsule.getCapsule_id());

                            booking.setCreate_time(create_time);
                            booking.setEnd_time(end_time);
                            booking.setUpdate_time(booking.getEnd_time());
                            booking.setCreate_date(Integer.valueOf(new DateTime(booking.getCreate_time() * 1000).toString("yyyyMMdd")));

                            int final_price_new = (int) (final_price - 500 + Math.random() * 1000);
                            booking.setFinal_price(final_price_new);
                            booking.setFrom_bonus(final_price_new);
                            booking.setPay_type(20);
                            booking.setMonth_card_flag(0);
                            while (true) {
                                long booking_id = (long) (1556831974 + Math.random() * 1000000);
                                if (bookingDao.getItem(new PrimaryKey("booking_id", booking_id)) == null) {
                                    booking.setBooking_id(booking_id);
                                    bookingDao.putItem(booking);
                                    return;
                                }
                            }

                        }

                    } while (bookingEndTime.isBefore(new LocalTime(21, 0)));

                    bookingDate = bookingDate.plusDays(1);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void delete(int year, int month, boolean delete) {
        List<Booking> bookingList = bookingDao.scan(new ScanSpec()
                .withMaxResultSize(Integer.MAX_VALUE)
                .withScanFilters(
                        new ScanFilter("create_time").lt(new LocalDate(year, month + 1, 1).toDate().getTime() / 1000)
                        , new ScanFilter("end_time").gt(new LocalDate(year, month, 1).toDate().getTime() / 1000)
                        , new ScanFilter("f1").eq(1)));
        log.debug(String.valueOf(bookingList.size()));
        if (delete) {
            bookingList.forEach(new Consumer<Booking>() {
                @Override
                public void accept(Booking booking) {
                    log.debug("删除订单：{}", booking.getBooking_id());
                    bookingDao.deleteItem(new PrimaryKey("booking_id", booking.getBooking_id()));
                }
            });
        }
    }

    public void update() {
        List<Booking> bookingList = bookingDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(new ScanFilter("f1").eq(1)));
        bookingList.forEach(booking -> {
            if (booking != null && new Integer(1).equals(booking.getF1())) {
                booking.setFinal_price(booking.getFinal_price() + 200);
                booking.setFrom_bonus(booking.getFrom_bonus() + 200);
                try {
                    log.debug("update booking:{}", booking.getBooking_id());
                    bookingDao.updateItem(new PrimaryKey("booking_id", booking.getBooking_id()), booking, new String[]{
                            "final_price",
                            "from_bonus",
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public static void main(String[] args) throws Exception {
        SpringUtils.init();
        SpringUtils.getBean(F1.class).update();
//        SpringUtils.getBean(F1.class).delete(2018, 5, true);
//        SpringUtils.getBean(F1.class).doWork(2018, 4, new FileInputStream(new File("/Users/whw/Documents/4-6月订单副本.xlsx")), "4月用户候选", 2070, 20);
//        SpringUtils.getBean(F1.class).doWork(2018, 5, new FileInputStream(new File("/Users/whw/Documents/4-6月订单副本.xlsx")), "5月用户候选", 1700, 27);
//        SpringUtils.getBean(F1.class).doWork(2018, 5, new FileInputStream(new File("/Users/whw/Documents/4-6月订单副本.xlsx")), "5月用户候选", 1700, 27);
//        SpringUtils.getBean(F1.class).doWork(2018, 6, new FileInputStream(new File("/Users/whw/Documents/4-6月订单副本.xlsx")), "6月用户候选", 1418, 90);
    }


}
