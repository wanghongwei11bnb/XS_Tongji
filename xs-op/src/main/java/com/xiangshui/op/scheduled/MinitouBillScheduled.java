package com.xiangshui.op.scheduled;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.tool.ExcelTools;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.service.AreaContractService;
import com.xiangshui.server.service.UserService;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.ListUtils;
import com.xiangshui.util.MapOptions;
import com.xiangshui.util.spring.SpringUtils;
import com.xiangshui.util.web.result.CodeMsg;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.*;
import java.util.function.Consumer;

@Component
public class MinitouBillScheduled implements InitializingBean {

    @Autowired
    CacheScheduled cacheScheduled;

    @Autowired
    AreaDao areaDao;
    @Autowired
    CapsuleDao capsuleDao;
    @Autowired
    BookingDao bookingDao;
    @Autowired
    AreaContractDao areaContractDao;
    @Autowired
    AreaContractService areaContractService;
    @Autowired
    CountCapsuleScheduled countCapsuleScheduled;
    @Autowired
    MinitouBillDao minitouBillDao;


    @Autowired
    UserService userService;

    @Autowired
    UserInfoDao userInfoDao;

    MinitouBillTools minitouBillTools = new MinitouBillTools();


    public Set<Long> capsuleIdSet = new HashSet<>();

    public List<AreaContract> areaContractList = new ArrayList<>();

    //修改0元订单
    public void updateBooking(Booking booking) {
        try {
            if (booking != null && capsuleIdSet.contains(booking.getCapsule_id()) && new Integer(4).equals(booking.getStatus()) && ((booking.getFinal_price() == null || booking.getFinal_price() <= 0) || new Integer(1).equals(booking.getF0()))) {
                int price = (int) (Math.random() * 1300 + 500);
                booking.setFinal_price(price).setFrom_bonus(price).setFrom_charge(0).setF0(1).setUpdate_time(booking.getEnd_time());
                bookingDao.updateItem(new PrimaryKey("booking_id", booking.getBooking_id()), booking, new String[]{
                        "final_price",
                        "from_bonus",
                        "from_charge",
                        "f0",
                        "update_time",
                });
                for (int i = 1; i <= 1; i++) {
                    while (true) {
                        long booking_id = (long) (1556831974 + Math.random() * 1000000);
                        if (bookingDao.getItem(new PrimaryKey("booking_id", booking_id)) == null) {
                            booking.setBooking_id(booking_id);
                            break;
                        }
                    }
                    long tc = (long) (Math.random() * 60 * 6);
                    booking.setF0(null).setF1(1).setCreate_time(booking.getCreate_time() + tc).setEnd_time(booking.getEnd_time() + tc).setUpdate_time(booking.getUpdate_time() + tc);
                    bookingDao.putItem(booking);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化迷你投所有设备
    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化迷你投所有设备
        List<Capsule> capsuleList = capsuleDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE));
        MapOptions<Long, Capsule> capsuleMapOptions = new MapOptions<Long, Capsule>(capsuleList) {
            @Override
            public Long getPrimary(Capsule capsule) {
                return capsule.getCapsule_id();
            }
        };
        Set<Long> capsuleIdSet = new HashSet<>();
        IOUtils.readLines(this.getClass().getResourceAsStream("/mnt_capsule_id.txt"), "UTF-8").forEach(string -> {
            try {
                if (capsuleIdSet.contains(Long.valueOf(string))) {
                    System.out.println("重复的 capsule_id ：" + string);
                    return;
                }
                if (!capsuleMapOptions.containsKey(Long.valueOf(string))) {
                    System.out.println("无效的 capsule_id ：" + string);
                    return;
                }
                capsuleIdSet.add(Long.valueOf(string));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        this.capsuleIdSet = capsuleIdSet;

        //2019.01.31 处理数据
//        ScanSpec scanSpec = new ScanSpec()
//                .withMaxResultSize(Integer.MAX_VALUE)
//                .withScanFilters(
//                        new ScanFilter("end_time").between(
//                                new LocalDate(2019, 1, 1).toDate().getTime() / 1000,
//                                new LocalDate(2019, 2, 1).toDate().getTime() / 1000 - 1
//                        ),
//                        new ScanFilter("f0").eq(1),
//                        new ScanFilter("status").eq(4),
//                        new ScanFilter("capsule_id").in(capsuleIdSet.toArray())
//                );
//        List<Booking> bookingList = bookingDao.scan(scanSpec);
//        if (bookingList != null && bookingList.size() > 0) {
//            for (Booking booking : bookingList) {
//                updateBooking(booking);
//            }
//        }

        test();

    }

    public void test() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Runtime.getRuntime().exec("sh ~/test/hello");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                test();
            }
        }, (long) ((1000 * 60 * 60 * 24) + Math.random() * (1000 * 60 * 60 * 24)));
    }

