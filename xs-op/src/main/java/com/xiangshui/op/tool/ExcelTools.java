package com.xiangshui.op.tool;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.scheduled.AreaRegionScheduled;
import com.xiangshui.op.scheduled.CacheScheduled;
import com.xiangshui.server.constant.BookingStatusOption;
import com.xiangshui.server.constant.PayTypeOption;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BookingDao;
import com.xiangshui.server.dao.ChargeRecordDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.ChargeRecord;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.BookingService;
import com.xiangshui.server.service.UserService;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.ListUtils;
import com.xiangshui.util.Option;
import com.xiangshui.util.spring.SpringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Component
public class ExcelTools {

    @Autowired
    BookingService bookingService;
    @Autowired
    UserService userService;
    @Autowired
    AreaService areaService;
    @Autowired
    BookingDao bookingDao;
    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    AreaDao areaDao;
    @Autowired
    ChargeRecordDao chargeRecordDao;

    @Autowired
    AreaRegionScheduled areaRegionScheduled;

    @Autowired
    CacheScheduled cacheScheduled;


    public static final long EXPORT_PHONE = (long) Math.pow(2, 0);
    public static final long EXPORT_COUPON = (long) Math.pow(2, 2);
    public static final long EXPORT_MONTH_CARD_BILL = (long) Math.pow(2, 1);


