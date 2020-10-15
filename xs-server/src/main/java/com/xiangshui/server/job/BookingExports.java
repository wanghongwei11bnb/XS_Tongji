package com.xiangshui.server.job;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BookingDao;
import com.xiangshui.server.dao.CapsuleDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.MapOptions;
import com.xiangshui.util.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class BookingExports {

    @Autowired
    AreaDao areaDao;

    @Autowired
    CapsuleDao capsuleDao;

    @Autowired
    BookingDao bookingDao;

    //    杭州58数据需求 请提供月度订单量 场地编号 3301020 时间  2019.11-2020.7
    public void test123() throws Exception {

        ScanSpec scanSpec = new ScanSpec().withMaxResultSize(Integer.MAX_VALUE);
        scanSpec.withScanFilters(
                new ScanFilter("create_time").between(
                        new LocalDate(2019, 11, 1).toDate().getTime() / 1000,
                        new LocalDate(2020, 7, 1).plusMonths(1).toDate().getTime() / 1000
                ),
                new ScanFilter("area_id").eq(3301020)
        );
        List<Booking> bookingList = bookingDao.scan(scanSpec);
        Map<Integer, Integer> map = new TreeMap<>();

        for (int month : new int[]{
                201909,
                201910,
                201911,
                201912,
                202001,
                202002,
                202003,
                202004,
                202005,
                202006,
                202007,
        }) {
            map.put(month, 0);
        }

        for (Booking booking : bookingList) {
            int month = Integer.valueOf(DateUtils.format(new Date(booking.getCreate_time() * 1000), "yyyyMM"));
            map.put(month, map.get(month) + 1);
        }

        for (Integer month : map.keySet()) {
            log.info(month + " - " + map.get(month));
        }

    }


    public void test() throws Exception {


        ScanSpec scanSpec = new ScanSpec().withMaxResultSize(Integer.MAX_VALUE);
        scanSpec.withScanFilters(
                new ScanFilter("create_time").between(
                        new LocalDate(2019, 11, 1).toDate().getTime() / 1000,
                        new LocalDate(2020, 7, 1).plusMonths(1).toDate().getTime() / 1000
                ),
                new ScanFilter("area_id").in(3301021)
        );

//        MapOptions<Integer, Area> areaMapOptions = new MapOptions<Integer, Area>(areaDao.scan()) {
//            @Override
//            public Integer getPrimary(Area area) {
//                return area.getArea_id();
//            }
//        };
        List<Booking> bookingList = bookingDao.scan(scanSpec);

        Map<Integer, Integer> map1 = new TreeMap<>();
        Map<Integer, Integer> map2 = new TreeMap<>();

        for (int month : new int[]{
                201911,
                201912,
                202001,
                202002,
                202003,
                202004,
                202005,
                202006,
                202007,
        }) {
            map1.put(month, 0);
            map2.put(month, 0);
        }

        Set<Long> set1 = new HashSet<>(Arrays.asList(
                3301021001l,
                3301021002l,
                3301021003l,
                3301021004l,
                3301021005l,
                3301021006l,
                3301021007l,
                3301021008l,
                3301021009l,
                33010210010l
        ));
        Set<Long> set2 = new HashSet<>(Arrays.asList(
                3301021011l,
                3301021012l,
                3301021013l,
                3301021014l
        ));


        for (Booking booking : bookingList) {
            int month = Integer.valueOf(DateUtils.format(new Date(booking.getCreate_time() * 1000), "yyyyMM"));
            Map<Integer, Integer> map = null;
            Set<Long> set = null;
            if (set1.contains(booking.getCapsule_id())) {
                set = set1;
                map = map1;
            } else if (set2.contains(booking.getCapsule_id())) {
                set = set2;
                map = map2;
            }
            if (set != null) {
                map.put(month, map.get(month) + 1);
            }
        }


        log.info(JSON.toJSONString(map1));
        log.info(JSON.toJSONString(map2));

    }


    public void df() {
        List<Booking> bookingList = bookingDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(
                new ScanFilter("area_id").in(2501003, 2501039, 2501002, 2501001, 2501005, 2501004)
        ));




    }


    public void ex(List<Booking> bookingList){



    }


    public static void main(String[] args) throws Exception {
        SpringUtils.init();
        SpringUtils.getBean(BookingExports.class).test123();


    }

}
