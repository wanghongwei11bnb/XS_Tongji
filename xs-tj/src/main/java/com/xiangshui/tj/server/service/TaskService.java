package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.redis.RedisService;
import com.xiangshui.tj.server.task.*;
import com.xiangshui.tj.websocket.WebSocketSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskService {



    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    BaseTask baseTask;
    @Autowired
    GeneralTask generalTask;
    @Autowired
    BookingTask bookingTask;
    @Autowired
    UsageRateForHourTask usageRateForHourTask;
    @Autowired
    CumulativeBookingTask cumulativeBookingTask;
    @Autowired
    CumulativeTimeTask cumulativeTimeTask;


    @Autowired
    UserDataManager userDataManager;
    @Autowired
    AreaDataManager areaDataManager;
    @Autowired
    CapsuleDataManager capsuleDataManager;
    @Autowired
    BookingDataManager bookingDataManager;
    @Autowired
    AppraiseDataManager appraiseDataManager;
    @Autowired
    WebSocketSessionManager sessionManager;
    @Autowired
    RedisService redisService;








}
