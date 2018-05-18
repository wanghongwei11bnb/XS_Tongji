package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.relation.BookingRelation;
import com.xiangshui.server.relation.CapsuleRelation;
import com.xiangshui.server.service.*;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class BookingController extends BaseController {

    @Autowired
    CityService cityService;
    @Autowired
    AreaService areaService;
    @Autowired
    AreaDao areaDao;
    @Autowired
    CapsuleDao capsuleDao;


    @Autowired
    FailureReportDao failureReportDao;

    @Autowired
    FailureReportService failureReportService;

    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    BookingDao bookingDao;

    @Autowired
    UserService userService;
    @Autowired
    BookingService bookingService;


    @GetMapping("/booking_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "booking_manage";
    }


    @GetMapping("/api/booking/search")
    @ResponseBody
    public Result search(Long booking_id, String city, String phone, Booking criteria, Date create_date_start, Date create_date_end) throws Exception {
        List<Booking> bookingList = null;
        if (booking_id != null) {
            Booking booking = bookingService.getBookingById(booking_id);
            if (booking != null) {
                bookingList = new ArrayList<>();
                bookingList.add(booking);
            }
        } else if (StringUtils.isNotBlank(city)) {
            bookingList = bookingService.getBookingListByCity(city);
        } else {
            List<ScanFilter> filterList = bookingDao.makeScanFilterList(criteria, "area_id", "capsule_id", "uin", "status");
            if (create_date_start != null && create_date_end != null) {
                filterList.add(new ScanFilter("create_time").between(
                        create_date_start.getTime() / 1000, (create_date_end.getTime() + 1000 * 60 * 60 * 24) / 1000
                ));
            } else if (create_date_start != null && create_date_end == null) {
                filterList.add(new ScanFilter("create_time").gt(create_date_start.getTime() / 1000));
            } else if (create_date_start == null && create_date_end != null) {
                filterList.add(new ScanFilter("create_time").lt((create_date_end.getTime() + 1000 * 60 * 60 * 24) / 1000));
            }

            if (StringUtils.isNotBlank(phone)) {
                UserInfo userInfo = userService.getUserInfoByPhone(phone);
                if (userInfo != null) {
                    filterList.add(new ScanFilter("uin").eq(userInfo.getUin()));
                }
            }
            ScanSpec scanSpec = new ScanSpec();
            scanSpec.withScanFilters(filterList.toArray(new ScanFilter[0]));
            bookingList = bookingDao.scan(scanSpec);
        }
        if (bookingList == null) {
            bookingList = new ArrayList<>();
        }
        return new Result(CodeMsg.SUCCESS)
                .putData("bookingList", bookingList)
                .putData("areaList", areaService.getAreaList(bookingList, null))
                .putData("userInfoList", userService.getUserInfoList(bookingList, null))
                ;
    }


    @GetMapping("/api/booking/{booking_id:\\d+}")
    @ResponseBody
    public Result get(@PathVariable("booking_id") Long booking_id) {
        Booking booking = bookingDao.getItem(new PrimaryKey("booking_id", booking_id));
        if (booking == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        BookingRelation bookingRelation = bookingService.toRelation(booking);
        areaService.matchAreaForBooking(bookingRelation);
        userService.matchUserInfoForBooking(bookingRelation);
        return new Result(CodeMsg.SUCCESS).putData("booking", bookingRelation);
    }


    @PostMapping("/api/booking/{booking_id:\\d+}/update/op")
    @ResponseBody
    public Result update_op(@PathVariable("booking_id") Long booking_id, Integer status, Integer final_price) throws Exception {
        Booking booking = bookingService.getBookingById(booking_id);
        if (booking == null) return new Result(CodeMsg.NO_FOUND);
        if (status == null) return new Result(-1, "订单状态不能为空");
        if (final_price != null && final_price <= 0) return new Result(-1, "订单金额必须大于0");
        booking.setStatus(status);
        booking.setFinal_price(final_price);
        if ((booking.getStatus() == 2 || booking.getStatus() == 3) && (booking.getFinal_price() == null || booking.getFinal_price() <= 0)) {
            return new Result(-1, "待支付的订单必须填写订单金额");
        }
        bookingDao.updateItem(new PrimaryKey("booking_id", booking.getBooking_id()), booking, new String[]{
                "status",
                "final_price",
        });
        return new Result(CodeMsg.SUCCESS);
    }


}
