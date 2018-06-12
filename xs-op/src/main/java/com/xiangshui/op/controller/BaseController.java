package com.xiangshui.op.controller;

import com.alibaba.fastjson.JSON;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.threadLocal.UsernameLocal;
import com.xiangshui.server.constant.AreaStatusOption;
import com.xiangshui.server.constant.CapsuleStatusOption;
import com.xiangshui.server.constant.DeviceVersionOption;
import com.xiangshui.server.constant.TimeLimitOption;
import com.xiangshui.server.dao.BaseDynamoDao;
import com.xiangshui.server.service.OpUserService;
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
import java.util.Set;

public class BaseController {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${isdebug}")
    boolean debug;

    @Autowired
    OpUserService opUserService;


    public void setClient(HttpServletRequest request) {
        String op_username = UsernameLocal.get();
        Set<String> authSet = opUserService.getAuthSet(op_username);
        request.setAttribute("auth_booking_show_phone", authSet != null && authSet.contains(AuthRequired.auth_booking_show_phone));
        request.setAttribute("auth_booking_download", authSet != null && authSet.contains(AuthRequired.auth_booking_download));
        request.setAttribute("authSet", JSON.toJSONString(authSet));
        request.setAttribute("debug", debug);
        request.setAttribute("ts", debug ? System.currentTimeMillis() : DateUtils.format("yyyyMMddHH"));
        request.setAttribute("op_username", UsernameLocal.get());
        request.setAttribute("DateUtils", DateUtils.class);
        request.setAttribute("JSON", JSON.class);
        request.setAttribute("maxResultSize", BaseDynamoDao.maxResultSize);
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
    }


}
