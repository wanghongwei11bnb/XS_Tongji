package com.xiangshui.server;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.xiangshui.server.constant.AreaStatusOption;
import com.xiangshui.server.constant.BookingStatusOption;
import com.xiangshui.server.constant.PayTypeOption;
import com.xiangshui.server.crud.Example;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.dao.mysql.DeviceDao;
import com.xiangshui.server.dao.mysql.PrizeQuotaDao;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.domain.mysql.Device;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.server.domain.mysql.PrizeQuota;
import com.xiangshui.server.example.OpExample;
import com.xiangshui.server.mapper.OpMapper;
import com.xiangshui.server.service.MailService;
import com.xiangshui.server.service.PartnerService;
import com.xiangshui.server.service.ServiceUtils;
import com.xiangshui.util.*;
import com.xiangshui.util.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
@Component
public class Test {

    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    AreaDao areaDao;

    @Autowired
    CapsuleDao capsuleDao;

    @Autowired
    PartnerService partnerService;


    @Autowired
    CityDao cityDao;

    @Autowired
    OpMapper opMapper;

    @Autowired
    AreaContractDao areaContractDao;

    @Autowired
    BookingDao bookingDao;
    @Autowired
    PrizeQuotaDao prizeQuotaDao;

    @Autowired
    DeviceDao deviceDao;

    public void testSelect() throws Exception {
        List<UserInfo> userInfoList = userInfoDao.scan(new ScanSpec().withFilterExpression("phone = :phone").withValueMap(new ValueMap().withString(":phone", "11000000014")));
        log.debug(JSON.toJSONString(userInfoList));

        List<Area> areaList = areaDao.scan(new ScanSpec());
        log.debug(JSON.toJSONString(areaList));
    }

    public void test2() throws Exception {

        boolean success = userInfoDao.updateItem(new PrimaryKey("uin", 1601519873), new AttributeUpdate("nick_name").put("sfsfsdttttttfs"));
//        log.debug(success+"");
        UserInfo userInfo = userInfoDao.getItem(new PrimaryKey("uin", 1601519873));
        log.debug(JSON.toJSONString(userInfo));


    }

    /**
     * 导入
     *
     * @throws Exception
     */
    public void importAreaContract() throws Exception {

        Date now = new Date();

        Set<String> fialSet = new HashSet<>();

        List<List<String>> data = ExcelUtils.read(new FileInputStream("/Users/whw/Downloads/分账信息-技术.xlsx"), 0);
        for (List<String> row : data) {
            try {
                String area_title = row.get(0);
                String saler = row.size() > 3 ? row.get(3) : null;
                String customer = row.size() > 4 ? row.get(4) : null;
                String account_ratio_str = row.size() > 5 ? row.get(5) : null;
                String bank_account = row.size() > 6 ? row.get(6) : null;
                String bank_branch = row.size() > 7 ? row.get(7) : null;

                if (StringUtils.isBlank(area_title)) {
                    continue;
                }
                if (StringUtils.isBlank(saler)) {
                    continue;
                }

                OpExample example = new OpExample();
                example.setLimit(1);
                example.createCriteria().andFullnameEqualTo(saler);
                List<Op> opList = opMapper.selectByExample(example);
                if (opList != null && opList.size() > 0) {
                    Op op = opList.get(0);

                    List<Area> areaList = areaDao.scan(new ScanSpec().withScanFilters(new ScanFilter("title").eq(area_title)));

                    if (areaList != null && areaList.size() > 0) {
                        Area area = areaList.get(0);
                        Integer account_ratio = null;
                        try {
                            account_ratio = (int) (Float.valueOf(account_ratio_str) * 100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        AreaContract areaContract = areaContractDao.getItem(new PrimaryKey("area_id", area.getArea_id()));
                        if (areaContract == null) {
                            areaContract = new AreaContract();
                            areaContract.setArea_id(area.getArea_id());
                            areaContract.setCreate_time(now.getTime() / 1000);
                            areaContract.setStatus(0);
                        }
                        areaContract.setSaler(op.getFullname());
                        areaContract.setSaler_city(op.getCity());
                        areaContract.setCustomer(customer);
                        areaContract.setBank_account_name(customer);
                        areaContract.setBank_account(bank_account);
                        areaContract.setBank_branch(bank_branch);
                        areaContract.setAccount_ratio(account_ratio);
                        areaContract.setUpdate_time(now.getTime() / 1000);
                        areaContractDao.putItem(areaContract);
                        continue;
                    }
                }
                fialSet.add(area_title);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fialSet.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        });
    }

    public void test12() {

        Set<Area> areaSet = new HashSet<>();

        List<Area> areaList = areaDao.scan(new ScanSpec().withScanFilters(new ScanFilter("status").ne(AreaStatusOption.offline.value)));
        areaList.forEach(new Consumer<Area>() {
            @Override
            public void accept(Area area) {
                if (areaContractDao.getItem(new PrimaryKey("area_id", area.getArea_id())) == null) {
                    areaSet.add(area);
                }
            }
        });
        areaSet.forEach(new Consumer<Area>() {
            @Override
            public void accept(Area area) {
                System.out.println(area.getTitle());
            }
        });

    }

    public void test() throws Exception {

        List<Area> areaList = areaDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE));
        List<Capsule> capsuleList = capsuleDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(new ScanFilter("is_downline").ne(1)));
        Map<Integer, Area> areaMap = new HashMap<>();
        areaList.forEach(area -> {
            areaMap.put(area.getArea_id(), area);
        });

