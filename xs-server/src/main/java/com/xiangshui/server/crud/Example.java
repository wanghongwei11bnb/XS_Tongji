package com.xiangshui.server.crud;

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
        this.skip = skip;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public Example setLimit(int limit) {
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

