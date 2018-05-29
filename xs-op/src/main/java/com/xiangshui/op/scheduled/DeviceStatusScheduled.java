package com.xiangshui.op.scheduled;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.bean.DeviceStatus;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.CapsuleDao;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.CapsuleService;
import com.xiangshui.server.service.DeviceService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class DeviceStatusScheduled {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${isdebug}")
    boolean debug;
    @Autowired
    AreaDao areaDao;
    @Autowired
    CapsuleDao capsuleDao;

    @Autowired
    AreaService areaService;
    @Autowired
    CapsuleService capsuleService;

    @Autowired
    DeviceService deviceService;

    public Map<Long, DeviceStatus> statusMap = new Hashtable<>();

    private BlockingQueue<Capsule> blockingQueue = new LinkedBlockingQueue<Capsule>(100000);


    @Scheduled(fixedDelay = 1000 * 60 * 20)
    public void put() {
        log.debug("［定时任务——获取硬件设备状态——入队］");
        List<Capsule> capsuleList = capsuleDao.scan(new ScanSpec().withAttributesToGet("capsule_id", "area_id", "device_id"));
        blockingQueue.addAll(capsuleList);
    }

    @Scheduled(fixedDelay = 1000)
    public void pop() {
        Capsule capsule = blockingQueue.poll();
        if (capsule == null || StringUtils.isBlank(capsule.getDevice_id())) {
            return;
        }
        log.debug("［定时任务——获取硬件设备状态］device_id:" + capsule.getDevice_id());
        try {
            JSONObject resp = areaService.deviceStatus(capsule.getDevice_id());
            DeviceStatus deviceStatus = new DeviceStatus();
            deviceStatus.setArea_id(capsule.getArea_id());
            deviceStatus.setCapsule_id(capsule.getCapsule_id());
            deviceStatus.setDevice_id(capsule.getDevice_id());
            deviceStatus.setUpdate_time(new Date());
            if (resp.getIntValue("ret") == 0) {
                deviceStatus.setStatus(resp.getIntValue("status"));
                deviceStatus.setWifi_flag(resp.getIntValue("wifi_flag"));
            } else {
                deviceStatus.setStatus_text(resp.getString("err"));
            }
            statusMap.put(capsule.getCapsule_id(), deviceStatus);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