        List<Capsule> activeCapsuleList = new ArrayList<>();
        capsuleList.forEach(capsule -> {
            if (new Integer(1).equals(capsule.getIs_downline())) {
                return;
            }
            Area area = areaMap.get(capsule.getArea_id());
            if (area == null || !(area.getStatus() == null || area.getStatus() == 0)) {
                return;
            }
            activeCapsuleList.add(capsule);
        });


        activeCapsuleList.sort((o1, o2) -> (int) ((o2.getCreate_time() != null ? o2.getCreate_time() : 0)
                - (o1.getCreate_time() != null ? o1.getCreate_time() : 0)));
        XSSFWorkbook workbook = ExcelUtils.export(Arrays.asList(
                new ExcelUtils.Column<Capsule>("头等舱编号") {
                    @Override
                    public String render(Capsule capsule) {
                        return String.valueOf(capsule.getCapsule_id());
                    }
                },
                new ExcelUtils.Column<Capsule>("城市") {
                    @Override
                    public String render(Capsule capsule) {
                        return areaMap.containsKey(capsule.getArea_id()) ? areaMap.get(capsule.getArea_id()).getCity() : null;
                    }
                },
                new ExcelUtils.Column<Capsule>("场地") {
                    @Override
                    public String render(Capsule capsule) {
                        return areaMap.containsKey(capsule.getArea_id()) ? areaMap.get(capsule.getArea_id()).getTitle() : null;
                    }
                },
                new ExcelUtils.Column<Capsule>("创建时间") {
                    @Override
                    public String render(Capsule capsule) {
                        return capsule.getCreate_time() != null ? DateUtils.format(capsule.getCreate_time() * 1000, "yyyy-MM-dd") : null;
                    }
                }
        ), activeCapsuleList);

