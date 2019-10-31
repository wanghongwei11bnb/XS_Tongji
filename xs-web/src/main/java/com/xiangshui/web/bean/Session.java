package com.xiangshui.web.bean;

public class Session {
    Integer uin;
    String token;

    public Integer getUin() {
        return uin;
    }

    public Session setUin(Integer uin) {
        this.uin = uin;
        return this;
    }

    public String getToken() {
        return token;
    }

    public Session setToken(String token) {
        this.token = token;
        return this;
    }
}
