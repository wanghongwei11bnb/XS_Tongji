package com.xiangshui.server.service;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.server.dao.DeviceRelationDao;
import com.xiangshui.server.domain.DeviceRelation;
import com.xiangshui.server.exception.XiangShuiException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DeviceService {

    @Value("${isdebug}")
    boolean debug;

    @Autowired
    DeviceRelationDao deviceRelationDao;

    /**
     * 判断锁是否关闭
     */
    public boolean isLocked(String device_id) throws IOException {
        if (StringUtils.isBlank(device_id)) {
            throw new XiangShuiException("设备编号为空");
        }
        if (debug) {
            return true;
        }
        String body = Jsoup.connect(
                "https://www.xiangshuispace.com"
                        + "/api/device/get_lock_info?"
                        + "d=" + device_id
                        + "&s=1")
                .header("User-Uin", "100000").execute().body();
        JSONObject resp = JSONObject.parseObject(body);
        if (resp.getInteger("ret") != null && resp.getInteger("ret") == 0) {
            if (resp.getInteger("status") != null && resp.getInteger("status") == 2) {
                return true;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 没有订单
     */
    public void no_order(String device_id) throws IOException {
        if (StringUtils.isBlank(device_id)) {
            throw new XiangShuiException("设备编号为空");
        }
        if (debug) {
            return;
        }
        try {
            Jsoup.connect(
                    "https://www.xiangshuispace.com"
                            + "/api/device/close_device?"
                            + "&device_id=" + device_id
                            + "&device_seq=16")
                    .header("User-Uin", "100000").method(Connection.Method.POST).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 订单与设备解绑
     */
    public void relieveBooking(String device_id) throws Exception {
        if (StringUtils.isBlank(device_id)) {
            throw new XiangShuiException("设备编号为空");
        }
        DeviceRelation deviceRelation = deviceRelationDao.getItem(new PrimaryKey("device_id", device_id));
        if (deviceRelation == null) {
            return;
        }
        deviceRelation.setBooking_id((long) 0);
        deviceRelationDao.updateItem(new PrimaryKey("device_id", device_id), deviceRelation, new String[]{
                "booking_id"
        });
    }


}
