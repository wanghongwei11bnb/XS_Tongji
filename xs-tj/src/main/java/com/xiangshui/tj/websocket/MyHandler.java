package com.xiangshui.tj.websocket;

import com.alibaba.fastjson.JSONObject;
import com.xiangshui.tj.constant.TaskModule;
import com.xiangshui.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class MyHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(MyHandler.class);

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put("constant_taskModule", ClassUtils.getStaticJSON(TaskModule.class));
        session.sendMessage(new TextMessage(jsonMessage.toJSONString()));
        sessionManager.addSession(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        log.info("handleTextMessage(" + session.getAttributes().get("uin") + "," + session.getAttributes().get("token") + "):" + message.getPayload());
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessionManager.removeSession(session);
        log.info("afterConnectionClosed");
    }

    @Override
    public boolean supportsPartialMessages() {
        return super.supportsPartialMessages();
    }
}
