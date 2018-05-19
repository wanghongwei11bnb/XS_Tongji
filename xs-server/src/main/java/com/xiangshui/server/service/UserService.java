package com.xiangshui.server.service;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.xiangshui.server.bean.UserSearch;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.dao.UserFaceDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.dao.UserRegisterDao;
import com.xiangshui.server.dao.UserWalletDao;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.relation.BookingRelation;
import com.xiangshui.server.relation.UserInfoRelation;
import com.xiangshui.util.CallBackForResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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


    public UserInfo getUserInfoByPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return null;
        }
        List<UserInfo> userInfoList = userInfoDao.scan(new ScanSpec().withScanFilters(new ScanFilter("phone").eq(phone)));
        if (userInfoList != null && userInfoList.size() > 0) {
            return userInfoList.get(0);
        } else {
            return null;
        }

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
            userInfoRelation.set_userWallet(getUserWalletByUin(userInfo.getUin()));
            userInfoRelation.set_userRegister(getUserRegisterByUin(userInfo.getUin()));
        }

        return userInfoRelationList;
    }


    public List<UserInfo> getUserInfoListByUins(Integer[] uins) {
        if (uins == null || uins.length == 0) {
            return null;
        }
        return userInfoDao.batchGetItem("uin", uins);
    }


    public Map<Integer, UserInfo> getUserInfoMapByUins(Integer[] uins) {
        if (uins == null || uins.length == 0) {
            return null;
        }
        List<UserInfo> userInfoList = userInfoDao.batchGetItem("uin", uins);
        if (userInfoList == null || userInfoList.size() == 0) {
            return null;
        }
        Map<Integer, UserInfo> userInfoMap = new HashMap<Integer, UserInfo>(userInfoList.size());
        for (UserInfo userInfo : userInfoList) {
            userInfoMap.put(userInfo.getUin(), userInfo);
        }
        return userInfoMap;
    }

    public void matchUserInfoForBooking(BookingRelation bookingRelation) {
        if (bookingRelation == null || bookingRelation.getUin() == null) return;
        bookingRelation.set_userInfo(getUserInfoByUin(bookingRelation.getUin()));
    }


    public void matchUserInfoForBooking(List<BookingRelation> bookingRelationList) {
        if (bookingRelationList == null || bookingRelationList.size() == 0) return;

        Set<Integer> uinSet = new HashSet<Integer>();
        for (BookingRelation bookingRelation : bookingRelationList) {
            if (bookingRelation.getUin() != null) {
                uinSet.add(bookingRelation.getUin());
            }
        }
        Map<Integer, UserInfo> userInfoMap = getUserInfoMapByUins(uinSet.toArray(new Integer[0]));
        for (BookingRelation bookingRelation : bookingRelationList) {
            if (userInfoMap.containsKey(bookingRelation.getUin())) {
                bookingRelation.set_userInfo(userInfoMap.get(bookingRelation.getUin()));
            }
        }
    }


    public UserInfoRelation toRelation(UserInfo userInfo) {
        if (userInfo == null) return null;
        UserInfoRelation userInfoRelation = new UserInfoRelation();
        BeanUtils.copyProperties(userInfo, userInfoRelation);
        return userInfoRelation;
    }

    public List<UserInfoRelation> toRelation(List<UserInfo> userInfoList) {
        if (userInfoList == null) return null;
        List<UserInfoRelation> userInfoRelationList = new ArrayList<UserInfoRelation>(userInfoList.size());
        for (UserInfo userInfo : userInfoList) {
            UserInfoRelation userInfoRelation = toRelation(userInfo);
            userInfoRelationList.add(userInfoRelation);
        }
        return userInfoRelationList;
    }


    public List<UserInfo> getUserInfoList(Integer[] uins, String[] attributes) {
        return userInfoDao.batchGetItem("uin", uins, attributes);
    }

    public Set<Integer> getUinSet(List<Booking> bookingList) {
        if (bookingList == null) return null;
        Set<Integer> uinSet = new HashSet<Integer>();
        for (Booking booking : bookingList) {
            if (booking != null && booking.getUin() != null) {
                uinSet.add(booking.getUin());
            }
        }
        return uinSet;
    }

    public List<UserInfo> getUserInfoList(List<Booking> bookingList, final String[] attributes) {
        if (bookingList == null) return null;
        final Set<Integer> uinSet = getUinSet(bookingList);
        if (uinSet == null || uinSet.size() == 0) return null;
        return ServiceUtils.division(uinSet.toArray(new Integer[0]), 100, new CallBackForResult<Integer[], List<UserInfo>>() {
            public List<UserInfo> run(Integer[] object) {
                return getUserInfoList(object, attributes);
            }
        }, new Integer[0]);
    }

}
