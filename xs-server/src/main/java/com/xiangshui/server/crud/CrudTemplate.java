package com.xiangshui.server.crud;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public abstract class CrudTemplate<I, T> {
    private Class<I> iClass;
    private Class<T> tClass;
    private String idFieldName;


    {
        iClass = (Class<I>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        idFieldName = getIdFieldName();
    }


    abstract public String getTableName();

    abstract public String getIdFieldName();

    public String getFullTableName() {
        return getTableName();
    }


    public T selectById(I id) {
        return null;
    }

    public void deleteById(I id) {

    }

    public void insert(T criteria) {

    }


    public void updateById(I id, T criteria, String[] fields) throws Exception {

        List<Object> paramList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(" UPDATE ").append(getFullTableName()).append(" SET ");

        boolean isFirst = true;
        for (String fieldName : fields) {
            if (StringUtils.isBlank(fieldName)) {
                throw new Exception("fieldName count be blank");
            }
            Field field = tClass.getDeclaredField(fieldName);
            if (field == null) {
                throw new Exception("field " + fieldName + " not found");
            }

            Object value = field.get(criteria);
            if (!isFirst) {
                sb.append(" , ");
            }
            sb.append(fieldName).append(" = ? ");
            paramList.add(value);
            isFirst = false;
        }

        sb.append(" WHERE ").append(idFieldName).append(" = ? ");
        paramList.add(id);

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
        private WhereItemType type;
        private String fieldName;
        private Object value1;
        private Object value2;
        private List valueList;
    }

    public static enum WhereItemType {
        eq, neq, in, nin, lt, lte, gt, gte, between, like, isnull
    }


}
