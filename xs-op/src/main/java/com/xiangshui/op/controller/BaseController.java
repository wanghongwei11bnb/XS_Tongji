package com.xiangshui.op.controller;

import com.alibaba.fastjson.JSON;
import com.xiangshui.op.annotation.AnnotationUtils;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.scheduled.AreaBillScheduled;
import com.xiangshui.op.scheduled.CacheScheduled;
import com.xiangshui.op.threadLocal.UsernameLocal;
import com.xiangshui.op.tool.BookingReportTools;
import com.xiangshui.server.constant.*;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.service.*;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

public class BaseController {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${isdebug}")
    boolean debug;



    @Autowired
    DiscountCouponDao discountCouponDao;

    @Autowired
    CityService cityService;
    @Autowired
    RedBagDao redBagDao;
    @Autowired
    AreaService areaService;
    @Autowired
    AreaDao areaDao;
    @Autowired
    S3Service s3Service;

    @Autowired
    AreaContractService areaContractService;

    @Autowired
    AreaContractDao areaContractDao;

    @Autowired
    CapsuleService capsuleService;

    @Autowired
    AreaBillScheduled areaBillScheduled;

    @Autowired
    AreaBillDao areaBillDao;

    @Autowired
    OpUserService opUserService;
    @Autowired
    ChargeRecordDao chargeRecordDao;
    @Autowired
    CashInfoDao cashInfoDao;
    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    UserService userService;
    @Autowired
    MonthCardService monthCardService;

    @Autowired
    BookingDao bookingDao;
    @Autowired
    CacheScheduled cacheScheduled;

    @Autowired
    BookingReportTools bookingReportTools;


    private long ts = System.currentTimeMillis();

    public void setClient(HttpServletRequest request) {
        String op_username = UsernameLocal.get();
        Set<String> authSet = opUserService.getAuthSet(op_username);
        Map<String, String> finalAuthMap = AnnotationUtils.getFinalAuthMap();
        request.setAttribute("authSet", JSON.toJSONString(authSet));
        request.setAttribute("finalAuthMap", JSON.toJSONString(finalAuthMap));
        request.setAttribute("debug", debug);
        request.setAttribute("ts", ts + "_" + (debug ? System.currentTimeMillis() : DateUtils.format("yyyyMMddHH")));
        request.setAttribute("op_username", UsernameLocal.get());
        request.setAttribute("DateUtils", DateUtils.class);
        request.setAttribute("JSON", JSON.class);
        request.setAttribute("maxResultSize", BaseDynamoDao.maxResultSize);
        request.setAttribute("testUinSet", JSON.toJSONString(areaBillScheduled.testUinSet));
        request.setAttribute("testPhoneSet", JSON.toJSONString(areaBillScheduled.testPhoneSet));
        setOptions(request);
    }


    /**
     * 基于@ExceptionHandler异常处理
     */
    @ExceptionHandler
    @ResponseBody
    public Result exp(HttpServletRequest request, Exception e) {
        log.error("", e);
        return new Result(CodeMsg.SERVER_ERROR.code, e.getMessage());
    }

    public void setOptions(HttpServletRequest request) {
        request.setAttribute("AreaStatusOption", JSON.toJSONString(AreaStatusOption.options));
        request.setAttribute("CapsuleStatusOption", JSON.toJSONString(CapsuleStatusOption.options));
        request.setAttribute("DeviceVersionOption", JSON.toJSONString(DeviceVersionOption.options));
        request.setAttribute("TimeLimitOption", JSON.toJSONString(TimeLimitOption.options));
        request.setAttribute("AreaContractStatusOption", JSON.toJSONString(AreaContractStatusOption.options));
        request.setAttribute("CityRegionOption", JSON.toJSONString(CityRegionOption.options));
    }


}
