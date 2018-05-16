package com.xiangshui.server;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.CapsuleDao;
import com.xiangshui.server.dao.CityDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.domain.City;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.service.PartnerService;
import com.xiangshui.util.CallBack;
import com.xiangshui.util.MD5;
import com.xiangshui.util.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Test {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    AreaDao areaDao;

    @Autowired
    CapsuleDao capsuleDao;

    @Autowired
    PartnerService partnerService;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    CityDao cityDao;


    public void testSelect() throws Exception {
        List<UserInfo> userInfoList = userInfoDao.scan(new ScanSpec().withFilterExpression("phone = :phone").withValueMap(new ValueMap().withString(":phone", "11000000014")));
        log.debug(JSON.toJSONString(userInfoList));

        List<Area> areaList = areaDao.scan(new ScanSpec());
        log.debug(JSON.toJSONString(areaList));
    }

    public void test2() throws Exception {

        boolean success = userInfoDao.updateItem(new PrimaryKey("uin", 1601519873), new AttributeUpdate("nick_name").put("sfsfsdttttttfs"));
//        log.debug(success+"");
        UserInfo userInfo = userInfoDao.getItem(new PrimaryKey("uin", 1601519873));
        log.debug(JSON.toJSONString(userInfo));


    }

    public void test() throws Exception {

        List<Capsule> capsuleList = capsuleDao.batchGetItem("capsule_id", new Object[]{1100002005});

        log.debug(JSON.toJSONString(capsuleList));
    }


    public static void main(String[] args) throws Exception {


        SpringUtils.init();
        SpringUtils.getBean(Test.class).test();

    }
}
