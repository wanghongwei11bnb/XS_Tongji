package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
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
import com.xiangshui.util.CallBack;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.Option;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
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

    @Menu(value = "订单管理")
    @AuthRequired("订单管理（全国）")
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
        String op_username = UsernameLocal.get();
        boolean auth_booking_show_phone = opUserService.getAuthSet(op_username).contains(AuthRequired.auth_booking_show_phone);
        boolean auth_booking_download = opUserService.getAuthSet(op_username).contains(AuthRequired.auth_booking_download);

        if (download == null) {
            download = false;
        }

        if (download && !auth_booking_download) {
            return new Result(CodeMsg.OPAUTH_FAIL);
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
            List<ScanFilter> filterList = bookingDao.makeScanFilterList(criteria, "area_id", "capsule_id", "uin", "status", "by_op");
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
                scanSpec.withMaxResultSize(BaseDynamoDao.maxDownloadSize);
            }
            bookingList = bookingDao.scan(scanSpec);
        }
        if (bookingList == null) {
            bookingList = new ArrayList<>();
        }
        List<Area> areaList = null;
        List<UserInfo> userInfoList = null;
        if (bookingList != null && bookingList.size() > 0) {
            areaList = areaService.getAreaListByBooking(bookingList, new String[]{"area_id", "title", "city", "address", "status"});
            Collections.sort(bookingList, new Comparator<Booking>() {
                @Override
                public int compare(Booking o1, Booking o2) {
                    return -(int) (o1.getCreate_time() - o2.getCreate_time());
                }
            });
            if (auth_booking_show_phone) {
                userInfoList = userService.getUserInfoList(bookingList, new String[]{"uin", "phone"});

            }
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
            headRow.add("实际充值金额");
            headRow.add("优惠金额");
            headRow.add("非会员付费金额");
            headRow.add("支付方式");
            headRow.add("是否使用月卡");
            headRow.add("头等舱编号");
            headRow.add("场地编号");
            headRow.add("场地名称");
            headRow.add("城市");
            headRow.add("地址");
            headRow.add("用户UIN");
            headRow.add("用户手机号");
            headRow.add("订单来源");
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
                        if (AreaStatusOption.offline.value.equals(area.getStatus())) {
                            return;
                        }
                        List<String> row = new ArrayList<>();
                        row.add(String.valueOf(booking.getBooking_id()));
                        row.add((booking.getCreate_time() != null && booking.getCreate_time() > 0 ?
                                DateUtils.format(booking.getCreate_time() * 1000, "yyyy-MM-dd HH:mm")
                                : ""));
                        row.add("" + (booking.getEnd_time() != null && booking.getEnd_time() > 0 ?
                                DateUtils.format(booking.getEnd_time() * 1000, "yyyy-MM-dd HH:mm")
                                : null));
                        row.add("" + Option.getActiveText(BookingStatusOption.options, booking.getStatus()));
                        row.add(booking.getFinal_price() != null ? String.valueOf(booking.getFinal_price() / 100f) : "");

                        row.add(booking.getFrom_charge() != null ? String.valueOf(booking.getFrom_charge() / 100f) : "");
                        row.add(booking.getFrom_bonus() != null ? String.valueOf(booking.getFrom_bonus() / 100f) : "");

                        row.add(booking.getUse_pay() != null ? booking.getUse_pay() / 100f + "" : "");
                        row.add("" + Option.getActiveText(PayTypeOption.options, booking.getPay_type()));
                        row.add(new Integer(1).equals(booking.getMonth_card_flag()) ? "是" : "否");
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
                        row.add(booking.getReq_from());
                        data.add(row);
                    }
                });
            }

            XSSFWorkbook workbook = ExcelUtils.export(data);
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

    @AuthRequired("更改订单")
    @GetMapping("/api/booking/{booking_id:\\d+}/user/byops")
    @ResponseBody
    public Result byops(HttpServletRequest request, HttpServletResponse response, @PathVariable("booking_id") Long booking_id, Date create_date_start, Date create_date_end) throws Exception {
        Booking booking = bookingService.getBookingById(booking_id);
        if (booking == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        Integer uin = booking.getUin();
        booking = new Booking();
        booking.setUin(uin);
        booking.setBy_op(1);
        return search(request, response, null, null, null, booking, create_date_start, create_date_end, false);
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


}
