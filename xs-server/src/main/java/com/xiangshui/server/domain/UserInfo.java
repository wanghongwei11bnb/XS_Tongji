package com.xiangshui.server.domain;

//            uInteger32 uin         = 1;   // 用户ID
//            uInteger32 id_verified = 2;   // 0 表示未通过身份证实名认证，1 表示通过了
//            string phone       = 3;   // 手机号
//            string real_name   = 4;   // 真实姓名
//            Integer32  balance     = 5;   // 钱包余额
//            Integer32  deposit     = 6;   // 押金余额
//            string nick_name   = 7;   // 昵称
//            uInteger32 need_deposit = 8;  // 0 表示不需要交纳押金；非0，需要
//            string out_trade_no = 9;  // 如果没交纳过则没有这个字段


import java.util.List;

public class UserInfo {
    /**
     * 用户ID
     */
    private Integer uin;
    /**
     * 用户注册手机号
     */
    private String phone;
    /**
     * 用户昵称
     */
    private String nick_name;
    /**
     * 0 表示不需要交纳押金；非0，需要
     */
    private Integer need_deposit;
    /**
     * ?????? 实名认证失败次数
     */
    private Integer fail_count;
    private Long update_time;
    private Long create_time;
    private Integer create_date;
    private Integer appraise_flag;

    private List<String> fail_data;
    /**
     * 0 表示未通过身份证实名认证，1 表示通过了
     */
    private Integer id_verified;
    private Integer invited_by;
    /**
     * 标示是不是首次   ？？？？？（评价？？订单）
     */
    private Integer is_first;

    private Integer block;

    public Integer getBlock() {
        return block;
    }

    public UserInfo setBlock(Integer block) {
        this.block = block;
        return this;
    }

    public List<String> getFail_data() {
        return fail_data;
    }

    public UserInfo setFail_data(List<String> fail_data) {
        this.fail_data = fail_data;
        return this;
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

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public Integer getNeed_deposit() {
        return need_deposit;
    }

    public void setNeed_deposit(Integer need_deposit) {
        this.need_deposit = need_deposit;
    }

    public Integer getFail_count() {
        return fail_count;
    }

    public void setFail_count(Integer fail_count) {
        this.fail_count = fail_count;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Long update_time) {
        this.update_time = update_time;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }

    public Integer getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Integer create_date) {
        this.create_date = create_date;
    }

    public Integer getAppraise_flag() {
        return appraise_flag;
    }

    public void setAppraise_flag(Integer appraise_flag) {
        this.appraise_flag = appraise_flag;
    }

    public Integer getId_verified() {
        return id_verified;
    }

    public void setId_verified(Integer id_verified) {
        this.id_verified = id_verified;
    }

    public Integer getInvited_by() {
        return invited_by;
    }

    public void setInvited_by(Integer invited_by) {
        this.invited_by = invited_by;
    }

    public Integer getIs_first() {
        return is_first;
    }

    public void setIs_first(Integer is_first) {
        this.is_first = is_first;
    }
}
