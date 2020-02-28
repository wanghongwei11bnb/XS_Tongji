package com.xiangshui.web.bean;

import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.domain.UserRegister;
import com.xiangshui.server.domain.UserWallet;
import lombok.Data;

@Data
public class Session {
    Integer uin;
    String token;
    String phone;
    UserRegister userRegister;
    UserInfo userInfo;
    UserWallet userWallet;
}
