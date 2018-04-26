package com.xiangshui.server.domain;

public class UserRegister {

    private Integer uin;
    private String nick_name;

    private String phone;
    private String register_from;
    private Long login_last_time;
    private String last_access_token;
    private String igetui_cltid;

    private Long update_time;

    public Integer getUin() {
        return uin;
    }

    public void setUin(Integer uin) {
        this.uin = uin;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegister_from() {
        return register_from;
    }

    public void setRegister_from(String register_from) {
        this.register_from = register_from;
    }

    public Long getLogin_last_time() {
        return login_last_time;
    }

    public void setLogin_last_time(Long login_last_time) {
        this.login_last_time = login_last_time;
    }

    public String getLast_access_token() {
        return last_access_token;
    }

    public void setLast_access_token(String last_access_token) {
        this.last_access_token = last_access_token;
    }

    public String getIgetui_cltid() {
        return igetui_cltid;
    }

    public void setIgetui_cltid(String igetui_cltid) {
        this.igetui_cltid = igetui_cltid;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Long update_time) {
        this.update_time = update_time;
    }
}
