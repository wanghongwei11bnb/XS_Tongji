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
        int code;
        if (booking.getCapsule_id() > 0) {
            code = (int) (booking.getCapsule_id() / 1000000);
        } else if (booking.getArea_id() > 0) {
            code = booking.getArea_id() / 1000;
        } else {
            return;
        }
        if (result.countBookingForCity.containsKey(code)) {
            result.countBookingForCity.put(code, result.countBookingForCity.get(code) + 1);
        }
    }

    public void reduce(Capsule capsule, Result result) {
        int code;
        if (capsule.getCapsule_id() > 0) {
            code = (int) (capsule.getCapsule_id() / 1000000);
        } else if (capsule.getArea_id() > 0) {
            code = capsule.getArea_id() / 1000;
        } else {
            return;
        }
        if (result.countCapsuleForCity.containsKey(code)) {
            result.countCapsuleForCity.put(code, result.countCapsuleForCity.get(code) + 1);
        }
    }


    public void reduce(Area area, Result result) {
        int code;
        if (area.getArea_id() > 0) {
            code = (area.getArea_id() / 1000);
        } else {
            return;
        }
        if (result.countAreaForCity.containsKey(code)) {
            result.countAreaForCity.put(code, result.countAreaForCity.get(code) + 1);
        }
    }

    public SendMessage toSendMessage(Result result) {


        if (City.cityMap != null) {
            for (int code : result.countBookingForCity.keySet()) {
                City city = City.cityMap.get(code);
                if (city != null) {
                    city.setCountBooking(result.countBookingForCity.get(code));
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
        public TreeMap<Integer, Integer> countAreaForCity;
        public TreeMap<Integer, Integer> countCapsuleForCity;
        public TreeMap<Integer, Integer> countBookingForCity;

        public Result() {

            countAreaForCity = new TreeMap<Integer, Integer>();
            countCapsuleForCity = new TreeMap<Integer, Integer>();
            countBookingForCity = new TreeMap<Integer, Integer>();

            for (City city : City.cityList) {
                countAreaForCity.put(city.getCode(), 0);
                countCapsuleForCity.put(city.getCode(), 0);
                countBookingForCity.put(city.getCode(), 0);

            }


        }
    }
}
