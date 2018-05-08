package com.xiangshui.tj.server.task;

import com.xiangshui.tj.server.bean.AreaTj;
import com.xiangshui.tj.server.bean.BookingTj;
import com.xiangshui.tj.server.bean.CapsuleTj;
import com.xiangshui.tj.server.bean.CityTj;
import com.xiangshui.tj.server.service.AreaDataManager;
import com.xiangshui.tj.server.service.BookingDataManager;
import com.xiangshui.tj.server.service.CapsuleDataManager;
import com.xiangshui.tj.websocket.message.GeneralMessage;
import com.xiangshui.tj.websocket.message.SendMessage;
import com.xiangshui.util.CallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.TreeMap;

@Component
public class GeneralTask extends AbstractTask<GeneralTask.Result> {

    private static final Logger log = LoggerFactory.getLogger(GeneralTask.class);

    public Result createResult() {
        return new Result();
    }


    public void handDataManager(AreaDataManager areaDataManager, Result result) {
//        result.countArea = areaDataManager.size();
    }

    public void handDataManager(CapsuleDataManager capsuleDataManager, Result result) {
//        result.countCapsule = capsuleDataManager.size();
    }

    public void handDataManager(BookingDataManager bookingDataManager, Result result) {
        result.countBooking = bookingDataManager.size();
    }


    public void reduceBooking(BookingTj booking, Result result) {
        CapsuleTj capsule = capsuleDataManager.getById(booking.getCapsule_id());
        if (capsule == null) {
            return;
        }
        AreaTj area = areaDataManager.getById(capsule.getArea_id());
        if (area == null) {
            return;
        }
        String cityName = area.getCity();
        if (result.countBookingForCity.containsKey(cityName)) {
            result.countBookingForCity.put(cityName, result.countBookingForCity.get(cityName) + 1);
        }
    }

    public void reduceCapsule(CapsuleTj capsule, Result result) {
        isOnline(capsule, new CallBack<AreaTj>() {
            @Override
            public void run(AreaTj object) {
                result.countCapsule++;
                String cityName = object.getCity();
                if (result.countCapsuleForCity.containsKey(cityName)) {
                    result.countCapsuleForCity.put(cityName, result.countCapsuleForCity.get(cityName) + 1);
                }
            }
        });
    }

    public void reduceArea(AreaTj area, Result result) {
        if (isOnline(area)) {
            result.countArea++;
            String cityName = area.getCity();
            if (result.countAreaForCity.containsKey(cityName)) {
                result.countAreaForCity.put(cityName, result.countAreaForCity.get(cityName) + 1);
            }
        }
    }

    public SendMessage toSendMessage(Result result) {
        if (CityTj.cityMap != null) {
            for (String cityName : result.countBookingForCity.keySet()) {
                CityTj city = CityTj.cityMap.get(cityName);
                if (city != null) {
                    city.setCountArea(result.countAreaForCity.get(cityName));
                    city.setCountCapsule(result.countCapsuleForCity.get(cityName));
                    city.setCountBooking(result.countBookingForCity.get(cityName));
                }
            }
        }

        GeneralMessage message = new GeneralMessage();
        message.countArea = result.countArea;
        message.countCapsule = result.countCapsule;
        message.countBooking = result.countBooking;
        message.countAreaForCity = result.countAreaForCity;
        message.countCapsuleForCity = result.countCapsuleForCity;
        message.countBookingForCity = result.countBookingForCity;
        return message;
    }

    public static class Result {
        public int countArea;
        public int countCapsule;
        public int countBooking;
        public TreeMap<String, Integer> countAreaForCity;
        public TreeMap<String, Integer> countCapsuleForCity;
        public TreeMap<String, Integer> countBookingForCity;

        public Result() {

            countAreaForCity = new TreeMap<String, Integer>();
            countCapsuleForCity = new TreeMap<String, Integer>();
            countBookingForCity = new TreeMap<String, Integer>();

            for (CityTj city : CityTj.cityMap.values()) {
                countAreaForCity.put(city.getCity(), 0);
                countCapsuleForCity.put(city.getCity(), 0);
                countBookingForCity.put(city.getCity(), 0);

            }


        }
    }
}
