package com.xiangshui.server.job;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.service.ServiceUtils;
import com.xiangshui.util.*;
import com.xiangshui.util.spring.SpringUtils;
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
public class UserExports {

    @Autowired
    AreaDao areaDao;

    @Autowired
    CapsuleDao capsuleDao;

    @Autowired
    BookingDao bookingDao;

    @Autowired
    CityDao cityDao;

    @Autowired
    UserInfoDao userInfoDao;

    @Autowired
    UserWalletDao userWalletDao;

    @Autowired
    MonthCardRecodeDao monthCardRecodeDao;


    public static void main(String[] args) throws Exception {
        SpringUtils.init();
        SpringUtils.getBean(UserExports.class).test();
    }

    private void test() throws IOException {
        MapOptions<Integer, Area> areaMapOptions = new MapOptions<Integer, Area>(areaDao.scan()) {
            @Override
            public Integer getPrimary(Area area) {
                return area.getArea_id();
            }
        };
        Map<Integer, String> uinCityMap = new HashMap<>();
        List<MonthCardRecode> monthCardRecodeList = monthCardRecodeDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(
                new ScanFilter("end_time").gt(System.currentTimeMillis() / 1000)
        ));
        MapOptions<Integer, MonthCardRecode> monthCardRecodeMapOptions = new MapOptions<Integer, MonthCardRecode>(monthCardRecodeList) {
            @Override
            public Integer getPrimary(MonthCardRecode monthCardRecode) {
                return monthCardRecode.getUin();
            }
        };
        for (MonthCardRecode monthCardRecode : monthCardRecodeList) {
            if (StringUtils.isNotBlank(monthCardRecode.getCity()) && !uinCityMap.containsKey(monthCardRecode.getUin())) {
                uinCityMap.put(monthCardRecode.getUin(), monthCardRecode.getCity());
            }
        }
        List<UserWallet> userWalletList = userWalletDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(
                new ScanFilter("charge").gt(0)
        ));
        userWalletList.addAll(userWalletDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(
                new ScanFilter("uin").in(ListUtils.fieldSet(monthCardRecodeList, MonthCardRecode::getUin).toArray())
        )));

        userWalletList = new ArrayList<>(new MapOptions<Integer, UserWallet>(userWalletList) {
            @Override
            public Integer getPrimary(UserWallet userWallet) {
                return userWallet.getUin();
            }
        }.values());


//        List<UserInfo> userInfoList = userInfoDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(
//                new ScanFilter("uin").in(ListUtils.fieldSet(userWalletList, UserWallet::getUin).toArray())
//        ));
//        MapOptions<Integer, UserWallet> userWalletMapOptions = new MapOptions<Integer, UserWallet>(userWalletList) {
//            @Override
//            public Integer getPrimary(UserWallet userWallet) {
//                return userWallet.getUin();
//            }
//        };
//        for (UserInfo userInfo : userInfoList) {
//            if (!uinCityMap.containsKey(userInfo.getUin())) {
//                List<Booking> bookingList = bookingDao.scan(new ScanSpec().withMaxResultSize(1).withScanFilters(
//                        new ScanFilter("uin").eq(userInfo.getUin())
//                ));
//                if (bookingList.size() > 0) {
//                    uinCityMap.put(userInfo.getUin(), areaMapOptions.tryValueForResult(bookingList.get(0).getArea_id(), area -> area.getCity(), null));
//                }
//            }
//        }


        List<Booking> bookingList = bookingDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(
                new ScanFilter("create_time").gt(new LocalDate(2019, 1, 1).toDate().getTime() / 1000)
        ));

        for (UserWallet userWallet : userWalletList) {
            if (!uinCityMap.containsKey(userWallet.getUin())) {
                List<Booking> bookingList2 = ListUtils.filter(bookingList, booking -> booking.getUin().equals(userWallet.getUin()));
                if (bookingList2.size() > 0) {
                    uinCityMap.put(userWallet.getUin(), areaMapOptions.tryValueForResult(bookingList2.get(0).getArea_id(), area -> area.getCity(), null));
                }
            }
        }
        ExcelUtils.export(Arrays.asList(
                new ExcelUtils.Column<UserWallet>("uin") {
                    @Override
                    public Object render(UserWallet userInfo) {
                        return userInfo.getUin();
                    }
                },
                new ExcelUtils.Column<UserWallet>("城市") {
                    @Override
                    public Object render(UserWallet userInfo) {
                        return uinCityMap.get(userInfo.getUin());
                    }
                },
                new ExcelUtils.Column<UserWallet>("充值余额") {
                    @Override
                    public Object render(UserWallet userInfo) {
                        return userInfo.getCharge() / 100f;
                    }
                },
                new ExcelUtils.Column<UserWallet>("月卡到期时间") {
                    @Override
                    public Object render(UserWallet userInfo) {
                        return monthCardRecodeMapOptions.tryValueForResult(userInfo.getUin(), monthCardRecode -> DateUtils.format(monthCardRecode.getEnd_time() * 1000, "yyyy-MM-dd"), null);
                    }
                }
        ), userWalletList, new File("/Users/whw/Downloads/userInfo.xlsx"));


    }

}
