package com.xiangshui.tj.config;

import com.xiangshui.tj.websocket.MyHandler;
import com.xiangshui.tj.websocket.MyHttpSessionHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Component
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private MyHandler myHandler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler, "/tj").addInterceptors(new MyHttpSessionHandshakeInterceptor()).setAllowedOrigins(
                "http://localhost:8080",
                "http://localhost:8081",
                "http://*:*"
        );
    }

}
