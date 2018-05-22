package com.xiangshui.op.interceptor;

import com.xiangshui.op.annotation.AuthPassport;
import com.xiangshui.server.domain.mysql.Op;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class AuthPassportInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        AuthPassport authPassport = method.getAnnotation(AuthPassport.class);
        if (authPassport != null) {
            Op op = (Op) httpServletRequest.getAttribute("op_auth");
            if (op == null) {
                return false;
            }
            String value = authPassport.value();
            if (value != null) {
                if (value.equals(op.getUsername())) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
