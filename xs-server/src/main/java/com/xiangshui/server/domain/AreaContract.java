package com.xiangshui.server.domain;

import com.xiangshui.server.domain.fragment.RangeRatio;

import java.util.List;

public class AreaContract {

    private Integer area_id;
    private String saler;
    private String saler_city;
    private String customer;
    private String customer_email;
    private String customer_contact;
    private Integer account_ratio;
    private List<RangeRatio> range_ratio_list;
    private String bank_account_name;
    private String bank_account;
    private String bank_branch;
    private String remark;

    private Long create_time;
    private Long update_time;
    private Integer status;

    public List<RangeRatio> getRange_ratio_list() {
        return range_ratio_list;
    }

    public AreaContract setRange_ratio_list(List<RangeRatio> range_ratio_list) {
        this.range_ratio_list = range_ratio_list;
        return this;
    }

    public String getBank_account_name() {
        return bank_account_name;
    }

    public AreaContract setBank_account_name(String bank_account_name) {
        this.bank_account_name = bank_account_name;
        return this;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public AreaContract setUpdate_time(Long update_time) {
        this.update_time = update_time;
        return this;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public AreaContract setCreate_time(Long create_time) {
        this.create_time = create_time;
        return this;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public String getCustomer_contact() {
        return customer_contact;
    }

    public AreaContract setCustomer_contact(String customer_contact) {
        this.customer_contact = customer_contact;
        return this;
    }

    public AreaContract setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public AreaContract setStatus(Integer status) {
        this.status = status;
        return this;
    }


    public Integer getArea_id() {
        return area_id;
    }

    public AreaContract setArea_id(Integer area_id) {
        this.area_id = area_id;
        return this;
    }

    public String getSaler() {
        return saler;
    }

    public AreaContract setSaler(String saler) {
        this.saler = saler;
        return this;
    }

    public String getSaler_city() {
        return saler_city;
    }

    public AreaContract setSaler_city(String saler_city) {
        this.saler_city = saler_city;
        return this;
    }

    public String getCustomer() {
        return customer;
    }

    public AreaContract setCustomer(String customer) {
        this.customer = customer;
        return this;
    }

    public Integer getAccount_ratio() {
        return account_ratio;
    }

    public AreaContract setAccount_ratio(Integer account_ratio) {
        this.account_ratio = account_ratio;
        return this;
    }

    public String getBank_account() {
        return bank_account;
    }

    public AreaContract setBank_account(String bank_account) {
        this.bank_account = bank_account;
        return this;
    }

    public String getBank_branch() {
        return bank_branch;
    }

    public AreaContract setBank_branch(String bank_branch) {
        this.bank_branch = bank_branch;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public AreaContract setRemark(String remark) {
        this.remark = remark;
        return this;
    }
}
