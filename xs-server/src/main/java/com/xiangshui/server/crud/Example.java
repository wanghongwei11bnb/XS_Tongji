package com.xiangshui.server.crud;



public class Example {
    private String orderByClause;
    private String columns;
    private int skip = 0;
    private int limit = 5000;
    private Conditions conditions = new Conditions();

    public Conditions getConditions() {
        return conditions;
    }

    public Example setConditions(Conditions conditions) {
        this.conditions = conditions;
        return this;
    }

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
            throw new CrudTemplateException("skip必须大于等于0");
        }
        this.skip = skip;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public Example setLimit(int limit) {
        if (limit < 1) {
            throw new CrudTemplateException("limit必须大于等于1");
        }
        this.limit = limit;
        return this;
    }


    public Example setPageParams(int pageNumber, int pageSize) {
        int skip = pageSize * (pageNumber - 1);
        int limit = pageSize;
        this.setSkip(skip);
        this.setLimit(limit);
        return this;
    }




}

