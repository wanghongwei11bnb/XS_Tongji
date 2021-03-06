package com.xiangshui.server.domain;

public class UserWallet {

    private Integer uin;

    /**
     * 钱包余额
     */
    private Integer balance;

    /**
     * 赠送
     */
    private Integer bonus;

    /**
     * 实际支付
     */
    private Integer charge;

    /**
     * 押金
     */
    private Integer deposit;

    private String openID;

    private Long update_time;

    private Integer month_card_flag;

    public Integer getMonth_card_flag() {
        return month_card_flag;
    }

    public UserWallet setMonth_card_flag(Integer month_card_flag) {
        this.month_card_flag = month_card_flag;
        return this;
    }

    public Integer getUin() {
        return uin;
    }

    public void setUin(Integer uin) {
        this.uin = uin;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getBonus() {
        return bonus;
    }

    public void setBonus(Integer bonus) {
        this.bonus = bonus;
    }

    public Integer getCharge() {
        return charge;
    }

    public void setCharge(Integer charge) {
        this.charge = charge;
    }

    public Integer getDeposit() {
        return deposit;
    }

    public void setDeposit(Integer deposit) {
        this.deposit = deposit;
    }

    public String getOpenID() {
        return openID;
    }

    public void setOpenID(String openID) {
        this.openID = openID;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Long update_time) {
        this.update_time = update_time;
    }
}
