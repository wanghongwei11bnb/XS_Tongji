package com.xiangshui.op.interceptor;

import com.alibaba.fastjson.JSON;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.bean.Session;
import com.xiangshui.op.threadLocal.SessionLocal;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.server.mapper.OpMapper;
import com.xiangshui.server.service.OpUserService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Set;

public class AuthPassportInterceptor implements HandlerInterceptor {

    @Value("${isdebug}")
    boolean debug;
    @Autowired
    OpMapper opMapper;

    @Autowired
    OpUserService opUserService;


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        boolean authed = false;
        String authName = null;
        AuthRequired authRequired = method.getAnnotation(AuthRequired.class);
        if (authRequired != null) {
            Session session = SessionLocal.get();
            if (session != null) {
                authName = authRequired.value();
                if (StringUtils.isNotBlank(authName)) {
                    Set<String> authSet = opUserService.getAuthSet(session.getUsername());
                    if (authSet.contains(authName)) {
                        authed = true;
                    }
                }
            }
        } else {
            authed = true;
        }
        if (authed) {
            return true;
        } else {
            if (httpServletRequest.getRequestURI().startsWith("/api")) {
                httpServletResponse.setHeader("Content-Type", "application/json;charset=UTF-8");
                if (StringUtils.isNotBlank(authName)) {
                    httpServletResponse.getWriter().write(new Result(CodeMsg.OPAUTH_FAIL).setMsg("没有权限：" + authName).toString());
                } else {
                    httpServletResponse.getWriter().write(new Result(CodeMsg.OPAUTH_FAIL).toString());
                }
                httpServletResponse.getWriter().flush();
                httpServletResponse.getWriter().close();
            } else {
                httpServletRequest.setAttribute("authName", authName);
                httpServletRequest.getRequestDispatcher("/error/no_auth").forward(httpServletRequest, httpServletResponse);
            }
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
