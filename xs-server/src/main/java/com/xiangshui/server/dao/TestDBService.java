package com.xiangshui.server.dao;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.xiangshui.util.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TestDBService {


    private static final Logger log = LoggerFactory.getLogger(TestDBService.class);


    public static void main(String[] args) throws Exception {
        test();
    }

    public static void test() {

        SpringUtils.init();


//        log.debug(JSON.toJSONString(SpringUtils.getBean(UserInfoDBService.class).scan(new ScanSpec().withFilterExpression("#phone = :phone").withNameMap(new NameMap().with("#phone", "phone")).withValueMap(new ValueMap().withString(":phone", "11000000014")))));
//        log.debug(JSON.toJSONString(SpringUtils.getBean(UserInfoDBService.class).updateItem(new PrimaryKey("uin", 1601519872), new AttributeUpdate("nick_name").put("123123"))));


        log.debug(JSON.toJSONString(SpringUtils.getBean(UserFaceDao.class).scan(new ScanSpec().withFilterExpression("uin = :uin").withValueMap(new ValueMap().withInt(":uin", 1306166750)))));


    }
}