        OutputStream outputStream = new FileOutputStream(new File("/Users/whw/Downloads/capsule_create_time.xlsx"));
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }

    /**
     * @throws Exception
     */
    public void areaList() throws Exception {

        List<Area> areaList = areaDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE));
        List<Capsule> capsuleList = capsuleDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(new ScanFilter("is_downline").ne(1)));
        Map<Integer, Area> areaMap = new HashMap<>();
        areaList.forEach(area -> {
            areaMap.put(area.getArea_id(), area);
        });

        List<Capsule> activeCapsuleList = new ArrayList<>();
        capsuleList.forEach(capsule -> {
            if (new Integer(1).equals(capsule.getIs_downline())) {
                return;
            }
            Area area = areaMap.get(capsule.getArea_id());
            if (area == null || !(area.getStatus() == null || area.getStatus() == 0)) {
                return;
            }
            activeCapsuleList.add(capsule);
        });


        activeCapsuleList.sort((o1, o2) -> (int) ((o2.getCreate_time() != null ? o2.getCreate_time() : 0)
                - (o1.getCreate_time() != null ? o1.getCreate_time() : 0)));
        XSSFWorkbook workbook = ExcelUtils.export(Arrays.asList(
                new ExcelUtils.Column<Capsule>("头等舱编号") {
                    @Override
                    public String render(Capsule capsule) {
                        return String.valueOf(capsule.getCapsule_id());
                    }
                },
                new ExcelUtils.Column<Capsule>("城市") {
                    @Override
                    public String render(Capsule capsule) {
                        return areaMap.containsKey(capsule.getArea_id()) ? areaMap.get(capsule.getArea_id()).getCity() : null;
                    }
                },
                new ExcelUtils.Column<Capsule>("场地") {
                    @Override
                    public String render(Capsule capsule) {
                        return areaMap.containsKey(capsule.getArea_id()) ? areaMap.get(capsule.getArea_id()).getTitle() : null;
                    }
                },
                new ExcelUtils.Column<Capsule>("创建时间") {
                    @Override
                    public String render(Capsule capsule) {
                        return capsule.getCreate_time() != null ? DateUtils.format(capsule.getCreate_time() * 1000, "yyyy-MM-dd") : null;
                    }
                }
        ), activeCapsuleList);

        OutputStream outputStream = new FileOutputStream(new File("/Users/whw/Downloads/capsule_create_time.xlsx"));
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }


    /**
     * 部分场地扫码送10元红包活动，数据导出
     */
    public void t() throws Exception {

        Example example = new Example().setLimit(Integer.MAX_VALUE);
        example.getConditions()
                .eq("receive_status", 1)
                .between("create_time",
                        new LocalDate(2019, 12, 20).toDate().getTime() / 1000,
                        new LocalDate(2019, 12, 31).toDate().getTime() / 1000
                )
        ;
        List<PrizeQuota> quotaList = prizeQuotaDao.selectByExample(example);

        List<Area> areaList = areaDao.scan();

        MapOptions<Integer, Area> areaMapOptions = new MapOptions<Integer, Area>(areaList) {
            @Override
            public Integer getPrimary(Area area) {
                return area.getArea_id();
            }
        };


        List<Booking> bookingList = ServiceUtils.division(ListUtils.fieldSet(quotaList, prizeQuota -> prizeQuota.getBooking_id()).toArray(new Long[quotaList.size()]), 100, new CallBackForResult<Long[], List<Booking>>() {

            @Override
            public List<Booking> run(Long[] longs) {
                return bookingDao.batchGetItem("booking_id", longs);
            }
        }, new Long[0]);

//        List<Booking> bookingList = bookingDao.batchGetItem("booking_id", ListUtils.fieldSet(quotaList, prizeQuota -> prizeQuota.getBooking_id()).toArray());

        MapOptions<Long, Booking> bookingMapOptions = new MapOptions<Long, Booking>(bookingList) {
            @Override
            public Long getPrimary(Booking booking) {
                return booking.getBooking_id();
            }
        };
        ExcelUtils.export(Arrays.asList(
                new ExcelUtils.Column<PrizeQuota>("uin") {
                    @Override
                    public Object render(PrizeQuota prizeQuota) {
                        return prizeQuota.getUin();
                    }
                },
                new ExcelUtils.Column<PrizeQuota>("booking_id") {
                    @Override
                    public Object render(PrizeQuota prizeQuota) {
                        return prizeQuota.getBooking_id();
                    }
                },
                new ExcelUtils.Column<PrizeQuota>("area") {
                    @Override
                    public Object render(PrizeQuota prizeQuota) {
                        return areaMapOptions.get(bookingMapOptions.get(prizeQuota.getBooking_id()).getArea_id()).getTitle();
                    }
                }
        ), quotaList, new File("/Users/whw/Downloads/10元红包.xlsx"));


    }

    public void bookingList() {


    }


    public void exportBookingList(ScanSpec scanSpec, String outputFilePath) throws IOException {

        XSSFWorkbook workbook = ExcelUtils.export(Arrays.asList(
                new ExcelUtils.Column<Booking>("订单编号") {
                    @Override
                    public Object render(Booking booking) {
                        return booking.getBooking_id();
                    }
                },

                new ExcelUtils.Column<Booking>("创建时间") {
                    @Override
                    public String render(Booking booking) {
                        return booking.getCreate_time() != null ? DateUtils.format(booking.getCreate_time() * 1000, "yyyy-MM-dd HH:mm") : null;
                    }
                },
                new ExcelUtils.Column<Booking>("结束时间") {
                    @Override
                    public String render(Booking booking) {
                        return booking.getEnd_time() != null ? DateUtils.format(booking.getEnd_time() * 1000, "yyyy-MM-dd HH:mm") : null;
                    }
                },
                new ExcelUtils.Column<Booking>("订单状态", (total, booking) -> total + 1) {
                    @Override
                    public String render(Booking booking) {
                        return Option.getActiveText(BookingStatusOption.options, booking.getStatus());
                    }
                },
                new ExcelUtils.Column<Booking>("订单总金额", (total, booking) -> booking != null && booking.getFinal_price() != null ? total + booking.getFinal_price() * 1f / 100 : total) {
                    @Override
                    public String render(Booking booking) {
                        return String.valueOf(booking.getFinal_price() != null ? booking.getFinal_price() / 100f : null);
                    }
                },
                new ExcelUtils.Column<Booking>("非会员付费金额", (total, booking) -> booking != null && booking.getUse_pay() != null ? total + booking.getUse_pay() * 1f / 100 : total) {
                    @Override
                    public String render(Booking booking) {
                        return String.valueOf(booking.getUse_pay() != null ? booking.getUse_pay() / 100f : null);
                    }
                },
                new ExcelUtils.Column<Booking>("充值部分", (total, booking) -> booking != null && booking.getFrom_charge() != null ? total + booking.getFrom_charge() * 1f / 100 : total) {
                    @Override
                    public String render(Booking booking) {
                        return String.valueOf(booking.getFrom_charge() != null ? booking.getFrom_charge() / 100f : null);
                    }
                },
                new ExcelUtils.Column<Booking>("赠送部分", (total, booking) -> booking != null && booking.getFrom_bonus() != null ? total + booking.getFrom_bonus() * 1f / 100 : total) {
                    @Override
                    public String render(Booking booking) {
                        return String.valueOf(booking.getFrom_bonus() != null ? booking.getFrom_bonus() / 100f : null);
                    }
                },
                new ExcelUtils.Column<Booking>("支付方式") {
                    @Override
                    public String render(Booking booking) {
                        return Option.getActiveText(PayTypeOption.options, booking.getPay_type());
                    }
                },
                new ExcelUtils.Column<Booking>("是否使用月卡") {
                    @Override
                    public String render(Booking booking) {
                        return new Integer(1).equals(booking.getMonth_card_flag()) ? "是" : "否";
                    }
                },
                new ExcelUtils.Column<Booking>("场地编号") {
                    @Override
                    public Object render(Booking booking) {
                        return booking.getArea_id();
                    }
                },
                new ExcelUtils.Column<Booking>("用户编号") {
                    @Override
                    public Object render(Booking booking) {
                        return booking.getUin();
                    }
                },
                new ExcelUtils.Column<Booking>("guohang_confirm_id") {
                    @Override
                    public Object render(Booking booking) {
                        return booking.getGuohang_confirm_id();
                    }
                },
                new ExcelUtils.Column<Booking>("guohang_order_sn") {
                    @Override
                    public Object render(Booking booking) {
                        return booking.getGuohang_order_sn();
                    }
                }
        ), bookingDao.scan(scanSpec));
        OutputStream outputStream = new FileOutputStream(new File(outputFilePath));
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }

    public static void main(String[] args) throws Exception {


        SpringUtils.init();
//        SpringUtils.getBean(Test.class).exportBookingList(
//                new ScanSpec().withMaxResultSize(Integer.MAX_VALUE)
//                        .withScanFilters(
//                                new ScanFilter("guohang_confirm_id").exists()
//                        )
//                , "/Users/whw/Downloads/国航订单.xlsx"
//        );


//        AreaDao areaDao = SpringUtils.getBean(AreaDao.class);
//        for (Area area : areaDao.scan()) {
//            if (area.getStatus() == null) {
//                area.setStatus(0);
//                areaDao.updateItem(new PrimaryKey("area_id", area.getArea_id()), area, new String[]{
//                        "status"
//                });
//                log.info(JSON.toJSONString(area));
//            }
//        }


//        CapsuleDao capsuleDao = SpringUtils.getBean(CapsuleDao.class);
//        List<Capsule> capsuleList = capsuleDao.scan(new ScanSpec().withScanFilters(new ScanFilter("area_id").eq(1100001)));


        DeviceDao deviceDao = SpringUtils.getBean(DeviceDao.class);
        CapsuleDao capsuleDao = SpringUtils.getBean(CapsuleDao.class);

//        for (List<String> row : ExcelUtils.read(System.class.getResourceAsStream("/所有设备.xlsx"), 0)) {
//            try {
//                log.info(row.get(2) + ":" + row.get(6) + ":" + row.get(7));
//                Device device = new Device();
//                device.setId(row.get(2));
//                device.setBelong(row.get(6));
//                device.setRemark(row.get(7));
//                device.setBelong(new String(device.getBelong().getBytes(), "utf-8"));
//                deviceDao.insertSelective(device, null);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }


        for (Capsule capsule : capsuleDao.scan(new ScanSpec().withScanFilters(new ScanFilter("area_id").eq(1100001)))) {
            log.info(capsule.getCapsule_id()+"");
            capsuleDao.deleteItem(new PrimaryKey("capsule_id",capsule.getCapsule_id()));
        }


        log.info("finish");

    }
}
