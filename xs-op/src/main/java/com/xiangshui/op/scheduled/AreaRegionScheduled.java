package com.xiangshui.op.scheduled;

import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.City;
import com.xiangshui.server.service.*;
import com.xiangshui.util.CallBack;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AreaRegionScheduled implements InitializingBean {


    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Autowired
    AreaService areaService;

    @Autowired
    AreaBillDao areaBillDao;
    @Autowired
    AreaContractDao areaContractDao;
    @Autowired
    AreaContractService areaContractService;

    @Autowired
    BookingService bookingService;
    @Autowired
    BookingDao bookingDao;

    @Autowired
    UserService userService;

    @Autowired
    GroupInfoDao groupInfoDao;

    @Autowired
    ChargeRecordDao chargeRecordDao;
    @Autowired
    CapsuleDao capsuleDao;
    @Autowired
    CityService cityService;
    @Autowired
    AreaDao areaDao;

    public Map<Integer, String> areaRegionMap = new HashMap<>();

    //    @Scheduled(fixedDelay = 1000 * 60 * 60 * 1)
    public void update() {
        Map<Integer, String> areaRegionMap = new HashMap<>();
        List<City> cityList = cityService.getCityList();
        areaDao.scan(null, area -> {
            if (area == null || StringUtils.isBlank(area.getCity())) {
                return;
            }
            for (City city : cityList) {
                if (city == null || !area.getCity().equals(city.getCity())) {
                    continue;
                }
                areaRegionMap.put(area.getArea_id(), city.getRegion());
                break;
            }
        });
        this.areaRegionMap = areaRegionMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        update();
    }
}
