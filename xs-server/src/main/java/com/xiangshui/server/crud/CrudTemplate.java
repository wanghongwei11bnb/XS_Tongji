package com.xiangshui.server.crud;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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


    public CrudTemplate() {
        try {
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
            rowMapper = new RowMapper<T>() {
                @Override
                public T mapRow(ResultSet resultSet, int i) throws SQLException {
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
                }
            };
        } catch (Exception e) {
            log.error("", e);
            throw new CrudTemplateException(e.getMessage());
        }
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
        Sql sql = new Sql().select(StringUtils.isNotBlank(columns) ? columns : Sql.STAR).from(getFullTableName()).where(primaryColumnName + "=?").limit(1);
        log.debug(sql.toString());
        return getJdbcTemplate().query(sql.toString(), new Object[]{primaryKey}, resultSetExtractor);
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
        Sql sql = new Sql().select(Sql.COUNT_STAR).from(getFullTableName()).where(example.getCriteria().makeSql()).limit(example.getSkip(), example.getLimit());
        List paramList = example.getCriteria().makeParamList();
        log.debug(sql.toString());
        log.debug(JSON.toJSONString(paramList));
        return getJdbcTemplate().queryForObject(sql.toString(), paramList.toArray(), int.class);
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

    public static class Criteria {
        private final List<Criterion> criterionList = new ArrayList<>();

        public Criteria addCriterion(Criterion criterion) {
            this.criterionList.add(criterion);
            return this;
        }

        public static String makeSql(List<Criterion> criterionList) {
            if (criterionList == null || criterionList.size() == 0) {
                return null;
            }
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
                        sql.append(makeSql(criterion.criterionList));
                        break;
                    case and:
                        sql.append(makeSql(criterion.criterionList));
                        break;
                    case single:
                        sql.append(criterion.column);
                        break;
                    case is_null:
                        sql.append(criterion.column).append("is null");
                        break;
                    case is_not_null:
                        sql.append(criterion.column).append("is not null");
                        break;
                    case eq:
                        sql.append(criterion.column).append(Sql.EQ).append(Sql.MARK);
                        break;
                    case not_eq:
                        sql.append(criterion.column).append(Sql.NE).append(Sql.MARK);
                        break;
                    case gt:
                        sql.append(criterion.column).append(Sql.GT).append(Sql.MARK);
                        break;
                    case gte:
                        sql.append(criterion.column).append(Sql.GTE).append(Sql.MARK);
                        break;
                    case lt:
                        sql.append(criterion.column).append(Sql.LT).append(Sql.MARK);
                        break;
                    case lte:
                        sql.append(criterion.column).append(Sql.LTE).append(Sql.MARK);
                        break;
                    case like:
                        sql.append(criterion.column).append(Sql.LIKE).append(Sql.MARK);
                        break;
                    case not_like:
                        sql.append(criterion.column).append(Sql.NOT_LIKE).append(Sql.MARK);
                        break;
                    case between:
                        sql.append(criterion.column).append(Sql.BETWEEN).append(Sql.MARK).append(Sql.AND).append(Sql.MARK);
                        break;
                    case in:
                    case not_in:
                        sql.append(criterion.column);
                        sql.append(criterion.type == in ? Sql.IN : Sql.NIN);
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

        public static List makeParamList(List<Criterion> criterionList) {
            List paramList = new ArrayList();
            for (int i = 0; i < criterionList.size(); i++) {
                Criterion criterion = criterionList.get(i);

                switch (criterion.type) {
                    case singleValue:
                        paramList.add(criterion.value);
                        break;
                    case betweenValue:
                        paramList.add(criterion.value);
                        paramList.add(criterion.secondValue);
                        break;
                    case listValue:
                        paramList.addAll(criterion.listValue);
                        break;
                    case or:
                    case and:
                        paramList.addAll(makeParamList(criterion.criterionList));
                        break;
                    default:
                        break;
                }
            }
            return paramList;
        }

        public String makeSql() {
            return makeSql(this.criterionList);
        }

        public List makeParamList() {
            return makeParamList(this.criterionList);
        }
    }


    public static class Criterion {
        public final CriterionType type;
        public final String column;
        public final Object value;
        public final Object secondValue;
        public final List listValue;
        public final List<Criterion> criterionList;

        private Criterion(CriterionType type, String column, Object value, Object secondValue, List listValue, List<Criterion> criterionList) {
            this.type = type;
            this.column = column;
            this.value = value;
            this.secondValue = secondValue;
            this.listValue = listValue;
            this.criterionList = criterionList;
        }

        public static Criterion or(List<Criterion> whereItemList) {
            return new Criterion(or, null, null, null, null, whereItemList);
        }

        public static Criterion or(Criterion... whereItems) {
            return or(Arrays.asList(whereItems));
        }

        public static Criterion and(List<Criterion> whereItemList) {
            return new Criterion(and, null, null, null, null, whereItemList);
        }

        public static Criterion and(Criterion... whereItems) {
            return and(Arrays.asList(whereItems));
        }

        public static Criterion single(String column) {
            return new Criterion(eq, column, null, null, null, null);
        }

        public static Criterion is_null(String column) {
            return new Criterion(is_null, column, null, null, null, null);
        }

        public static Criterion is_not_null(String column) {
            return new Criterion(is_null, column, null, null, null, null);
        }


        public static Criterion eq(String column, Object value) {
            return new Criterion(eq, column, value, null, null, null);
        }

        public static Criterion not_eq(String column, Object value) {
            return new Criterion(not_eq, column, value, null, null, null);
        }

        public static Criterion gt(String column, Object value) {
            return new Criterion(gt, column, value, null, null, null);
        }

        public static Criterion gte(String column, Object value) {
            return new Criterion(gte, column, value, null, null, null);
        }

        public static Criterion lt(String column, Object value) {
            return new Criterion(lt, column, value, null, null, null);
        }

        public static Criterion lte(String column, Object value) {
            return new Criterion(lte, column, value, null, null, null);
        }

        public static Criterion like(String column, Object value) {
            return new Criterion(like, column, value, null, null, null);
        }

        public static Criterion not_like(String column, Object value) {
            return new Criterion(not_like, column, value, null, null, null);
        }

        public static Criterion between(String column, Object value, Object secondValue) {
            return new Criterion(between, column, value, secondValue, null, null);
        }

        public static Criterion in(String column, List listValue) {
            return new Criterion(in, column, null, null, listValue, null);
        }

        public static Criterion not_in(String column, List listValue) {
            return new Criterion(not_in, column, null, null, listValue, null);
        }

    }


    public enum CriterionType {
        single, is_null, is_not_null, eq, not_eq, gt, gte, lt, lte, like, not_like, between, in, not_in, or, and
    }


    private static abstract class CollectCallback {
        public abstract void run(String fieldName, Field field, String columnName, Object columnValue);
    }


    public static class Sql {
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

    public static class CrudTemplateException extends RuntimeException {
        public CrudTemplateException(String message) {
            super(message);
        }
    }

}
