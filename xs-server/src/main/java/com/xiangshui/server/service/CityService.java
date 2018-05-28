package com.xiangshui.server.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.City;
import com.xiangshui.server.dao.CityDao;
import com.xiangshui.server.dao.redis.CityKeyPrefix;
import com.xiangshui.server.dao.redis.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CityService {

    @Autowired
    RedisService redisService;

    @Autowired
    CityDao cityDao;

    @Autowired
    AreaDao areaDao;

    public List<City> getCityList() {
        String cache = redisService.get(CityKeyPrefix.list_all, String.class);
        if (StringUtils.isNotBlank(cache)) {
            JSONArray cacheJson = JSONArray.parseArray(cache);
            List<City> cityList = new ArrayList<City>();
            for (int i = 0; i < cacheJson.size(); i++) {
                cityList.add(cacheJson.getJSONObject(i).toJavaObject(City.class));
            }
            return cityList;
        } else {
            List<City> cityList = cityDao.scan(new ScanSpec());
            cityList.sort(new Comparator<City>() {
                public int compare(City o1, City o2) {
                    return o1.getCity().compareTo(o2.getCity());
                }
            });
            redisService.set(CityKeyPrefix.list_all, JSON.toJSONString(cityList));
            return cityList;
        }
    }

    public List<City> getActiveCityList() {
        String cache = redisService.get(CityKeyPrefix.list_active, String.class);
        if (StringUtils.isNotBlank(cache)) {
            JSONArray cacheJson = JSONArray.parseArray(cache);
            List<City> cityList = new ArrayList<City>();
            for (int i = 0; i < cacheJson.size(); i++) {
                cityList.add(cacheJson.getJSONObject(i).toJavaObject(City.class));
            }
            return cityList;
        } else {
            Set<String> citySet = new TreeSet<String>();
            List<Area> areaList = areaDao.scan(new ScanSpec().withAttributesToGet("city"));
            for (Area area : areaList) {
                citySet.add(area.getCity());
            }
            List<City> cityList = cityDao.batchGetItem("city", citySet.toArray());
            cityList.sort(new Comparator<City>() {
                public int compare(City o1, City o2) {
                    return o1.getCity().compareTo(o2.getCity());
                }
            });
            redisService.set(CityKeyPrefix.list_active, JSON.toJSONString(cityList));
            return cityList;
        }
    }

    public City getByCityName(String cityName) {
        if (StringUtils.isBlank(cityName)) {
            return null;
        }
        return cityDao.getItem(new PrimaryKey("city", cityName));
    }


}
