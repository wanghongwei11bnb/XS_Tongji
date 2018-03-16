package com.xiangshui.tj.websocket;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class MyHttpSessionHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    private static final Logger log = LoggerFactory.getLogger(MyHttpSessionHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
            HttpServletRequest servletRequest = servletServerHttpRequest.getServletRequest();
            String uin = servletRequest.getParameter("uin");
            String token = servletRequest.getParameter("token");
            if (StringUtils.isBlank(uin) && StringUtils.isBlank(token)) {
                log.info("匿名建立websocket连接");
                return true;
            } else if (StringUtils.isNotBlank(uin) && StringUtils.isNotBlank(token)) {
                log.info("实名建立websocket连接：uin=" + uin + ",token=" + token);
                attributes.put("uin", uin);
                attributes.put("token", token);
                return true;
            } else {
                log.error("uin和token必须同时传递或不传");
            }
        } else {
            log.error("request请求类型不是ServletServerHttpRequest类型");
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
}
