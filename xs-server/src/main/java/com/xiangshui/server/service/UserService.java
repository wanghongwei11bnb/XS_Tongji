package com.xiangshui.server.service;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.xiangshui.server.bean.UserSearch;
import com.xiangshui.server.domain.UserFace;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.domain.UserRegister;
import com.xiangshui.server.domain.UserWallet;
import com.xiangshui.server.dao.UserFaceDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.dao.UserRegisterDao;
import com.xiangshui.server.dao.UserWalletDao;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.relation.UserInfoRelation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Autowired
    RedisService redisService;

    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    UserWalletDao userWalletDao;
    @Autowired
    UserRegisterDao userRegisterDao;
    @Autowired
    UserFaceDao userFaceDao;


    public UserInfo getUserInfoByUin(int uin) {
        return userInfoDao.getItem(new PrimaryKey("uin", uin));
    }

    public UserWallet getUserWalletByUin(int uin) {
        return userWalletDao.getItem(new PrimaryKey("uin", uin));
    }

    public UserRegister getUserRegisterByUin(int uin) {
        return userRegisterDao.getItem(new PrimaryKey("uin", uin));
    }

    public List<UserFace> getUserFaceListByUin(int uin) {
        return userFaceDao.scan(new ScanSpec().withFilterExpression("uin = :uin").withValueMap(new ValueMap().withInt(":uin", uin)));
    }


    public List<UserInfo> search(UserSearch userSearch) {
        List<UserInfo> userInfoList = new ArrayList<UserInfo>();
        if (userSearch == null) {
            return userInfoList;
        }
        if (userSearch.getUin() != null) {
            UserInfo userInfo = getUserInfoByUin(userSearch.getUin());
            if (userInfo != null) {
                userInfoList.add(userInfo);
            }
        } else if (StringUtils.isNotBlank(userSearch.getPhone())) {
            ScanSpec scanSpec = new ScanSpec().withFilterExpression("phone = :phone").withValueMap(new ValueMap().withString(":phone", userSearch.getPhone()));
            userInfoList = userInfoDao.scan(scanSpec);
        } else {
            ScanSpec scanSpec = new ScanSpec();
        }
        return userInfoList;
    }


    public List<UserInfoRelation> mapperRelation(List<UserInfo> userInfoList) {
        if (userInfoList == null) {
            return null;
        }
        List<UserInfoRelation> userInfoRelationList = new ArrayList<UserInfoRelation>();
        for (UserInfo userInfo : userInfoList) {
            UserInfoRelation userInfoRelation = new UserInfoRelation();
            BeanUtils.copyProperties(userInfo, userInfoRelation);
            userInfoRelation.setUserWalletObj(getUserWalletByUin(userInfo.getUin()));
            userInfoRelation.setUserRegisterObj(getUserRegisterByUin(userInfo.getUin()));
        }

        return userInfoRelationList;
    }

}
