package com.xiangshui.server.crud;

import com.xiangshui.server.Test;
import com.xiangshui.util.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class CrudTemplateUtils {


    private static String getJavaType(String sqlType) {
        switch (sqlType) {
            case "varchar":
            case "text":
            case "enum":
                return "String";
            case "int":
                return "Integer";
            case "datetime":
                return "Date";
            default:
                return null;
        }
    }

    public static void makePojo(JdbcTemplate jdbcTemplate, String tableName) {

        String sql = String.format("select * from information_schema.COLUMNS where table_name = '%s' order by ORDINAL_POSITION;", tableName);
        jdbcTemplate.query(sql, (Object[]) null, resultSet -> {

            StringBuilder stringBuilder = new StringBuilder().append("\n");
            resultSet.beforeFirst();
            while (resultSet.next()) {
                String COLUMN_NAME = resultSet.getString("COLUMN_NAME");
                String DATA_TYPE = resultSet.getString("DATA_TYPE");
                stringBuilder
                        .append("private ")
                        .append(getJavaType(DATA_TYPE) + " ")
                        .append(COLUMN_NAME)
                        .append(";")
                        .append("\n");
            }
            log.info(stringBuilder.toString());
        });
    }

    public static void main(String[] args) {
        SpringUtils.init();
        makePojo(SpringUtils.getBean(JdbcTemplate.class), "prize_quota");
    }
}
