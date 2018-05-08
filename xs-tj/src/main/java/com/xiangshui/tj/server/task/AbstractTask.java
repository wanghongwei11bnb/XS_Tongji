package com.xiangshui.tj.server.task;

import com.xiangshui.tj.server.bean.AreaTj;
import com.xiangshui.tj.server.bean.BookingTj;
import com.xiangshui.tj.server.bean.CapsuleTj;
import com.xiangshui.tj.server.service.*;
import com.xiangshui.tj.websocket.message.SendMessage;
import com.xiangshui.util.CallBack;
import org.springframework.beans.factory.annotation.Autowired;

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

    abstract public void reduceBooking(BookingTj booking, R r);

    abstract public void reduceCapsule(CapsuleTj capsule, R r);

    abstract public void reduceArea(AreaTj area, R r);

    public abstract SendMessage toSendMessage(R result);


    protected boolean isOnline(AreaTj areaTj) {
        if (areaTj == null) {
            return false;
        } else if (areaTj.getStatus() == -1) {
            return false;
        } else if (areaTj.getTitle().indexOf("待运营") > -1 || areaTj.getTitle().indexOf("已下线") > -1) {
            return false;
        } else {
            return true;
        }
    }

    protected boolean isOnline(CapsuleTj capsuleTj) {
        return isOnline(capsuleTj, null);
    }

    protected boolean isOnline(CapsuleTj capsuleTj, CallBack<AreaTj> callBack) {
        if (capsuleTj == null) {
            return false;
        } else if (capsuleTj.getIs_downline() == 1) {
            return false;
        } else {
            AreaTj areaTj = areaDataManager.getById(capsuleTj.getArea_id());
            if (areaTj == null) {
                return false;
            } else if (!isOnline(areaTj)) {
                return false;
            } else {
                if (callBack != null) {
                    callBack.run(areaTj);
                }
                return true;
            }
        }
    }
}
