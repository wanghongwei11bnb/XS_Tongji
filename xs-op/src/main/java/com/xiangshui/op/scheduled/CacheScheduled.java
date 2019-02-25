package com.xiangshui.op.scheduled;

import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.AreaContract;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.domain.City;
import com.xiangshui.util.MapOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheScheduled {

    @Autowired
    CityDao cityDao;

    @Autowired
    AreaDao areaDao;
    @Autowired
    CapsuleDao capsuleDao;
    @Autowired
    BookingDao bookingDao;
    @Autowired
    AreaContractDao areaContractDao;


    public MapOptions<String, City> cityMapOptions;
    public MapOptions<Integer, Area> areaMapOptions;
    public MapOptions<Integer, AreaContract> areaContractMapOptions;
    public MapOptions<Long, Capsule> capsuleMapOptions;

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void task() {
        updateCache();
    }

    public void updateCache() {

        cityMapOptions = new MapOptions<String, City>(cityDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE))) {
            @Override
            public String getPrimary(City city) {
                return city.getCity();
            }
        };

        areaMapOptions = new MapOptions<Integer, Area>(areaDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE))) {
            @Override
            public Integer getPrimary(Area area) {
                return area.getArea_id();
            }
        };

        areaContractMapOptions = new MapOptions<Integer, AreaContract>(areaContractDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE))) {
            @Override
            public Integer getPrimary(AreaContract areaContract) {
                return areaContract.getArea_id();
            }
        };
        capsuleMapOptions = new MapOptions<Long, Capsule>(capsuleDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE))) {
            @Override
            public Long getPrimary(Capsule capsule) {
                return capsule.getCapsule_id();
            }
        };
    }


}
