package com.xiangshui.tj.task;

import com.xiangshui.tj.bean.Area;
import com.xiangshui.tj.bean.Booking;
import com.xiangshui.tj.bean.Capsule;

public interface Task {
    void reduce(int event, Booking booking);

    void reduce(int event, Capsule capsule);

    void reduce(int event, Area area);
}