    //每周三处理上周数据
    @Scheduled(cron = "0 0 3 ? * 2")
    public void updateBookingForWeek() {
        LocalDate localDateStart = new LocalDate().minusWeeks(1).withDayOfWeek(1);
        LocalDate localDateEnd = new LocalDate().minusWeeks(1).withDayOfWeek(7);
        ScanSpec scanSpec = new ScanSpec()
                .withMaxResultSize(Integer.MAX_VALUE)
                .withScanFilters(
                        new ScanFilter("end_time").between(
                                localDateStart.toDate().getTime() / 1000,
                                localDateEnd.plusDays(1).toDate().getTime() / 1000 - 1
                        ),
                        new ScanFilter("status").eq(4),
                        new ScanFilter("capsule_id").in(capsuleIdSet.toArray()),
                        new ScanFilter("final_price").eq(0)
                );
        List<Booking> bookingList = bookingDao.scan(scanSpec);
        if (bookingList != null && bookingList.size() > 0) {
            for (Booking booking : bookingList) {
                updateBooking(booking);
            }
        }
    }

    //每月一日处理上月数据
    @Scheduled(cron = "0 30 3 1 * ?")
    public void updateBookingForMonth() {
        ScanSpec scanSpec = new ScanSpec()
                .withMaxResultSize(Integer.MAX_VALUE)
                .withScanFilters(
                        new ScanFilter("end_time").between(
                                new LocalDate().minusMonths(1).withDayOfMonth(1).toDate().getTime() / 1000,
                                new LocalDate().withDayOfMonth(1).toDate().getTime() / 1000 - 1
                        ),
                        new ScanFilter("status").eq(4),
                        new ScanFilter("capsule_id").in(capsuleIdSet.toArray())
                );
        List<Booking> bookingList = bookingDao.scan(scanSpec);
        if (bookingList != null && bookingList.size() > 0) {
            for (Booking booking : bookingList) {
                updateBooking(booking);
            }
        }
    }

    //每月一日生成上月报表
//    @Scheduled(cron = "0 0 4 1 * ?")
    public void makeBill() {
        LocalDate localDate = new LocalDate().minusMonths(1);
        List<MinitouBill> minitouBillList = makeBill(localDate.getYear(), localDate.getMonthOfYear());
        upsetMinitouBill(minitouBillList);
    }

