package com.xiangshui.tj.server.task;

import com.xiangshui.tj.server.bean.Area;
import com.xiangshui.tj.server.bean.Booking;
import com.xiangshui.tj.server.bean.Capsule;
import com.xiangshui.tj.server.redis.RedisService;
import com.xiangshui.tj.server.service.*;
import com.xiangshui.tj.websocket.message.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Iterator;

abstract public class AbstractTask<R> {


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

    abstract public void reduceBooking(Booking booking, R r);

    abstract public void reduceCapsule(Capsule capsule, R r);

    abstract public void reduceArea(Area area, R r);

    public abstract SendMessage toSendMessage(R result);
}
