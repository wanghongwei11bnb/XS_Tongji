package com.xiangshui.server.crud;

import com.alibaba.fastjson.JSON;
import com.xiangshui.server.crud.assist.CollectCallback;
import com.xiangshui.server.crud.assist.CrudTemplateException;
import com.xiangshui.server.crud.assist.Example;
import com.xiangshui.server.crud.assist.Sql;
import com.xiangshui.server.exception.XiangShuiException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public abstract class CrudTemplate<T> {

    protected static final Logger log = LoggerFactory.getLogger(CrudTemplate.class);

    protected Class<T> tableClass;
    protected String tableName;

    protected String primaryFieldName;
    protected Field primaryField;
    protected String primaryColumnName;

    protected String secondPrimaryFieldName;
    protected Field secondPrimaryField;
    protected String secondPrimaryColumnName;

    protected Map<String, String> columnNameMap = new HashMap<>();
    protected Map<String, Field> fieldMap = new HashMap<>();

    protected ResultSetExtractor<T> resultSetExtractor;
    protected RowMapper<T> rowMapper;

    public CrudTemplate() {
        try {
            tableName = getFullTableName();
            tableClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
            if (StringUtils.isBlank(tableName)) {
                throw new CrudTemplateException("tableName 不能为空");
            }
            initPrimary();
            for (Field field : tableClass.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (fieldName.equals(primaryFieldName) || fieldName.equals(secondPrimaryFieldName)) {
                    continue;
                }
                String columnName = defineColumnName(fieldName, field);
                if (StringUtils.isBlank(columnName)) {
                    throw new CrudTemplateException("columnName 不能为空");
                }
                if (columnName.equals(primaryColumnName) || columnName.equals(secondPrimaryColumnName)) {
                    throw new CrudTemplateException("columnName 重复");
                }
                if (columnNameMap.containsValue(columnName)) {
                    throw new CrudTemplateException("columnName 重复");
                }
                fieldMap.put(fieldName, field);
                columnNameMap.put(fieldName, columnName);
            }
            rowMapper = (resultSet, i) -> {
                try {
                    return mapperResultSet(resultSet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            };

            resultSetExtractor = resultSet -> {
                if (resultSet.next()) {
                    return rowMapper.mapRow(resultSet, 1);
                } else {
                    return null;
                }
            };

        } catch (Exception e) {
            log.error("", e);
            throw new CrudTemplateException(e.getMessage());
        }
    }

    boolean isExistColumn(ResultSet resultSet, String columnName) {
        try {
            if (resultSet.findColumn(columnName) >= 1) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    T mapperResultSet(ResultSet resultSet) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, SQLException {
        T t = tableClass.getConstructor().newInstance();
        if (StringUtils.isNotBlank(primaryFieldName) && isExistColumn(resultSet, primaryColumnName)) {
            primaryField.set(t, resultSet.getObject(primaryColumnName));
        }
        if (StringUtils.isNotBlank(secondPrimaryFieldName) && isExistColumn(resultSet, secondPrimaryColumnName)) {
            secondPrimaryField.set(t, resultSet.getObject(secondPrimaryColumnName));
        }
        for (String fieldName : fieldMap.keySet()) {
            if (isExistColumn(resultSet, fieldName)) {
                fieldMap.get(fieldName).set(t, resultSet.getObject(columnNameMap.get(fieldName)));
            }
        }
        return t;
    }


    protected void initPrimary() throws NoSuchFieldException {
    }

    public abstract JdbcTemplate getJdbcTemplate();

    abstract protected String getTableName();

    protected String getFullTableName() {
        return getTableName();
    }

    protected String defineColumnName(String fieldName, Field field) {
        return fieldName;
    }


    private int insert(T record, String[] fields, boolean selective) throws NoSuchFieldException, IllegalAccessException {
        Sql columnsSql = new Sql();
        Sql valuesSql = new Sql();
        List paramList = new ArrayList();
        collectFields(record, fields, selective, new CollectCallback() {
            @Override
            public void run(String fieldName, Field field, String columnName, Object columnValue) {
                if (columnsSql.length() > 0) {
                    columnsSql.append(Sql.COMMA);
                }
                columnsSql.append(columnName);
                if (valuesSql.length() > 0) {
                    valuesSql.append(Sql.COMMA);
                }
                valuesSql.append(Sql.MARK);
                paramList.add(columnValue);
            }
        });
        if (columnsSql.length() == 0) {
            throw new XiangShuiException("没有要更新的列");
        }
        Sql sql = new Sql().insertInto(getFullTableName(), columnsSql.toString()).values(valuesSql.toString());
        log.debug(sql.toString());
        return getJdbcTemplate().update(sql.toString(), paramList.toArray());
    }

    public int insert(T record, String[] fields) throws NoSuchFieldException, IllegalAccessException {
        return insert(record, fields, false);
    }

    public int insertSelective(T record, String[] fields) throws NoSuchFieldException, IllegalAccessException {
        return insert(record, fields, true);
    }


    public List<T> selectByExample(Example example) {
        String sqlWhere = example.getCriteria().makeSql();
        List paramList = example.getCriteria().makeParamList();
        Sql sql = new Sql()
                .select(StringUtils.isNotBlank(example.getColumns()) ? example.getColumns() : Sql.STAR)
                .from(getFullTableName())
                .append(
                        StringUtils.isNotBlank(sqlWhere) ? new Sql().where(sqlWhere).toString() : null
                )
                .append(
                        StringUtils.isNotBlank(example.getOrderByClause()) ? new String[]{Sql.ORDER_BY, example.getOrderByClause()} : null
                )
                .limit(example.getSkip(), example.getLimit());
        log.debug(sql.toString());
        return getJdbcTemplate().query(sql.toString(), paramList.toArray(), rowMapper);
    }


    public int countByExample(Example example) {
        String sqlWhere = example.getCriteria().makeSql();
        List paramList = example.getCriteria().makeParamList();
        Sql sql = new Sql()
                .select(Sql.COUNT_STAR)
                .from(getFullTableName())
                .append(
                        StringUtils.isNotBlank(sqlWhere) ? new Sql().where(sqlWhere).toString() : null
                );
        log.debug(sql.toString());
        log.debug(JSON.toJSONString(paramList));
        return getJdbcTemplate().queryForObject(sql.toString(), paramList.toArray(), int.class);
    }


    protected void collectFields(T record, String[] fields, boolean selective, CollectCallback callback) throws IllegalAccessException {
        Set<String> fieldSet = new HashSet<>();
        if (fields != null && fields.length > 0) {
            Collections.addAll(fieldSet, fields);
        } else {
            if (StringUtils.isNotBlank(secondPrimaryFieldName)) {
                fieldSet.add(secondPrimaryFieldName);
            }
            if (StringUtils.isNotBlank(primaryFieldName)) {
                fieldSet.add(primaryFieldName);
            }
            fieldSet.addAll(columnNameMap.keySet());
        }
        for (String fieldName : fieldSet) {
            Field field = getFieldByFieldName(fieldName);
            String columnName = getColumnNameByFieldName(fieldName);
            Object columnValue = field.get(record);
            if (columnValue == null && selective) {
                continue;
            }
            callback.run(fieldName, field, columnName, columnValue);
        }
    }

    public Field getFieldByFieldName(String fieldName) {
        if (fieldName.equals(primaryFieldName)) {
            return primaryField;
        } else if (fieldName.equals(secondPrimaryFieldName)) {
            return secondPrimaryField;
        } else {
            return fieldMap.get(fieldName);
        }
    }


    public String getColumnNameByFieldName(String fieldName) {
        if (fieldName.equals(primaryFieldName)) {
            return primaryColumnName;
        } else if (fieldName.equals(secondPrimaryFieldName)) {
            return secondPrimaryColumnName;
        } else {
            return columnNameMap.get(fieldName);
        }
    }


}
