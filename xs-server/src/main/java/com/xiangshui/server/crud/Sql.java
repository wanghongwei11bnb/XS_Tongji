package com.xiangshui.server.crud;

import org.apache.commons.lang3.StringUtils;

public class Sql {
    private StringBuilder stringBuilder = new StringBuilder();
    static final String INSERT_INTO = "insert into";
    static final String DELETE = " delete ";
    static final String DELETE_FROM = "delete from";
    static final String UPDATE = "update";
    static final String SELECT = "select";
    static final String FROM = "from";
    static final String WHERE = "where";
    static final String SET = "set";
    static final String EQ = "=";
    static final String NE = "<>";
    static final String IN = "in";
    static final String NIN = "not in";
    static final String GT = ">";
    static final String GTE = ">=";
    static final String LT = "<";
    static final String LTE = "<=";
    static final String LIKE = "like";
    static final String NOT_LIKE = "not like";
    static final String BL = "(";
    static final String BR = ")";
    static final String COMMA = ",";
    static final String VALUES = "values";
    static final String MARK = "?";
    static final String BLANK = " ";
    static final String GROUP_BY = "group by";
    static final String ORDER_BY = "order by";
    static final String LIMIT = "limit";
    static final String BETWEEN = "between";
    static final String OR = "or";
    static final String AND = "and";
    static final String COUNT_STAR = "count(*)";
    static final String STAR = "*";

    public Sql append(String fragment) {
        if (StringUtils.isNotBlank(fragment)) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(BLANK);
            }
            stringBuilder.append(fragment);
        }
        return this;
    }

    public Sql append(String[] fragments) {
        if (fragments != null) {
            for (int i = 0; i < fragments.length; i++) {
                append(fragments[i]);
            }
        }
        return this;
    }


    public Sql insertInto(String tableName, String columns) {
        append(INSERT_INTO);
        append(tableName);
        append(BL);
        append(columns);
        append(BR);
        return this;
    }


    public Sql values(String values) {
        append(VALUES);
        append(BL);
        append(values);
        append(BR);
        return this;
    }


    public Sql deleteFrom(String tableName) {
        append(DELETE_FROM);
        append(tableName);
        return this;
    }


    public Sql where(String condition) {
        append(WHERE);
        append(condition);
        return this;
    }


    public Sql update(String tableName) {
        append(UPDATE);
        append(tableName);
        return this;
    }


    public Sql groupBy(String groupBy) {
        append(GROUP_BY);
        append(groupBy);
        return this;
    }

    public Sql orderBy(String orderBy) {
        append(ORDER_BY);
        append(orderBy);
        return this;
    }


    public Sql select(String columns) {
        append(SELECT);
        append(columns);
        return this;
    }


    public Sql limit(int limit) {
        append(LIMIT);
        append(String.valueOf(limit));
        return this;
    }

    public Sql limit(int skip, int limit) {
        append(LIMIT);
        append(String.valueOf(skip));
        append(COMMA);
        append(String.valueOf(limit));
        return this;
    }


    public Sql from(String tableName) {
        append(FROM);
        append(tableName);
        return this;
    }


    public Sql set(String updating) {
        append(SET);
        append(updating);
        return this;
    }

    public int length() {
        return stringBuilder.length();
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}

