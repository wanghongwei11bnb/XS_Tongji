package com.xiangshui.server.relation;

import com.xiangshui.server.domain.UserFace;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.domain.UserRegister;
import com.xiangshui.server.domain.UserWallet;

public class UserInfoRelation extends UserInfo {

    private UserWallet _userWallet;
    private UserRegister _userRegister;

    private UserFace _userFace;

    public UserWallet get_userWallet() {
        return _userWallet;
    }

    public void set_userWallet(UserWallet _userWallet) {
        this._userWallet = _userWallet;
    }

    public UserRegister get_userRegister() {
        return _userRegister;
    }

    public void set_userRegister(UserRegister _userRegister) {
        this._userRegister = _userRegister;
    }

    public UserFace get_userFace() {
        return _userFace;
    }

    public void set_userFace(UserFace _userFace) {
        this._userFace = _userFace;
    }
}
