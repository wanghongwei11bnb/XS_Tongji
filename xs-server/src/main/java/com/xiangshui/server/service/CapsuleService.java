package com.xiangshui.server.service;

import com.xiangshui.server.dao.CapsuleDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CapsuleService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CapsuleDao capsuleDao;
    @Autowired
    AreaService areaService;





}
