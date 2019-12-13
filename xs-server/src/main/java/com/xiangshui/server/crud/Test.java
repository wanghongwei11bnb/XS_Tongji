package com.xiangshui.server.crud;

import org.springframework.jdbc.core.JdbcTemplate;

public class Test extends CrudTemplate<Object> implements PrimaryAutoIncr{


    @Override
    public JdbcTemplate getJdbcTemplate() {
        return null;
    }

    @Override
    protected String getTableName() {
        return "user";
    }

    public static void main(String[] args) {
    new Test();
    }
}
