package com.xiangshui.tj.server.bean;

//            uint32 uin         = 1;   // 用户ID
//            uint32 id_verified = 2;   // 0 表示未通过身份证实名认证，1 表示通过了
//            string phone       = 3;   // 手机号
//            string real_name   = 4;   // 真实姓名
//            int32  balance     = 5;   // 钱包余额
//            int32  deposit     = 6;   // 押金余额
//            string nick_name   = 7;   // 昵称
//            uint32 need_deposit = 8;  // 0 表示不需要交纳押金；非0，需要
//            string out_trade_no = 9;  // 如果没交纳过则没有这个字段


public class UserTj {

    private int uin;
    private String phone;
    private String nick_name;

    public int getUin() {
        return uin;
    }

    public void setUin(int uin) {
        this.uin = uin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }
}
