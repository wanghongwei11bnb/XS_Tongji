package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.scheduled.CacheScheduled;
import com.xiangshui.op.threadLocal.SessionLocal;
import com.xiangshui.op.threadLocal.UsernameLocal;
import com.xiangshui.server.constant.AreaStatusOption;
import com.xiangshui.server.constant.BookingStatusOption;
import com.xiangshui.server.constant.CapsuleStatusOption;
import com.xiangshui.server.constant.PayTypeOption;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.domain.fragment.CapsuleType;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.relation.BookingRelation;
import com.xiangshui.server.relation.CapsuleRelation;
import com.xiangshui.server.service.*;
import com.xiangshui.op.tool.ExcelTools;
import com.xiangshui.server.tool.BookingGroupTool;
import com.xiangshui.util.*;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Controller
public class BookingController extends BaseController implements InitializingBean {
    @Autowired
    ExcelTools excelTools;
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
    RedisService redisService;


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
    @Autowired
    OpUserService opUserService;

    @Autowired
    CacheScheduled cacheScheduled;


    @Autowired
    BookingGroupTool bookingGroupTool;

    @Menu(value = "订单管理")
    @AuthRequired({AuthRequired.auth_booking, AuthRequired.auth_booking_all})
    @GetMapping("/booking_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "booking_manage";
    }

