package com.xiangshui.op.scheduled;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.util.MapOptions;
import com.xiangshui.util.web.result.CodeMsg;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

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
    CountCapsuleScheduled countCapsuleScheduled;
    @Autowired
    MinitouBillDao minitouBillDao;


    public Set<Long> capsuleIdSet = new HashSet<>();

    public List<AreaContract> areaContractList = new ArrayList<>();


    public void updateBooking(Booking booking) {
        try {
            if (booking != null && capsuleIdSet.contains(booking.getCapsule_id()) && new Integer(4).equals(booking.getStatus()) && (booking.getFinal_price() == null || booking.getFinal_price() <= 0)) {
                int price = (int) (Math.random() * 500 + 500);
                booking.setFinal_price(price).setFrom_bonus(price).setFrom_charge(0).setF0(1);
                bookingDao.updateItem(new PrimaryKey("booking_id", booking.getBooking_id()), booking, new String[]{
                        "final_price",
                        "from_bonus",
                        "from_charge",
                        "f0",
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
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


        ScanSpec scanSpec = new ScanSpec()
                .withMaxResultSize(Integer.MAX_VALUE)
                .withScanFilters(
                        new ScanFilter("end_time").between(
                                new LocalDate(2018, 12, 1).toDate().getTime() / 1000,
                                new LocalDate(2019, 1, 1).toDate().getTime() / 1000 - 1
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


    @Scheduled(cron = "0 30 3 ? * 2")
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


    @Scheduled(cron = "0 0 3 1 * ?")
    public void updateBookingForMonth() {
        LocalDate localDateEnd = new LocalDate().withDayOfMonth(1);
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
        scanFilterList.add(new ScanFilter("capsule_id").eq(capsule_id));
        scanFilterList.add(new ScanFilter("create_time").between(dateTimeStart, dateTimeEnd));
        scanFilterList.add(new ScanFilter("status").eq(4));
        scanFilterList.add(new ScanFilter("final_price").gt(0));
        scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        List<Booking> bookingList = bookingDao.scan(scanSpec);
        return makeBill(capsule_id, year, month, bookingList);
    }


    public MinitouBill makeBill(long capsule_id, int year, int month, List<Booking> bookingList) {
        Capsule capsule = cacheScheduled.capsuleMapOptions.get(capsule_id);
        if (capsule == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }
        Area area = cacheScheduled.areaMapOptions.get(capsule.getArea_id());
        if (area == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }
        AreaContract areaContract = cacheScheduled.areaContractMapOptions.get(capsule.getArea_id());
        MinitouBill minitouBill = new MinitouBill().setUpdate_time(System.currentTimeMillis() / 1000);
        minitouBill.setBill_id(capsule_id * 1000000 + year * 100 + month);
        minitouBill.setCapsule_id(capsule_id).setArea_id(area.getArea_id()).setYear(year).setMonth(month);
        int final_price = 0;
        int account_ratio = areaContract != null && areaContract.getAccount_ratio() != null ? areaContract.getAccount_ratio() : 0;
        int ratio_price = 0;
        int rent_price = 0;
        int other_price = 0;
        int net_price = 0;
        for (Booking booking : bookingList) {
//            final_price += (booking.getFrom_charge() != null ? booking.getFrom_charge() : 0) + (booking.getUse_pay() != null ? booking.getUse_pay() : 0);
            final_price += booking.getFinal_price() != null ? booking.getFinal_price() : 0;
        }
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


    public List<MinitouBill> makeBill(int year, int month) {
        long dateTimeStart = new LocalDate(year, month, 1).toDate().getTime() / 1000;
        long dateTimeEnd = new LocalDate(year, month, 1).plusMonths(1).toDate().getTime() / 1000 - 1;
        List<ScanFilter> scanFilterList = new ArrayList<>();
        scanFilterList.add(new ScanFilter("create_time").between(dateTimeStart, dateTimeEnd));
        scanFilterList.add(new ScanFilter("status").eq(4));
        scanFilterList.add(new ScanFilter("capsule_id").in(capsuleIdSet.toArray()));
        ScanSpec scanSpec = new ScanSpec()
                .withMaxResultSize(Integer.MAX_VALUE)
                .withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        List<Booking> bookingList = bookingDao.scan(scanSpec);
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


}
