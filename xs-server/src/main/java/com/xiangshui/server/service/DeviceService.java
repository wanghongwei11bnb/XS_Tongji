package com.xiangshui.server.service;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.server.dao.DeviceRelationDao;
import com.xiangshui.server.domain.DeviceRelation;
import com.xiangshui.server.exception.XiangShuiException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class DeviceService {

    @Value("${isdebug}")
    boolean debug;

    @Autowired
    DeviceRelationDao deviceRelationDao;


    public static JSONObject getDeviceStatus(String device_id) throws IOException {
        String url = String.format("http://52.80.56.139:8877/api/device/%s/status", device_id);
        String body = Jsoup.connect(url)
                .header("User-Uin", "100000")
                .execute().body();
        if (StringUtils.isBlank(body)) throw new XiangShuiException("获取设备状态失败");
        JSONObject resp = JSONObject.parseObject(body);
        log.info("DeviceService.checkStatus：device_id={},resp={}", device_id, resp.toJSONString());
        if (!new Integer(0).equals(resp.getInteger("ret")))
            throw new XiangShuiException(String.format("获取设备状态失败：ret=%s,err=%s",
                    String.valueOf(resp.get("ret")), String.valueOf(resp.get("err"))));
        return resp;
    }

    public static Integer getWifiStatus(String device_id) throws IOException {
        return getDeviceStatus(device_id).getInteger("wifi_flag");
    }


    /**
     * 向设备发送指令
     *
     * @param device_id
     * @param lock_seq
     * @throws IOException
     */
    public static void open_lock(String device_id, String lock_seq) throws IOException {
        String url = String.format("http://www.xiangshuispace.com/api/device/open_lock");
        String body = Jsoup.connect(url)
                .header("User-Uin", "100000").header("Content-Type", "application/json")
                .method(Connection.Method.POST)
                .requestBody(new JSONObject()
                        .fluentPut("device_id", device_id)
                        .fluentPut("lock_seq", lock_seq)
                        .toJSONString())
                .execute().body();
        if (StringUtils.isBlank(body)) throw new XiangShuiException("操控设备失败");
        JSONObject resp = JSONObject.parseObject(body);
        if (!new Integer(0).equals(resp.getInteger("ret")))
            throw new XiangShuiException(String.format("操控设备失败：ret=%s,err=%s",
                    String.valueOf(resp.get("ret")), String.valueOf(resp.get("err"))));

    }

    /**
     * 开灯
     */
    public static void openLamp(String device_id) throws IOException {
        open_lock(device_id, "1");
    }

    /**
     * 关灯
     */
    public static void closeLamp(String device_id) throws IOException {
        open_lock(device_id, "2");
    }


    /**
     * 判断锁是否关闭
     */
    public static boolean isLocked(String device_id) throws IOException {
        if (StringUtils.isBlank(device_id)) {
            throw new XiangShuiException("设备编号为空");
        }
        String body = Jsoup.connect(String.format("https://www.xiangshuispace.com/api/device/get_lock_info?d=%s&s=1", device_id))
                .header("User-Uin", "100000").execute().body();
        if (StringUtils.isBlank(body)) throw new XiangShuiException("判断锁状态失败");
        JSONObject resp = JSONObject.parseObject(body);
        return new Integer(0).equals(resp.getInteger("ret")) && new Integer(2).equals(resp.getInteger("status"));
    }


    /**
     * 判断舱内是否无人
     */
    public static boolean is_no_people() throws IOException {

        return false;
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
            Jsoup.connect("https://www.xiangshuispace.com" + "/api/device/close_device")
                    .header("User-Uin", "100000")
                    .method(Connection.Method.POST)
                    .requestBody(new JSONObject().fluentPut("device_id", device_id).fluentPut("device_seq", 16).toJSONString())
                    .execute();
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


    public static void main(String[] args) throws IOException {
        String device_id = "34374716323938360081786D";
        log.debug(getDeviceStatus(device_id).toJSONString());
        log.debug(getWifiStatus(device_id) + "");
        log.debug(isLocked(device_id) + "");
        closeLamp(device_id);
    }


}
