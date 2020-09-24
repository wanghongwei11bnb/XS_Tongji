package com.xiangshui.server.cache;

import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.CapsuleDao;
import com.xiangshui.server.dao.CityDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.domain.City;
import com.xiangshui.util.MapOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BaseCache {
    @Autowired
    CityDao cityDao;
    @Autowired
    AreaDao areaDao;
    @Autowired
    CapsuleDao capsuleDao;


    public List<City> cityList = new ArrayList<>();
    public List<Area> areaList = new ArrayList<>();
    public List<Capsule> capsuleList = new ArrayList<>();

    public MapOptions<String, City> cityMapOptions;
    public MapOptions<Integer, Area> areaMapOptions;
    public MapOptions<Long, Capsule> capsuleMapOptions;

    public Map<Integer, Integer> countCapsuleForAreaMap = new HashMap<>();

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void refresh() {
        this.cityList = cityDao.scan();
        this.areaList = areaDao.scan();
        this.capsuleList = capsuleDao.scan();
        this.cityMapOptions = new MapOptions<String, City>(this.cityList) {
            @Override
            public String getPrimary(City city) {
                return city.getCity();
            }
        };
        this.areaMapOptions = new MapOptions<Integer, Area>(this.areaList) {
            @Override
            public Integer getPrimary(Area area) {
                return area.getArea_id();
            }
        };
        this.capsuleMapOptions = new MapOptions<Long, Capsule>(this.capsuleList) {
            @Override
            public Long getPrimary(Capsule capsule) {
                return capsule.getCapsule_id();
            }
        };
        this.countCapsuleForAreaMap = new HashMap<>();
        this.areaList.forEach(area -> {
            this.countCapsuleForAreaMap.put(area.getArea_id(), 0);
            this.capsuleList.forEach(capsule -> {
                if (area.getArea_id().equals(capsule.getArea_id())) {
                    this.countCapsuleForAreaMap.put(area.getArea_id(), this.countCapsuleForAreaMap.get(area.getArea_id() + 1));
                }
            });
        });
    }
}
