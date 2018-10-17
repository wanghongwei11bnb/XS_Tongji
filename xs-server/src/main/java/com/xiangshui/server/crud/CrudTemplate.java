package com.xiangshui.server.crud;

import com.alibaba.fastjson.JSON;
import com.xiangshui.server.crud.assist.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.*;


public abstract class CrudTemplate<T> {

    protected static final Logger log = LoggerFactory.getLogger(CrudTemplate.class);

    protected Class<T> tableClass;
    protected String tableName;

    protected boolean primary;
    protected boolean primaryAutoIncr;
    protected String primaryFieldName;
    protected Field primaryField;
    protected String primaryColumnName;

    protected boolean secondPrimary;
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
            tableClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            if (StringUtils.isBlank(tableName)) {
                throw new CrudTemplateException("tableName 不能为空");
            }
            initPrimary();
            initSecondPrimary();
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
                if (columnName.equals(primaryColumnName) || columnName.equals(secondPrimaryColumnName) || columnNameMap.containsValue(columnName)) {
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
        if (primary && isExistColumn(resultSet, primaryColumnName)) {
            primaryField.set(t, resultSet.getObject(primaryColumnName));
        }
        if (secondPrimary && isExistColumn(resultSet, secondPrimaryColumnName)) {
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

    protected void initSecondPrimary() throws NoSuchFieldException {
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
            throw new CrudTemplateException("没有要写入的字段");
        }
        Sql sql = new Sql().insertInto(getFullTableName(), columnsSql.toString()).values(valuesSql.toString());
        log.debug(sql.toString());

        if (primary && primaryAutoIncr && primaryField.get(record) == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int result = getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
                    if (paramList != null) {
                        for (int i = 0; i < paramList.size(); i++) {
                            preparedStatement.setObject(i + 1, paramList.get(i));
                        }
                    }
                    return preparedStatement;
                }
            }, keyHolder);
            if (primaryField.getType() == byte.class || primaryField.getType() == Byte.class) {
                primaryField.set(record, keyHolder.getKey().byteValue());
            } else if (primaryField.getType() == short.class || primaryField.getType() == Short.class) {
                primaryField.set(record, keyHolder.getKey().shortValue());
            } else if (primaryField.getType() == int.class || primaryField.getType() == Integer.class) {
                primaryField.set(record, keyHolder.getKey().intValue());
            } else if (primaryField.getType() == long.class || primaryField.getType() == Long.class) {
                primaryField.set(record, keyHolder.getKey().longValue());
            } else if (primaryField.getType() == float.class || primaryField.getType() == Float.class) {
                primaryField.set(record, keyHolder.getKey().floatValue());
            } else if (primaryField.getType() == double.class || primaryField.getType() == Double.class) {
                primaryField.set(record, keyHolder.getKey().doubleValue());
            }
            return result;
        } else {
            return getJdbcTemplate().update(sql.toString(), paramList.toArray());
        }
    }


    public int insert(T record, String[] fields) throws NoSuchFieldException, IllegalAccessException {
        return insert(record, fields, false);
    }

    public int insertSelective(T record, String[] fields) throws NoSuchFieldException, IllegalAccessException {
        return insert(record, fields, true);
    }


    public List<T> selectByExample(Example example) {
        if (example == null) example = new Example();
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
        if (example == null) example = new Example();
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
            if (primary) {
                fieldSet.add(primaryFieldName);
            }
            if (secondPrimary) {
                fieldSet.add(secondPrimaryFieldName);
            }
            fieldSet.addAll(fieldMap.keySet());
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

    public List<Criterion> makeCriterionList(T record, String[] fields, boolean selective) throws IllegalAccessException {
        List<Criterion> criterionList = new ArrayList<>();
        collectFields(record, fields, selective, new CollectCallback() {
            @Override
            public void run(String fieldName, Field field, String columnName, Object columnValue) {
                if (columnValue == null) {
                    criterionList.add(Criterion.is_null(columnName));
                } else {
                    criterionList.add(Criterion.eq(columnName, columnValue));
                }

            }
        });
        return criterionList;
    }


}
