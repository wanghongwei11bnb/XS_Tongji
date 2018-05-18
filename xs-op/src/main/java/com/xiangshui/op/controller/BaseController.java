package com.xiangshui.op.controller;

import com.alibaba.fastjson.JSON;
import com.xiangshui.server.constant.AreaStatusOption;
import com.xiangshui.server.constant.CapsuleStatusOption;
import com.xiangshui.server.constant.DeviceVersionOption;
import com.xiangshui.server.constant.TimeLimitOption;
import com.xiangshui.server.dao.BaseDynamoDao;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());


    public void setClient(HttpServletRequest request) {
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
