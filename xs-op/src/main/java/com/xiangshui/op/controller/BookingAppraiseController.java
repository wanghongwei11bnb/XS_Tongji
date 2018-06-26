package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BookingAppraiseDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.BookingAppraise;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.service.ServiceUtils;
import com.xiangshui.server.service.UserService;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.Consumer;

@Controller
public class BookingAppraiseController extends BaseController {


    @Autowired
    BookingAppraiseDao bookingAppraiseDao;

    @Autowired
    UserService userService;

    @Autowired

    UserInfoDao userInfoDao;
    @Autowired
    AreaDao areaDao;

    @Menu("用户评论列表")
    @AuthRequired("用户评论列表")
    @GetMapping("/booking_appraise_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "booking_appraise_manage";
    }

    @GetMapping("/api/booking_appraise/search")
    @ResponseBody
    public Result search(HttpServletRequest request, HttpServletResponse response, String phone,
                         BookingAppraise criteria, Date create_date_start, Date create_date_end, Boolean download) throws NoSuchFieldException, IllegalAccessException {

        if (download == null) download = false;
        if (criteria == null) criteria = new BookingAppraise();

        if (StringUtils.isNotBlank(phone)) {
            UserInfo userInfo = userService.getUserInfoByPhone(phone);
            if (userInfo == null) {
                throw new XiangShuiException("手机号对应的用户不存在");
            } else {
                criteria.setUin(userInfo.getUin());
            }
        }

        List<ScanFilter> scanFilterList = bookingAppraiseDao.makeScanFilterList(criteria, new String[]{
                "booking_id",
                "area_id",
                "score",
                "uin",
        });
        if (scanFilterList == null) {
            scanFilterList = new ArrayList<>();
        }
        if (StringUtils.isNotBlank(criteria.getSuggest())) {
            scanFilterList.add(new ScanFilter("suggest").contains(criteria.getSuggest()));
        }

        bookingAppraiseDao.appendDateRangeFilter(scanFilterList, "createtime", create_date_start, create_date_end);

        ScanSpec scanSpec = new ScanSpec();
        if (scanFilterList != null && scanFilterList.size() > 0) {
            scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        }

        List<BookingAppraise> bookingAppraiseList = bookingAppraiseDao.scan(scanSpec);

        List<Area> areaList = null;
        List<UserInfo> userInfoList = null;

        if (bookingAppraiseList != null && bookingAppraiseList.size() > 0) {

            Set<Integer> uinSet = new HashSet<>();
            Set<Integer> areaIdSet = new HashSet<>();

            bookingAppraiseList.forEach(new Consumer<BookingAppraise>() {
                @Override
                public void accept(BookingAppraise bookingAppraise) {
                    if (bookingAppraise == null) return;
                    if (bookingAppraise.getUin() != null) {
                        uinSet.add(bookingAppraise.getUin());
                    }
                    if (bookingAppraise.getArea_id() != null) {
                        areaIdSet.add(bookingAppraise.getArea_id());
                    }
                }
            });

            if (uinSet.size() > 0) {
                userInfoList = ServiceUtils.division(uinSet.toArray(new Integer[uinSet.size()]), 100, new CallBackForResult<Integer[], List<UserInfo>>() {
                    @Override
                    public List<UserInfo> run(Integer[] uins) {
                        return userInfoDao.batchGetItem("uin", uins, new String[]{
                                "uin",
                                "phone",
                        });
                    }
                }, new Integer[0]);
            }
            if (areaIdSet.size() > 0) {
                areaList = ServiceUtils.division(areaIdSet.toArray(new Integer[areaIdSet.size()]), 100, new CallBackForResult<Integer[], List<Area>>() {
                    @Override
                    public List<Area> run(Integer[] areaIds) {
                        return areaDao.batchGetItem("area_id", areaIds, new String[]{
                                "area_id",
                                "title",
                                "city",
                                "address",
                        });
                    }
                }, new Integer[0]);
            }

        }

        return new Result(CodeMsg.SUCCESS)
                .putData("bookingAppraiseList", bookingAppraiseList)
                .putData("areaList", areaList)
                .putData("userInfoList", userInfoList);

    }


    @GetMapping("/api/booking_appraise/{booking_id:\\d+}")
    @ResponseBody
    public Result getByBookingId(@PathVariable("booking_id") long booking_id) {
        BookingAppraise bookingAppraise = bookingAppraiseDao.getItem(new PrimaryKey("booking_id", booking_id));
        if (bookingAppraise == null) {
            return new Result(CodeMsg.NO_FOUND);
        } else {
            return new Result(CodeMsg.SUCCESS)
                    .putData("bookingAppraise", bookingAppraise)
                    .putData("userInfo", userService.getUserInfoByUin(bookingAppraise.getUin()))
                    .putData("area", bookingAppraise.getArea_id() != null ? areaDao.getItem(new PrimaryKey("area_id", bookingAppraise.getArea_id())) : null);
        }
    }


}
