package com.xiangshui.tj.websocket;

import com.xiangshui.tj.server.bean.Appraise;
import com.xiangshui.tj.server.bean.City;
import com.xiangshui.tj.server.dynamedb.DynamoDBService;
import com.xiangshui.tj.server.redis.RedisService;
import com.xiangshui.tj.server.redis.SendMessagePrefix;
import com.xiangshui.tj.server.service.*;
import com.xiangshui.tj.server.task.BaseTask;
import com.xiangshui.tj.server.task.BookingTask;
import com.xiangshui.tj.websocket.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

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
    @Autowired
    RedisService redisService;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        sessionManager.addSession(session);

        List<SendMessage> messageList = new ArrayList<SendMessage>();
        //ContractMessage
        if (City.cityList != null) {
            ContractMessage contractMessage = new ContractMessage();
            contractMessage.setCityList(City.cityList);
            redisService.set(SendMessagePrefix.cache, contractMessage.getClass().getSimpleName(), contractMessage);
            messageList.add(contractMessage);
        } else if (redisService.exists(SendMessagePrefix.cache, ContractMessage.class.getSimpleName())) {
            messageList.add(redisService.get(SendMessagePrefix.cache, ContractMessage.class.getSimpleName(), ContractMessage.class));
        }
        //InitAppraiseMessage
        if (appraiseDataManager.getMap() != null && appraiseDataManager.getMap().size() > 0) {
            List<Appraise> appraiseList = new ArrayList<Appraise>(appraiseDataManager.getMap().values());
            InitAppraiseMessage initAppraiseMessage = new InitAppraiseMessage();
            initAppraiseMessage.setAppraiseList(appraiseList);
            redisService.set(SendMessagePrefix.cache, initAppraiseMessage.getClass().getSimpleName(), initAppraiseMessage);
            messageList.add(initAppraiseMessage);
        } else if (redisService.exists(SendMessagePrefix.cache, InitAppraiseMessage.class.getSimpleName())) {
            messageList.add(redisService.get(SendMessagePrefix.cache, InitAppraiseMessage.class.getSimpleName(), InitAppraiseMessage.class));
        }
        //UsageRateMessage
        if (redisService.exists(SendMessagePrefix.cache, UsageRateMessage.class.getSimpleName())) {
            messageList.add(redisService.get(SendMessagePrefix.cache, UsageRateMessage.class.getSimpleName(), UsageRateMessage.class));
        }
        //CumulativeBookingMessage
        if (redisService.exists(SendMessagePrefix.cache, CumulativeBookingMessage.class.getSimpleName())) {
            messageList.add(redisService.get(SendMessagePrefix.cache, CumulativeBookingMessage.class.getSimpleName(), CumulativeBookingMessage.class));
        }
        //CumulativeTimeMessage
        if (redisService.exists(SendMessagePrefix.cache, CumulativeTimeMessage.class.getSimpleName())) {
            messageList.add(redisService.get(SendMessagePrefix.cache, CumulativeTimeMessage.class.getSimpleName(), CumulativeTimeMessage.class));
        }
        //ListMessage
        ListMessage listMessage = new ListMessage();
        listMessage.setMessageList(messageList);
        sessionManager.sendMessage(session, listMessage);

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
