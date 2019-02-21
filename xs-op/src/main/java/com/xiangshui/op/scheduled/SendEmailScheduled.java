package com.xiangshui.op.scheduled;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.service.*;
import com.xiangshui.util.*;
import com.xiangshui.util.spring.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

@Component
public class SendEmailScheduled {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AreaBillScheduled areaBillScheduled;


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
    CityDao cityDao;


    @Scheduled(cron = "0 0 6 * * ?")
    public void make() throws IOException {
        makeForSendEmail(new LocalDate().minusDays(1));
    }

    public void make(LocalDate localDate, CallBack2<List<AreaItem>, List<CapsuleItem>> callBack) throws IOException {
        //数据
        Map<Integer, String> operatorMap = new HashMap<>();

        for (List<String> stringList : ExcelUtils.read(System.class.getResourceAsStream("/场地运营.xlsx"), "运营场地")) {
            if (stringList == null || stringList.size() < 10
                    || StringUtils.isBlank(stringList.get(0)) || StringUtils.isBlank(stringList.get(9))
                    || !stringList.get(0).matches("^\\d+$")
                    ) continue;
            operatorMap.put(Integer.valueOf(stringList.get(0)), stringList.get(9));
        }

        List<AreaContract> areaContractList = areaContractDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE));
        MapOptions<Integer, AreaContract> areaContractMapOptions = new MapOptions<Integer, AreaContract>(areaContractList) {
            @Override
            public Integer getPrimary(AreaContract areaContract) {
                return areaContract.getArea_id();
            }
        };

        List<City> cityList = cityDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE));
        MapOptions<String, City> cityMapOptions = new MapOptions<String, City>(cityList) {
            @Override
            public String getPrimary(City city) {
                return city.getCity();
            }
        };


        List<Area> areaList = null;
        MapOptions<Integer, Area> areaMapOptions = null;
        List<Capsule> capsuleList = null;
        MapOptions<Long, Capsule> capsuleMapOptions = null;
        List<Booking> bookingList = null;
        List<AreaItem> areaItemList = null;
        MapOptions<Integer, AreaItem> areaItemMapOptions = null;
        List<CapsuleItem> capsuleItemList = null;
        MapOptions<Long, CapsuleItem> capsuleItemMapOptions = null;

        //场地数据
        {
            areaList = areaDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE));
            areaList = ListUtils.filter(areaList, area -> !new Integer(-1).equals(area.getStatus()) && !new Integer(-2).equals(area.getStatus()));
            areaMapOptions = new MapOptions<Integer, Area>(areaList) {
                @Override
                public Integer getPrimary(Area area) {
                    return area.getArea_id();
                }
            };
        }
        //设备数据
        {
            capsuleList = capsuleDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE));
            MapOptions<Integer, Area> finalAreaMapOptions = areaMapOptions;
            capsuleList = ListUtils.filter(capsuleList, capsule -> !new Integer(1).equals(capsule.getIs_downline()) && finalAreaMapOptions.containsKey(capsule.getArea_id()));
            capsuleMapOptions = new MapOptions<Long, Capsule>(capsuleList) {
                @Override
                public Long getPrimary(Capsule capsule) {
                    return capsule.getCapsule_id();
                }
            };
        }
        //订单数据
        {
            bookingList = bookingDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(
                    new ScanFilter("status").eq(4),
                    new ScanFilter("update_time").between(
                            localDate.getDayOfMonth() == 1 ? localDate.minusDays(1).toDate().getTime() / 1000 : localDate.withDayOfMonth(1).toDate().getTime() / 1000,
                            localDate.plusMonths(1).withDayOfMonth(1).toDate().getTime() / 1000
                    ),
                    new ScanFilter("f1").ne(1)
            ));
            MapOptions<Long, Capsule> finalCapsuleMapOptions = capsuleMapOptions;
            bookingList = ListUtils.filter(bookingList, booking -> !new Integer(1).equals(booking.getF1()) && !areaBillScheduled.testUinSet.contains(booking.getUin()) && finalCapsuleMapOptions.containsKey(booking.getCapsule_id()));
            for (Booking booking : bookingList) {
                if (new Integer(1).equals(booking.getF0())) {
                    booking.setFinal_price(0).setFrom_bonus(0).setFrom_charge(0);
                }
            }
        }


        {
            areaItemList = new ArrayList<>();
            for (Area area : areaList) {
                areaItemList.add(new AreaItem(area.getArea_id())
                        .setTitle(area.getTitle())
                        .setCity(area.getCity())
                        .setTime(area.getCreate_time())
                        .setSaler(areaContractMapOptions.containsKey(area.getArea_id()) ? areaContractMapOptions.get(area.getArea_id()).getSaler() : null)
                        .setOperator(operatorMap.get(area.getArea_id()))
                        .setProvince(cityMapOptions.containsKey(area.getCity()) ? cityMapOptions.get(area.getCity()).getProvince() : null)
                );
            }
            areaItemMapOptions = new MapOptions<Integer, AreaItem>(areaItemList) {
                @Override
                public Integer getPrimary(AreaItem areaItem) {
                    return areaItem.area_id;
                }
            };
            capsuleItemList = new ArrayList<>();
            for (Capsule capsule : capsuleList) {
                Area area = areaMapOptions.get(capsule.getArea_id());
                capsuleItemList.add(new CapsuleItem(capsule.getCapsule_id())
                        .setArea_id(capsule.getArea_id())
                        .setTitle(area.getTitle())
                        .setCity(area.getCity())
                        .setTime(area.getCreate_time())
                        .setSaler(areaContractMapOptions.containsKey(area.getArea_id()) ? areaContractMapOptions.get(area.getArea_id()).getSaler() : null)
                        .setOperator(operatorMap.get(area.getArea_id()))
                        .setProvince(cityMapOptions.containsKey(area.getCity()) ? cityMapOptions.get(area.getCity()).getProvince() : null)
                );
                areaItemMapOptions.get(capsule.getArea_id()).capsule_count++;
            }
            capsuleItemMapOptions = new MapOptions<Long, CapsuleItem>(capsuleItemList) {
                @Override
                public Long getPrimary(CapsuleItem capsuleItem) {
                    return capsuleItem.capsule_id;
                }
            };
            //排序
            {
                areaItemList.sort((o1, o2) -> {
                    if (o1.getCity().compareTo(o2.getCity()) != 0) {
                        return o1.getCity().compareTo(o2.getCity());
                    } else {
                        return o1.getArea_id().compareTo(o2.getArea_id());
                    }
                });

                MapOptions<Integer, Area> finalAreaMapOptions1 = areaMapOptions;
                capsuleItemList.sort((o1, o2) -> {
                    if (finalAreaMapOptions1.get(o1.getArea_id()).getCity().compareTo(finalAreaMapOptions1.get(o2.getArea_id()).getCity()) != 0) {
                        return finalAreaMapOptions1.get(o1.getArea_id()).getCity().compareTo(finalAreaMapOptions1.get(o2.getArea_id()).getCity());
                    } else {
                        return o1.getCapsule_id().compareTo(o2.getCapsule_id());
                    }
                });
            }


            long main_day_time_start = localDate.toDate().getTime() / 1000;
            long main_day_time_end = localDate.plusDays(1).toDate().getTime() / 1000;

            long prev_day_time_start = localDate.minusDays(1).toDate().getTime() / 1000;
            long prev_day_time_end = localDate.toDate().getTime() / 1000;

            long main_month_time_start = localDate.withDayOfMonth(1).toDate().getTime() / 1000;
            long main_month_time_end = localDate.withDayOfMonth(1).plusMonths(1).toDate().getTime() / 1000;

            for (Booking booking : bookingList) {
                AreaItem areaItem = areaItemMapOptions.get(booking.getArea_id());
                CapsuleItem capsuleItem = capsuleItemMapOptions.get(booking.getCapsule_id());
                //当天订单
                if (main_day_time_start <= booking.getCreate_time() && booking.getCreate_time() <= main_day_time_end) {
                    areaItem.main_day_booking_count++;
                    areaItem.main_day_booking_price += (booking.getUse_pay() != null ? booking.getUse_pay() : 0)
                            + (booking.getFrom_charge() != null ? booking.getFrom_charge() : 0)
                    ;
                    capsuleItem.main_day_booking_count++;
                    capsuleItem.main_day_booking_price += (booking.getUse_pay() != null ? booking.getUse_pay() : 0)
                            + (booking.getFrom_charge() != null ? booking.getFrom_charge() : 0)
                    ;

                }
                //前一天订单
                if (prev_day_time_start <= booking.getCreate_time() && booking.getCreate_time() <= prev_day_time_end) {
                    areaItem.prev_day_booking_count++;
                    areaItem.prev_day_booking_price += (booking.getUse_pay() != null ? booking.getUse_pay() : 0)
                            + (booking.getFrom_charge() != null ? booking.getFrom_charge() : 0)
                    ;
                    capsuleItem.prev_day_booking_count++;
                    capsuleItem.prev_day_booking_price += (booking.getUse_pay() != null ? booking.getUse_pay() : 0)
                            + (booking.getFrom_charge() != null ? booking.getFrom_charge() : 0)
                    ;
                }
                //当月订单
                if (main_month_time_start <= booking.getCreate_time() && booking.getCreate_time() <= main_month_time_end) {
                    areaItem.main_month_booking_count++;
                    areaItem.main_month_booking_price += (booking.getUse_pay() != null ? booking.getUse_pay() : 0)
                            + (booking.getFrom_charge() != null ? booking.getFrom_charge() : 0)
                    ;
                    capsuleItem.main_month_booking_count++;
                    capsuleItem.main_month_booking_price += (booking.getUse_pay() != null ? booking.getUse_pay() : 0)
                            + (booking.getFrom_charge() != null ? booking.getFrom_charge() : 0)
                    ;
                }
            }

        }
        if (callBack != null) {
            callBack.run(areaItemList, capsuleItemList);
        }
    }


    public static class AreaItem {
        public Integer area_id;
        public String province;
        public String city;
        public String title;
        public String saler;
        public String operator;
        public Long time;
        public int main_day_booking_count;
        public int main_day_booking_price;
        public int prev_day_booking_count;
        public int prev_day_booking_price;
        public int main_month_booking_count;
        public int main_month_booking_price;
        public int capsule_count;

        public String getProvince() {
            return province;
        }

        public AreaItem setProvince(String province) {
            this.province = province;
            return this;
        }

        public AreaItem(int area_id) {
            this.area_id = area_id;
        }

        public Integer getArea_id() {
            return area_id;
        }

        public AreaItem setArea_id(Integer area_id) {
            this.area_id = area_id;
            return this;
        }

        public String getCity() {
            return city;
        }

        public AreaItem setCity(String city) {
            this.city = city;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public AreaItem setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getSaler() {
            return saler;
        }

        public AreaItem setSaler(String saler) {
            this.saler = saler;
            return this;
        }

        public String getOperator() {
            return operator;
        }

        public AreaItem setOperator(String operator) {
            this.operator = operator;
            return this;
        }

        public Long getTime() {
            return time;
        }

        public AreaItem setTime(Long time) {
            this.time = time;
            return this;
        }

        public int getMain_day_booking_count() {
            return main_day_booking_count;
        }

        public AreaItem setMain_day_booking_count(int main_day_booking_count) {
            this.main_day_booking_count = main_day_booking_count;
            return this;
        }

        public int getMain_day_booking_price() {
            return main_day_booking_price;
        }

        public AreaItem setMain_day_booking_price(int main_day_booking_price) {
            this.main_day_booking_price = main_day_booking_price;
            return this;
        }

        public int getPrev_day_booking_count() {
            return prev_day_booking_count;
        }

        public AreaItem setPrev_day_booking_count(int prev_day_booking_count) {
            this.prev_day_booking_count = prev_day_booking_count;
            return this;
        }

        public int getPrev_day_booking_price() {
            return prev_day_booking_price;
        }

        public AreaItem setPrev_day_booking_price(int prev_day_booking_price) {
            this.prev_day_booking_price = prev_day_booking_price;
            return this;
        }

        public int getMain_month_booking_count() {
            return main_month_booking_count;
        }

        public AreaItem setMain_month_booking_count(int main_month_booking_count) {
            this.main_month_booking_count = main_month_booking_count;
            return this;
        }

        public int getMain_month_booking_price() {
            return main_month_booking_price;
        }

        public AreaItem setMain_month_booking_price(int main_month_booking_price) {
            this.main_month_booking_price = main_month_booking_price;
            return this;
        }

        public int getCapsule_count() {
            return capsule_count;
        }

        public AreaItem setCapsule_count(int capsule_count) {
            this.capsule_count = capsule_count;
            return this;
        }
    }

    public static class CapsuleItem {
        public Integer area_id;
        public Long capsule_id;
        public String province;
        public String city;
        public String title;
        public String saler;
        public String operator;
        public Long time;
        public int main_day_booking_count;
        public int main_day_booking_price;
        public int prev_day_booking_count;
        public int prev_day_booking_price;
        public int main_month_booking_count;
        public int main_month_booking_price;

        public String getProvince() {
            return province;
        }

        public CapsuleItem setProvince(String province) {
            this.province = province;
            return this;
        }

        public CapsuleItem(long capsule_id) {
            this.capsule_id = capsule_id;
        }

        public Integer getArea_id() {
            return area_id;
        }

        public CapsuleItem setArea_id(Integer area_id) {
            this.area_id = area_id;
            return this;
        }

        public Long getCapsule_id() {
            return capsule_id;
        }

        public CapsuleItem setCapsule_id(Long capsule_id) {
            this.capsule_id = capsule_id;
            return this;
        }

        public String getCity() {
            return city;
        }

        public CapsuleItem setCity(String city) {
            this.city = city;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public CapsuleItem setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getSaler() {
            return saler;
        }

        public CapsuleItem setSaler(String saler) {
            this.saler = saler;
            return this;
        }

        public String getOperator() {
            return operator;
        }

        public CapsuleItem setOperator(String operator) {
            this.operator = operator;
            return this;
        }

        public Long getTime() {
            return time;
        }

        public CapsuleItem setTime(Long time) {
            this.time = time;
            return this;
        }

        public int getMain_day_booking_count() {
            return main_day_booking_count;
        }

        public CapsuleItem setMain_day_booking_count(int main_day_booking_count) {
            this.main_day_booking_count = main_day_booking_count;
            return this;
        }

        public int getMain_day_booking_price() {
            return main_day_booking_price;
        }

        public CapsuleItem setMain_day_booking_price(int main_day_booking_price) {
            this.main_day_booking_price = main_day_booking_price;
            return this;
        }

        public int getPrev_day_booking_count() {
            return prev_day_booking_count;
        }

        public CapsuleItem setPrev_day_booking_count(int prev_day_booking_count) {
            this.prev_day_booking_count = prev_day_booking_count;
            return this;
        }

        public int getPrev_day_booking_price() {
            return prev_day_booking_price;
        }

        public CapsuleItem setPrev_day_booking_price(int prev_day_booking_price) {
            this.prev_day_booking_price = prev_day_booking_price;
            return this;
        }

        public int getMain_month_booking_count() {
            return main_month_booking_count;
        }

        public CapsuleItem setMain_month_booking_count(int main_month_booking_count) {
            this.main_month_booking_count = main_month_booking_count;
            return this;
        }

        public int getMain_month_booking_price() {
            return main_month_booking_price;
        }

        public CapsuleItem setMain_month_booking_price(int main_month_booking_price) {
            this.main_month_booking_price = main_month_booking_price;
            return this;
        }
    }


    public void makeForSendEmail(LocalDate localDate) throws IOException {
        make(localDate, (areaItemList, capsuleItemList) -> {
            try {
                XSSFWorkbook areaWorkbook = ExcelUtils.export(Arrays.asList(
                        new ExcelUtils.Column<AreaItem>("场地编号") {
                            @Override
                            public String render(AreaItem areaItem) {
                                return String.valueOf(areaItem.area_id);
                            }
                        },
                        new ExcelUtils.Column<AreaItem>("省份") {
                            @Override
                            public String render(AreaItem areaItem) {
                                return String.valueOf(areaItem.province);
                            }
                        },
                        new ExcelUtils.Column<AreaItem>("城市") {
                            @Override
                            public String render(AreaItem areaItem) {
                                return String.valueOf(areaItem.city);
                            }
                        },
                        new ExcelUtils.Column<AreaItem>("场地名称") {
                            @Override
                            public String render(AreaItem areaItem) {
                                return String.valueOf(areaItem.title);
                            }
                        },
                        new ExcelUtils.Column<AreaItem>("舱数") {
                            @Override
                            public Object render(AreaItem areaItem) {
                                return areaItem.capsule_count;
                            }
                        },
                        new ExcelUtils.Column<AreaItem>("订单" + DateUtils.format(localDate.minusDays(1).toDate(), "MM-dd")) {
                            @Override
                            public Object render(AreaItem areaItem) {
                                return areaItem.prev_day_booking_count;
                            }
                        },
                        new ExcelUtils.Column<AreaItem>("收入" + DateUtils.format(localDate.minusDays(1).toDate(), "MM-dd")) {
                            @Override
                            public Object render(AreaItem areaItem) {
                                return areaItem.prev_day_booking_price / 100f;
                            }
                        },
                        new ExcelUtils.Column<AreaItem>("订单" + DateUtils.format(localDate.toDate(), "MM-dd")) {
                            @Override
                            public Object render(AreaItem areaItem) {
                                return areaItem.main_day_booking_count;
                            }
                        },
                        new ExcelUtils.Column<AreaItem>("收入" + DateUtils.format(localDate.toDate(), "MM-dd")) {
                            @Override
                            public Object render(AreaItem areaItem) {
                                return areaItem.main_day_booking_price / 100f;
                            }
                        },
                        new ExcelUtils.Column<AreaItem>(localDate.getMonthOfYear() + "月订单") {
                            @Override
                            public Object render(AreaItem areaItem) {
                                return areaItem.main_month_booking_count;
                            }
                        },
                        new ExcelUtils.Column<AreaItem>(localDate.getMonthOfYear() + "月收入") {
                            @Override
                            public Object render(AreaItem areaItem) {
                                return areaItem.main_month_booking_price / 100f;
                            }
                        },
                        new ExcelUtils.Column<AreaItem>("BD人员") {
                            @Override
                            public String render(AreaItem areaItem) {
                                return areaItem.saler;
                            }
                        },
                        new ExcelUtils.Column<AreaItem>("运营人员") {
                            @Override
                            public String render(AreaItem areaItem) {
                                return areaItem.operator;
                            }
                        }
                ), areaItemList);
                XSSFWorkbook capsuleWorkbook = ExcelUtils.export(Arrays.asList(
                        new ExcelUtils.Column<CapsuleItem>("场地编号") {
                            @Override
                            public String render(CapsuleItem capsuleItem) {
                                return String.valueOf(capsuleItem.area_id);
                            }
                        },
                        new ExcelUtils.Column<CapsuleItem>("省份") {
                            @Override
                            public String render(CapsuleItem capsuleItem) {
                                return String.valueOf(capsuleItem.province);
                            }
                        },
                        new ExcelUtils.Column<CapsuleItem>("城市") {
                            @Override
                            public String render(CapsuleItem capsuleItem) {
                                return String.valueOf(capsuleItem.city);
                            }
                        },
                        new ExcelUtils.Column<CapsuleItem>("场地名称") {
                            @Override
                            public String render(CapsuleItem capsuleItem) {
                                return String.valueOf(capsuleItem.title);
                            }
                        },
                        new ExcelUtils.Column<CapsuleItem>("订单" + DateUtils.format(localDate.minusDays(1).toDate(), "MM-dd")) {
                            @Override
                            public Object render(CapsuleItem capsuleItem) {
                                return capsuleItem.prev_day_booking_count;
                            }
                        },
                        new ExcelUtils.Column<CapsuleItem>("收入" + DateUtils.format(localDate.minusDays(1).toDate(), "MM-dd")) {
                            @Override
                            public Object render(CapsuleItem capsuleItem) {
                                return capsuleItem.prev_day_booking_price / 100f;
                            }
                        },
                        new ExcelUtils.Column<CapsuleItem>("订单" + DateUtils.format(localDate.toDate(), "MM-dd")) {
                            @Override
                            public Object render(CapsuleItem capsuleItem) {
                                return capsuleItem.main_day_booking_count;
                            }
                        },
                        new ExcelUtils.Column<CapsuleItem>("收入" + DateUtils.format(localDate.toDate(), "MM-dd")) {
                            @Override
                            public Object render(CapsuleItem capsuleItem) {
                                return capsuleItem.main_day_booking_price / 100f;
                            }
                        },
                        new ExcelUtils.Column<CapsuleItem>(localDate.getMonthOfYear() + "月订单") {
                            @Override
                            public Object render(CapsuleItem capsuleItem) {
                                return capsuleItem.main_month_booking_count;
                            }
                        },
                        new ExcelUtils.Column<CapsuleItem>(localDate.getMonthOfYear() + "月收入") {
                            @Override
                            public Object render(CapsuleItem capsuleItem) {
                                return capsuleItem.main_month_booking_price / 100f;
                            }
                        },
                        new ExcelUtils.Column<CapsuleItem>("BD人员") {
                            @Override
                            public String render(CapsuleItem capsuleItem) {
                                return capsuleItem.saler;
                            }
                        },
                        new ExcelUtils.Column<CapsuleItem>("运营人员") {
                            @Override
                            public String render(CapsuleItem capsuleItem) {
                                return capsuleItem.operator;
                            }
                        }
                ), capsuleItemList);
                MailService.send(
                        new String[]{
//                                "richard@xiangshuispace.com",
//                                "xubo@xiangshuispace.com",
//                                "chenlei@xiangshuispace.com",
//                                "zhaoyuan@xiangshuispace.com",
                                "hongwei@xiangshuispace.com",
                        },
                        new String[]{
//                                "hongwei@xiangshuispace.com",
                        },
                        "场地运营日报：" + DateUtils.format(localDate.toDate(), "yyyy-MM-dd"),
                        "场地运营日报：" + DateUtils.format(localDate.toDate(), "yyyy-MM-dd"),
                        new MailService.Attachment("场地收入日报." + DateUtils.format(localDate.toDate(), "yyyy-MM-dd") + ".xlsx", ExcelUtils.toBytes(areaWorkbook), "data/xlsx"),
                        new MailService.Attachment("单舱收入日报." + DateUtils.format(localDate.toDate(), "yyyy-MM-dd") + ".xlsx", ExcelUtils.toBytes(capsuleWorkbook), "data/xlsx")
                );
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) throws MessagingException, IOException {
        SpringUtils.init();
        SpringUtils.getBean(SendEmailScheduled.class).makeForSendEmail(new LocalDate().minusDays(1));
    }


}