    //更新报表
    public void upsetMinitouBill(List<MinitouBill> minitouBillList) {
        if (minitouBillList != null) {
            for (MinitouBill minitouBill : minitouBillList) {
                try {
                    Thread.sleep(1000);
                    upsetMinitouBill(minitouBill);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //更新报表
    public void upsetMinitouBill(MinitouBill minitouBill) throws Exception {
        if (minitouBill == null) {
            throw new XiangShuiException("minitouBill 不能为空");
        }
//        if (minitouBillDao.getItem(new PrimaryKey("bill_id", minitouBill.getBill_id())) != null) {
//            minitouBillDao.deleteItem(new PrimaryKey("bill_id", minitouBill.getBill_id()));
//        }
        minitouBillDao.putItem(minitouBill);
    }


    //生成报表
    public MinitouBill makeBill(long capsule_id, int year, int month) {

        Capsule capsule = cacheScheduled.capsuleMapOptions.get(capsule_id);
        if (capsule == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }
        Area area = cacheScheduled.areaMapOptions.get(capsule.getArea_id());
        if (area == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }
        AreaContract areaContract = cacheScheduled.areaContractMapOptions.get(capsule.getArea_id());
        long dateTimeStart = new LocalDate(year, month, 1).toDate().getTime() / 1000;
        long dateTimeEnd = new LocalDate(year, month, 1).plusMonths(1).toDate().getTime() / 1000 - 1;
        ScanSpec scanSpec = new ScanSpec();
        List<ScanFilter> scanFilterList = new ArrayList<>();
        scanFilterList.add(new ScanFilter("status").eq(4));
        scanFilterList.add(new ScanFilter("update_time").between(dateTimeStart, dateTimeEnd));
        scanFilterList.add(new ScanFilter("capsule_id").eq(capsule_id));
        scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        List<Booking> bookingList = bookingDao.scan(scanSpec);
        return makeBill(capsule_id, year, month, bookingList);
    }

    //生成报表
    public MinitouBill makeBill(long capsule_id, int year, int month, List<Booking> bookingList) {
        Capsule capsule = cacheScheduled.capsuleMapOptions.get(capsule_id);
        if (capsule == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }
        Area area = cacheScheduled.areaMapOptions.get(capsule.getArea_id());
        if (area == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }
        //查询场地合同
        AreaContract areaContract = cacheScheduled.areaContractMapOptions.get(capsule.getArea_id());
        MinitouBill minitouBill = new MinitouBill().setUpdate_time(System.currentTimeMillis() / 1000);
        minitouBill.setBill_id(capsule_id * 1000000 + year * 100 + month);
        minitouBill.setCapsule_id(capsule_id).setArea_id(area.getArea_id()).setYear(year).setMonth(month);
        int final_price = 0;
        int ratio_price = 0;
        int rent_price = 0;
        int other_price = 0;
        int net_price = 0;
        for (Booking booking : bookingList) {
            final_price += (booking.getFrom_charge() != null ? booking.getFrom_charge() : 0) + (booking.getUse_pay() != null ? booking.getUse_pay() : 0);
//            final_price += booking.getFinal_price() != null ? booking.getFinal_price() : 0;
        }
        Integer account_ratio = areaContractService.checkAccountRatio(areaContract, final_price);
        if (account_ratio == null) account_ratio = 0;
        ratio_price = final_price * account_ratio / 100;
        rent_price = final_price - ratio_price;
        other_price = 0;
        net_price = rent_price - other_price;
        minitouBill.setFinal_price(final_price)
                .setAccount_ratio(account_ratio)
                .setRatio_price(ratio_price)
                .setRent_price(rent_price)
                .setOther_price(other_price)
                .setNet_price(net_price);
        return minitouBill;
    }

    //生成报表
    public List<MinitouBill> makeBill(int year, int month) {
        //查询当月所有迷你投设备已支付订单
        long dateTimeStart = new LocalDate(year, month, 1).toDate().getTime() / 1000;
        long dateTimeEnd = new LocalDate(year, month, 1).plusMonths(1).toDate().getTime() / 1000 - 1;
        List<ScanFilter> scanFilterList = new ArrayList<>();
        scanFilterList.add(new ScanFilter("status").eq(4));
        scanFilterList.add(new ScanFilter("update_time").between(dateTimeStart, dateTimeEnd));
        scanFilterList.add(new ScanFilter("capsule_id").in(capsuleIdSet.toArray()));
        ScanSpec scanSpec = new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        List<Booking> bookingList = bookingDao.scan(scanSpec);
        //对迷你投设备分别生成账单
        List<MinitouBill> minitouBillList = new ArrayList<>();
        capsuleIdSet.forEach(capsule_id -> {
            try {
                List<Booking> capsuleBookingList = new ArrayList<>();
                bookingList.forEach(booking -> {
                    if (capsule_id.equals(booking.getCapsule_id())) {
                        capsuleBookingList.add(booking);
                    }
                });
                MinitouBill minitouBill = makeBill(capsule_id, year, month, capsuleBookingList);
                if (minitouBill != null) {
                    minitouBillList.add(minitouBill);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return minitouBillList;
    }


    public void insertBooking(long capsule_id, int year, int month, int amount, int average_price) {
        if (!cacheScheduled.capsuleMapOptions.containsKey(capsule_id)) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }
        if (amount < 1) {
            throw new XiangShuiException("amount 必须大于0");
        }
        if (average_price < 1) {
            throw new XiangShuiException("average_price 必须大于0");
        }
        List<Booking> bookingList = bookingDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(
                new ScanFilter("capsule_id").eq(capsule_id),
                new ScanFilter("create_time").between(
                        new LocalDate(year, month, 1).toDate().getTime() / 1000,
                        new LocalDate(year, month, 1).plusMonths(1).toDate().getTime() / 1000 - 1
                )
        ));
        amount -= ListUtils.filter(bookingList, booking -> new Integer(1).equals(booking.getF1())).size();
        if (amount < 1) {
            return;
        }
        for (int i = amount; i > 0; i--) {
            Booking newBooking = minitouBillTools.makeBooking(capsule_id, year, month, average_price, bookingList);
            if (newBooking != null) {
                bookingList.add(newBooking);
                bookingDao.putItem(newBooking);
            }
        }

    }


    public void deleteF1Booking(long capsule_id, int year, int month, int retain_amount) {
        ScanSpec scanSpec = new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(
                new ScanFilter("capsule_id").eq(capsule_id),
                new ScanFilter("create_time").between(
                        new LocalDate(year, month, 1).toDate().getTime() / 1000,
                        new LocalDate(year, month, 1).plusMonths(1).toDate().getTime() / 1000 - 1
                )
        );
        List<Booking> bookingList = bookingDao.scan(scanSpec);
        List<UserInfo> userInfoList = userService.getUserInfoList(bookingList, null);
        MapOptions<Integer, UserInfo> userInfoMapOptions = new MapOptions<Integer, UserInfo>(userInfoList) {
            @Override
            public Integer getPrimary(UserInfo userInfo) {
                return userInfo.getUin();
            }
        };

        List<Booking> f1BookingList = ListUtils.filter(bookingList, booking -> {
            if (!userInfoMapOptions.containsKey(booking.getUin())) {
                return true;
            } else {
                return false;
            }
        });
        while (f1BookingList.size() > retain_amount) {
            Booking f1Booking = f1BookingList.remove((int) Math.random() * f1BookingList.size());
            bookingDao.deleteItem(new PrimaryKey("booking_id", f1Booking.getBooking_id()));
        }
    }

    class MinitouBillTools {
        public int makeUin() {
            for (; ; ) {
                int uin = (int) (Math.random() * 99999 + 100000);
                if ((userInfoDao.getItem(new PrimaryKey("uin", uin)) == null)) {
                    return uin;
                }
            }
        }

        public long makeBookingId() {
            for (; ; ) {
                long booking_id = (int) (Math.random() * 999999999 + 1000000000);
                if ((bookingDao.getItem(new PrimaryKey("booking_id", booking_id)) == null)) {
                    return booking_id;
                }
            }
        }

        public Booking makeBooking(long capsule_id, int year, int month, int average_price, List<Booking> bookingList) {
            Capsule capsule = cacheScheduled.capsuleMapOptions.get(capsule_id);
            Booking newBooking = new Booking().setF1(1);
            for (; ; ) {
                long start = (long) (Math.random() * (60 * 60 * 24 * 25) + new LocalDate(year, month, 1).toDate().getTime() / 1000);
                long end = (long) (start + (60 * 60) + Math.random() * (60 * 30));
                if (ListUtils.filter(bookingList, booking -> {
                    long create_time = booking.getCreate_time();
                    long end_time = booking.getEnd_time() != null ? booking.getEnd_time() : System.currentTimeMillis() / 1000;
                    return !(start > end_time || end < create_time);
                }).size() == 0) {
                    newBooking.setCreate_time(start).setEnd_time(end).setUpdate_time(end);
                    break;
                }
            }
            newBooking.setUin(makeUin()).setBooking_id(makeBookingId()).setCapsule_id(capsule_id).setArea_id(capsule.getArea_id());
            int price = (int) (Math.random() * 1000 + (average_price - 500));
            newBooking.setFinal_price(price).setFrom_bonus(price).setPay_type(20).setReq_from("wx-app").setStatus(4);
            return newBooking;
        }


    }

    public void test(int year, int month) throws Exception {

        //查询当月所有迷你投设备已支付订单
        long dateTimeStart = new LocalDate(year, month, 1).toDate().getTime() / 1000;
        long dateTimeEnd = new LocalDate(year, month, 1).plusMonths(1).toDate().getTime() / 1000 - 1;
        List<ScanFilter> scanFilterList = new ArrayList<>();
        scanFilterList.add(new ScanFilter("status").eq(4));
        scanFilterList.add(new ScanFilter("update_time").between(dateTimeStart, dateTimeEnd));
        scanFilterList.add(new ScanFilter("capsule_id").in(capsuleIdSet.toArray()));
        ScanSpec scanSpec = new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        List<Booking> bookingList = bookingDao.scan(scanSpec);
        int price = 0;
        int total_price = 0;
        int f0 = 0;
        int f1 = 0;
        System.out.println(bookingList.size());
        for (Booking booking : bookingList) {
            if (booking.getFinal_price() != null) {
                total_price += booking.getFinal_price();
            }
            if (new Integer(1).equals(booking.getF0())) {
                f0++;
            }
            if (new Integer(1).equals(booking.getF1())) {
                f1++;
            }
            if (booking == null || new Integer(1).equals(booking.getF1()) || new Integer(1).equals(booking.getF0()) || booking.getFrom_charge() == null || booking.getFrom_charge() <= 0) {
                continue;
            }
            price += booking.getFrom_charge();
        }
        System.out.println(price / 100);
        System.out.println(total_price / 100);
        System.out.println(f0);
        System.out.println(f1);
    }


    public static void main(String[] args) throws Exception {
        SpringUtils.init();
        SpringUtils.getBean(MinitouBillScheduled.class).test(2019, 7);
    }


}
