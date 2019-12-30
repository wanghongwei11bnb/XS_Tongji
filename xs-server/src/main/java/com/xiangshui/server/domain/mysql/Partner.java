package com.xiangshui.server.domain.mysql;

import lombok.Data;

import java.util.Date;
@Data
public class Partner {
    private Integer id;

    private String phone;

    private String city;

    private String email;

    private String passwd;

    private String remark;

    private String address;

    private String theme;

    private Date createTime;

    private Date updateTime;

    private Date lastLoginTime;

}