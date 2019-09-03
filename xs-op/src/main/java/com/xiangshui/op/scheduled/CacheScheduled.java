package com.xiangshui.op.scheduled;

import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.*;
import com.xiangshui.util.MapOptions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CacheScheduled implements InitializingBean {

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
    @Autowired
    UserInfoDao userInfoDao;

    public List<City> cityList;
    public List<Area> areaList;
    public List<AreaContract> areaContractList;
    public List<Capsule> capsuleList;
    public List<UserInfo> userInfoList;

    public MapOptions<String, City> cityMapOptions;
    public MapOptions<Integer, Area> areaMapOptions;
    public MapOptions<Integer, AreaContract> areaContractMapOptions;
    public MapOptions<Long, Capsule> capsuleMapOptions;
    public MapOptions<Integer, UserInfo> userInfoMapOptions;
    public Map<String, Integer> phoneUinMap;

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void task() {
        updateCache();
    }

    public void updateCache() {

        cityList = cityDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE));
        areaList = areaDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE));
        capsuleList = capsuleDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE));
        areaContractList = areaContractDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE));
        userInfoList = userInfoDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withAttributesToGet(new String[]{
                "uin",
                "phone",
        }));

        cityMapOptions = new MapOptions<String, City>(cityList) {
            @Override
            public String getPrimary(City city) {
                return city.getCity();
            }
        };

        areaMapOptions = new MapOptions<Integer, Area>(areaList) {
            @Override
            public Integer getPrimary(Area area) {
                return area.getArea_id();
            }
        };

        areaContractMapOptions = new MapOptions<Integer, AreaContract>(areaContractList) {
            @Override
            public Integer getPrimary(AreaContract areaContract) {
                return areaContract.getArea_id();
            }
        };
        capsuleMapOptions = new MapOptions<Long, Capsule>(capsuleList) {
            @Override
            public Long getPrimary(Capsule capsule) {
                return capsule.getCapsule_id();
            }
        };
        userInfoMapOptions = new MapOptions<Integer, UserInfo>(userInfoList) {
            @Override
            public Integer getPrimary(UserInfo userInfo) {
                return userInfo.getUin();
            }
        };
        phoneUinMap=new HashMap<>();
        for (UserInfo userInfo : userInfoList) {
            phoneUinMap.put(userInfo.getPhone(),userInfo.getUin());
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        updateCache();
    }
}
