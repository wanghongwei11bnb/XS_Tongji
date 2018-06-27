package com.xiangshui.server.service;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.BaseDynamoDao;
import com.xiangshui.server.dao.MonthCardRecodeDao;
import com.xiangshui.server.domain.MonthCardRecode;
import com.xiangshui.server.exception.XiangShuiException;
import org.apache.commons.lang3.StringUtils;
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

}
