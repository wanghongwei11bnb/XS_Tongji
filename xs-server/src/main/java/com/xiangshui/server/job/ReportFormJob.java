package com.xiangshui.server.job;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.service.AreaContractService;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.BookingService;
import com.xiangshui.server.service.UserService;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.ListUtils;
import com.xiangshui.util.MapOptions;
import com.xiangshui.util.spring.SpringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class ReportFormJob {


    @Autowired
    AreaService areaService;
    @Autowired
    AreaDao areaDao;
    @Autowired
    CapsuleDao capsuleDao;

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
    @Autowired
    DepositRecordDao depositRecordDao;
    @Autowired
    CityDao cityDao;
    @Autowired
    CashInfoDao cashInfoDao;
    @Autowired
    UserInfoDao userInfoDao;

    public static String[] testPhones = new String[]{
            "13601189739",
            "15110096035",
            "13396046716",
            "19858194658",
            "15343438387",
            "13917891313",
            "18571702468",
            "18932889669",
            "13720062196",
    };

    public List<UserInfo> getTestUserInfoList() {
        return userInfoDao.scan(new ScanSpec().withScanFilters(
                new ScanFilter("phone").in(testPhones)
        ));
    }

    public List<ChargeRecord> getActiveMonthCardList(LocalDate start, LocalDate end) {
        List<ChargeRecord> chargeRecordList = chargeRecordDao.scan(new ScanSpec()
                .withMaxResultSize(Integer.MAX_VALUE)
                .withScanFilters(
                        new ScanFilter("subject").in("享+-月卡充值", "享+-季卡充值"),
                        new ScanFilter("status").eq(1),
                        new ScanFilter("update_time").between(
                                start.toDate().getTime() / 1000,
                                end.plusDays(1).toDate().getTime() / 1000 - 1
                        )
                )
        );

        return chargeRecordList;
    }

    public List<Area> getActiveAreaList() {
        List<Area> areaList = ListUtils.filter(areaDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE)), area -> area != null && (area.getStatus() == null || area.getStatus() == 0));
        areaList.sort(Comparator.comparing(Area::getArea_id));
        return areaList;
    }

    public List<Capsule> getActiveCapsuleList(List<Integer> area_id_list) {
        List<Capsule> capsuleList = ListUtils.filter(capsuleDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(new ScanFilter("area_id").in(area_id_list.toArray()))), capsule -> capsule != null
                && (!new Integer(1).equals(capsule.getIs_downline()))
                && StringUtils.isNotBlank(capsule.getDevice_id()) && capsule.getDevice_id().matches("^[0-9a-zA-Z]{24}$")
        );
        return capsuleList;
    }

    public List<City> getActiveCityList() {
        return cityDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE));
    }


    public List<Booking> getActiveBookingList(LocalDate start, LocalDate end) {
        List<Booking> bookingList = ListUtils.filter(bookingDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(
                new ScanFilter("create_time").between(
                        start.toDate().getTime() / 1000,
                        end.plusDays(1).toDate().getTime() / 1000 - 1
                )
        )), booking -> booking != null && (booking.getF1() == null || booking.getF1() == 0));
        for (Booking booking : bookingList) {
            if (new Integer(1).equals(booking.getF0())) {
                booking.setFinal_price(0);
                booking.setFrom_charge(0);
                booking.setFrom_bonus(0);
                booking.setFrom_guohang(0);
            }
        }
        bookingList = ListUtils.filter(bookingList, booking -> !new Integer(1).equals(booking.getF1()));
        bookingList.sort(Comparator.comparing(Booking::getCreate_time));
        return bookingList;
    }

    public List<ReportFormRow> makeReportForm(LocalDate start, LocalDate end) {

        List<UserInfo> testUserInfoList = getTestUserInfoList();

        Set<Integer> testUins = ListUtils.fieldSet(testUserInfoList, userInfo -> userInfo.getUin());

        List<ReportFormRow> reportFormRowList = new ArrayList<>();

        MapOptions<String, City> cityMapOptions = new MapOptions<String, City>(getActiveCityList()) {
            @Override
            public String getPrimary(City city) {
                return city.getCity();
            }
        };

        List<Area> areaList = getActiveAreaList();

        List<Integer> area_id_list = new ArrayList<>();

        for (Object area_id : ListUtils.fieldSet(areaList, (CallBackForResult<Area, Object>) Area::getArea_id)) {
            area_id_list.add((Integer) area_id);
        }

        List<Capsule> capsuleList = getActiveCapsuleList(area_id_list);

        List<Booking> bookingList = getActiveBookingList(start, end);
        List<ChargeRecord> chargeRecordList = getActiveMonthCardList(start, end);

        for (ChargeRecord chargeRecord : chargeRecordList) {
            List<Booking> tempBookingList = ListUtils.filter(bookingList, booking -> chargeRecord.getUin().equals(booking.getUin()));
            if (tempBookingList.size() > 0) {
                chargeRecord.setBill_booking_id(tempBookingList.get(0).getBooking_id());
                chargeRecord.setBill_area_id(tempBookingList.get(0).getArea_id());
            }
        }


        for (Area area : areaList) {
            ReportFormRow reportFormRow = new ReportFormRow();

            reportFormRow.setCapsule_count(ListUtils.filter(capsuleList, capsule -> area.getArea_id().equals(capsule.getArea_id())).size());
            if (reportFormRow.getCapsule_count() == 0) continue;

            reportFormRow.setRegion(cityMapOptions.tryValueForResult(area.getCity(), City::getRegion, null));
            reportFormRow.setProvince(cityMapOptions.tryValueForResult(area.getCity(), City::getProvince, null));
            reportFormRow.setCity(area.getCity());
            reportFormRow.setArea_id(area.getArea_id());
            reportFormRow.setArea_title(area.getTitle());


            List<Booking> tempBookingList = ListUtils.filter(bookingList, booking -> area.getArea_id().equals(booking.getArea_id()));
            List<ChargeRecord> tempChargeRecordList = ListUtils.filter(chargeRecordList, chargeRecord -> area.getArea_id().equals(chargeRecord.getBill_area_id()));

            List<Booking> activeTempBookingList = ListUtils.filter(tempBookingList, booking -> !testUins.contains(booking.getUin()));

//            List<ChargeRecord> testTempChargeRecordList = ListUtils.filter(tempChargeRecordList, chargeRecord -> !testUins.contains(chargeRecord.getUin()));


            // count
            {
                reportFormRow.setBooking_count(tempBookingList.size());
                reportFormRow.setActive_booking_count(activeTempBookingList.size());

                reportFormRow.setBooking_count_each_capsule(reportFormRow.getBooking_count() / reportFormRow.getCapsule_count());
                reportFormRow.setActive_booking_count_each_capsule(reportFormRow.getActive_booking_count() / reportFormRow.getCapsule_count());

            }

            // price
            {
                reportFormRow.setPrice_income_card_1((int) ListUtils.fieldSum(tempChargeRecordList, chargeRecord -> chargeRecord != null && "享+-月卡充值".equals(chargeRecord.getSubject()) && chargeRecord.getPrice() != null ? Double.valueOf(chargeRecord.getPrice()) : null));
                reportFormRow.setPrice_income_card_3((int) ListUtils.fieldSum(tempChargeRecordList, chargeRecord -> chargeRecord != null && "享+-季卡充值".equals(chargeRecord.getSubject()) && chargeRecord.getPrice() != null ? Double.valueOf(chargeRecord.getPrice()) : null));
                reportFormRow.setPrice_income_booking((int) ListUtils.fieldSum(tempBookingList, booking -> (double) ((booking.getFrom_charge() != null ? booking.getFrom_charge() : 0) + (booking.getUse_pay() != null ? booking.getUse_pay() : 0))));
                reportFormRow.setPrice_income_total(reportFormRow.getPrice_income_card_1() + reportFormRow.getPrice_income_card_3() + reportFormRow.getPrice_income_booking());
                reportFormRow.setPrice_income_each_capsule(reportFormRow.getPrice_income_total() / reportFormRow.getCapsule_count());
            }


            // income
            {
                reportFormRow.setIncome_total(reportFormRow.getPrice_income_total() + (int) ListUtils.fieldSum(bookingList, booking -> booking.getFrom_bonus() != null ? Double.valueOf(booking.getFrom_bonus()) : null));
                reportFormRow.setIncome_total_each_capsule(reportFormRow.getIncome_total() / reportFormRow.getCapsule_count());

                reportFormRow.setActive_income_total(reportFormRow.getPrice_income_total() + (int) ListUtils.fieldSum(activeTempBookingList, booking -> booking.getFrom_bonus() != null ? Double.valueOf(booking.getFrom_bonus()) : null));
                reportFormRow.setIncome_total_each_capsule(reportFormRow.getActive_income_total() / reportFormRow.getCapsule_count());

            }
            reportFormRowList.add(reportFormRow);
        }
        return reportFormRowList;

    }


    public void test() throws IOException {

        List<ReportFormRow> reportFormRowList = makeReportForm(new LocalDate(2021, 6, 1), new LocalDate(2021, 6, 1).plusMonths(1).minusDays(1));


        ExcelUtils.export(Arrays.asList(
                new ExcelUtils.Column<ReportFormRow>("区域") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getRegion();
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("城市") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getCity();
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("场地编号") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getArea_id();
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("场地名称") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getArea_title();
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("舱数") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getCapsule_count();
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("订单总收入") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getIncome_total() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("单舱总收入") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getIncome_total_each_capsule() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("订单量") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getBooking_count();
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("真实订单收入") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getActive_income_total() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("真实单舱收入") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getActive_income_total_each_capsule() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("真实订单量") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getActive_booking_count();
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("真实单舱订单量") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getActive_booking_count_each_capsule();
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("现金收入") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getPrice_income_total() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("单舱现金收入") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getPrice_income_each_capsule() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("订单现金收入(不含月卡押金）") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getPrice_income_booking() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("月卡") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getPrice_income_card_1() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormRow>("季卡") {
                    @Override
                    public Object render(ReportFormRow reportFormRow) {
                        return reportFormRow.getPrice_income_card_3() / 100f;
                    }
                }
        ), reportFormRowList, new File("/Users/whw/Documents/reportFormRowList.xlsx"));


        log.info("finish");
    }


    public static void main(String[] args) throws Exception {
        SpringUtils.init();
        SpringUtils.getBean(ReportFormJob.class).test();
    }


    @Data
    public static class ReportFormRow {

        private String region;
        private String province;
        private String city;
        private Integer area_id;
        private String area_title;
        private Integer capsule_count = 0;

        private Integer income_total = 0;
        private Integer income_total_each_capsule = 0;

        private Integer active_income_total = 0;
        private Integer active_income_total_each_capsule = 0;


        private Integer booking_count = 0;
        private Integer booking_count_each_capsule = 0;
        private Integer active_booking_count = 0;
        private Integer active_booking_count_each_capsule = 0;


        private Integer price_income_total = 0;
        private Integer price_income_each_capsule = 0;
        private Integer price_income_booking = 0;
        private Integer price_income_card_1 = 0;
        private Integer price_income_card_3 = 0;

    }

}
