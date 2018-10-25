package com.xiangshui.web.controller;

import com.alibaba.fastjson.JSON;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class BaseController {


    protected final Logger log = LoggerFactory.getLogger(this.getClass());


    @Value("${isdebug}")
    boolean debug;

    private long ts = System.currentTimeMillis();

    public void setClient(HttpServletRequest request) {
        request.setAttribute("debug", debug);
        request.setAttribute("ts", ts + "_" + (debug ? System.currentTimeMillis() : DateUtils.format("yyyyMMddHH")));
        request.setAttribute("DateUtils", DateUtils.class);
        request.setAttribute("JSON", JSON.class);
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


}
