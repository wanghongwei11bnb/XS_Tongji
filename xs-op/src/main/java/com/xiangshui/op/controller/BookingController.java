package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.server.constant.CapsuleStatusOption;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.domain.fragment.CapsuleType;
import com.xiangshui.server.exception.XiangShuiException;
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
    DeviceService deviceService;
    @Autowired
    CapsuleService capsuleService;


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

    @AuthRequired("订单管理（全部）")
    @GetMapping("/booking_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "booking_manage";
    }

    @AuthRequired("订单管理（全部）")
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
        if (bookingList != null && bookingList.size() > 0) {
            Collections.sort(bookingList, new Comparator<Booking>() {
                @Override
                public int compare(Booking o1, Booking o2) {
                    return -(int) (o1.getCreate_time() - o2.getCreate_time());
                }
            });
        }
        return new Result(CodeMsg.SUCCESS)
                .putData("bookingList", bookingList)
                .putData("areaList", areaService.getAreaListByBooking(bookingList, null))
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


//    @PostMapping("/api/booking/{booking_id:\\d+}/update/final_price")
//    @ResponseBody
//    public Result update_op(@PathVariable("booking_id") Long booking_id, Integer final_price) throws Exception {
//        Booking booking = bookingService.getBookingById(booking_id);
//        if (booking == null) return new Result(CodeMsg.NO_FOUND);
//        if (booking.getStatus() == 1 || booking.getStatus() == 4) return new Result(-1, "只能对待支付的订单进行修改");
//        if (final_price != null && final_price <= 0) return new Result(-1, "订单金额必须大于0");
//        booking.setFinal_price(final_price);
//        bookingDao.updateItem(new PrimaryKey("booking_id", booking.getBooking_id()), booking, new String[]{
//                "final_price",
//        });
//        return new Result(CodeMsg.SUCCESS);
//    }
//
//    @PostMapping("/api/booking/{booking_id:\\d+}/update/status")
//    @ResponseBody
//    public Result update_status(@PathVariable("booking_id") Long booking_id, Integer status, Integer final_price) throws Exception {
//        Booking booking = bookingService.getBookingById(booking_id);
//        if (booking == null) return new Result(CodeMsg.NO_FOUND);
//        Capsule capsule = capsuleService.getCapsuleById(booking.getCapsule_id());
//        if (capsule == null) return new Result(-1, "未查到头等舱信息");
//        if (booking.getStatus() != 1) return new Result(-1, "只能对进行中的订单进行修改");
//        if (status == null || status != 2) return new Result(-1, "只能更改为待支付");
//        if (final_price != null && final_price <= 0) return new Result(-1, "订单金额必须大于0");
//        if (!deviceService.isLocked(capsule.getDevice_id())) return new Result(-1, "门锁没有关闭");
//        deviceService.relieveBooking(capsule.getDevice_id());
//        capsule.setStatus(CapsuleStatusOption.free.value);
//        capsuleDao.updateItem(new PrimaryKey("capsule_id", capsule.getCapsule_id()), capsule, new String[]{"status"});
//        booking.setEnd_time(System.currentTimeMillis() / 1000);
//        booking.setStatus(status);
//        booking.setFinal_price(final_price);
//        bookingDao.updateItem(new PrimaryKey("booking_id", booking.getBooking_id()), booking, new String[]{
//                "status",
//                "final_price",
//                "end_time"
//        });
//        return new Result(CodeMsg.SUCCESS);
//    }


    @PostMapping("/api/booking/{booking_id:\\d+}/update/op")
    @ResponseBody
    public Result update_op(@PathVariable("booking_id") Long booking_id, Integer status, Integer final_price) throws Exception {
        Booking booking = bookingService.getBookingById(booking_id);
        if (booking == null) return new Result(CodeMsg.NO_FOUND);
        if (booking.getStatus() == 4) return new Result(-1, "已支付的订单不能修改");

        if (status == null || status != 2) return new Result(-1, "只能更改为待支付");
        if (final_price == null || final_price <= 0) return new Result(-1, "订单金额必须大于0");

        if (booking.getStatus() == 1) {
            Capsule capsule = capsuleService.getCapsuleById(booking.getCapsule_id());
            if (capsule == null) return new Result(-1, "未查到头等舱信息");
            if (!deviceService.isLocked(capsule.getDevice_id())) return new Result(-1, "门锁没有关闭");
            deviceService.relieveBooking(capsule.getDevice_id());
            capsule.setStatus(CapsuleStatusOption.free.value);
            capsuleDao.updateItem(new PrimaryKey("capsule_id", capsule.getCapsule_id()), capsule, new String[]{"status"});

            booking.setEnd_time(System.currentTimeMillis() / 1000);
            booking.setStatus(status);
            booking.setFinal_price(final_price);
            bookingDao.updateItem(new PrimaryKey("booking_id", booking.getBooking_id()), booking, new String[]{
                    "status",
                    "final_price",
                    "end_time"
            });
        } else {
            booking.setFinal_price(final_price);
            bookingDao.updateItem(new PrimaryKey("booking_id", booking.getBooking_id()), booking, new String[]{
                    "final_price",
            });
        }
        return new Result(CodeMsg.SUCCESS);
    }


}
