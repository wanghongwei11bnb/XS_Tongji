package com.xiangshui.server.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.domain.City;
import com.xiangshui.server.dao.CityDao;
import com.xiangshui.server.dao.redis.CityKeyPrefix;
import com.xiangshui.server.dao.redis.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CityService {

    @Autowired
    RedisService redisService;

    @Autowired
    CityDao cityDao;


    public List<City> getCityList() {
        String cache = redisService.get(CityKeyPrefix.cache, "list", String.class);
        if (StringUtils.isNotBlank(cache)) {
            JSONArray cacheJson = JSONArray.parseArray(cache);
            List<City> cityList = new ArrayList<City>();
            for (int i = 0; i < cacheJson.size(); i++) {
                cityList.add(cacheJson.getJSONObject(i).toJavaObject(City.class));
            }
            return cityList;
        } else {
            List<City> cityList = cityDao.scan(new ScanSpec());
            redisService.set(CityKeyPrefix.cache, "list", JSON.toJSONString(cityList));
            return cityList;
        }
    }

}
