package com.xiangshui.server.job;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.ChargeRecord;
import com.xiangshui.server.service.*;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.spring.SpringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@Component
public class MonthCardBill {


    @Autowired
    DiscountCouponDao discountCouponDao;

    @Autowired
    CityService cityService;
    @Autowired
    RedBagDao redBagDao;
    @Autowired
    AreaService areaService;
    @Autowired
    AreaDao areaDao;
    @Autowired
    S3Service s3Service;

    @Autowired
    AreaContractService areaContractService;

    @Autowired
    AreaContractDao areaContractDao;

    @Autowired
    CapsuleService capsuleService;


    @Autowired
    AreaBillDao areaBillDao;

    @Autowired
    OpUserService opUserService;
    @Autowired
    ChargeRecordDao chargeRecordDao;
    @Autowired
    CashInfoDao cashInfoDao;
    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    UserService userService;
    @Autowired
    MonthCardService monthCardService;

    @Autowired
    BookingDao bookingDao;


    public void test() throws IOException {

//        10.26~11.25

        long start_time = new LocalDate(2019, 10, 26).toDate().getTime() / 1000;
        long end_time = new LocalDate(2019, 11, 25).plusDays(1).toDate().getTime() / 1000;

        List<ChargeRecord> chargeRecordList = chargeRecordDao.scan(new ScanSpec()
                .withMaxResultSize(Integer.MAX_VALUE)
                .withScanFilters(
                        new ScanFilter("subject").in(new String[]{"享+-月卡充值", "享+-季卡充值"}),
                        new ScanFilter("status").eq(1),
                        new ScanFilter("price").gt(1000),
                        new ScanFilter("update_time").between(
                                start_time,
                                end_time
                        )
                )
        );
        chargeRecordList.sort(Comparator.comparing(ChargeRecord::getUpdate_time));

        start_time = chargeRecordList.get(0).getUpdate_time() - 60 * 60 * 24;
        end_time = chargeRecordList.get(chargeRecordList.size() - 1).getUpdate_time() + 60 * 60 * 24 * 30;

        List<Booking> bookingList = bookingDao.scan(new ScanSpec()
                .withMaxResultSize(Integer.MAX_VALUE)
                .withScanFilters(
                        new ScanFilter("status").eq(4),
                        new ScanFilter("update_time").between(
                                start_time,
                                end_time
                        )
                )
        );

        Map<String, Booking> map = new HashMap();

        for (ChargeRecord chargeRecord : chargeRecordList) {
            start_time = chargeRecord.getUpdate_time() - 60 * 60 * 24;
            end_time = chargeRecord.getUpdate_time() + 60 * 60 * 24 * 30;
            for (Booking booking : bookingList) {
                if (!booking.getUin().equals(chargeRecord.getUin())) continue;
                if (!(start_time < booking.getUpdate_time() && booking.getUpdate_time() < end_time)) continue;
                map.put(chargeRecord.getOut_trade_no(), booking);
            }
        }


        XSSFWorkbook workbook = ExcelUtils.export(Arrays.asList(
                new ExcelUtils.Column<ChargeRecord>("业务类型") {
                    @Override
                    public Object render(ChargeRecord chargeRecord) {
                        return chargeRecord.getSubject();
                    }
                },
                new ExcelUtils.Column<ChargeRecord>("交易时间") {
                    @Override
                    public Object render(ChargeRecord chargeRecord) {
                        return DateUtils.format(chargeRecord.getUpdate_time() * 1000);
                    }
                },
                new ExcelUtils.Column<ChargeRecord>("交易金额") {
                    @Override
                    public Object render(ChargeRecord chargeRecord) {
                        return chargeRecord.getPrice() / 100;
                    }
                },
                new ExcelUtils.Column<ChargeRecord>("用户编号") {
                    @Override
                    public Object render(ChargeRecord chargeRecord) {
                        return chargeRecord.getUin();
                    }
                },
                new ExcelUtils.Column<ChargeRecord>("场地编号") {
                    @Override
                    public Object render(ChargeRecord chargeRecord) {
                        return map.containsKey(chargeRecord.getOut_trade_no()) ? map.get(chargeRecord.getOut_trade_no()).getArea_id() : null;
                    }
                },
                new ExcelUtils.Column<ChargeRecord>("头等舱编号") {
                    @Override
                    public Object render(ChargeRecord chargeRecord) {
                        return map.containsKey(chargeRecord.getOut_trade_no()) ? map.get(chargeRecord.getOut_trade_no()).getCapsule_id() : null;
                    }
                }
        ), chargeRecordList);
        OutputStream outputStream = new FileOutputStream(new File("/Users/whw/Downloads/月卡.xlsx"));
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();


    }

    public static void main(String[] args) throws IOException {
        SpringUtils.init();
        SpringUtils.getBean(MonthCardBill.class).test();
    }
}
