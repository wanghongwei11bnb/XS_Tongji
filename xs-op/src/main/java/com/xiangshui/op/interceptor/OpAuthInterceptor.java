package com.xiangshui.op.interceptor;

import com.xiangshui.op.annotation.AuthPassport;
import com.xiangshui.server.dao.redis.OpPrefix;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class OpAuthInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${isdebug}")
    boolean debug;

    @Autowired
    RedisService redisService;

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        httpServletRequest.setAttribute("ts", debug ? System.currentTimeMillis() : DateUtils.format("yyyyMMddHH"));
        httpServletRequest.setAttribute("debug", debug);
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("op_token".equals(cookie.getName())) {
                    String op_token = cookie.getValue();
                    if (StringUtils.isNotBlank(op_token)) {
                        Op op = redisService.get(OpPrefix.op_token, op_token, Op.class);
                        if (op != null) {
                            httpServletRequest.setAttribute("op_auth", op);
                        }
                    }
                }
            }
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        AuthPassport authPassport = method.getAnnotation(AuthPassport.class);
        if (authPassport != null) {
            Op op = (Op) httpServletRequest.getAttribute("op_auth");
            if (op == null) {
                return false;
            }
            String[] values = authPassport.value();
            if (values != null) {
                for (String value : values) {
                    if (value.equals(op.getUsername())) {
                        return true;
                    }
                }
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
