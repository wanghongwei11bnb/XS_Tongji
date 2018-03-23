package com.xiangshui.tj.server.task;

import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.redis.RedisService;
import com.xiangshui.tj.server.service.AreaDataManager;
import com.xiangshui.tj.server.service.BookingDataManager;
import com.xiangshui.tj.server.service.CapsuleDataManager;
import com.xiangshui.tj.websocket.message.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Iterator;

abstract public class Task<R> {


    public TaskEntry<R> createTaskEntry() {
        TaskEntry<R> taskEntry = new TaskEntry<R>();
        R result = createResult();
        taskEntry.setTask(this);
        taskEntry.setResult(result);
        return taskEntry;
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

    public abstract SendMessage toSendMessage(R result);
}
