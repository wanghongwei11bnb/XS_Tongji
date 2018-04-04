package com.xiangshui.tj.server.task;

import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.bean.City;
import com.xiangshui.tj.server.service.AreaDataManager;
import com.xiangshui.tj.server.service.BookingDataManager;
import com.xiangshui.tj.server.service.CapsuleDataManager;
import com.xiangshui.tj.websocket.message.GeneralMessage;
import com.xiangshui.tj.websocket.message.SendMessage;
import org.springframework.stereotype.Component;

import java.util.TreeMap;

@Component
public class GeneralTask extends Task<GeneralTask.Result> {

    public Result createResult() {
        return new Result();
    }


    public void handDataManager(AreaDataManager areaDataManager, Result result) {
        result.countArea = areaDataManager.size();
    }

    public void handDataManager(CapsuleDataManager capsuleDataManager, Result result) {
        result.countCapsule = capsuleDataManager.size();
    }

    public void handDataManager(BookingDataManager bookingDataManager, Result result) {
        result.countBooking = bookingDataManager.size();
    }


    public void reduce(Booking booking, Result result) {
        try {
            String cityName = areaDataManager.getMap().get(booking.getArea_id()).getCity();
            if (result.countBookingForCity.containsKey(cityName)) {
                result.countBookingForCity.put(cityName, result.countBookingForCity.get(cityName) + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reduce(Capsule capsule, Result result) {
        try {
            String cityName = areaDataManager.getMap().get(capsule.getArea_id()).getCity();
            if (result.countCapsuleForCity.containsKey(cityName)) {
                result.countCapsuleForCity.put(cityName, result.countCapsuleForCity.get(cityName) + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void reduce(Area area, Result result) {
        try {
            String cityName = area.getCity();

            if (result.countAreaForCity.containsKey(cityName)) {
                result.countAreaForCity.put(cityName, result.countAreaForCity.get(cityName) + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SendMessage toSendMessage(Result result) {
        if (City.cityMap != null) {
            for (String cityName : result.countBookingForCity.keySet()) {
                City city = City.cityMap.get(cityName);
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

            for (City city : City.cityMap.values()) {
                countAreaForCity.put(city.getCity(), 0);
                countCapsuleForCity.put(city.getCity(), 0);
                countBookingForCity.put(city.getCity(), 0);

            }


        }
    }
}
