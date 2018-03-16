package com.xiangshui.tj.service;

import com.xiangshui.tj.bean.Booking;
import com.xiangshui.tj.bean.Capsule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataManager {
    private static final Logger log = LoggerFactory.getLogger(DataManager.class);

    private static Map<Long, Booking> bookingMap;

    private static Map<Long, Capsule> capsuleMap;




}
