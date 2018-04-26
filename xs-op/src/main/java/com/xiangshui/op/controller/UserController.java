package com.xiangshui.op.controller;

import com.xiangshui.server.bean.PaginationResult;
import com.xiangshui.server.bean.UserSearch;
import com.xiangshui.server.dao.UserFaceDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.dao.UserRegisterDao;
import com.xiangshui.server.dao.UserWalletDao;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.relation.UserInfoRelation;
import com.xiangshui.server.service.UserService;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController  extends BaseController{


    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    UserWalletDao userWalletDao;
    @Autowired
    UserRegisterDao userRegisterDao;
    @Autowired
    UserFaceDao userFaceDao;

    @Autowired
    UserService userService;

    @GetMapping("/user_manage")
    public String index() {
        return "user_manage";
    }

    @GetMapping("/api/user/search")
    @ResponseBody
    public PaginationResult search(UserSearch userSearch) {
        List<UserInfo> userInfoList = userService.search(userSearch);
        if (userInfoList == null) {
            return new PaginationResult(0, null);
        }
        List<UserInfoRelation> userInfoRelationList = userService.mapperRelation(userInfoList);
        return new PaginationResult(userInfoRelationList.size(), userInfoRelationList);
    }
}
