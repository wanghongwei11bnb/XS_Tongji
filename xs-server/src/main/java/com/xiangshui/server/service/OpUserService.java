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
}
