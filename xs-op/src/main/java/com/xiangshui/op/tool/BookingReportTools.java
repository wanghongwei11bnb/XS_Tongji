package com.xiangshui.op.tool;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.scheduled.CacheScheduled;
import com.xiangshui.server.dao.BookingDao;
import com.xiangshui.server.dao.ChargeRecordDao;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.service.ServiceUtils;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.ListUtils;
import com.xiangshui.util.MapOptions;
import com.xiangshui.util.spring.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BookingReportTools {

    public static final long EXPORT_GROUP_BY_AREA = (long) Math.pow(2, 1);


    @Autowired
    CacheScheduled cacheScheduled;
    @Autowired
    BookingDao bookingDao;
    @Autowired
    ChargeRecordDao chargeRecordDao;


    public BookingReportResult make(List<Booking> bookingList, long EXPORT) {
        BookingReportResult reportResult = new BookingReportResult();
        if (bookingList != null) {
            List<ChargeRecord> chargeRecordList = chargeRecordDao.scan(new ScanSpec().withScanFilters(
                    new ScanFilter("subject").in(new String[]{"享+-月卡充值", "享+-季卡充值"}),
                    new ScanFilter("bill_area_id").in(ListUtils.fieldSet(bookingList, Booking::getArea_id).toArray())
            ));
            MapOptions<Long, ChargeRecord> chargeRecordMapOptions = new MapOptions<Long, ChargeRecord>(chargeRecordList) {
                @Override
                public Long getPrimary(ChargeRecord chargeRecord) {
                    return chargeRecord.getBill_booking_id();
                }
            };
            for (Booking booking : bookingList) {
                if (booking == null) continue;
                reduce(reportResult, booking, chargeRecordMapOptions.get(booking.getBooking_id()));
                if ((EXPORT & EXPORT_GROUP_BY_AREA) == EXPORT_GROUP_BY_AREA) {
                    reduce_group_by_area(reportResult, booking, chargeRecordMapOptions.get(booking.getBooking_id()));
                }
            }
            reportResult.userInfoList = cacheScheduled.userInfoMapOptions.selectByPrimarys(reportResult.uinSet);
            reportResult.areaList = cacheScheduled.areaMapOptions.selectByPrimarys(reportResult.areaIdSet);
            reportResult.capsuleList = cacheScheduled.capsuleMapOptions.selectByPrimarys(reportResult.capsuleIdSet);
        }
        return reportResult;
    }


    public void reduce(BookingReportResult reportResult, Booking booking, ChargeRecord chargeRecord) {
        if (booking == null) return;
        reportResult.countBooking++;
        if (booking.getUin() != null) {
            reportResult.uinSet.add(booking.getUin());
            reportResult.countBookingMapForUin.incr(booking.getUin());
        }
        if (booking.getArea_id() != null) {
            reportResult.areaIdSet.add(booking.getArea_id());
            reportResult.countBookingMapForArea.incr(booking.getArea_id());
        }
        if (booking.getCapsule_id() != null) {
            reportResult.capsuleIdSet.add(booking.getCapsule_id());
            reportResult.countBookingMapForCapsule.incr(booking.getCapsule_id());
        }
        if (StringUtils.isNotBlank(booking.getReq_from())) {
            reportResult.countBookingMapForReqFrom.incr(booking.getReq_from());
        }

        if (new Integer(4).equals(booking.getStatus())) {
            if (booking.getFrom_charge() != null) {
                reportResult.totalAmount += booking.getFrom_charge();
            }
            if (booking.getUse_pay() != null) {
                reportResult.totalAmount += booking.getUse_pay();
            }
            if (chargeRecord != null && new Integer(1).equals(chargeRecord.getStatus()) && chargeRecord.getPrice() != null) {
                reportResult.monthCardAmount += chargeRecord.getPrice();
            }
        }

    }

    public void reduce_group_by_area(BookingReportResult reportResult, Booking booking, ChargeRecord chargeRecord) {
        if (booking == null) return;
        if (booking.getArea_id() == null) return;
        if (!reportResult.reportResultMapGroupForArea.containsKey(booking.getArea_id())) {
            reportResult.reportResultMapGroupForArea.put(booking.getArea_id(), new BookingReportResult());
        }
        reduce(reportResult.reportResultMapGroupForArea.get(booking.getArea_id()), booking, chargeRecord);
    }


    public static class BookingReportResult {

        public Integer countBooking;
        public Integer totalAmount;
        public Integer monthCardAmount;

        public Set<Integer> uinSet;
        public Set<Integer> areaIdSet;
        public Set<Long> capsuleIdSet;


        public CountMap<Integer> countBookingMapForUin;
        public CountMap<Integer> countBookingMapForArea;
        public CountMap<Long> countBookingMapForCapsule;
        public CountMap<String> countBookingMapForReqFrom;

        public Map<Integer, BookingReportResult> reportResultMapGroupForArea;
        public Map<Long, BookingReportResult> reportResultMapGroupForCapsule;


        public BookingReportResult() {
            countBooking = 0;
            totalAmount = 0;
            monthCardAmount = 0;

            uinSet = new HashSet<>();
            areaIdSet = new HashSet<>();
            capsuleIdSet = new HashSet<>();

            countBookingMapForUin = new CountMap<>();
            countBookingMapForArea = new CountMap<>();
            countBookingMapForCapsule = new CountMap<>();
            countBookingMapForReqFrom = new CountMap<>();

            reportResultMapGroupForArea = new HashMap<>();
            reportResultMapGroupForCapsule = new HashMap<>();


        }

        public List<UserInfo> userInfoList;
        public List<Area> areaList;
        public List<Capsule> capsuleList;

    }

    public static class CountMap<K> extends HashMap<K, Integer> {
        public void incr(K k) {
            if (this.containsKey(k)) {
                this.put(k, this.get(k) + 1);
            } else {
                this.put(k, 1);
            }
        }
    }


    public void test() {

        List<Booking> bookingList = bookingDao.scan();

        BookingReportResult reportResult = make(bookingList, EXPORT_GROUP_BY_AREA);

        System.out.println(JSON.toJSONString(reportResult));
    }


    public static void main(String[] args) {
        SpringUtils.init();
        SpringUtils.getBean(BookingReportTools.class).test();
    }


}
