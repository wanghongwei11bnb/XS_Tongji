package com.xiangshui.op.interceptor;

import com.xiangshui.op.bean.OpRecord;
import com.xiangshui.op.bean.Session;
import com.xiangshui.op.threadLocal.SessionLocal;
import com.xiangshui.op.threadLocal.UsernameLocal;
import com.xiangshui.server.dao.redis.OpPrefix;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SetOpAuthInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${isdebug}")
    boolean debug;

    @Autowired
    RedisService redisService;

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("op_session".equals(cookie.getName())) {
                    String op_session = cookie.getValue();
                    if (StringUtils.isNotBlank(op_session)) {
                        Session session = redisService.get(OpPrefix.session, op_session, Session.class);
                        if (session != null) {
                            SessionLocal.set(session);
                            UsernameLocal.set(session.getUsername());
                        }
                    }
                }
            }
        }
        return true;
    }

    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        UsernameLocal.remove();
        SessionLocal.remove();
    }
}
