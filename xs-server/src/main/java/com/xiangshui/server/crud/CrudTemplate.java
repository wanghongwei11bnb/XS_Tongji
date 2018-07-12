package com.xiangshui.server.crud;

import com.alibaba.fastjson.JSON;
import com.xiangshui.server.domain.mysql.Partner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public abstract class CrudTemplate<P, T> {

    private JdbcTemplate jdbcTemplate;

    private Class<P> primaryClass;
    private Class<T> tableClass;
    private String primaryFieldName;
    private String primaryColumnName;
    private Map<String, String> fieldMap = new HashMap<>();

    private String tableName;


    private static final String INSERT_INTO = " SELECT ";
    private static final String DELETE = " SELECT ";
    private static final String UPDATE = " SELECT ";
    private static final String SELECT = " SELECT ";
    private static final String FROM = " SELECT ";
    private static final String WHERE = " SELECT ";
    private static final String SET = " SELECT ";
    private static final String EQ = " SELECT ";
    private static final String NE = " SELECT ";
    private static final String IN = " SELECT ";
    private static final String NIN = " SELECT ";
    private static final String GT = " SELECT ";
    private static final String GTE = " SELECT ";
    private static final String LT = " SELECT ";
    private static final String LTE = " SELECT ";
    private static final String LIKE = " SELECT ";

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
            String fieldName = field.getName();
            String columnName = defineColumnName(fieldName, field);
            if (StringUtils.isBlank(columnName)) {
                throw new CrudTemplateException("columnName 不能为空");
            }
            if (primaryFieldName.equals(fieldName)) {
                primaryColumnName = columnName;
            } else {
                if (primaryColumnName.equals(columnName) || fieldMap.containsValue(columnName)) {
                    throw new CrudTemplateException("columnName 重复");
                }
                fieldMap.put(fieldName, columnName);
            }
        }
        if (StringUtils.isBlank(primaryColumnName)) {
            throw new CrudTemplateException("primaryColumnName 不能为空");
        }
    }

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
        stringBuilder.append("insert into ").append(tableName).append(" ");
        stringBuilder.append("(");
        for (int i = 0; i < insertItemList.size(); i++) {
            InsertItem insertItem = insertItemList.get(i);
            if (i == 0) {
                stringBuilder.append(insertItem.columnName);
            } else {
                stringBuilder.append(",").append(insertItem.columnName);
            }
        }
        stringBuilder.append(")");
        stringBuilder.append(" values ");
        stringBuilder.append("(");

        for (int i = 0; i < insertItemList.size(); i++) {
            InsertItem insertItem = insertItemList.get(i);
            if (i == 0) {
                stringBuilder.append("?");
            } else {
                stringBuilder.append(",").append("?");
            }
            paramList.add(insertItem.columnValue);
        }
        stringBuilder.append(")");

        System.out.println(stringBuilder.toString());
        System.out.println(JSON.toJSONString(paramList));
        String sql = stringBuilder.toString();
        return jdbcTemplate.update(sql, paramList.toArray());
    }

    public int insert(T t, String[] fields) throws NoSuchFieldException, IllegalAccessException {
        return insert(t, fields, false);
    }

    public int insertSelective(T t, String[] fields) throws NoSuchFieldException, IllegalAccessException {
        return insert(t, fields, true);
    }


    public int deleteByPrimaryKey(P primaryKey) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DELETE FROM ").append(tableName).append(" WHERE ").append(primaryColumnName).append("=?");
        String sql = stringBuilder.toString();
        System.out.println(sql);
        return jdbcTemplate.update(sql, primaryKey);
    }


    public T selectByPrimaryKey(P primaryKey, String columns) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ")
                .append(StringUtils.isNotBlank(columns) ? columns : "*")
                .append(" FROM ").append(tableName).append(" WHERE ").append(primaryColumnName).append("=?");
        stringBuilder.append("count(*)");
        stringBuilder.append(" from ").append(tableName);
        System.out.println(stringBuilder.toString());
        return jdbcTemplate.queryForObject(stringBuilder.toString(), tableClass);
    }


    public int count() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select count(*) from").append(" ").append(tableName);
        System.out.println(stringBuilder.toString());
        return jdbcTemplate.queryForObject(stringBuilder.toString(), int.class);
    }


    private void collectFields(T t, String[] fields, boolean selective, CollectCallback callback) throws NoSuchFieldException, IllegalAccessException {
        Set<String> fieldSet;
        if (fields != null && fields.length > 0) {
            fieldSet = new HashSet<>();
            for (String fieldName : fields) {
                fieldSet.add(fieldName);
            }
        } else {
            fieldSet = new HashSet<>(fieldMap.keySet());
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
                columnName = fieldMap.get(fieldName);
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


    public static void main(String[] args) throws Exception {


        CrudTemplate template = new CrudTemplate<Integer, Partner>() {
            @Override
            public String getTableName() {
                return "partner";
            }

            @Override
            public String getPrimaryFieldName() {
                return "id";
            }

            @Override
            public String defineColumnName(String fieldName, Field field) {
                if ("createTime".equals(fieldName)) {
                    return "create_time";
                }
                if ("updateTime".equals(fieldName)) {
                    return "update_time";
                }
                if ("lastLoginTime".equals(fieldName)) {
                    return "last_login_time";
                }
                return super.defineColumnName(fieldName, field);
            }
        };

        Partner partner = new Partner();
        partner.setId(2342);
        partner.setAddress("sdfsf");
        partner.setUpdateTime(new Date());

        template.insertSelective(partner, new String[]{
                "address",
                "remark",
        });

        System.out.println();
    }

}
