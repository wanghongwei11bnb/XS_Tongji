package com.xiangshui.tj.server.service;

import com.alibaba.fastjson.JSONObject;
import com.xiangshui.tj.server.bean.*;
import com.xiangshui.tj.server.constant.GoodAppraiseCache;
import com.xiangshui.tj.server.constant.ReceiveEvent;
import com.xiangshui.tj.server.redis.RedisService;
import com.xiangshui.tj.server.redis.SendMessagePrefix;
import com.xiangshui.tj.server.task.*;
import com.xiangshui.tj.websocket.WebSocketSessionManager;
import com.xiangshui.tj.websocket.message.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

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

    public void receive(int event, UserTj user) {
        userDataManager.save(user);
    }

    public void receive(int event, AreaTj area) {
        areaDataManager.save(area);
        String cityName = area.getCity();
        if (StringUtils.isNotBlank(cityName) && !CityTj.cityMap.containsKey(cityName)) {
            CityTj city = new CityTj();
            city.setCity(cityName);
            if (CityTj.cityList != null) {
                for (CityTj cityItem : CityTj.cityList) {
                    if (cityName.equals(cityItem.getCity())) {
                        city.setProvince(cityItem.getProvince());
                        break;
                    }
                }
            }
            try {
                String string = Jsoup.connect("http://api.map.baidu.com/geocoder/v2/?address=" + city.getProvince() + city.getCity() + "&output=json&ak=" + "71UPECanchHaS66O2KsxPBSetZkCV7wW").execute().body();
                JSONObject resp = JSONObject.parseObject(string);
                if (resp.getIntValue("status") == 0) {
                    JSONObject location = resp.getJSONObject("result").getJSONObject("location");
                    city.setLat(location.getFloatValue("lat"));
                    city.setLng(location.getFloatValue("lng"));
                }
            } catch (Exception e) {
                log.error("", e);
            }
            CityTj.cityMap.put(cityName, city);
        }
    }

    public void receive(int event, CapsuleTj capsule) {
        if (capsule.getCreate_time() > 0) {
            capsule.setCreate_time_date(new Date(capsule.getCreate_time() * 1000l));
        }
        if (capsule.getUpdate_time() > 0) {
            capsule.setUpdate_time_date(new Date(capsule.getUpdate_time() * 1000l));
        }
        capsuleDataManager.save(capsule);
    }

    public void receive(int event, BookingTj booking) {
        bookingDataManager.save(booking);
        CapsuleTj capsule = capsuleDataManager.getById(booking.getCapsule_id());
        if (capsule != null) {
            if (capsule.getLastBookingTime() == null || booking.getCreate_time() * 1000 > capsule.getLastBookingTime().getTime()) {
                capsule.setLastBookingTime(new Date(booking.getCreate_time() * 1000));
            }

            Date endTime = booking.getStatus() == 1 ? new Date() : new Date(booking.getEnd_time() * 1000);
            if (capsule.getLastUseTime() == null || endTime.getTime() > capsule.getLastUseTime().getTime()) {
                capsule.setLastUseTime(new Date());
            }
        }


        if (event == ReceiveEvent.BOOKING_START) {
            PushBookingMessage pushBookingMessage = new PushBookingMessage();
            pushBookingMessage.setBooking(booking);
            pushBookingMessage.setArea(areaDataManager.getById(booking.getArea_id()));
            pushBookingMessage.setCapsule(capsuleDataManager.getById(booking.getCapsule_id()));
            UserTj user = userDataManager.getById(booking.getUin());
            if (user != null) {
                booking.setNick_name(user.getNick_name());
                booking.setPhone(user.getPhone());
            }
            sessionManager.sendMessage(pushBookingMessage);
        }
//        if (event != ReceiveEvent.HISTORY_DATA) {
//            doTask(new AbstractTask[]{generalTask, usageRateForHourTask, cumulativeBookingTask, cumulativeTimeTask}, new DataManager[]{areaDataManager, capsuleDataManager, bookingDataManager});
//        }
    }

    public void receive(int event, AppraiseTj appraise) {
        if (
                appraise.getAppraise() == null
                        || appraise.getAppraise().size() == 0
                        || (
                        appraise.getAppraise().size() == 1
                                && (StringUtils.isBlank(appraise.getAppraise().get(0)) || appraise.getAppraise().get(0).trim().equals("无")))
                ) {
            appraise.setAppraise(null);
        }
        if (StringUtils.isBlank(appraise.getSuggest()) && appraise.getAppraise() == null) {
            if (appraise.getScore() < 5) {
                return;
            } else {
                appraise.setSuggest(GoodAppraiseCache.random());
            }
        }
        UserTj user = userDataManager.getById(appraise.getUin());
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


    public void doTask(AbstractTask[] tasks, DataManager[] dataManagers) {
        List<TaskEntry> taskEntryList = new ArrayList<TaskEntry>();
        for (AbstractTask task : tasks) {
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

            dataManager.foreach(new BiConsumer() {
                @Override
                public void accept(Object k, Object object) {
                    for (TaskEntry taskEntry : taskEntryList) {
                        if (dataManagerClass == AreaDataManager.class) {
                            taskEntry.getTask().reduceArea((AreaTj) object, taskEntry.getResult());
                        } else if (dataManagerClass == CapsuleDataManager.class) {
                            taskEntry.getTask().reduceCapsule((CapsuleTj) object, taskEntry.getResult());
                        } else if (dataManagerClass == BookingDataManager.class) {
                            taskEntry.getTask().reduceBooking((BookingTj) object, taskEntry.getResult());
                        }
                    }
                }
            });

        }
        List<SendMessage> messageList = new ArrayList<SendMessage>();
        for (TaskEntry taskEntry : taskEntryList) {
            SendMessage message = taskEntry.getTask().toSendMessage(taskEntry.getResult());
            if (message != null) {
                redisService.set(SendMessagePrefix.cache, message.getClass().getSimpleName(), message);
                messageList.add(message);
            }
        }
        ListMessage listMessage = new ListMessage();
        listMessage.setMessageList(messageList);
        sessionManager.sendMessage(listMessage);
    }


}
