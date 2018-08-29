package com.xiangshui.op.scheduled;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.constant.AreaContractStatusOption;
import com.xiangshui.server.constant.AreaStatusOption;
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
    ChargeRecordDao chargeRecordDao;

    public Set<Integer> testUinSet = new HashSet<>();
    public Set<String> testPhoneSet = new HashSet<>();


    @Scheduled(cron = "0 0 1 1,6 * ?")
    public void makeBill() {
        LocalDate localDate = LocalDate.now().minusMonths(1);
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
                makeBill(areaContract.getArea_id(), localDate.getYear(), localDate.getMonthOfYear());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void makeBill(int area_id, int year, int month) {
        Date now = new Date();
        long bill_id = area_id * 1000000l + year * 100 + month;
        AreaBill areaBill = areaBillDao.getItem(new PrimaryKey("bill_id", bill_id));
        if (areaBill != null && Integer.valueOf(1).equals(areaBill.getStatus())) {
            throw new XiangShuiException("账单已计算，不能再次生成啦");
        }
        AreaContract areaContract = areaContractService.getByAreaId(area_id);
        if (areaContract == null) {
            throw new XiangShuiException("没匹配到场地合同");
        }
        if (!Integer.valueOf(AreaContractStatusOption.adopt.value).equals(areaContract.getStatus())) {
            throw new XiangShuiException("审核未通过");
        }
        if (areaContract.getAccount_ratio() == null) {
            throw new XiangShuiException("该场地没有设置分账比例");
        }
        if (!(0 < areaContract.getAccount_ratio() && areaContract.getAccount_ratio() < 100)) {
            throw new XiangShuiException("该场地分账比例设置有误");
        }

        LocalDate startDate = new LocalDate(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).plusDays(-1);


        long l1 = startDate.toDate().getTime() / 1000;
        long l2 = endDate.plusDays(1).toDate().getTime() / 1000;

        List<ChargeRecord> chargeRecordList = chargeRecordDao.scan(new ScanSpec().withScanFilters(
                new ScanFilter("create_time").between(l1, l2),
                new ScanFilter("bill_area_id").eq(area_id),
                new ScanFilter("bill_booking_id").exists()
        ));
        List<Booking> bookingList = bookingDao.scan(
                new ScanSpec()
                        .withScanFilters(
                                new ScanFilter("area_id").eq(area_id),
                                new ScanFilter("status").eq(BookingStatusOption.pay.value),
                                new ScanFilter(year * 100 + month > 201807 ? "create_time" : "update_time").between(l1, l2)
                        ).withMaxResultSize(Integer.MAX_VALUE)
        );
        int booking_count = 0;
        int final_price = 0;
        int charge_price = 0;
        int pay_price = 0;

        if (bookingList != null && bookingList.size() > 0) {
            for (int i = 0; i < bookingList.size(); i++) {
                Booking booking = bookingList.get(i);
                if (testUinSet.contains(booking.getUin())) {
                    continue;
                }
                if (booking.getFinal_price() == null || booking.getFinal_price() == 0) {
                    continue;
                }
                if ((booking.getFrom_charge() != null ? booking.getFrom_charge() : 0) + (booking.getUse_pay() != null ? booking.getUse_pay() : 0) == 0) {
                    continue;
                }
                final_price += booking.getFinal_price();
                if (booking.getFrom_charge() != null && booking.getFrom_charge() > 0) {
                    charge_price += booking.getFrom_charge();
                }
                if (booking.getUse_pay() != null && booking.getUse_pay() > 0) {
                    pay_price += booking.getUse_pay();
                }
                booking_count++;
            }
        }

        int month_card_price = 0;
        for (ChargeRecord chargeRecord : chargeRecordList) {
            if (chargeRecord != null && chargeRecord.getBill_area_id() != null && chargeRecord.getBill_booking_id() != null && chargeRecord.getPrice() != null) {
                month_card_price += chargeRecord.getPrice();
            }
        }

        if (areaBill == null) {
            areaBill = new AreaBill();
            areaBill.setBill_id(bill_id);
            areaBill.setArea_id(area_id);
            areaBill.setCreate_time(now.getTime() / 1000);
        } else {
            areaBill.setStatus(0);
        }

        areaBill.setYear(year);
        areaBill.setMonth(month);

        areaBill.setAccount_ratio(areaContract.getAccount_ratio());
        areaBill.setBooking_count(booking_count);
        areaBill.setFinal_price(final_price);
        areaBill.setCharge_price(charge_price);
        areaBill.setPay_price(pay_price);
        areaBill.setMonth_card_price(month_card_price);
        areaBill.setRatio_price((charge_price + pay_price + (new LocalDate(year, month, 1).toDate().getTime() > new LocalDate(2018, 7, 1).toDate().getTime() ? month_card_price : 0)) * areaContract.getAccount_ratio() / 100);
        areaBill.setUpdate_time(now.getTime() / 1000);
        areaBillDao.putItem(areaBill);
    }

    public List<Booking> billBookingList(int area_id, long create_time_start, long create_time_end) {
        List<Booking> activeBookingList = new ArrayList<>();
        List<Booking> bookingList = bookingDao.scan(
                new ScanSpec()
                        .withScanFilters(
                                new ScanFilter("area_id").eq(area_id),
                                new ScanFilter("status").eq(BookingStatusOption.pay.value),
                                new ScanFilter(Integer.valueOf(DateUtils.format(create_time_start * 1000, "yyyyMM")) > 201807 ? "create_time" : "update_time").between(create_time_start, create_time_end)
                        ).withMaxResultSize(Integer.MAX_VALUE)
        );
        if (bookingList != null && bookingList.size() > 0) {
            for (int i = 0; i < bookingList.size(); i++) {
                Booking booking = bookingList.get(i);
                if (testUinSet.contains(booking.getUin())) {
                    continue;
                }
                if (booking.getFinal_price() == null || booking.getFinal_price() == 0) {
                    continue;
                }
                if ((booking.getFrom_charge() != null ? booking.getFrom_charge() : 0) + (booking.getUse_pay() != null ? booking.getUse_pay() : 0) == 0) {
                    continue;
                }
                activeBookingList.add(booking);
            }
        }
        return activeBookingList;
    }

    public List<Booking> billBookingList(int area_id, int year, int month) {
        return billBookingList(
                area_id,
                new LocalDate(year, month, 1).toDate().getTime() / 1000,
                new LocalDate(year, month, 1).plusMonths(1).toDate().getTime() / 1000
        );
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
                    testPhoneSet.forEach(s -> {
                        log.debug("加载测试手机号：" + s);
                        UserInfo userInfo = userService.getUserInfoByPhone(s);
                        if (userInfo != null) {
                            testUinSet.add(userInfo.getUin());
                        }
                    });
//                    List<Booking> bookingList = bookingDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE));
//                    test(2018, 4);
//                    test(2018, 5);
//                    test(2018, 6);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public void test(int year, int month) throws IOException {
        Set<Long> bookingIdSet = new HashSet<>();
        for (String line : IOUtils.readLines(this.getClass().getResourceAsStream("/m" + month + ".txt"), "UTF-8")) {
            try {
                if (StringUtils.isNotBlank(line)) {
                    bookingIdSet.add(Long.valueOf(line));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<Booking> activeBookingList = new ArrayList<>();
        List<Booking> bookingListOld = bookingDao.scan(
                new ScanSpec()
                        .withScanFilters(
                                new ScanFilter("status").eq(BookingStatusOption.pay.value),
                                new ScanFilter("update_time").between(
                                        new LocalDate(year, month, 1).toDate().getTime() / 1000
                                        , new LocalDate(year, month, 1).plusMonths(1).toDate().getTime() / 1000
                                )
                        ).withMaxResultSize(Integer.MAX_VALUE)
        );


        if (bookingListOld != null && bookingListOld.size() > 0) {
            for (int i = 0; i < bookingListOld.size(); i++) {

                Booking booking = bookingListOld.get(i);
//                if (new Integer(1).equals(booking.getF1())) {
//                    booking.setFinal_price(booking.getFinal_price() + 200);
//                    booking.setFrom_bonus(booking.getFrom_bonus() + 200);
//                }
                if (bookingIdSet.contains(booking.getBooking_id())) {
                    activeBookingList.add(booking);
                    continue;
                }

                if (testUinSet.contains(booking.getUin())) {

                    continue;
                }
                if (booking.getFinal_price() == null || booking.getFinal_price() == 0) {

                    continue;
                }
//                if ((booking.getFrom_charge() != null ? booking.getFrom_charge() : 0) + (booking.getUse_pay() != null ? booking.getUse_pay() : 0) == 0) {
//                    continue;
//                }

                activeBookingList.add(booking);

            }
        }


        if (activeBookingList == null) {
            activeBookingList = new ArrayList<>();
        }
        List<Area> areaList = null;
        if (activeBookingList != null && activeBookingList.size() > 0) {
            areaList = areaService.getAreaListByBooking(activeBookingList, new String[]{"area_id", "title", "city", "address", "status"});
            Collections.sort(activeBookingList, new Comparator<Booking>() {
                @Override
                public int compare(Booking o1, Booking o2) {
                    return -(int) (o1.getCreate_time() - o2.getCreate_time());
                }
            });

        }
        Map<Integer, Area> areaMap = new HashMap<>();
        Map<Integer, UserInfo> userInfoMap = new HashMap<>();

        if (areaList != null && areaList.size() > 0) {
            areaList.forEach(new Consumer<Area>() {
                @Override
                public void accept(Area area) {
                    if (area != null) {
                        areaMap.put(area.getArea_id(), area);
                    }
                }
            });
        }

        List<List<String>> data = new ArrayList<>();

        List<String> headRow = new ArrayList<>();
        headRow.add("订单编号");
        headRow.add("创建时间");
        headRow.add("结束时间");
        headRow.add("订单状态");
        headRow.add("订单总金额");
        headRow.add("实际充值金额");
        headRow.add("系统赠送金额");
        headRow.add("实际付款金额");
        headRow.add("支付方式");
        headRow.add("是否使用月卡");
        headRow.add("头等舱编号");
        headRow.add("场地编号");
        headRow.add("场地名称");
        headRow.add("城市");
        headRow.add("地址");
        headRow.add("用户UIN");
        headRow.add("用户手机号");
        headRow.add("订单来源");
        data.add(headRow);
        if (activeBookingList != null && activeBookingList.size() > 0) {
            activeBookingList.forEach(new Consumer<Booking>() {
                @Override
                public void accept(Booking booking) {
                    if (booking == null) {
                        return;
                    }
                    Area area = areaMap.get(booking.getArea_id());
                    if (area == null) {
                        return;
                    }
//                    if (AreaStatusOption.offline.value.equals(area.getStatus())) {
//                        return;
//                    }
                    List<String> row = new ArrayList<>();
                    row.add(String.valueOf(booking.getBooking_id()));
                    row.add((booking.getCreate_time() != null && booking.getCreate_time() > 0 ?
                            DateUtils.format(booking.getCreate_time() * 1000, "yyyy-MM-dd HH:mm")
                            : ""));
                    row.add("" + (booking.getEnd_time() != null && booking.getEnd_time() > 0 ?
                            DateUtils.format(booking.getEnd_time() * 1000, "yyyy-MM-dd HH:mm")
                            : null));
                    row.add("" + Option.getActiveText(BookingStatusOption.options, booking.getStatus()));
                    row.add(booking.getFinal_price() != null ? String.valueOf(booking.getFinal_price() / 100f) : "");

                    row.add(booking.getFrom_charge() != null ? String.valueOf(booking.getFrom_charge() / 100f) : "");
                    row.add(booking.getFrom_bonus() != null ? String.valueOf(booking.getFrom_bonus() / 100f) : "");

                    row.add(booking.getUse_pay() != null ? booking.getUse_pay() / 100f + "" : "");
                    row.add("" + Option.getActiveText(PayTypeOption.options, booking.getPay_type()));
                    row.add(new Integer(1).equals(booking.getMonth_card_flag()) ? "是" : "否");
                    row.add("" + booking.getCapsule_id());
                    row.add("" + booking.getArea_id());
                    row.add("" + (areaMap.containsKey(booking.getArea_id()) ?
                            areaMap.get(booking.getArea_id()).getTitle()
                            : null));
                    row.add("" + (areaMap.containsKey(booking.getArea_id()) ?
                            areaMap.get(booking.getArea_id()).getCity()
                            : null));
                    row.add("" + (areaMap.containsKey(booking.getArea_id()) ?
                            areaMap.get(booking.getArea_id()).getAddress()
                            : null));
                    row.add("" + booking.getUin());
                    row.add("" + (userInfoMap.containsKey(booking.getUin()) ?
                            userInfoMap.get(booking.getUin()).getPhone()
                            : null));
                    row.add(booking.getReq_from());
                    data.add(row);
                }
            });
        }

        XSSFWorkbook workbook = ExcelUtils.export(data);
        workbook.write(new FileOutputStream(new File("/Users/whw/Downloads/booking_" + year + "_" + month + ".xlsx")));

    }

    public void test(List<Booking> bookingList, int year, int month) throws IOException {
        Set<Long> bookingIdSet = new HashSet<>();
        for (String line : IOUtils.readLines(this.getClass().getResourceAsStream("/m" + month + ".txt"), "UTF-8")) {
            try {
                if (StringUtils.isNotBlank(line)) {
                    bookingIdSet.add(Long.valueOf(line));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<Booking> activeBookingList = new ArrayList<>();
        bookingList.forEach(booking -> {
            if (booking != null && bookingIdSet.contains(booking.getBooking_id())) {
                activeBookingList.add(booking);
            }
        });


        List<Area> areaList = null;
        if (activeBookingList != null && activeBookingList.size() > 0) {
            areaList = areaService.getAreaListByBooking(activeBookingList, new String[]{"area_id", "title", "city", "address", "status"});
            Collections.sort(activeBookingList, new Comparator<Booking>() {
                @Override
                public int compare(Booking o1, Booking o2) {
                    return -(int) (o1.getCreate_time() - o2.getCreate_time());
                }
            });

        }
        Map<Integer, Area> areaMap = new HashMap<>();
        Map<Integer, UserInfo> userInfoMap = new HashMap<>();

        if (areaList != null && areaList.size() > 0) {
            areaList.forEach(new Consumer<Area>() {
                @Override
                public void accept(Area area) {
                    if (area != null) {
                        areaMap.put(area.getArea_id(), area);
                    }
                }
            });
        }

        List<List<String>> data = new ArrayList<>();

        List<String> headRow = new ArrayList<>();
        headRow.add("订单编号");
        headRow.add("创建时间");
        headRow.add("结束时间");
        headRow.add("订单状态");
        headRow.add("订单总金额");
        headRow.add("实际充值金额");
        headRow.add("系统赠送金额");
        headRow.add("实际付款金额");
        headRow.add("支付方式");
        headRow.add("是否使用月卡");
        headRow.add("头等舱编号");
        headRow.add("场地编号");
        headRow.add("场地名称");
        headRow.add("城市");
        headRow.add("地址");
        headRow.add("用户UIN");
        headRow.add("用户手机号");
        headRow.add("订单来源");
        data.add(headRow);
        if (activeBookingList != null && activeBookingList.size() > 0) {
            activeBookingList.forEach(new Consumer<Booking>() {
                @Override
                public void accept(Booking booking) {
                    if (booking == null) {
                        return;
                    }
                    Area area = areaMap.get(booking.getArea_id());
                    if (area == null) {
                        return;
                    }
//                    if (AreaStatusOption.offline.value.equals(area.getStatus())) {
//                        return;
//                    }
                    List<String> row = new ArrayList<>();
                    row.add(String.valueOf(booking.getBooking_id()));
                    row.add((booking.getCreate_time() != null && booking.getCreate_time() > 0 ?
                            DateUtils.format(booking.getCreate_time() * 1000, "yyyy-MM-dd HH:mm")
                            : ""));
                    row.add("" + (booking.getEnd_time() != null && booking.getEnd_time() > 0 ?
                            DateUtils.format(booking.getEnd_time() * 1000, "yyyy-MM-dd HH:mm")
                            : null));
                    row.add("" + Option.getActiveText(BookingStatusOption.options, booking.getStatus()));
                    row.add(booking.getFinal_price() != null ? String.valueOf(booking.getFinal_price() / 100f) : "");

                    row.add(booking.getFrom_charge() != null ? String.valueOf(booking.getFrom_charge() / 100f) : "");
                    row.add(booking.getFrom_bonus() != null ? String.valueOf(booking.getFrom_bonus() / 100f) : "");

                    row.add(booking.getUse_pay() != null ? booking.getUse_pay() / 100f + "" : "");
                    row.add("" + Option.getActiveText(PayTypeOption.options, booking.getPay_type()));
                    row.add(new Integer(1).equals(booking.getMonth_card_flag()) ? "是" : "否");
                    row.add("" + booking.getCapsule_id());
                    row.add("" + booking.getArea_id());
                    row.add("" + (areaMap.containsKey(booking.getArea_id()) ?
                            areaMap.get(booking.getArea_id()).getTitle()
                            : null));
                    row.add("" + (areaMap.containsKey(booking.getArea_id()) ?
                            areaMap.get(booking.getArea_id()).getCity()
                            : null));
                    row.add("" + (areaMap.containsKey(booking.getArea_id()) ?
                            areaMap.get(booking.getArea_id()).getAddress()
                            : null));
                    row.add("" + booking.getUin());
                    row.add("" + (userInfoMap.containsKey(booking.getUin()) ?
                            userInfoMap.get(booking.getUin()).getPhone()
                            : null));
                    row.add(booking.getReq_from());
                    data.add(row);
                }
            });
        }

        XSSFWorkbook workbook = ExcelUtils.export(data);
        workbook.write(new FileOutputStream(new File("/Users/whw/Downloads/booking_2_" + year + "_" + month + ".xlsx")));
    }

    public static void main(String[] args) throws Exception {
        SpringUtils.init();
//        SpringUtils.getBean(AreaBillScheduled.class).test(2018, 4);

    }
}
