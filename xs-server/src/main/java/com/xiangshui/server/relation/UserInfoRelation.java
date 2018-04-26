package com.xiangshui.server.relation;

import com.xiangshui.server.domain.UserFace;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.domain.UserRegister;
import com.xiangshui.server.domain.UserWallet;

public class UserInfoRelation extends UserInfo {

    private UserWallet userWalletObj;
    private UserRegister userRegisterObj;
    private UserFace userFaceObj;

    public UserWallet getUserWalletObj() {
        return userWalletObj;
    }

    public void setUserWalletObj(UserWallet userWalletObj) {
        this.userWalletObj = userWalletObj;
    }

    public UserRegister getUserRegisterObj() {
        return userRegisterObj;
    }

    public void setUserRegisterObj(UserRegister userRegisterObj) {
        this.userRegisterObj = userRegisterObj;
    }

    public UserFace getUserFaceObj() {
        return userFaceObj;
    }

    public void setUserFaceObj(UserFace userFaceObj) {
        this.userFaceObj = userFaceObj;
    }
}
