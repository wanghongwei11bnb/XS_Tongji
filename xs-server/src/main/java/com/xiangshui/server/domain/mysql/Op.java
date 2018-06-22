package com.xiangshui.server.domain.mysql;

public class Op {
    private String username;

    private String password;

    private String realname;

    private String areas;

    private String operateExtend;

    private String operateRemove;

    private String allOperates;

    private String roles;

    private String auths;

    private String citys;

    private String fullname;

    private String city;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname == null ? null : realname.trim();
    }

    public String getAreas() {
        return areas;
    }

    public void setAreas(String areas) {
        this.areas = areas == null ? null : areas.trim();
    }

    public String getOperateExtend() {
        return operateExtend;
    }

    public void setOperateExtend(String operateExtend) {
        this.operateExtend = operateExtend == null ? null : operateExtend.trim();
    }

    public String getOperateRemove() {
        return operateRemove;
    }

    public void setOperateRemove(String operateRemove) {
        this.operateRemove = operateRemove == null ? null : operateRemove.trim();
    }

    public String getAllOperates() {
        return allOperates;
    }

    public void setAllOperates(String allOperates) {
        this.allOperates = allOperates == null ? null : allOperates.trim();
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles == null ? null : roles.trim();
    }

    public String getAuths() {
        return auths;
    }

    public void setAuths(String auths) {
        this.auths = auths == null ? null : auths.trim();
    }

    public String getCitys() {
        return citys;
    }

    public void setCitys(String citys) {
        this.citys = citys == null ? null : citys.trim();
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname == null ? null : fullname.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }
}