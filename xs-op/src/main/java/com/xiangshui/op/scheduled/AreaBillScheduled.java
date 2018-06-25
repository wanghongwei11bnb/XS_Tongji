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
        final Date now = new Date();
        areaContractDao.scan(new ScanSpec().withScanFilters(new ScanFilter("status").eq(AreaContractStatusOption.adopt.value)), new CallBack<AreaContract>() {
            @Override
            public void run(AreaContract areaContract) {
                try {
                    makeBill(areaContract.getArea_id(), now.getYear() + 1900, now.getMonth() + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void makeBill(int area_id, int year, int month) {
        Date now = new Date();
        Date month_date = DateUtils.createDate(year, month, 1);
        Date next_month_date;
        if (month == 12) {
            next_month_date = DateUtils.createDate(year + 1, 1, 1);
        } else {
            next_month_date = DateUtils.createDate(year, month + 1, 1);
        }
        String bill_id = area_id + "_" + DateUtils.format(month_date, "yyyyMM");

        AreaBill areaBill = areaBillDao.getItem(new PrimaryKey("bill_id", bill_id));

        if (areaBill == null) {
            areaBill = new AreaBill();
            areaBill.setBill_id(bill_id);
            areaBill.setArea_id(area_id);
            areaBill.setMonth(Integer.valueOf(DateUtils.format(month_date, "yyyyMM")));
            areaBill.setCreate_time(now.getTime() / 1000);
        } else {
            if (areaBill.getStatus() != null && areaBill.getStatus() == 1) {
                return;
            }
            areaBill.setStatus(0);
        }

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

        areaBill.setAccount_ratio(areaContract.getAccount_ratio());


        List<Booking> bookingList = bookingDao.scan(
                new ScanSpec()
                        .withScanFilters(
                                new ScanFilter("area_id").eq(area_id),
                                new ScanFilter("status").eq(BookingStatusOption.pay.value),
                                new ScanFilter("update_time").between(
                                        month_date.getTime() / 1000 - 1,
                                        next_month_date.getTime() / 1000
                                )
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


        areaBill.setBooking_count(booking_count);
        areaBill.setFinal_price(final_price);
        areaBill.setCharge_price(charge_price);
        areaBill.setPay_price(pay_price);
        areaBill.setRatio_price((charge_price + pay_price) / 100 * areaContract.getAccount_ratio());

        areaBill.setUpdate_time(now.getTime() / 1000);

        areaBillDao.putItem(areaBill);
    }


}
