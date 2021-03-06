package com.xiangshui.op.interceptor;

import com.alibaba.fastjson.JSON;
import com.xiangshui.op.bean.Session;
import com.xiangshui.op.threadLocal.SessionLocal;
import com.xiangshui.server.dao.redis.OpPrefix;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RedisService redisService;

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if (httpServletRequest.getRequestURI().startsWith("/login")
                || httpServletRequest.getRequestURI().startsWith("/api/login")
                || httpServletRequest.getRequestURI().startsWith("/api/logout")
                ) {
            return true;
        }
        Session session = SessionLocal.get();
        if (session == null) {
            if (httpServletRequest.getRequestURI().startsWith("/api/")) {
                httpServletResponse.setHeader("Content-Type", "application/json;charset=UTF-8");
                httpServletResponse.getWriter().write(JSON.toJSONString(new Result(CodeMsg.NO_LOGIN)));
                httpServletResponse.getWriter().flush();
                httpServletResponse.getWriter().close();
            } else {
                httpServletRequest.getRequestDispatcher("/login").forward(httpServletRequest, httpServletResponse);
            }
            return false;
        }
        return true;
    }

    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
