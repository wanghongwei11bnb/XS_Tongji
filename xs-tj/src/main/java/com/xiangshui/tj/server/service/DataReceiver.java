package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.*;
import com.xiangshui.tj.server.constant.ReceiveEvent;
import com.xiangshui.tj.server.redis.RedisService;
import com.xiangshui.tj.server.redis.SendMessagePrefix;
import com.xiangshui.tj.server.task.*;
import com.xiangshui.tj.websocket.WebSocketSessionManager;
import com.xiangshui.tj.websocket.message.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataReceiver {


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

    private static final Logger log = LoggerFactory.getLogger(DataReceiver.class);

    public void receive(int event, User user) {
        userDataManager.save(user);
    }

    public void receive(int event, Area area) {
        areaDataManager.save(area);
        String cityName = area.getCity();
        if (StringUtils.isNotBlank(cityName)) {
            City city = new City();
            city.setCity(cityName);
            if (City.cityList != null) {
                for (City cityItem : City.cityList) {
                    if (cityName.equals(cityItem.getCity())) {
                        city.setProvince(cityItem.getProvince());
                        break;
                    }
                }
            }
            City.cityMap.put(cityName, city);
        }
    }

    public void receive(int event, Capsule capsule) {

        if (capsule.getStatus() == -1) {
            return;
        }

        capsuleDataManager.save(capsule);
    }

    public void receive(int event, Booking booking) {
        bookingDataManager.save(booking);
        if (event == ReceiveEvent.BOOKING_START) {
            PushBookingMessage pushBookingMessage = new PushBookingMessage();
            pushBookingMessage.setBooking(booking);
            pushBookingMessage.setArea(areaDataManager.getById(booking.getArea_id()));
            pushBookingMessage.setCapsule(capsuleDataManager.getById(booking.getCapsule_id()));
            User user = userDataManager.getById(booking.getUin());
            if (user != null) {
                booking.setNick_name(user.getNick_name());
                booking.setPhone(user.getPhone());
            }
            sessionManager.sendMessage(pushBookingMessage);
        }
        if (event != ReceiveEvent.HISTORY_DATA) {
            doTask(new Task[]{generalTask, usageRateForHourTask, cumulativeBookingTask, cumulativeTimeTask}, new DataManager[]{areaDataManager, capsuleDataManager, bookingDataManager});
        }
    }

    public void receive(int event, Appraise appraise) {

        if (
                StringUtils.isBlank(appraise.getSuggest()) && (
                        appraise.getAppraise() == null
                                || appraise.getAppraise().size() == 0
                                || (
                                appraise.getAppraise().size() == 1
                                        && (StringUtils.isBlank(appraise.getAppraise().get(0)) || appraise.getAppraise().get(0).trim().equals("无")))
                )
                ) {
            return;
        }


        User user = userDataManager.getById(appraise.getUin());
        if (user != null) {
            appraise.setPhone(user.getPhone());
            appraise.setNick_name(user.getNick_name());
            appraise.setPhone(user.getPhone());
        }
        appraiseDataManager.save(appraise);
        if (event == ReceiveEvent.APPRAISE) {
            PushAppraiseMessage message = new PushAppraiseMessage();
            message.setAppraise(appraise);
            sessionManager.sendMessage(message);
        }

    }


    public void doTask(Task[] tasks, DataManager[] dataManagers) {
        List<TaskEntry> taskEntryList = new ArrayList<TaskEntry>();
        for (Task task : tasks) {
            taskEntryList.add(task.createTaskEntry());
        }
        for (DataManager dataManager : dataManagers) {
            Class dataManagerClass = dataManager.getClass();
            for (TaskEntry taskEntry : taskEntryList) {
                if (dataManagerClass == AreaDataManager.class) {
                    taskEntry.getTask().handDataManager((AreaDataManager) dataManager, taskEntry.getResult());
                } else if (dataManagerClass == CapsuleDataManager.class) {
                    taskEntry.getTask().handDataManager((CapsuleDataManager) dataManager, taskEntry.getResult());
                } else if (dataManagerClass == BookingDataManager.class) {
                    taskEntry.getTask().handDataManager((BookingDataManager) dataManager, taskEntry.getResult());
                }
            }
            for (Object object : new ArrayList(dataManager.getMap().values())) {
                for (TaskEntry taskEntry : taskEntryList) {
                    if (dataManagerClass == AreaDataManager.class) {
                        taskEntry.getTask().reduce((Area) object, taskEntry.getResult());
                    } else if (dataManagerClass == CapsuleDataManager.class) {
                        taskEntry.getTask().reduce((Capsule) object, taskEntry.getResult());
                    } else if (dataManagerClass == BookingDataManager.class) {
                        taskEntry.getTask().reduce((Booking) object, taskEntry.getResult());
                    }
                }
            }
        }
        List<SendMessage> messageList = new ArrayList<SendMessage>();
        for (TaskEntry taskEntry : taskEntryList) {
            SendMessage message = taskEntry.getTask().toSendMessage(taskEntry.getResult());
            redisService.set(SendMessagePrefix.cache, message.getClass().getSimpleName(), message);
            messageList.add(message);
        }
        ListMessage listMessage = new ListMessage();
        listMessage.setMessageList(messageList);
        sessionManager.sendMessage(listMessage);
    }


}
