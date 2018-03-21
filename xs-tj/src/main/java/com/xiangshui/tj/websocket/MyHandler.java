package com.xiangshui.tj.websocket;

import com.xiangshui.tj.server.bean.Appraise;
import com.xiangshui.tj.server.bean.City;
import com.xiangshui.tj.server.dao.DynamoDBService;
import com.xiangshui.tj.server.service.*;
import com.xiangshui.tj.server.task.BaseTask;
import com.xiangshui.tj.server.task.BookingTask;
import com.xiangshui.tj.websocket.message.ContractMessage;
import com.xiangshui.tj.websocket.message.UsageRateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(MyHandler.class);

    @Autowired
    WebSocketSessionManager sessionManager;

    @Autowired
    DynamoDBService dynamoDBService;

    @Autowired
    DataReceiver dataReceiver;


    @Autowired
    AreaDataManager areaDataManager;
    @Autowired
    CapsuleDataManager capsuleDataManager;
    @Autowired
    BookingDataManager bookingDataManager;
    @Autowired
    AppraiseDataManager appraiseDataManager;


    @Autowired
    BaseTask baseTask;
    @Autowired
    BookingTask bookingTask;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        sessionManager.addSession(session);


        ContractMessage contractMessage = new ContractMessage();
        contractMessage.setCityList(City.cityList);

        if (appraiseDataManager.getMap() != null && appraiseDataManager.size() > 0) {
            List<Appraise> appraiseList = new ArrayList(appraiseDataManager.getMap().values());
            List<Appraise> sendList = new ArrayList();
            for (int i = 0; i < 10; i++) {
                if (appraiseList.size() >= i + 1) {
                    Appraise appraise = appraiseList.get(appraiseList.size() - i - 1);
                    sendList.add(appraise);
                }
            }
            contractMessage.setAppraiseList(sendList);
        }

        sessionManager.sendMessage(session, contractMessage);
        if (UsageRateMessage.last != null) {
            sessionManager.sendMessage(session, UsageRateMessage.last);
        }
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
