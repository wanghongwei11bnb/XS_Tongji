package com.xiangshui.server.job;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.server.dao.BookingDao;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.service.BookingService;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.function.Consumer;

@Component
public class F0 {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BookingService bookingService;

    @Autowired
    BookingDao bookingDao;


    public void doWork(InputStream inputStream, String sheetName) throws IOException {
        List<List<String>> data = ExcelUtils.read(inputStream, sheetName);

        data.forEach(strings -> {
            log.debug("{} {}", strings.get(0), strings.get(7));
            try {
                long booking_id = Long.valueOf(strings.get(0));
                int final_price = Integer.valueOf(strings.get(7)) * 100;
                Booking booking = bookingService.getBookingById(booking_id);
                if (booking == null) {
                    return;
                }
                if (booking.getF0() != null && booking.getF0() == 1) {
                    return;
                }
                if (booking.getFinal_price() != null && booking.getFinal_price() > 0) {
                    return;
                }
                if (booking.getFrom_bonus() != null && booking.getFrom_bonus() > 0) {
                    return;
                }
                if (booking.getFrom_charge() != null && booking.getFrom_charge() > 0) {
                    return;
                }
                if (booking.getStatus() == null || booking.getStatus() == 1) {
                    return;
                }
                booking.setFinal_price(final_price);
                booking.setFrom_bonus(final_price);
                booking.setF0(1);
                bookingDao.updateItem(new PrimaryKey("booking_id", booking_id), booking, new String[]{
                        "final_price",
                        "from_bonus",
                        "f0",
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public static void main(String[] args) throws IOException {
        SpringUtils.init();
//        SpringUtils.getBean(F0.class).doWork(new FileInputStream(new File("/Users/whw/Documents/4-6月订单副本.xlsx")), "17.09-18.04月0元订单");
//        SpringUtils.getBean(F0.class).doWork(new FileInputStream(new File("/Users/whw/Documents/4-6月订单副本.xlsx")), "5月0元订单");
//        SpringUtils.getBean(F0.class).doWork(new FileInputStream(new File("/Users/whw/Documents/4-6月订单副本.xlsx")), "6月0元订单");
        SpringUtils.getBean(F0.class).doWork(new FileInputStream(new File("/Users/whw/Downloads/7月订单222.xlsx")), "0元订单恢复");
    }


}
