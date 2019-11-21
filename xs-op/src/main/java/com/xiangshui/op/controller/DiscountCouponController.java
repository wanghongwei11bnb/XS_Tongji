package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.scheduled.CountCapsuleScheduled;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.CapsuleService;
import com.xiangshui.server.service.CityService;
import com.xiangshui.server.service.S3Service;
import com.xiangshui.util.ListUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class DiscountCouponController extends BaseController {


    @Menu(value = "优惠券管理")
    @AuthRequired(AuthRequired.auth_coupon)
    @GetMapping("/discount_coupon_manage")
    public String area_manage(HttpServletRequest request) {
        setClient(request);
        return "discount_coupon_manage";
    }

    @GetMapping("/api/discount_coupon/search")
    @AuthRequired(AuthRequired.auth_coupon)
    @ResponseBody
    public Result search(DiscountCoupon criteria, String phone) throws NoSuchFieldException, IllegalAccessException {
        if (criteria == null) criteria = new DiscountCoupon();
        if (criteria.getUin() == null && StringUtils.isNotBlank(phone)) {
            UserInfo userInfo = userService.getUserInfoByPhone(phone);
            if (userInfo == null) {
                return new Result(-1, "手机号码不存在");
            } else {
                criteria.setUin(userInfo.getUin());
            }
        }

        ScanSpec scanSpec = new ScanSpec();
        List<ScanFilter> scanFilterList = discountCouponDao.makeScanFilterList(criteria, new String[]{
                "coupon_id",
                "uin",
                "status",
                "type",
        });

        scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        List<DiscountCoupon> couponList = discountCouponDao.scan(scanSpec);

        if (couponList != null) {
            couponList.sort((o1, o2) -> o2.getCreate_time().compareTo(o1.getCreate_time()));
        }

        Set<Integer> uinSet = ListUtils.fieldSet(couponList, DiscountCoupon::getUin);

        List<UserInfo> userInfoList = userService.getUserInfoList(uinSet.toArray(new Integer[uinSet.size()]), null);


        return new Result(CodeMsg.SUCCESS)
                .putData("couponList", couponList)
                .putData("userInfoList", userInfoList);
    }


    @GetMapping("/api/discount_coupon/{coupon_id:\\d+}")
    @AuthRequired(AuthRequired.auth_coupon)
    @ResponseBody
    public Result get(@PathVariable("coupon_id") Long coupon_id) {

        DiscountCoupon coupon = discountCouponDao.getItem(new PrimaryKey("coupon_id", coupon_id));
        if (coupon != null) {
            return new Result(CodeMsg.SUCCESS).putData("coupon", coupon).putData("userInfo", userService.getUserInfoByUin(coupon.getUin()));
        } else {
            return new Result(CodeMsg.NO_FOUND);
        }
    }


}
