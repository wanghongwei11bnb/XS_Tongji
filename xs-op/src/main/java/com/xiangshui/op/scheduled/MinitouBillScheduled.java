package com.xiangshui.op.scheduled;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.AreaContractDao;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BookingDao;
import com.xiangshui.server.dao.CapsuleDao;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.util.web.result.CodeMsg;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MinitouBillScheduled implements InitializingBean {

    @Autowired
    AreaDao areaDao;
    @Autowired
    CapsuleDao capsuleDao;
    @Autowired
    BookingDao bookingDao;
    @Autowired
    AreaContractDao areaContractDao;
    @Autowired
    CountCapsuleScheduled countCapsuleScheduled;


    public Set<Long> capsuleIdSet = new HashSet<>();

    public List<AreaContract> areaContractList = new ArrayList<>();


    public void makeBill(long capsule_id, int year, int month) {

        Capsule capsule = capsuleDao.getItem(new PrimaryKey("capsule_id", capsule_id));
        if (capsule == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }
        Area area = areaDao.getItem(new PrimaryKey("area_id", capsule.getArea_id()));
        if (area == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }
        AreaContract areaContract = areaContractDao.getItem(new PrimaryKey("area_id", area.getArea_id()));
        long dateTimeStart = new LocalDate(year, month, 1).toDate().getTime() / 1000;
        long dateTimeEnd = new LocalDate(year, month, 1).plusMonths(1).toDate().getTime() / 1000 - 1;
        ScanSpec scanSpec = new ScanSpec();
        List<ScanFilter> scanFilterList = new ArrayList<>();
        scanFilterList.add(new ScanFilter("create_time").between(dateTimeStart, dateTimeEnd));
        scanFilterList.add(new ScanFilter("status").eq(4));
        scanFilterList.add(new ScanFilter("final_price").gt(0));
        scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        List<Booking> bookingList = bookingDao.scan(scanSpec);


        MinitouBill minitouBill = new MinitouBill();
        minitouBill.setBill_id(capsule_id * 1000000 + year * 100 + month);
        minitouBill.setCapsule_id(capsule_id).setYear(year).setMonth(month);

        int final_price = 0;
        int account_ratio = areaContract != null && areaContract.getAccount_ratio() != null ? areaContract.getAccount_ratio() : 0;
        int ratio_price = 0;
        int rent_price = 0;
        int other_price = 0;
        int net_price = 0;

        bookingList.forEach(booking -> {





        });


    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Set<Long> capsuleIdSet = new HashSet<>();
        IOUtils.readLines(this.getClass().getResourceAsStream("/mnt_capsule_id.txt"), "UTF-8").forEach(string -> {
            capsuleIdSet.add(Long.valueOf(string));
        });
        this.capsuleIdSet = capsuleIdSet;
    }
}
