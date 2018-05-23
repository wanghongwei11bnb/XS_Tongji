package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.constant.AreaStatusOption;
import com.xiangshui.server.constant.BookingStatusOption;
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
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.Option;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.Consumer;

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

    @Menu(value = "订单管理", sort = 901)
    @AuthRequired("订单管理（全部）")
    @GetMapping("/booking_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "booking_manage";
    }

    @GetMapping("/api/booking/search")
    @ResponseBody
    public Result search(HttpServletRequest request, HttpServletResponse response,
                         Long booking_id, String city, String phone, Booking criteria, Date create_date_start, Date create_date_end,
                         Boolean download) throws Exception {
        if (download == null) {
            download = false;
        }
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
            if (download) {
                scanSpec.withMaxResultSize(10000);
            }
            bookingList = bookingDao.scan(scanSpec);
        }
        if (bookingList == null) {
            bookingList = new ArrayList<>();
        }
        List<Area> areaList = null;
        List<UserInfo> userInfoList = null;
        if (bookingList != null && bookingList.size() > 0) {
            userInfoList = userService.getUserInfoList(bookingList, null);
            areaList = areaService.getAreaListByBooking(bookingList, null);
            Collections.sort(bookingList, new Comparator<Booking>() {
                @Override
                public int compare(Booking o1, Booking o2) {
                    return -(int) (o1.getCreate_time() - o2.getCreate_time());
                }
            });
        }
        if (download) {
            Map<Integer, Area> areaMap = new HashMap<>();
            Map<Integer, UserInfo> userInfoMap = new HashMap<>();
            if (userInfoList != null && userInfoList.size() > 0) {
                userInfoList.forEach(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) {
                        if (userInfo != null) {
                            userInfoMap.put(userInfo.getUin(), userInfo);
                        }
                    }
                });
            }
            if (areaList != null && areaList.size() > 0) {
                areaList.forEach(new Consumer<Area>() {
                    @Override
                    public void accept(Area area) {
                        if (area != null) {
                            areaMap.put(area.getArea_id(), area);
                        }
                    }
                });
            }

            List<List<String>> data = new ArrayList<>();

            List<String> headRow = new ArrayList<>();
            headRow.add("订单编号");
            headRow.add("创建时间");
            headRow.add("结束时间");
            headRow.add("订单状态");
            headRow.add("订单总金额");
            headRow.add("头等舱编号");
            headRow.add("场地编号");
            headRow.add("场地名称");
            headRow.add("城市");
            headRow.add("地址");
            headRow.add("用户UIN");
            headRow.add("用户手机号");
            data.add(headRow);
            if (bookingList != null && bookingList.size() > 0) {
                bookingList.forEach(new Consumer<Booking>() {
                    @Override
                    public void accept(Booking booking) {
                        if (booking == null) {
                            return;
                        }
                        Area area = areaMap.get(booking.getArea_id());
                        if (area == null) {
                            return;
                        }
                        if (area.getStatus() == AreaStatusOption.offline.value
                                || area.getStatus() == AreaStatusOption.stay.value
                                || area.getTitle().indexOf("待运营") > -1) {
                            return;
                        }
                        List<String> row = new ArrayList<>();
                        row.add("" + booking.getBooking_id());
                        row.add("" + (booking.getCreate_time() != null && booking.getCreate_time() > 0 ?
                                DateUtils.format(booking.getCreate_time() * 1000, "yyyy-MM-dd HH:mm")
                                : null));
                        row.add("" + (booking.getEnd_time() != null && booking.getEnd_time() > 0 ?
                                DateUtils.format(booking.getEnd_time() * 1000, "yyyy-MM-dd HH:mm")
                                : null));
                        row.add("" + Option.getActiveText(BookingStatusOption.options, booking.getStatus()));
                        row.add("" + booking.getFinal_price());
                        row.add("" + booking.getCapsule_id());
                        row.add("" + booking.getArea_id());
                        row.add("" + (areaMap.containsKey(booking.getArea_id()) ?
                                areaMap.get(booking.getArea_id()).getTitle()
                                : null));
                        row.add("" + (areaMap.containsKey(booking.getArea_id()) ?
                                areaMap.get(booking.getArea_id()).getCity()
                                : null));
                        row.add("" + (areaMap.containsKey(booking.getArea_id()) ?
                                areaMap.get(booking.getArea_id()).getAddress()
                                : null));
                        row.add("" + booking.getUin());
                        row.add("" + (userInfoMap.containsKey(booking.getUin()) ?
                                userInfoMap.get(booking.getUin()).getPhone()
                                : null));
                        data.add(row);
                    }
                });
            }

            HSSFWorkbook workbook = ExcelUtils.export(data);
            response.addHeader("Content-Disposition", "attachment;filename=" + new String("booking.xlsx".getBytes()));
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            workbook.close();
            return null;
        } else {
            return new Result(CodeMsg.SUCCESS)
                    .putData("bookingList", bookingList)
                    .putData("areaList", areaList)
                    .putData("userInfoList", userInfoList)
                    ;
        }
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


    @AuthRequired("更改订单")
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