    @GetMapping("/api/booking/search")
    @ResponseBody
    public Result search(HttpServletRequest request, HttpServletResponse response,
                         Long booking_id, String city, String phone, Booking criteria, Date create_date_start, Date create_date_end,
                         Integer payMonth, Boolean download, String group, String groupSelects) throws Exception {
        String op_username = UsernameLocal.get();
        Set<Integer> areaSet = opUserService.getAreaSet(op_username);
        boolean auth_booking_show_phone = opUserService.getAuthSet(op_username).contains(AuthRequired.auth_booking_show_phone);
        boolean auth_booking_show_coupon = opUserService.getAuthSet(op_username).contains(AuthRequired.auth_booking_show_coupon);
        boolean auth_booking_download = opUserService.getAuthSet(op_username).contains(AuthRequired.auth_booking_download);
        boolean auth_booking_all = opUserService.getAuthSet(op_username).contains(AuthRequired.auth_booking_all);
        if (download == null) {
            download = false;
        }
        if (!auth_booking_all) {
            if (criteria.getArea_id() != null && !areaSet.contains(criteria.getArea_id())) {
                return new Result(CodeMsg.OPAUTH_FAIL).setMsg("您没有该场地的权限：" + criteria.getArea_id());
            }
            if (criteria.getCapsule_id() != null) {
                Capsule capsule = cacheScheduled.capsuleMapOptions.get(criteria.getCapsule_id());
                if (capsule != null && !areaSet.contains(capsule.getArea_id())) {
                    return new Result(CodeMsg.OPAUTH_FAIL).setMsg("您没有该设备的权限：" + criteria.getCapsule_id());
                }
            }
        }
        if (download && !auth_booking_download) {
            return new Result(CodeMsg.OPAUTH_FAIL).setMsg("您没有下载权限");
        }
        if (download && payMonth != null) {
            int year = payMonth / 100;
            int month = payMonth % 100;
            Set<Long> bookingIdSet = new HashSet<>();
            try {
                for (String line : IOUtils.readLines(this.getClass().getResourceAsStream("/m" + month + ".txt"), "UTF-8")) {
                    try {
                        if (StringUtils.isNotBlank(line)) {
                            bookingIdSet.add(Long.valueOf(line));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<Booking> activeBookingList = new ArrayList<>();
            List<Booking> bookingListOld = bookingDao.scan(
                    new ScanSpec()
                            .withScanFilters(
                                    new ScanFilter("status").eq(BookingStatusOption.pay.value),
                                    new ScanFilter("update_time").between(
                                            new LocalDate(year, month, 1).toDate().getTime() / 1000
                                            , new LocalDate(year, month, 1).plusMonths(1).toDate().getTime() / 1000
                                    )
                            ).withMaxResultSize(Integer.MAX_VALUE)
            );
            if (bookingListOld != null && bookingListOld.size() > 0) {
                for (Booking booking : bookingListOld) {
                    if (bookingIdSet.contains(booking.getBooking_id())) {
                        activeBookingList.add(booking);
                        continue;
                    }
                    if (areaBillScheduled.testUinSet.contains(booking.getUin())) {
                        continue;
                    }
                    if (booking.getFinal_price() == null || booking.getFinal_price() == 0) {
                        continue;
                    }
                    activeBookingList.add(booking);
                }
            }


            if (activeBookingList == null) {
                activeBookingList = new ArrayList<>();
            }
            Collections.sort(activeBookingList, (o1, o2) -> -(int) (o1.getCreate_time() - o2.getCreate_time()));
            excelTools.exportBookingList(activeBookingList, (auth_booking_show_phone ? ExcelTools.EXPORT_PHONE : 0) | (auth_booking_show_coupon ? ExcelTools.EXPORT_COUPON : 0), null, response, "booking.xlsx");
            return null;
        }

        List<Booking> bookingList = null;

        List<ScanFilter> filterList = new ArrayList<>();


        if (StringUtils.isNotBlank(city)) {
            filterList.add(new ScanFilter("area_id").in(ListUtils.fieldSet(ListUtils.filter(cacheScheduled.areaList, area ->
                    city.equals(area.getCity()) && (auth_booking_all || areaSet.contains(area.getArea_id()))
            ), Area::getArea_id).toArray()));
        } else {
            if (criteria.getArea_id() != null) {
                filterList.add(new ScanFilter("area_id").eq(criteria.getArea_id()));
            }
            if (criteria.getCapsule_id() != null) {
                filterList.add(new ScanFilter("capsule_id").eq(criteria.getCapsule_id()));
            }
        }

        if (filterList.size() == 0) {
            filterList.add(new ScanFilter("area_id").in(ListUtils.fieldSet(ListUtils.filter(cacheScheduled.areaList, area ->
                    auth_booking_all || areaSet.contains(area.getArea_id())
            ), Area::getArea_id).toArray()));
        }


        filterList.addAll(bookingDao.makeScanFilterList(criteria, "booking_id", "uin", "status", "by_op"));
        bookingDao.appendDateRangeFilter(filterList, "create_time", create_date_start, create_date_end);
        if (StringUtils.isNotBlank(phone)) {
            UserInfo userInfo = userService.getUserInfoByPhone(phone);
            if (userInfo != null) {
                filterList.add(new ScanFilter("uin").eq(userInfo.getUin()));
            }
        }
        ScanSpec scanSpec = new ScanSpec();
        scanSpec.withScanFilters(filterList.toArray(new ScanFilter[0]));
        if (download) {
            scanSpec.withMaxResultSize(BaseDynamoDao.maxDownloadSize);
        }
        bookingList = bookingDao.scan(scanSpec);

        bookingList = capsuleAuthorityTools.filterBooking(bookingList);

        if (bookingList == null) {
            bookingList = new ArrayList<>();
        }


        if (bookingList != null && bookingList.size() > 0) {
            Collections.sort(bookingList, (o1, o2) -> -(int) (o1.getCreate_time() - o2.getCreate_time()));
        }

        if (download) {
            if (StringUtils.isNotBlank(group)) {
                BookingGroupTool.GroupItem groupItem = bookingGroupTool.mkGroupItem(group);
                List<BookingGroupTool.SelectItem> selectItemList = ListUtils.map(Arrays.asList(groupSelects.split(",")), s -> bookingGroupTool.mkSelectItem(s));
                bookingGroupTool.group(bookingList, groupItem, selectItemList, response, "booking.xlsx");
            } else {
                excelTools.exportBookingList(bookingList, (auth_booking_show_phone ? ExcelTools.EXPORT_PHONE : 0) | (auth_booking_show_coupon ? ExcelTools.EXPORT_COUPON : 0), null, response, "booking.xlsx");
            }
            return null;
        } else {
            return new Result(CodeMsg.SUCCESS)
                    .putData("bookingList", bookingList).putData("cityList", cacheScheduled.cityList)
                    .putData("areaList", cacheScheduled.areaMapOptions.selectByPrimarys(ListUtils.fieldSet(bookingList, Booking::getArea_id)))
                    .putData("userInfoList", auth_booking_show_phone ? cacheScheduled.userInfoMapOptions.selectByPrimarys(ListUtils.fieldSet(bookingList, Booking::getUin)) : null)
                    ;
        }
    }

    @AuthRequired("更改订单")
    @GetMapping("/api/booking/{booking_id:\\d+}/user/byops")
    @ResponseBody
    public Result byops(HttpServletRequest request, HttpServletResponse response, @PathVariable("booking_id") Long booking_id, Date create_date_start, Date create_date_end) throws Exception {
        Booking booking = bookingService.getBookingById(booking_id);
        if (booking == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        capsuleAuthorityTools.auth(booking, SessionLocal.getUsername(),true);
        Integer uin = booking.getUin();
        booking = new Booking();
        booking.setUin(uin);
        booking.setBy_op(1);
        return search(request, response, null, null, null, booking, create_date_start, create_date_end, null, false, null, null);
    }


    @GetMapping("/api/booking/{booking_id:\\d+}")
    @ResponseBody
    public Result get(@PathVariable("booking_id") Long booking_id) {
        Booking booking = bookingDao.getItem(new PrimaryKey("booking_id", booking_id));
        if (booking == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        capsuleAuthorityTools.auth(booking, SessionLocal.getUsername(),true);
        BookingRelation bookingRelation = bookingService.toRelation(booking);
        areaService.matchAreaForBooking(bookingRelation);
        userService.matchUserInfoForBooking(bookingRelation);
        return new Result(CodeMsg.SUCCESS).putData("booking", bookingRelation);
    }


    @AuthRequired("更改订单")
    @PostMapping("/api/booking/{booking_id:\\d+}/update/op")
    @ResponseBody
    public Result update_op(@PathVariable("booking_id") Long booking_id, Integer status, Integer final_price) throws Exception {
        Booking booking = bookingService.getBookingById(booking_id);
        if (booking == null) return new Result(CodeMsg.NO_FOUND);
        capsuleAuthorityTools.auth(booking, SessionLocal.getUsername(),true);
        booking.setBy_op(1);
        if (booking.getStatus() == 4) return new Result(-1, "已支付的订单不能修改");

        if (status == null || status != 2) return new Result(-1, "只能更改为待支付");
        if (final_price == null || final_price < 0) return new Result(-1, "订单金额必须大于等于0");

        if (booking.getStatus() == 1) {
            Capsule capsule = capsuleService.getCapsuleById(booking.getCapsule_id());
            if (capsule == null) return new Result(-1, "未查到头等舱信息");
            //设备与订单解绑
            deviceService.relieveBooking(capsule.getDevice_id());
            deviceService.no_order(capsule.getDevice_id());
            //更改舱状态
            capsule.setStatus(CapsuleStatusOption.free.value);
            capsuleDao.updateItem(new PrimaryKey("capsule_id", capsule.getCapsule_id()), capsule, new String[]{"status"});
            //更改订单
            booking.setEnd_time(System.currentTimeMillis() / 1000);
            booking.setStatus(status);
            booking.setFinal_price(final_price);
            if (booking.getFinal_price() == 0) {
                booking.setStatus(BookingStatusOption.pay.value);
            }
            bookingDao.updateItem(new PrimaryKey("booking_id", booking.getBooking_id()), booking, new String[]{
                    "status",
                    "final_price",
                    "end_time",
                    "by_op",
            });
            //redis
            redisService.run(object -> {
                object.zrem("time_out", String.valueOf(booking.getUin()));
//                object.zrem("time_out_new", String.valueOf(booking.getUin()));
                object.zrem("time_out_new", booking.getUin() + "&" + booking.getCapsule_id());
                object.srem("capsule_time", String.valueOf(booking.getCapsule_id()));
                object.zadd("light_on", (System.currentTimeMillis() + 1000 * 60 * 22) / 1000, capsule.getDevice_id());
            });

        } else {
            //更改订单
            booking.setFinal_price(final_price);
            booking.setStatus(BookingStatusOption.unpay2.value);
            if (booking.getFinal_price() == 0) {
                booking.setStatus(BookingStatusOption.pay.value);
            }
            bookingDao.updateItem(new PrimaryKey("booking_id", booking.getBooking_id()), booking, new String[]{
                    "final_price",
                    "status",
                    "by_op",
            });
        }
        return new Result(CodeMsg.SUCCESS);
    }


    @PostMapping("/api/booking/{booking_id:\\d+}/checkPrice")
    @ResponseBody
    public Result checkPrice(@PathVariable("booking_id") Long booking_id, Date end_time) throws IOException {
        int price = bookingService.checkPrice(booking_id, end_time != null ? end_time.getTime() / 1000 : null);
        return new Result(CodeMsg.SUCCESS).putData("price", price);
    }


    @Override
    public void afterPropertiesSet() {
        bookingDao.setHandleScanSpec(scanSpec -> {
            String op_username = UsernameLocal.get();
            if (StringUtils.isNotBlank(op_username)) {
                boolean auth_f1 = opUserService.getAuthSet(op_username).contains(AuthRequired.auth_f1);
                if (!auth_f1) {
                    if (scanSpec == null) {
                        scanSpec = new ScanSpec();
                    }
                    if (scanSpec.getScanFilters() == null) {
                        scanSpec.withScanFilters(new ScanFilter("f1").notExist());
                    } else {
                        for (ScanFilter scanFilter : scanSpec.getScanFilters()) {
                            if (scanFilter.getAttribute().equals("f1")) {
                                scanFilter.notExist();
                                return;
                            }
                        }
                        List<ScanFilter> newScanFilter = new ArrayList<>();
                        newScanFilter.addAll(scanSpec.getScanFilters());
                        newScanFilter.add(new ScanFilter("f1").notExist());
                        scanSpec.withScanFilters(newScanFilter.toArray(new ScanFilter[newScanFilter.size()]));
                    }
                }
            }
        });
    }
}
