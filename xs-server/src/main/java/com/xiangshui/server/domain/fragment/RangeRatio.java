package com.xiangshui.server.domain.fragment;

public class RangeRatio {
    public Integer lte;
    public Integer gte;
    private Integer account_ratio;

    public Integer getLte() {
        return lte;
    }

    public RangeRatio setLte(Integer lte) {
        this.lte = lte;
        return this;
    }

    public Integer getGte() {
        return gte;
    }

    public RangeRatio setGte(Integer gte) {
        this.gte = gte;
        return this;
    }

    public Integer getAccount_ratio() {
        return account_ratio;
    }

    public RangeRatio setAccount_ratio(Integer account_ratio) {
        this.account_ratio = account_ratio;
        return this;
    }
}
