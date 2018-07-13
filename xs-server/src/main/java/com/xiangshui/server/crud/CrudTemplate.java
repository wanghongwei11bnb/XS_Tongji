package com.xiangshui.server.crud;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiangshui.server.domain.mysql.Partner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class CrudTemplate<P, T> {


    private Class<P> primaryClass;
    private Class<T> tableClass;
    private String primaryFieldName;
    private Field primaryField;
    private String primaryColumnName;
    private Map<String, String> columnNameMap = new HashMap<>();
    private Map<String, Field> fieldMap = new HashMap<>();

    private String tableName;


    static final String INSERT_INTO = " INSERT INTO ";
    static final String DELETE = " DELETE ";
    static final String DELETE_FROM = " DELETE FROM ";
    static final String UPDATE = " UPDATE ";
    static final String SELECT = " SELECT ";
    static final String FROM = " FROM ";
    static final String WHERE = " WHERE ";
    static final String SET = " SET ";
    static final String EQ = " = ";
    static final String NE = " <> ";
    static final String IN = " IN ";
    static final String NIN = " NOT IN ";
    static final String GT = " > ";
    static final String GTE = " >= ";
    static final String LT = " < ";
    static final String LTE = " <= ";
    static final String LIKE = " LIKE ";
    static final String NOT_LIKE = " NOT LIKE ";
    static final String BL = " ( ";
    static final String BR = " ) ";
    static final String COMMA = " , ";
    static final String VALUES = " VALUES ";
    static final String MARK = " ? ";

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


    private int insert(T t, String[] fields, boolean selective) throws NoSuchFieldException, IllegalAccessException {
        List<InsertItem> insertItemList = new ArrayList<>();
        collectFields(t, fields, selective, new CollectCallback() {
            @Override
            public void run(String fieldName, Field field, String columnName, Object columnValue) {
                insertItemList.add(new InsertItem(columnName, columnValue));
            }
        });
        StringBuilder stringBuilder = new StringBuilder();
        List<Object> paramList = new ArrayList<>();
        stringBuilder.append(INSERT_INTO).append(tableName).append(BL);
        for (int i = 0; i < insertItemList.size(); i++) {
            InsertItem insertItem = insertItemList.get(i);
            if (i == 0) {
                stringBuilder.append(insertItem.columnName);
            } else {
                stringBuilder.append(COMMA).append(insertItem.columnName);
            }
        }
        stringBuilder.append(BR);
        stringBuilder.append(VALUES);
        stringBuilder.append(BL);

        for (int i = 0; i < insertItemList.size(); i++) {
            InsertItem insertItem = insertItemList.get(i);
            if (i == 0) {
                stringBuilder.append(MARK);
            } else {
                stringBuilder.append(COMMA).append(MARK);
            }
            paramList.add(insertItem.columnValue);
        }
        stringBuilder.append(BR);

        System.out.println(stringBuilder.toString());
        System.out.println(JSON.toJSONString(paramList));
        String sql = stringBuilder.toString();
        return getJdbcTemplate().update(sql, paramList.toArray());
    }

    public int insert(T t, String[] fields) throws NoSuchFieldException, IllegalAccessException {
        return insert(t, fields, false);
    }

    public int insertSelective(T t, String[] fields) throws NoSuchFieldException, IllegalAccessException {
        return insert(t, fields, true);
    }


    public int deleteByPrimaryKey(P primaryKey) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DELETE_FROM).append(tableName).append(WHERE).append(primaryColumnName).append(EQ).append(MARK);
        String sql = stringBuilder.toString();
        System.out.println(sql);
        return getJdbcTemplate().update(sql, primaryKey);
    }


    public T selectByPrimaryKey(P primaryKey, String columns) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SELECT)
                .append(StringUtils.isNotBlank(columns) ? columns : "*")
                .append(FROM).append(tableName).append(WHERE).append(primaryColumnName).append(EQ).append(MARK);
        System.out.println(stringBuilder.toString());
        return getJdbcTemplate().query(stringBuilder.toString(), new Object[]{primaryKey}, new ResultSetExtractor<T>() {
            @Override
            public T extractData(ResultSet resultSet) throws SQLException, DataAccessException {
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
            }
        });

    }


    public int count() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SELECT).append("count(*)").append(FROM).append(" ").append(tableName);
        System.out.println(stringBuilder.toString());
        return getJdbcTemplate().queryForObject(stringBuilder.toString(), int.class);
    }


    private void collectFields(T t, String[] fields, boolean selective, CollectCallback callback) throws NoSuchFieldException, IllegalAccessException {
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
            Object columnValue = field.get(t);
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


    public static class InsertItem {
        public String columnName;
        public Object columnValue;

        public InsertItem(String columnName, Object columnValue) {
            this.columnName = columnName;
            this.columnValue = columnValue;
        }
    }


    public static class SetItem {
        private String fieldName;
        private Object value;

        public String getFieldName() {
            return fieldName;
        }

        public SetItem setFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public Object getValue() {
            return value;
        }

        public SetItem setValue(Object value) {
            this.value = value;
            return this;
        }
    }


    public static class WhereItem {
        public WhereItemType type;
        public String fieldName;
        public Object value;
        public Object value1;
        public Object value2;
        public List valueList;
        public List<WhereItem> whereItemList;

        private WhereItem() {
        }

        public WhereItem(WhereItemType type, String fieldName, Object value) {
            this.type = type;
            this.fieldName = fieldName;
            this.value = value;
        }

        public WhereItem(WhereItemType type, String fieldName, Object value1, Object value2) {
            this.type = type;
            this.fieldName = fieldName;
            this.value1 = value1;
            this.value2 = value2;
        }

        public WhereItem(WhereItemType type, String fieldName, List valueList) {
            this.type = type;
            this.fieldName = fieldName;
            this.valueList = valueList;
        }

        public static WhereItem or(String fieldName, List<WhereItem> whereItemList) {
            WhereItem whereItem = new WhereItem();
            whereItem.type = WhereItemType.or;
            whereItem.whereItemList = whereItemList;
            return whereItem;
        }

        public static WhereItem or(String fieldName, WhereItem... whereItems) {
            return or(fieldName, Arrays.asList(whereItems));
        }
    }


    public static enum WhereItemType {
        eq, neq, in, nin, lt, lte, gt, gte, between, like, isnull, or
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

        private final String BLANK = " ";

        public Sql append(String fragment) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(BLANK);
            }
            stringBuilder.append(fragment);
            return this;
        }


        public Sql insertInto(String tableName, String columns) {
            append("insert into");
            append(tableName);
            append("(");
            append(columns);
            append(")");
            return this;
        }


        public Sql values(String values) {
            append("values (");
            append(values);
            append(")");
            return this;
        }


        public Sql deleteFrom(String tableName) {
            append("select from");
            append(tableName);
            return this;
        }


        public Sql where(String condition) {
            append("where");
            append(condition);
            return this;
        }


        public Sql update(String tableName) {
            append("update");
            append(tableName);
            return this;
        }


        public Sql groupBy(String groupBy) {
            append("group by");
            append(groupBy);
            return this;
        }

        public Sql orderBy(String orderBy) {
            append("order by");
            append(orderBy);
            return this;
        }


        public Sql select(String columns) {
            append("select");
            append(columns);
            return this;
        }


        public Sql limit(int limit) {
            append("limit");
            append(String.valueOf(limit));
            return this;
        }

        public Sql limit(int skip, int limit) {
            append("limit");
            append(String.valueOf(skip));
            append(",");
            append(String.valueOf(limit));
            return this;
        }


        public Sql from(String tableName) {
            append("from");
            append(tableName);
            return this;
        }


        public Sql set(String updating) {
            append("set");
            append(updating);
            return this;
        }


        @Override
        public String toString() {
            return stringBuilder.toString();
        }
    }


    public static void main(String[] args) throws Exception {


//        CrudTemplate template = new CrudTemplate<Integer, Partner>() {
//            @Override
//            public JdbcTemplate getJdbcTemplate() {
//                return null;
//            }
//
//            @Override
//            public String getTableName() {
//                return "partner";
//            }
//
//            @Override
//            public String getPrimaryFieldName() {
//                return "id";
//            }
//
//            @Override
//            public String defineColumnName(String fieldName, Field field) {
//                if ("createTime".equals(fieldName)) {
//                    return "create_time";
//                }
//                if ("updateTime".equals(fieldName)) {
//                    return "update_time";
//                }
//                if ("lastLoginTime".equals(fieldName)) {
//                    return "last_login_time";
//                }
//                return super.defineColumnName(fieldName, field);
//            }
//        };
//
//        Partner partner = new Partner();
//        partner.setId(2342);
//        partner.setAddress("sdfsf");
//        partner.setUpdateTime(new Date());
//
//        template.insertSelective(partner, new String[]{
//                "address",
//                "remark",
//        });

        System.out.println(new Sql().select("*").from("article").limit(5, 5));
    }

}
