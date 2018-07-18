package com.xiangshui.server.crud;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Consumer;

import static com.xiangshui.server.crud.CrudTemplate.CriterionType.*;

public abstract class CrudTemplate<P, T> {

    private static final Logger log = LoggerFactory.getLogger(CrudTemplate.class);

    private Class<P> primaryClass;
    private Class<T> tableClass;

    private String primaryFieldName;
    private Field primaryField;
    private String primaryColumnName;

    private Map<String, String> columnNameMap = new HashMap<>();
    private Map<String, Field> fieldMap = new HashMap<>();

    private String tableName;

    ResultSetExtractor<T> resultSetExtractor;
    RowMapper<T> rowMapper;


    public CrudTemplate() throws CrudTemplateException {

        primaryClass = (Class<P>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        tableClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        tableName = getFullTableName();
        if (StringUtils.isBlank(tableName)) {
            throw new CrudTemplateException("tableName 不能为空");
        }
        primaryFieldName = getPrimaryFieldName();
        if (StringUtils.isBlank(primaryFieldName)) {
            throw new CrudTemplateException("primaryFieldName 不能为空");
        }
        for (Field field : tableClass.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String columnName = defineColumnName(fieldName, field);
            if (StringUtils.isBlank(columnName)) {
                throw new CrudTemplateException("columnName 不能为空");
            }
            if (primaryFieldName.equals(fieldName)) {
                primaryColumnName = columnName;
                primaryField = field;
            } else {
                if (primaryColumnName.equals(columnName) || fieldMap.containsValue(columnName)) {
                    throw new CrudTemplateException("columnName 重复");
                }
                columnNameMap.put(fieldName, columnName);
                fieldMap.put(fieldName, field);
            }
        }
        if (StringUtils.isBlank(primaryColumnName)) {
            throw new CrudTemplateException("primaryColumnName 不能为空");
        }


        resultSetExtractor = resultSet -> {
            resultSet.next();
            try {
                T t = tableClass.getConstructor().newInstance();
                primaryField.set(t, resultSet.getObject(primaryColumnName));
                for (String fieldName : fieldMap.keySet()) {
                    fieldMap.get(fieldName).set(t, resultSet.getObject(columnNameMap.get(fieldName)));
                }
                return t;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };
        rowMapper = new BeanPropertyRowMapper<T>();
    }

    public abstract JdbcTemplate getJdbcTemplate();

    abstract public String getTableName();

    abstract public String getPrimaryFieldName();

    public String getFullTableName() {
        return getTableName();
    }

    public String defineColumnName(String fieldName, Field field) {
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


    public int deleteByPrimaryKey(P primaryKey) {
        Sql sql = new Sql().deleteFrom(getFullTableName()).where(primaryColumnName).append(Sql.EQ).append(Sql.MARK);
        log.debug(sql.toString());
        return getJdbcTemplate().update(sql.toString(), primaryKey);
    }

    private int updateByPrimaryKey(T record, String[] fields, boolean selective) throws NoSuchFieldException, IllegalAccessException {
        List paramList = new ArrayList();
        Sql setSql = new Sql();
        Sql whereSql = new Sql();
        collectFields(record, fields, selective, new CollectCallback() {
            @Override
            public void run(String fieldName, Field field, String columnName, Object columnValue) {
                if (primaryFieldName.equals(fieldName)) {
                    return;
                }
                if (setSql.length() > 0) {
                    setSql.append(Sql.COMMA);
                }
                setSql.append(columnName).append(Sql.EQ).append(Sql.MARK);
                paramList.add(columnValue);
            }
        });
        whereSql.append(primaryColumnName).append(Sql.EQ).append(Sql.MARK);
        paramList.add(primaryField.get(record));
        Sql sql = new Sql().update(getFullTableName()).set(setSql.toString()).where(whereSql.toString());
        log.debug(sql.toString());
        return getJdbcTemplate().update(sql.toString(), paramList.toArray());
    }

    public int updateByPrimaryKey(T record, String[] fields) throws NoSuchFieldException, IllegalAccessException {
        return updateByPrimaryKey(record, fields, false);
    }

    public int updateByPrimaryKeySelective(T record, String[] fields) throws NoSuchFieldException, IllegalAccessException {
        return updateByPrimaryKey(record, fields, true);
    }


    public T selectByPrimaryKey(P primaryKey, String columns) {
        Sql sql = new Sql().select(StringUtils.isNotBlank(columns) ? columns : "*").from(getFullTableName()).where(primaryColumnName + "=?");
        log.debug(sql.toString());
        return getJdbcTemplate().query(sql.toString(), new Object[]{primaryKey}, resultSetExtractor);
    }


    public List<T> selectByExample() {
        Sql sql = new Sql();
        log.debug(sql.toString());
        return getJdbcTemplate().query(sql.toString(), new Object[]{}, rowMapper);
    }


    public int countByExample(Example example) {
        Sql sql = new Sql().select("count(*)").from(getFullTableName());
        return getJdbcTemplate().queryForObject(sql.toString(), int.class);
    }


    private void collectFields(T record, String[] fields, boolean selective, CollectCallback callback) throws NoSuchFieldException, IllegalAccessException {
        Set<String> fieldSet;
        if (fields != null && fields.length > 0) {
            fieldSet = new HashSet<>();
            for (String fieldName : fields) {
                fieldSet.add(fieldName);
            }
        } else {
            fieldSet = new HashSet<>(columnNameMap.keySet());
            fieldSet.add(primaryFieldName);
        }
        for (String fieldName : fieldSet) {
            Field field = tableClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object columnValue = field.get(record);
            if (columnValue == null && selective) {
                continue;
            }
            String columnName;
            if (primaryFieldName.equals(fieldName)) {
                columnName = primaryColumnName;
            } else {
                columnName = columnNameMap.get(fieldName);
            }
            callback.run(fieldName, field, columnName, columnValue);
        }
    }


    public static class Example {

    }

    public static class Criterion {
        public CriterionType type;
        public String condition;
        public Object value;
        public Object secondValue;
        public List listValue;
        public List<Criterion> criterionList;

        private Criterion(CriterionType type, String condition, Object value, Object secondValue, List listValue, List<Criterion> criterionList) {
            this.type = type;
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.listValue = listValue;
            this.criterionList = criterionList;
        }

        public static Criterion noValue(String condition) {
            return new Criterion(noValue, condition, null, null, null, null);
        }

        public static Criterion singleValue(String condition, Object value) {
            return new Criterion(singleValue, condition, value, null, null, null);
        }

        public static Criterion betweenValue(String condition, Object value, Object secondValue) {
            return new Criterion(betweenValue, condition, value, secondValue, null, null);
        }

        public static Criterion listValue(String condition, List listValue) {
            return new Criterion(CriterionType.listValue, condition, null, null, listValue, null);
        }

        public static Criterion or(String fieldName, List<Criterion> whereItemList) {
            return new Criterion(or, null, null, null, null, whereItemList);
        }

        public static Criterion or(String fieldName, Criterion... whereItems) {
            return or(fieldName, Arrays.asList(whereItems));
        }

        public static String sql(List<Criterion> criterionList) {
            Sql sql = new Sql();
            sql.append(Sql.BL);
            for (int i = 0; i < criterionList.size(); i++) {
                Criterion criterion = criterionList.get(i);
                if (i > 0) {
                    if (criterion.type == or) {
                        sql.append(Sql.OR);
                    } else {
                        sql.append(Sql.AND);
                    }
                }
                switch (criterion.type) {
                    case or:
                        sql.append(sql(criterion.criterionList));
                        break;
                    case noValue:
                        sql.append(criterion.condition);
                        break;
                    case singleValue:
                        sql.append(criterion.condition).append(Sql.MARK);
                        break;
                    case betweenValue:
                        sql.append(criterion.condition).append(Sql.BETWEEN).append(Sql.MARK).append(Sql.AND).append(Sql.MARK);
                        break;
                    case listValue:
                        sql.append(criterion.condition);
                        List listValue = criterion.listValue;
                        sql.append(Sql.BL);
                        for (int j = 0; j < listValue.size(); j++) {
                            if (j > 0) {
                                sql.append(Sql.COMMA);
                            }
                            sql.append(Sql.MARK);
                        }
                        sql.append(Sql.BR);
                        break;
                    default:
                        break;
                }
            }
            sql.append(Sql.BR);
            return sql.toString();
        }

    }


    public enum CriterionType {
        noValue, singleValue, betweenValue, listValue, or, and
    }


    private static abstract class CollectCallback {
        public abstract void run(String fieldName, Field field, String columnName, Object columnValue);
    }

    public static class CrudTemplateException extends Exception {
        public CrudTemplateException(String message) {
            super(message);
        }
    }


    static class Sql {
        private StringBuilder stringBuilder = new StringBuilder();


        static final String INSERT_INTO = "INSERT INTO";
        static final String DELETE = " DELETE ";
        static final String DELETE_FROM = "DELETE FROM";
        static final String UPDATE = "UPDATE";
        static final String SELECT = "SELECT";
        static final String FROM = "FROM";
        static final String WHERE = "WHERE";
        static final String SET = "SET";
        static final String EQ = "=";
        static final String NE = "<>";
        static final String IN = "IN";
        static final String NIN = "NOT IN";
        static final String GT = ">";
        static final String GTE = ">=";
        static final String LT = "<";
        static final String LTE = "<=";
        static final String LIKE = "LIKE";
        static final String NOT_LIKE = "NOT LIKE";
        static final String BL = "(";
        static final String BR = ")";
        static final String COMMA = ",";
        static final String VALUES = "VALUES";
        static final String MARK = "?";
        static final String BLANK = " ";
        static final String GROUP_BY = "GROUP BY";
        static final String ORDER_BY = "ORDER BY";
        static final String LIMIT = "LIMIT";
        static final String BETWEEN = "BETWEEN";
        static final String OR = "OR";
        static final String AND = "AND";

        public Sql append(String fragment) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(BLANK);
            }
            stringBuilder.append(fragment);
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

}
