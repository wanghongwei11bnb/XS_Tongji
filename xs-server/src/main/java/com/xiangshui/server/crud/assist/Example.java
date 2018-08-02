package com.xiangshui.server.crud.assist;

import com.xiangshui.server.exception.XiangShuiException;

public class Example {
    private String orderByClause;
    private String columns;
    private int skip = 0;
    private int limit = 5000;
    private Criteria criteria = new Criteria();

    public String getOrderByClause() {
        return orderByClause;
    }

    public Example setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
        return this;
    }

    public String getColumns() {
        return columns;
    }

    public Example setColumns(String columns) {
        this.columns = columns;
        return this;
    }

    public int getSkip() {
        return skip;
    }

    public Example setSkip(int skip) {
        if (skip < 0) {
            throw new XiangShuiException("skip必须大于等于0");
        }
        this.skip = skip;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public Example setLimit(int limit) {
        if (limit < 1) {
            throw new XiangShuiException("limit必须大于等于1");
        }
        this.limit = limit;
        return this;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public Example setCriteria(Criteria criteria) {
        this.criteria = criteria;
        return this;
    }


}

