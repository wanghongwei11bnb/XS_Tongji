package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.scheduled.CountCapsuleScheduled;
import com.xiangshui.op.scheduled.MinitouBillScheduled;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BookingDao;
import com.xiangshui.server.dao.CapsuleDao;
import com.xiangshui.server.dao.MinitouBillDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.MinitouBill;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.ServiceUtils;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

import static com.xiangshui.op.annotation.AuthRequired.auth_minitou_investor;

@Controller
public class MinitouBillController extends BaseController {


    @Autowired
    AreaDao areaDao;
    @Autowired
    CapsuleDao capsuleDao;
    @Autowired
    BookingDao bookingDao;
    @Autowired
    MinitouBillDao minitouBillDao;
    @Autowired
    CountCapsuleScheduled countCapsuleScheduled;
    @Autowired
    MinitouBillScheduled minitouBillScheduled;


    @Autowired
    AreaService areaService;

    @Menu("订单列表")
    @GetMapping("/mnt_booking_manage")
    @AuthRequired(auth_minitou_investor)
    public String index(HttpServletRequest request) {
        setClient(request);
        return "mnt_booking_manage";
    }

    @GetMapping("/api/mnt/booking/search")
    @ResponseBody
    public Result booking_search(Date date) {
        if (date == null) {
            return new Result(-1, "请选择日期");
        }
        LocalDate localDateStart = new LocalDate(date);
        ScanSpec scanSpec = new ScanSpec().withMaxResultSize(Integer.MAX_VALUE);
        List<ScanFilter> scanFilterList = new ArrayList<>();
        scanFilterList.add(new ScanFilter("create_time").between(localDateStart.toDate().getTime() / 1000, localDateStart.plusDays(1).toDate().getTime() / 1000 - 1));
        scanFilterList.add(new ScanFilter("status").eq(4));
        scanFilterList.add(new ScanFilter("final_price").gt(0));
        scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        List<Booking> bookingList = bookingDao.scan(scanSpec);
        List<Booking> activeBookingList = new ArrayList<>();
        if (bookingList.size() > 0) {
            bookingList.forEach(booking -> {
                if (minitouBillScheduled.capsuleIdSet.contains(booking.getCapsule_id())) {
                    activeBookingList.add(booking);
                }
            });
        }
        return new Result(CodeMsg.SUCCESS)
                .putData("bookingList", activeBookingList)
                .putData("areaList", areaService.getAreaListByBooking(activeBookingList, null))
                .putData("countGroupArea", countCapsuleScheduled.countGroupArea);
    }


    @GetMapping("/api/mnt/bill/search")
    @ResponseBody
    public Result bill_search(Integer year, Integer month) {
        if (year == null || month == null) {
            return new Result(-1, "请选择月份");
        }

        ScanSpec scanSpec = new ScanSpec();
        scanSpec.withScanFilters(
                new ScanFilter("year").eq(year),
                new ScanFilter("month").eq(month)
        );

        List<MinitouBill> minitouBillList = minitouBillDao.scan(scanSpec);
        Result result = new Result(CodeMsg.SUCCESS);
        result.putData("countGroupArea", countCapsuleScheduled.countGroupArea);
        return result;
    }


}
