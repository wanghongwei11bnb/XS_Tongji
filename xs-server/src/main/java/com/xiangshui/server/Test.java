package com.xiangshui.server;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.xiangshui.server.constant.AreaStatusOption;
import com.xiangshui.server.crud.Example;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.dao.mysql.PrizeQuotaDao;
import com.xiangshui.server.domain.*;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
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

    public static void main(String[] args) throws Exception {


//        SpringUtils.init();
//        SpringUtils.getBean(Test.class).bookingList();
//        SpringUtils.getBean(Test.class).importAreaContract();
//        SpringUtils.getBean(MailService.class).sendHtml("973119204@qq.com", "test", "<html><head></head><body><h1>hello!!spring html Mail</h1></body></html>");


//        System.out.println(System.getProperties().getProperty("user.home"));
//        System.out.println(System.getProperties().getProperty("user.dir"));


        log.info(System.currentTimeMillis() + "");
        log.info((System.currentTimeMillis() - new LocalDate(2020, 1, 1).toDate().getTime()) + "");
        log.info((new LocalDate(2120, 1, 1).toDate().getTime() - new LocalDate(2020, 1, 1).toDate().getTime()) + "");

    }
}
