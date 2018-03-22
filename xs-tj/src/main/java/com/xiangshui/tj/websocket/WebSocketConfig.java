package com.xiangshui.tj.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@EnableWebMvc
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private MyHandler myHandler;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler, "/tj")
                .addInterceptors(new MyHttpSessionHandshakeInterceptor())
                .setAllowedOrigins(
                        "http://www.xiangshuispace.com", "http://dev.xiangshuispace.com",
                        "http://op.xiangshuispace.com", "http://devop.xiangshuispace.com",
                        "http://h5.xiangshuispace.com", "http://dev.h5.xiangshuispace.com",
                        "http://tj.xiangshuispace.com", "http://dev.tj.xiangshuispace.com",
                        "http://localhost:8081", "http://localhost:8080",
                        "http://*:*",
                        "file://"
                );
    }
}
