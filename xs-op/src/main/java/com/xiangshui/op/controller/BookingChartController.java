package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.count.*;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.service.*;
import com.xiangshui.server.tool.ExcelTools;
import com.xiangshui.util.CallBack;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class BookingChartController extends BaseController {


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

    @Menu(value = "订单统计")
    @AuthRequired("订单统计")
    @GetMapping("/booking_chart")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "booking_chart";
    }


    @PostMapping("/api/booking/count")
    @AuthRequired("订单统计")
    @ResponseBody
    public Result count(HttpServletRequest request, HttpServletResponse response, String city, String phone, Booking criteria, Date create_date_start, Date create_date_end, String processor) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        if (StringUtils.isBlank(processor)) {
            return new Result(-1, "请选择统计类型");
        }
        if (criteria == null) criteria = new Booking();
        Class processorClass = null;
        try {
            processorClass = Class.forName("com.xiangshui.op.count." + processor);
        } catch (ClassNotFoundException e) {
            return new Result(-1, "未找到处理器");
        }
        if (processorClass == null || !CountProcessor.class.isAssignableFrom(processorClass)) {
            return new Result(-1, "未找到处理器");
        }
        CountProcessor processorExample = (CountProcessor) processorClass.getConstructor().newInstance();

        if (create_date_start == null) {
            return new Result(-1, "请选择开始日期");
        }

        if (processorExample instanceof CountBookingForCountAtDayUnit) {
            ((CountBookingForCountAtDayUnit) processorExample).initDateRange(create_date_start, create_date_end);
        }
        if (processorExample instanceof CountBookingForCountMonthCardAtDayUnit) {
            ((CountBookingForCountMonthCardAtDayUnit) processorExample).initDateRange(create_date_start, create_date_end);
        }
        if (processorExample instanceof CountBookingForCountPriceAtDayUnit) {
            ((CountBookingForCountPriceAtDayUnit) processorExample).initDateRange(create_date_start, create_date_end);
        }

        List<ScanFilter> scanFilterList = bookingDao.makeScanFilterList(criteria, new String[]{
                "uin",
                "area_id",
                "capsule_id",
        });
        if (criteria.getArea_id() == null && criteria.getCapsule_id() == null && criteria.getBooking_id() == null && StringUtils.isNotBlank(city)) {
            List<Integer> areaIdList = new ArrayList<>();
            List<Area> areaList = areaDao.scan(new ScanSpec().withScanFilters(new ScanFilter("city").eq(city)));
            if (areaList.size() == 0) {
                return new Result(-1, "该城市没有场地");
            }
            areaList.forEach(area -> areaIdList.add(area.getArea_id()));
            scanFilterList.add(new ScanFilter("area_id").in(areaIdList.toArray()));
        }
        if (criteria.getUin() == null && StringUtils.isNotBlank(phone)) {
            UserInfo userInfo = userService.getUserInfoByPhone(phone);
            if (userInfo == null) {
                return new Result(-1, "未找到该用户");
            }
            scanFilterList.add(new ScanFilter("uin").eq(userInfo.getUin()));
        }
        bookingDao.appendDateRangeFilter(scanFilterList, "create_time", create_date_start, create_date_end);
        ScanSpec scanSpec = new ScanSpec();
        if (scanFilterList.size() > 0) {
            scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        }
        scanSpec.withMaxResultSize(Integer.MAX_VALUE);
        List<Booking> bookingList = bookingDao.scan(scanSpec);
        CountResult countResult = processorExample.count(bookingList);
        return new Result(CodeMsg.SUCCESS).putData("countResult", countResult);
    }


}
