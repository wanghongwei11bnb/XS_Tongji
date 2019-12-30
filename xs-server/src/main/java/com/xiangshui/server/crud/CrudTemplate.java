package com.xiangshui.server.crud;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Slf4j
public abstract class CrudTemplate<T> {

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
            tableClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            tableName = getFullTableName();
            if (StringUtils.isBlank(tableName)) {
                throw new CrudTemplateException("tableName 不能为空");
            }
            if (this instanceof SinglePrimaryCrudTemplate) {
                SinglePrimaryCrudTemplate singlePrimaryCrudTemplate = (SinglePrimaryCrudTemplate) this;
                singlePrimaryCrudTemplate.initPrimary();
            }
            if (this instanceof DoublePrimaryCrudTemplate) {
                DoublePrimaryCrudTemplate doublePrimaryCrudTemplate = (DoublePrimaryCrudTemplate) this;
                doublePrimaryCrudTemplate.initPrimary();
                doublePrimaryCrudTemplate.initSecondPrimary();
            }
            if (this instanceof PrimaryAutoIncr) {
                primaryAutoIncr = true;
            }
            for (Field field : tableClass.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (fieldName.equals(primaryFieldName) || fieldName.equals(secondPrimaryFieldName)) {
                    continue;
                }
                String columnName = defineColumnName(fieldName, field);
                if (StringUtils.isBlank(columnName)) {
                    throw new CrudTemplateException(fieldName + " 对应的 columnName 不能为空");
                }
                if (columnName.equals(primaryColumnName) || columnName.equals(secondPrimaryColumnName) || columnNameMap.containsValue(columnName)) {
                    throw new CrudTemplateException(fieldName + " 对应的 columnName 重复");
                }
                fieldMap.put(fieldName, field);
                columnNameMap.put(fieldName, columnName);
            }

            rowMapper = BeanPropertyRowMapper.newInstance(tableClass);

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

    public abstract JdbcTemplate getJdbcTemplate();

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
            int result = getJdbcTemplate().update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
                if (paramList != null) {
                    for (int i = 0; i < paramList.size(); i++) {
                        preparedStatement.setObject(i + 1, paramList.get(i));
                    }
                }
                return preparedStatement;
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
        ConditionsResult conditionsResult = example.getConditions().export();
        String sqlWhere = conditionsResult.sql.toString();
        List params = conditionsResult.params;
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
        return getJdbcTemplate().query(sql.toString(), params.toArray(), rowMapper);
    }

    public List<T> query(String sql, Object... params) {
        return getJdbcTemplate().query(sql, params, rowMapper);
    }

    public int update(String sql, Object... params) {
        return getJdbcTemplate().update(sql, params);
    }

    public T selectOne(Conditions conditions, String orderByClause, String columns) {
        Example example = new Example().setSkip(0).setLimit(1);
        if (conditions != null) example.setConditions(conditions);
        if (StringUtils.isNotBlank(orderByClause)) example.setOrderByClause(orderByClause);
        if (StringUtils.isNotBlank(columns)) example.setColumns(columns);
        List<T> data = selectByExample(example);
        if (data != null && data.size() > 0) {
            return data.get(0);
        } else {
            return null;
        }
    }

    public <F> List<F> group(String field, Conditions conditions, Class<F> fClass) {
        if (StringUtils.isBlank(field) || !columnNameMap.containsKey(field)) {
            throw new CrudTemplateException("field 无效");
        }
        if (conditions == null) conditions = new Conditions();
        ConditionsResult conditionsResult = conditions.export();
        String sqlWhere = conditionsResult.sql.toString();
        List params = conditionsResult.params;
        Sql sql = new Sql()
                .select(columnNameMap.get(field))
                .from(getFullTableName())
                .append(
                        StringUtils.isNotBlank(sqlWhere) ? new Sql().where(sqlWhere).toString() : null
                )
                .groupBy(columnNameMap.get(field));
        log.debug(sql.toString());
        log.debug(JSON.toJSONString(params));
        return getJdbcTemplate().queryForList(sql.toString(), params != null ? params.toArray() : null, fClass);
    }

    public int countByConditions(Conditions conditions) {
        if (conditions == null) conditions = new Conditions();
        ConditionsResult conditionsResult = conditions.export();
        String sqlWhere = conditionsResult.sql.toString();
        List params = conditionsResult.params;
        Sql sql = new Sql()
                .select(Sql.COUNT_STAR)
                .from(getFullTableName())
                .append(
                        StringUtils.isNotBlank(sqlWhere) ? new Sql().where(sqlWhere).toString() : null
                );
        log.debug(sql.toString());
        return getJdbcTemplate().queryForObject(sql.toString(), params.toArray(), int.class);
    }

    public int updateByExample(Example example, T record, String[] fields, boolean selective) throws IllegalAccessException {
        if (example == null) example = new Example();
        Sql updating = new Sql();
        List params = new ArrayList();
        ConditionsResult conditionsResult = example.getConditions().export();
        String sqlWhere = conditionsResult.sql.toString();
        params.addAll(conditionsResult.params);
        collectFields(record, fields, selective, new CollectCallback() {
            @Override
            public void run(String fieldName, Field field, String columnName, Object columnValue) {
                if (updating.length() > 0) {
                    updating.append(Sql.COMMA);
                }
                updating.append(columnName).append(Sql.EQ).append(Sql.MARK);
            }
        });
        Sql sql = new Sql()
                .update(getFullTableName())
                .set(updating.toString())
                .append(
                        StringUtils.isNotBlank(sqlWhere) ? new Sql().where(sqlWhere).toString() : null
                )
                .append(
                        StringUtils.isNotBlank(example.getOrderByClause()) ? new String[]{Sql.ORDER_BY, example.getOrderByClause()} : null
                )
                .limit(example.getSkip(), example.getLimit());
        return getJdbcTemplate().update(sql.toString(), params.toArray());

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

    public List<Condition> makeConditionList(T record, String[] fields, boolean selective) throws IllegalAccessException {
        List<Condition> conditionList = new ArrayList<>();
        collectFields(record, fields, selective, new CollectCallback() {
            @Override
            public void run(String fieldName, Field field, String columnName, Object columnValue) {
                if (columnValue == null) {
                    conditionList.add(new Condition(Condition.Type.is_null, columnName, null, null, null, null));
                } else {
                    conditionList.add(new Condition(Condition.Type.eq, columnName, columnValue, null, null, null));
                }
            }
        });
        return conditionList;
    }


    public static abstract class CollectCallback {
        public abstract void run(String fieldName, Field field, String columnName, Object columnValue);
    }

}
