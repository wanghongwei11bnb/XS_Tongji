package com.xiangshui.tj.server.task;

import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.service.AreaDataManager;
import com.xiangshui.tj.server.service.BookingDataManager;
import com.xiangshui.tj.server.service.CapsuleDataManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Iterator;

abstract public class Task<R> {


    @Autowired
    AreaDataManager areaDataManager;
    @Autowired
    CapsuleDataManager capsuleDataManager;
    @Autowired
    BookingDataManager bookingDataManager;


    protected boolean isToday(int ts) {
        Date date = new Date();
        date.setSeconds(0);
        date.setMinutes(0);
        date.setHours(0);
        return date.getTime() / 1000 <= ts && ts < (date.getTime() / 1000) - (60 * 60 * 24);
    }

    public R tongji() {
        R r = createResult();
        handDataManager(areaDataManager, r);
        handDataManager(capsuleDataManager, r);
        handDataManager(bookingDataManager, r);

        if (reduce_for_area()) {
            for (Iterator<Area> it = areaDataManager.getMap().values().iterator(); it.hasNext(); ) {
                Area area = it.next();
                reduce(area, r);
            }

        }

        if (reduce_for_capsule()) {
            for (Iterator<Capsule> it = capsuleDataManager.getMap().values().iterator(); it.hasNext(); ) {
                Capsule capsule = it.next();
                reduce(capsule, r);
            }
        }
        if (reduce_for_booking()) {
            for (Iterator<Booking> it = bookingDataManager.getMap().values().iterator(); it.hasNext(); ) {
                Booking booking = it.next();
                reduce(booking, r);
            }
        }
        return r;
    }

    abstract public R createResult();

    abstract public void handDataManager(AreaDataManager areaDataManager, R r);

    abstract public void handDataManager(CapsuleDataManager capsuleDataManager, R r);

    abstract public void handDataManager(BookingDataManager bookingDataManager, R r);

    abstract public void reduce(Booking booking, R r);

    abstract public void reduce(Capsule capsule, R r);

    abstract public void reduce(Area area, R r);

    public boolean reduce_for_area() {
        return false;
    }

    public boolean reduce_for_capsule() {
        return false;
    }

    public boolean reduce_for_booking() {
        return false;
    }
}