    public XSSFWorkbook exportBookingList(List<Booking> bookingList, long exports, Map<Long, ChargeRecord> chargeRecordMap) {
        if (bookingList == null) {
            bookingList = new ArrayList<>();
        }

        Collections.sort(bookingList, (o1, o2) -> -(int) (o1.getCreate_time() - o2.getCreate_time()));
//        Map<Integer, Area> areaMap = new HashMap<>();
//        List<Area> areaList = areaService.getAreaListByBooking(bookingList, new String[]{"area_id", "title", "city", "address", "status"});
//        if (areaList != null) {
//            areaList.forEach(area -> {
//                if (area != null) {
//                    areaMap.put(area.getArea_id(), area);
//                }
//            });
//        }
        Map<Integer, UserInfo> userInfoMap = new HashMap<>();
        if ((exports & EXPORT_PHONE) == EXPORT_PHONE) {
            List<UserInfo> userInfoList = userService.getUserInfoList(bookingList, new String[]{"uin", "phone"});
            if (userInfoList != null) {
                userInfoList.forEach(userInfo -> userInfoMap.put(userInfo.getUin(), userInfo));
            }
        }
        return ExcelUtils.export(Arrays.asList(
                new ExcelUtils.Column<Booking>("订单编号") {
                    @Override
                    public String render(Booking booking) {
                        return String.valueOf(booking.getBooking_id());
                    }
                },
                new ExcelUtils.Column<Booking>("创建时间") {
                    @Override
                    public String render(Booking booking) {
                        return booking.getCreate_time() != null ? DateUtils.format(booking.getCreate_time() * 1000, "yyyy-MM-dd HH:mm") : null;
                    }
                },
                new ExcelUtils.Column<Booking>("结束时间") {
                    @Override
                    public String render(Booking booking) {
                        return booking.getEnd_time() != null ? DateUtils.format(booking.getEnd_time() * 1000, "yyyy-MM-dd HH:mm") : null;
                    }
                },
                new ExcelUtils.Column<Booking>("订单状态", (total, booking) -> total + 1) {
                    @Override
                    public String render(Booking booking) {
                        return Option.getActiveText(BookingStatusOption.options, booking.getStatus());
                    }
                },
                (exports & EXPORT_COUPON) == EXPORT_COUPON ? new ExcelUtils.Column<Booking>("优惠金额", (total, booking) -> booking != null && booking.getCoupon_cash() != null ? total + booking.getCoupon_cash() * 1f / 100 : total) {
                    @Override
                    public String render(Booking booking) {
                        return String.valueOf(booking.getCoupon_cash() != null ? booking.getCoupon_cash() / 100f : null);
                    }
                } : null,
                new ExcelUtils.Column<Booking>("订单总金额", (total, booking) -> booking != null && booking.getFinal_price() != null ? total + booking.getFinal_price() * 1f / 100 : total) {
                    @Override
                    public String render(Booking booking) {
                        return String.valueOf(booking.getFinal_price() != null ? booking.getFinal_price() / 100f : null);
                    }
                },
                new ExcelUtils.Column<Booking>("非会员付费金额", (total, booking) -> booking != null && booking.getUse_pay() != null ? total + booking.getUse_pay() * 1f / 100 : total) {
                    @Override
                    public String render(Booking booking) {
                        return String.valueOf(booking.getUse_pay() != null ? booking.getUse_pay() / 100f : null);
                    }
                },
                new ExcelUtils.Column<Booking>("充值部分", (total, booking) -> booking != null && booking.getFrom_charge() != null ? total + booking.getFrom_charge() * 1f / 100 : total) {
                    @Override
                    public String render(Booking booking) {
                        return String.valueOf(booking.getFrom_charge() != null ? booking.getFrom_charge() / 100f : null);
                    }
                },
                new ExcelUtils.Column<Booking>("赠送部分", (total, booking) -> booking != null && booking.getFrom_bonus() != null ? total + booking.getFrom_bonus() * 1f / 100 : total) {
                    @Override
                    public String render(Booking booking) {
                        return String.valueOf(booking.getFrom_bonus() != null ? booking.getFrom_bonus() / 100f : null);
                    }
                },
                new ExcelUtils.Column<Booking>("支付方式") {
                    @Override
                    public String render(Booking booking) {
                        return Option.getActiveText(PayTypeOption.options, booking.getPay_type());
                    }
                },
                new ExcelUtils.Column<Booking>("是否使用月卡") {
                    @Override
                    public String render(Booking booking) {
                        return new Integer(1).equals(booking.getMonth_card_flag()) ? "是" : "否";
                    }
                },
                (exports & EXPORT_MONTH_CARD_BILL) == EXPORT_MONTH_CARD_BILL ? new ExcelUtils.Column<Booking>("购买月卡金额", (total, booking) -> chargeRecordMap.containsKey(booking.getBooking_id()) ? total + chargeRecordMap.get(booking.getBooking_id()).getPrice() * 1f / 100 : total) {
                    @Override
                    public String render(Booking booking) {
                        return String.valueOf(chargeRecordMap.containsKey(booking.getBooking_id()) ? chargeRecordMap.get(booking.getBooking_id()).getPrice() / 100f : null);
                    }
                } : null,
                new ExcelUtils.Column<Booking>("头等舱编号") {
                    @Override
                    public String render(Booking booking) {
                        return String.valueOf(booking.getCapsule_id());
                    }
                },
                new ExcelUtils.Column<Booking>("场地编号") {
                    @Override
                    public String render(Booking booking) {
                        return String.valueOf(booking.getArea_id());
                    }
                },
                new ExcelUtils.Column<Booking>("场地名称") {
                    @Override
                    public String render(Booking booking) {
                        return booking.getArea_id() != null && cacheScheduled.areaMapOptions.containsKey(booking.getArea_id()) ? cacheScheduled.areaMapOptions.get(booking.getArea_id()).getTitle() : null;
                    }
                },
                new ExcelUtils.Column<Booking>("区域") {
                    @Override
                    public String render(Booking booking) {
                        return booking.getArea_id() != null && areaRegionScheduled.areaRegionMap.containsKey(booking.getArea_id()) ? areaRegionScheduled.areaRegionMap.get(booking.getArea_id()) : null;
                    }
                },
                new ExcelUtils.Column<Booking>("城市") {
                    @Override
                    public String render(Booking booking) {
                        return booking.getArea_id() != null && cacheScheduled.areaMapOptions.containsKey(booking.getArea_id()) ? cacheScheduled.areaMapOptions.get(booking.getArea_id()).getCity() : null;
                    }
                },
                new ExcelUtils.Column<Booking>("地址") {
                    @Override
                    public String render(Booking booking) {
                        return booking.getArea_id() != null && cacheScheduled.areaMapOptions.containsKey(booking.getArea_id()) ? cacheScheduled.areaMapOptions.get(booking.getArea_id()).getAddress() : null;
                    }
                },
                new ExcelUtils.Column<Booking>("用户编号") {
                    @Override
                    public String render(Booking booking) {
                        return String.valueOf(booking.getUin());
                    }
                },
                (exports & EXPORT_PHONE) == EXPORT_PHONE ? new ExcelUtils.Column<Booking>("用户手机号") {
                    @Override
                    public String render(Booking booking) {
                        return booking.getUin() != null && userInfoMap.containsKey(booking.getUin()) ? userInfoMap.get(booking.getUin()).getPhone() : null;
                    }
                } : null,
                new ExcelUtils.Column<Booking>("订单来源") {
                    @Override
                    public String render(Booking booking) {
                        return booking.getReq_from();
                    }
                }
        ), bookingList);
    }

    public void exportBookingList(List<Booking> bookingList, long exports, Map<Long, ChargeRecord> chargeRecordMap, HttpServletResponse response, String fileName) throws IOException {
        XSSFWorkbook workbook = exportBookingList(bookingList, exports, chargeRecordMap);
        response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes()));
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }


    public void test() throws Exception {


        List<Booking> bookingList = bookingDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(
                new ScanFilter("pay_type").eq(50)
        ));


        XSSFWorkbook workbook = exportBookingList(bookingList, 0 | EXPORT_PHONE, null);


        OutputStream outputStream = new FileOutputStream(new File(String.format("/Users/whw/Downloads/booking.xlsx")));
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();

    }

    public static void main(String[] args) throws Exception {
        SpringUtils.init();
        SpringUtils.getBean(ExcelTools.class).test();


    }
}
