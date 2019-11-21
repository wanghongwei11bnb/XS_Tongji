package com.xiangshui.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.xiangshui.web.annotation.LoginRequired;
import com.xiangshui.web.bean.Session;
import com.xiangshui.web.threadLocal.SessionLocal;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class LoginRequiredInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RedisService redisService;

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) o;
        Method method = handlerMethod.getMethod();
        LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
        if (loginRequired != null) {
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
        }
        return true;
    }

    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
