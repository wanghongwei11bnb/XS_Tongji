package com.xiangshui.op.scheduled;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.constant.AreaContractStatusOption;
import com.xiangshui.server.constant.BookingStatusOption;
import com.xiangshui.server.dao.AreaBillDao;
import com.xiangshui.server.dao.AreaContractDao;
import com.xiangshui.server.dao.BookingDao;
import com.xiangshui.server.domain.AreaBill;
import com.xiangshui.server.domain.AreaContract;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.service.AreaContractService;
import com.xiangshui.server.service.BookingService;
import com.xiangshui.util.CallBack;
import com.xiangshui.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@Component
public class AreaBillScheduled {


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


    //    @Scheduled(cron = "0 0 1 1 * ?")
    @Scheduled(cron = "0/10 * * * * ?")
    public void makeBill() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MONTH, -1);
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
                makeBill(areaContract.getArea_id(), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void makeBill(int area_id, int year, int month) {

        Date now = new Date();

        long bill_id = area_id * 1000000l + year * 100 + month;


        AreaContract areaContract = areaContractService.getByAreaId(area_id);
        if (areaContract == null) {
            throw new XiangShuiException("没匹配到场地合同");
        }

        if (areaContract.getAccount_ratio() == null) {
            throw new XiangShuiException("该场地没有设置分账比例");
        }

        if (!(0 < areaContract.getAccount_ratio() && areaContract.getAccount_ratio() < 100)) {
            throw new XiangShuiException("该场地分账比例设置有误");
        }


        Calendar c1 = Calendar.getInstance();
        c1.set(year, month - 1, 1);
        c1.set(Calendar.MILLISECOND, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.HOUR_OF_DAY, 0);
        long l1 = c1.getTimeInMillis() / 1000;
        Calendar c2 = Calendar.getInstance();
        c2.set(year, month - 1, 1);
        c2.add(Calendar.MONTH, 1);
        c2.set(Calendar.MILLISECOND, 0);
        c2.set(Calendar.SECOND, 0);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.HOUR_OF_DAY, 0);
        long l2 = c2.getTimeInMillis() / 1000;


        List<Booking> bookingList = bookingDao.scan(
                new ScanSpec()
                        .withScanFilters(
                                new ScanFilter("area_id").eq(area_id),
                                new ScanFilter("status").eq(BookingStatusOption.pay.value),
                                new ScanFilter("update_time").between(l1, l2)
                        )
        );


        int booking_count = 0;
        int final_price = 0;
        int charge_price = 0;
        int pay_price = 0;
        if (bookingList != null && bookingList.size() > 0) {
            for (int i = 0; i < bookingList.size(); i++) {
                Booking booking = bookingList.get(i);
                booking_count++;
                if (booking.getFinal_price() != null && booking.getFinal_price() > 0) {
                    final_price += booking.getFinal_price();
                }

                if (booking.getFrom_charge() != null && booking.getFrom_charge() > 0) {
                    charge_price += booking.getFrom_charge();
                }

                if (booking.getUse_pay() != null && booking.getUse_pay() > 0) {
                    pay_price += booking.getUse_pay();
                }
            }
        }


        AreaBill areaBill = areaBillDao.getItem(new PrimaryKey("bill_id", bill_id));


        if (areaBill == null) {
            areaBill = new AreaBill();
            areaBill.setBill_id(bill_id);
            areaBill.setArea_id(area_id);
            areaBill.setCreate_time(now.getTime() / 1000);
        } else {
            if (areaBill.getStatus() != null && areaBill.getStatus() == 1) {
                throw new XiangShuiException("已付款的账单");
            }
            areaBill.setStatus(0);
        }

        areaBill.setYear(year);
        areaBill.setMonth(month);

        areaBill.setAccount_ratio(areaContract.getAccount_ratio());
        areaBill.setBooking_count(booking_count);
        areaBill.setFinal_price(final_price);
        areaBill.setCharge_price(charge_price);
        areaBill.setPay_price(pay_price);
        areaBill.setRatio_price((charge_price + pay_price) / 100 * areaContract.getAccount_ratio());

        areaBill.setUpdate_time(now.getTime() / 1000);

        areaBillDao.putItem(areaBill);
    }


}
