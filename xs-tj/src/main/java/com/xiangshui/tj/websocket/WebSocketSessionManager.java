package com.xiangshui.tj.websocket;

import com.alibaba.fastjson.JSON;
import com.xiangshui.tj.websocket.message.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class WebSocketSessionManager {

    private static final Logger log = LoggerFactory.getLogger(WebSocketSessionManager.class);

    private final Set<WebSocketSession> sessionSet = new HashSet();

    public void addSession(WebSocketSession session) {
        synchronized (sessionSet) {
            sessionSet.add(session);
        }
    }


    public void removeSession(WebSocketSession session) {
        synchronized (sessionSet) {
            sessionSet.remove(session);
        }
    }

    public int sizeSession() {
        synchronized (sessionSet) {
            return sessionSet.size();
        }
    }

    public void sendMessage(SendMessage message) {
        synchronized (sessionSet) {
            String msg = JSON.toJSONString(message);
            log.info("sendMessage:" + message.getClass().getSimpleName() + ":" + msg);
            for (WebSocketSession session : sessionSet) {
                try {
                    session.sendMessage(new TextMessage(msg));
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    public void sendMessage(WebSocketSession session, SendMessage message) {
        String msg = JSON.toJSONString(message);
        log.info("sendMessage:" + message.getClass().getSimpleName() + ":" + msg);
        try {
            session.sendMessage(new TextMessage(msg));
        } catch (IOException e) {
            log.error("", e);
        }
    }
}
