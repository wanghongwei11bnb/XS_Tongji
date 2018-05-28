package com.xiangshui.op.scheduled;

import com.xiangshui.op.bean.DeviceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Hashtable;
import java.util.Map;

@Component
public class DeviceStatusScheduled {
    public Map<Long, DeviceStatus> statusMap = new Hashtable<>();


}
