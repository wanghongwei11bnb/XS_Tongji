package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.service.ServiceUtils;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.ListUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
public class InvitedController extends BaseController {


    @GetMapping("/invited_manage")
    @Menu(value = "受邀纪录")
    @AuthRequired("受邀纪录")
    public String view(HttpServletRequest request) {
        setClient(request);
        return "invited_manage";
    }


    @PostMapping("/api/invited/search")
    @AuthRequired("受邀纪录")
    @ResponseBody
    public Result search(
            String invited_bys,
            Date invited_date_start, Date invited_date_end,
            Integer booking_status,
            Date booking_date_start, Date booking_date_end,
            Boolean download,
            HttpServletRequest request, HttpServletResponse response
    ) throws IOException {
        if (download == null) download = false;
        {
            if (invited_date_start == null || invited_date_end == null) throw new XiangShuiException("邀请日期不能为空");
            if (invited_date_start.compareTo(invited_date_end) > 0) throw new XiangShuiException("邀请日期输入有误");
            if (new LocalDate(invited_date_start).plusDays(30 * 3).compareTo(new LocalDate(invited_date_end)) < 0)
                throw new XiangShuiException("邀请日期范围不能大于3个月");
        }
        {
            if (booking_date_start == null || booking_date_end == null) throw new XiangShuiException("订单日期不能为空");
            if (booking_date_start.compareTo(booking_date_end) > 0) throw new XiangShuiException("订单日期输入有误");
            if (new LocalDate(booking_date_start).plusDays(30 * 3).compareTo(new LocalDate(booking_date_end)) < 0)
                throw new XiangShuiException("订单日期范围不能大于3个月");
        }

        List<UserInfo> userInfoList;
        {
            List<ScanFilter> scanFilterList = new ArrayList<>();
            scanFilterList.add(new ScanFilter("create_time").between(
                    new LocalDate(invited_date_start).toDate().getTime() / 1000,
                    new LocalDate(invited_date_end).plusDays(1).toDate().getTime() / 1000
            ));
            if (StringUtils.isNotBlank(invited_bys)) {
                Set<Integer> invited_by_set = new HashSet<>();
                for (String s : invited_bys.split("\n")) {
                    if (StringUtils.isNotBlank(s) && (s = s.trim()).matches("^\\d+$")) {
                        invited_by_set.add(Integer.valueOf(s));
                    }
                }
                if (invited_by_set.size() > 0) {
                    if (invited_by_set.size() > 100) {
                        throw new XiangShuiException("邀请人uin数量不能超过100");
                    }
                    scanFilterList.add(new ScanFilter("invited_by").in(invited_by_set.toArray()));
                } else {
                    scanFilterList.add(new ScanFilter("invited_by").exists());
                }
            } else {
                scanFilterList.add(new ScanFilter("invited_by").exists());
            }

            userInfoList = userInfoDao.scan(new ScanSpec()
                    .withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]))
            );
        }

        List<Booking> bookingList = null;
        if (userInfoList != null && userInfoList.size() > 0) {
            Set<Integer> uinSet = ListUtils.fieldSet(userInfoList, UserInfo::getUin);
            if (uinSet.size() > 0) {
                bookingList = ServiceUtils.division(uinSet.toArray(new Integer[uinSet.size()]), 100, uins -> {
                    List<ScanFilter> scanFilterList = new ArrayList<>();
                    scanFilterList.add(new ScanFilter("create_time").between(booking_date_start.getTime() / 1000, new LocalDate(booking_date_end).plusDays(1).toDate().getTime() / 1000));
                    scanFilterList.add(new ScanFilter("uin").in(uins));
                    if (booking_status != null) {
                        scanFilterList.add(new ScanFilter("status").eq(booking_status));
                    }
                    return bookingDao.scan(new ScanSpec()
                            .withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]))
                            .withAttributesToGet(new String[]{
                                    "booking_id",
                                    "uin",
                            })
                    );
                }, new Integer[0]);
            }
        }
        if (download) {
            Map<Integer, Integer> countBookingMap = new HashMap<>();
            if (bookingList != null) {
                for (Booking booking : bookingList) {
                    Integer uin = booking.getUin();
                    if (countBookingMap.containsKey(uin)) {
                        countBookingMap.put(uin, countBookingMap.get(uin) + 1);
                    } else {
                        countBookingMap.put(uin, 1);
                    }
                }
            }

            ExcelUtils.export(Arrays.asList(
                    new ExcelUtils.Column<UserInfo>("被邀请人uin") {
                        @Override
                        public Object render(UserInfo userInfo) {
                            return userInfo.getUin();
                        }
                    },
                    new ExcelUtils.Column<UserInfo>("被邀请人手机号") {
                        @Override
                        public Object render(UserInfo userInfo) {
                            return userInfo.getPhone();
                        }
                    },
                    new ExcelUtils.Column<UserInfo>("被邀请时间") {
                        @Override
                        public Object render(UserInfo userInfo) {
                            return DateUtils.format(userInfo.getCreate_time() * 1000);
                        }
                    },
                    new ExcelUtils.Column<UserInfo>("邀请人uin") {
                        @Override
                        public Object render(UserInfo userInfo) {
                            return userInfo.getInvited_by();
                        }
                    },
                    new ExcelUtils.Column<UserInfo>("被邀请人订单数") {
                        @Override
                        public Object render(UserInfo userInfo) {
                            return countBookingMap.get(userInfo.getUin());
                        }
                    }
            ), userInfoList, response, "invited.xlsx");
            return null;
        } else {
            return new Result(CodeMsg.SUCCESS)
                    .putData("userInfoList", userInfoList)
                    .putData("bookingList", bookingList)
                    ;
        }

    }


}
