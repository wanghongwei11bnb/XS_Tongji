package com.xiangshui.server.domain;

import java.util.List;

public class GroupInfo {

    private Long group_id;
    private Long create_time;
    private Integer group_amount;
    private Integer group_status;
    private Integer group_type;
    private Long update_time;
    private Integer group_master;
    private List<Integer> uin_list;
    private List<String> out_trade_no_list;


    public Long getGroup_id() {
        return group_id;
    }

    public GroupInfo setGroup_id(Long group_id) {
        this.group_id = group_id;
        return this;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public GroupInfo setCreate_time(Long create_time) {
        this.create_time = create_time;
        return this;
    }

    public Integer getGroup_amount() {
        return group_amount;
    }

    public GroupInfo setGroup_amount(Integer group_amount) {
        this.group_amount = group_amount;
        return this;
    }

    public Integer getGroup_status() {
        return group_status;
    }

    public GroupInfo setGroup_status(Integer group_status) {
        this.group_status = group_status;
        return this;
    }

    public Integer getGroup_type() {
        return group_type;
    }

    public GroupInfo setGroup_type(Integer group_type) {
        this.group_type = group_type;
        return this;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public GroupInfo setUpdate_time(Long update_time) {
        this.update_time = update_time;
        return this;
    }

    public Integer getGroup_master() {
        return group_master;
    }

    public GroupInfo setGroup_master(Integer group_master) {
        this.group_master = group_master;
        return this;
    }

    public List<Integer> getUin_list() {
        return uin_list;
    }

    public GroupInfo setUin_list(List<Integer> uin_list) {
        this.uin_list = uin_list;
        return this;
    }

    public List<String> getOut_trade_no_list() {
        return out_trade_no_list;
    }

    public GroupInfo setOut_trade_no_list(List<String> out_trade_no_list) {
        this.out_trade_no_list = out_trade_no_list;
        return this;
    }
}
