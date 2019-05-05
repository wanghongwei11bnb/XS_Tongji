package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.scheduled.AreaBillScheduled;
import com.xiangshui.server.constant.AreaStatusOption;
import com.xiangshui.server.dao.AreaBillDao;
import com.xiangshui.server.dao.AreaContractDao;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BaseDynamoDao;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.service.*;
import com.xiangshui.util.*;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
public class CashInfoController extends BaseController {


    @Menu("奖金明细")
    @AuthRequired("用户管理")
    @GetMapping("/cash_info_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "cash_info_manage";
    }


    @GetMapping("/api/cash_info/search")
    @AuthRequired("用户管理")
    @ResponseBody
    public Result search(HttpServletRequest request, HttpServletResponse response, CashInfo criteria, String phone, Date create_date_start, Date create_date_end, Boolean download) throws NoSuchFieldException, IllegalAccessException, IOException {
        if (download == null) download = false;
        if (criteria == null) {
            criteria = new CashInfo();
        }
        List<ScanFilter> scanFilterList = cashInfoDao.makeScanFilterList(criteria, new String[]{
                "cash_id",
                "type",
                "uin",
                "booking_id",
        });
        if (scanFilterList == null) {
            scanFilterList = new ArrayList<>();
        }
        if (criteria.getUin() == null && StringUtils.isNotBlank(phone)) {
            UserInfo userInfo = userService.getUserInfoByPhone(phone);
            if (userInfo == null) {
                return new Result(-1, "用户不存在：" + phone);
            }
            scanFilterList.add(new ScanFilter("uin").eq(userInfo.getUin()));
        }
        cashInfoDao.appendDateRangeFilter(scanFilterList, "create_time", create_date_start, create_date_end);
        ScanSpec scanSpec = new ScanSpec();
        if (scanFilterList.size() > 0) {
            scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        }
        if (download) {
            scanSpec.withMaxResultSize(BaseDynamoDao.maxDownloadSize);
        }
        List<CashInfo> cashInfoList = cashInfoDao.scan(scanSpec);
        if (cashInfoList != null && cashInfoList.size() > 0) {
            cashInfoList.sort((o1, o2) -> o2.getCreate_time().compareTo(o1.getCreate_time()));
        }
        Set<Integer> uinSet = ListUtils.fieldSet(cashInfoList, cashInfo -> cashInfo.getUin());
        List<UserInfo> userInfoList = uinSet.size() > 0 ? userInfoDao.scan(new ScanSpec().withAttributesToGet(new String[]{"uin", "phone"}).withScanFilters(
                new ScanFilter("uin").in(uinSet.toArray())
        )) : null;

        if (download) {
            MapOptions<Integer, UserInfo> userInfoMapOptions = new MapOptions<Integer, UserInfo>(userInfoList) {
                @Override
                public Integer getPrimary(UserInfo userInfo) {
                    return userInfo.getUin();
                }
            };
            ExcelUtils.export(Arrays.asList(
                    new ExcelUtils.Column<CashInfo>("ID") {
                        @Override
                        public Object render(CashInfo cashInfo) {
                            return cashInfo.getCash_id();
                        }
                    },
                    new ExcelUtils.Column<CashInfo>("业务类型") {
                        @Override
                        public Object render(CashInfo cashInfo) {
                            switch (cashInfo.getType()) {
                                case 1:
                                    return "发放";
                                case 2:
                                    return "提现";
                                case 3:
                                    return "充值";
                                default:
                                    return null;
                            }
                        }
                    },
                    new ExcelUtils.Column<CashInfo>("时间") {
                        @Override
                        public Object render(CashInfo cashInfo) {
                            return cashInfo.getCreate_time() != null ? DateUtils.format(cashInfo.getCreate_time() * 1000) : null;
                        }
                    },
                    new ExcelUtils.Column<CashInfo>("金额") {
                        @Override
                        public Object render(CashInfo cashInfo) {
                            return cashInfo.getCash_num() != null ? cashInfo.getCash_num() / 100f : null;
                        }
                    },
                    new ExcelUtils.Column<CashInfo>("用户编号") {
                        @Override
                        public Object render(CashInfo cashInfo) {
                            return cashInfo.getUin();
                        }
                    },
                    new ExcelUtils.Column<CashInfo>("用户手机号") {
                        @Override
                        public Object render(CashInfo cashInfo) {
                            return userInfoMapOptions.containsKey(cashInfo.getUin()) ? userInfoMapOptions.get(cashInfo.getUin()).getPhone() : null;
                        }
                    },
                    new ExcelUtils.Column<CashInfo>("订单编号") {
                        @Override
                        public Object render(CashInfo cashInfo) {
                            return cashInfo.getBooking_id();
                        }
                    }
            ), cashInfoList, response, "cashInfoList.xlsx");
            return null;
        }
        return new Result(CodeMsg.SUCCESS)
                .putData("cashInfoList", cashInfoList)
                .putData("userInfoList", userInfoList);
    }

}
