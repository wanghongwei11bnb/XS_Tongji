package com.xiangshui.tj.config;

import com.xiangshui.tj.websocket.MyHandler;
import com.xiangshui.tj.websocket.MyHttpSessionHandshakeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/tj").addInterceptors(new MyHttpSessionHandshakeInterceptor());
    }

    @Bean
    public WebSocketHandler myHandler() {
        return new MyHandler();
    }
}
