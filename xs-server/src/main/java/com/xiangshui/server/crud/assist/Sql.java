package com.xiangshui.server.crud.assist;

import org.apache.commons.lang3.StringUtils;

public class Sql {
    private StringBuilder stringBuilder = new StringBuilder();
    public static final String INSERT_INTO = "insert into";
    public static final String DELETE = " delete ";
    public static final String DELETE_FROM = "delete from";
    public static final String UPDATE = "update";
    public static final String SELECT = "select";
    public static final String FROM = "from";
    public static final String WHERE = "where";
    public static final String SET = "set";
    public static final String EQ = "=";
    public static final String NE = "<>";
    public static final String IN = "in";
    public static final String NIN = "not in";
    public static final String GT = ">";
    public static final String GTE = ">=";
    public static final String LT = "<";
    public static final String LTE = "<=";
    public static final String LIKE = "like";
    public static final String NOT_LIKE = "not like";
    public static final String BL = "(";
    public static final String BR = ")";
    public static final String COMMA = ",";
    public static final String VALUES = "values";
    public static final String MARK = "?";
    public static final String BLANK = " ";
    public static final String GROUP_BY = "group by";
    public static final String ORDER_BY = "order by";
    public static final String LIMIT = "limit";
    public static final String BETWEEN = "between";
    public static final String OR = "or";
    public static final String AND = "and";
    public static final String COUNT_STAR = "count(*)";
    public static final String STAR = "*";

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

