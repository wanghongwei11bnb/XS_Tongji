package com.xiangshui.op.interceptor;

import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.bean.Session;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.server.mapper.OpMapper;
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

public class AuthPassportInterceptor implements HandlerInterceptor {

    @Value("${isdebug}")
    boolean debug;
    @Autowired
    OpMapper opMapper;



    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        boolean authed = false;
        AuthRequired authRequired = method.getAnnotation(AuthRequired.class);
        if (authRequired != null) {
            Session session = (Session) httpServletRequest.getAttribute("session");
            if (session != null) {
                String value = authRequired.value();
                if (StringUtils.isNotBlank(value)) {
                    Op op = opMapper.selectByPrimaryKey(session.getUsername(), "auths");
                    if (op != null && StringUtils.isNotBlank(op.getAuths())) {
                        String[] authArr = op.getAuths().split(",");
                        if (authArr != null && authArr.length > 0) {
                            for (String auth : authArr) {
                                if (value.equals(auth)) {
                                    authed = true;
                                }
                            }
                        }
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
                httpServletResponse.getWriter().write(new Result(CodeMsg.OPAUTH_FAIL).toString());
                httpServletResponse.getWriter().flush();
                httpServletResponse.getWriter().close();
            } else {
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
