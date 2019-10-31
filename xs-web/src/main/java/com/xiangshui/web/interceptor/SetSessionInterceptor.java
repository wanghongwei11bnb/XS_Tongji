package com.xiangshui.web.interceptor;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.dao.UserRegisterDao;
import com.xiangshui.server.dao.UserWalletDao;
import com.xiangshui.server.dao.redis.OpPrefix;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.UserRegister;
import com.xiangshui.web.bean.Session;
import com.xiangshui.web.threadLocal.SessionLocal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SetSessionInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${isdebug}")
    boolean debug;


    @Autowired
    UserInfoDao userInfoDao;

    @Autowired
    UserRegisterDao userRegisterDao;

    @Autowired
    UserWalletDao userWalletDao;

    @Autowired
    RedisService redisService;

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        String user_uin = httpServletRequest.getHeader("User-Uin");
        String client_token = httpServletRequest.getHeader("Client-Token");
        if (StringUtils.isNotBlank(user_uin) && user_uin.matches("^\\d+$") && StringUtils.isNotBlank(client_token)) {
            Integer uin = Integer.valueOf(user_uin);
            UserRegister userRegister = userRegisterDao.getItem(new PrimaryKey("uin", uin));
            if (userRegister != null && client_token.equals(userRegister.getLast_access_token())) {
                Session session = new Session().setUin(uin).setToken(client_token);
                SessionLocal.set(session);
            }
        }
        return true;
    }

    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        SessionLocal.remove();
    }
}
