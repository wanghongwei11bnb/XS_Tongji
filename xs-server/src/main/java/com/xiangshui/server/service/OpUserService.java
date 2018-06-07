package com.xiangshui.server.service;

import com.xiangshui.server.dao.redis.OpPrefix;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.server.example.OpExample;
import com.xiangshui.server.mapper.OpMapper;
import com.xiangshui.util.MD5;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class OpUserService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    OpMapper opMapper;
    @Autowired
    RedisService redisService;

    public static final String password_pre = "11bnb_opsc";


    public boolean authOp(String username, String password) {
        Op op = getOpByUsername(username, null);
        return authOp(op, password);
    }

    public boolean authOp(Op op, String password) {
        if (op == null) {
            return false;
        }
        if (op.getPassword().equals(MD5.getMD5(password_pre + password).toLowerCase())) {
            return true;
        }
        return false;
    }

    public Op getOpByUsername(String username, String fields) {
        Op op = redisService.get(OpPrefix.cache, username, Op.class);
        if (op == null) {
            op = opMapper.selectByPrimaryKey(username, fields);
            if (op != null) {
                redisService.set(OpPrefix.cache, op.getUsername(), op);
            }
        }
        return op;
    }

    public void cleanCache(String username) {
        redisService.del(OpPrefix.cache, username);
        redisService.del(OpPrefix.auth_set, username);
        redisService.del(OpPrefix.city_set, username);
        redisService.del(OpPrefix.area_set, username);
    }

    public Set<String> getAuthSet(String username) {
        Set<String> authSet = redisService.get(OpPrefix.auth_set, username, HashSet.class);
        if (authSet == null) {
            authSet = new HashSet<String>();
            Op op = getOpByUsername(username, null);
            if (op != null && StringUtils.isNotBlank(op.getAuths())) {
                String[] authArray = op.getAuths().split(",");
                if (authArray != null && authArray.length > 0) {
                    for (String auth : authArray) {
                        if (StringUtils.isNotBlank(auth)) {
                            authSet.add(auth);
                        }
                    }
                }
            }
            redisService.set(OpPrefix.auth_set, username, authSet);
        }
        return authSet;
    }

    public Set<String> getCitySet(String username) {
        Set<String> citySet = redisService.get(OpPrefix.city_set, username, HashSet.class);
        if (citySet == null) {
            citySet = new HashSet<String>();
            Op op = getOpByUsername(username, null);
            if (op != null && StringUtils.isNotBlank(op.getCitys())) {
                String[] cityArray = op.getCitys().split(",");
                if (cityArray != null && cityArray.length > 0) {
                    for (String city : cityArray) {
                        if (StringUtils.isNotBlank(city)) {
                            citySet.add(city);
                        }
                    }
                }
            }
            redisService.set(OpPrefix.city_set, username, citySet);
        }
        return citySet;
    }

    public Set<Integer> getAreaSet(String username) {
        Set<Integer> areaSet = redisService.get(OpPrefix.area_set, username, HashSet.class);

        if (areaSet == null) {
            areaSet = new HashSet<Integer>();
            Op op = getOpByUsername(username, null);
            if (op != null && StringUtils.isNotBlank(op.getAreas())) {
                String[] areaArray = op.getAreas().split(",");
                if (areaArray != null && areaArray.length > 0) {
                    for (String area : areaArray) {
                        if (StringUtils.isNotBlank(area)) {
                            areaSet.add(Integer.valueOf(area));
                        }
                    }
                }
            }
            redisService.set(OpPrefix.area_set, username, areaSet);
        }
        return areaSet;
    }


    public boolean authArea(Op op, int area_id) {
        if (op == null) return false;
        String areas = op.getAreas();
        if (StringUtils.isBlank(areas)) {
            return false;
        }
        String[] areaArr = areas.split("[,|，]");
        for (String area : areaArr) {
            if (area_id == Integer.parseInt(area)) {
                return true;
            }
        }
        return false;
    }


    public boolean authCity(String username, String city) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(city)) {
            return false;
        }
        Set<String> citySet = getCitySet(username);
        if (citySet != null && citySet.contains(city)) {
            return true;
        } else {
            return false;
        }
    }

}
