package com.xiangshui.web.controller;

import com.xiangshui.server.dao.MonthCardRecodeDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.dao.UserWalletDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class MonthCardController extends BaseController {
    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    UserWalletDao userWalletDao;
    @Autowired
    MonthCardRecodeDao monthCardRecodeDao;






}
