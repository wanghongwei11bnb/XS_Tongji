package com.xiangshui.server.bean;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class UserSearch {
    private Integer uin;
    private String phone;
    private Date register_time_lte;
    private Date register_time_gte;


    private Integer rows;
    private Integer page;

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getUin() {
        return uin;
    }

    public void setUin(Integer uin) {
        this.uin = uin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getRegister_time_lte() {
        return register_time_lte;
    }

    public void setRegister_time_lte(Date register_time_lte) {
        this.register_time_lte = register_time_lte;
    }

    public Date getRegister_time_gte() {
        return register_time_gte;
    }

    public void setRegister_time_gte(Date register_time_gte) {
        this.register_time_gte = register_time_gte;
    }



}
