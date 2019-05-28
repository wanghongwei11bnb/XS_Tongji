package com.xiangshui.server.service;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.BaseDynamoDao;
import com.xiangshui.server.dao.MonthCardRecodeDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.dao.UserWalletDao;
import com.xiangshui.server.domain.MonthCardRecode;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.domain.UserWallet;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.util.spring.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class MonthCardService {

    @Value("${isdebug}")
    protected boolean debug;

    @Autowired
    MonthCardRecodeDao monthCardRecodeDao;

    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    UserWalletDao userWalletDao;


    public MonthCardRecode getMonthCardRecodeByUin(int uin, String[] fields) {
        ScanSpec scanSpec = new ScanSpec().withScanFilters(new ScanFilter("uin").eq(uin)).withMaxResultSize(1);
        if (fields != null && fields.length > 0) {
            scanSpec.withAttributesToGet(fields);
        }
        List<MonthCardRecode> monthCardRecodeList = monthCardRecodeDao.scan(scanSpec);
        if (monthCardRecodeList != null && monthCardRecodeList.size() > 0) {
            return monthCardRecodeList.get(0);
        } else {
            return null;
        }
    }

    public MonthCardRecode getMonthCardRecodeByPhone(String phone, String[] fields) {
        if (StringUtils.isBlank(phone)) {
            throw new XiangShuiException("手机号码不能为空");
        }

        long card_no = Long.valueOf(phone);
        GetItemSpec getItemSpec = new GetItemSpec();
        getItemSpec.withPrimaryKey(new PrimaryKey("card_no", card_no));
        if (fields != null && fields.length > 0) {
            getItemSpec.withAttributesToGet(fields);
        }
        return monthCardRecodeDao.getItem(getItemSpec);
    }

    public List<MonthCardRecode> search(MonthCardRecode criteria,
                                        Date create_date_start, Date create_date_end,
                                        Date end_time_start, Date end_time_end,
                                        String[] fields, boolean download) throws NoSuchFieldException, IllegalAccessException {
        ScanSpec scanSpec = new ScanSpec();
        if (fields != null && fields.length > 0) {
            scanSpec.withAttributesToGet(fields);
        }
        List<ScanFilter> scanFilterList = monthCardRecodeDao.makeScanFilterList(criteria, new String[]{
                "card_no",
                "uin",
                "city",
        });
        if (scanFilterList == null) {
            scanFilterList = new ArrayList<>();
        }
        monthCardRecodeDao.appendDateRangeFilter(scanFilterList, "end_time", end_time_start, end_time_end);
        monthCardRecodeDao.appendDateRangeFilter(scanFilterList, "date_time", create_date_start, create_date_end);
        if (scanFilterList.size() > 0) {
            scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        }
        if (download) {
            scanSpec.withMaxResultSize(BaseDynamoDao.maxDownloadSize);
        }
        return monthCardRecodeDao.scan(scanSpec);
    }

    public void deleteMonthCard(int uin) throws Exception {
        if (uin <= 0) throw new XiangShuiException("uin必须大于0");
        UserInfo userInfo = userInfoDao.getItem(new PrimaryKey("uin", uin));
        if (userInfo == null) throw new XiangShuiException("userInfo未找到");
        MonthCardRecode monthCardRecode = monthCardRecodeDao.getItem(new PrimaryKey("card_no", Long.valueOf(userInfo.getPhone())));
        if (monthCardRecode == null) throw new XiangShuiException("monthCardRecode未找到");
        monthCardRecodeDao.deleteItem(new PrimaryKey("card_no", Long.valueOf(userInfo.getPhone())));

        UserWallet userWallet = userWalletDao.getItem(new PrimaryKey("uin", userInfo.getUin()));
        if (userWallet != null) {
            userWallet.setMonth_card_flag(0);
            userWalletDao.updateItem(new PrimaryKey("uin", userInfo.getUin()), userWallet, new String[]{"month_card_flag"});
        }
    }

    public void deleteMonthCard(String phone) throws Exception {
        if (StringUtils.isBlank(phone)) throw new XiangShuiException("phone不能为空");
        List<UserInfo> userInfoList = userInfoDao.scan(new ScanSpec().withScanFilters(
                new ScanFilter("phone").eq(phone)
        ));
        if (userInfoList == null || userInfoList.size() == 0) throw new XiangShuiException("userInfo未找到");
        UserInfo userInfo = userInfoList.get(0);
        deleteMonthCard(userInfo.getUin());
    }

    public void appendMonthCardTo(UserInfo userInfo, LocalDate localDate) throws Exception {
        if (localDate == null) throw new XiangShuiException("localDate不能为空");
        if (userInfo == null) throw new XiangShuiException("userInfo未找到");
        MonthCardRecode monthCardRecode = monthCardRecodeDao.getItem(new PrimaryKey("card_no", Long.valueOf(userInfo.getPhone())));
        UserWallet userWallet = userWalletDao.getItem(new PrimaryKey("uin", userInfo.getUin()));
        if (userWallet == null) throw new XiangShuiException("userWallet未找到");
        if (monthCardRecode != null) {
            monthCardRecode.setEnd_time(localDate.plusDays(1).toDate().getTime() / 1000 - 1);
            monthCardRecodeDao.updateItem(new PrimaryKey("card_no", Long.valueOf(userInfo.getPhone())), monthCardRecode, new String[]{"end_time"});
        } else {
            monthCardRecode = new MonthCardRecode()
                    .setCard_no(Long.valueOf(userInfo.getPhone()))
                    .setUin(userInfo.getUin())
                    .setEnd_time(localDate.plusDays(1).toDate().getTime() / 1000 - 1)
                    .setDate_time(new LocalDate().toDate().getTime() / 1000);
            monthCardRecodeDao.putItem(monthCardRecode);
        }
        userWallet.setMonth_card_flag(1);
        userWalletDao.updateItem(new PrimaryKey("uin", userInfo.getUin()), userWallet, new String[]{"month_card_flag"});
    }


    public void appendMonthCardTo(int uin, LocalDate localDate) throws Exception {
        if (uin <= 0) throw new XiangShuiException("uin必须大于0");
        if (localDate == null) throw new XiangShuiException("localDate不能为空");
        UserInfo userInfo = userInfoDao.getItem(new PrimaryKey("uin", uin));
        if (userInfo == null) throw new XiangShuiException("userInfo未找到");
        appendMonthCardTo(userInfo, localDate);
    }

    public void appendMonthCardTo(String phone, LocalDate localDate) throws Exception {

        if (StringUtils.isBlank(phone)) throw new XiangShuiException("phone不能为空");
        if (localDate == null) throw new XiangShuiException("localDate不能为空");
        List<UserInfo> userInfoList = userInfoDao.scan(new ScanSpec().withScanFilters(
                new ScanFilter("phone").eq(phone)
        ));
        if (userInfoList == null || userInfoList.size() == 0) throw new XiangShuiException("userInfo未找到");
        UserInfo userInfo = userInfoList.get(0);
        appendMonthCardTo(userInfo, localDate);
    }

    public static void main(String[] args) throws Exception {
        SpringUtils.init();
        MonthCardService service = SpringUtils.getBean(MonthCardService.class);
//        service.appendMonthCardTo("13911198403", new LocalDate(2019, 6, 24));


        service.deleteMonthCard("13718275564");

    }
}
