package com.xiangshui.server.crud;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class SinglePrimaryCrudTemplate<P, T> extends CrudTemplate<T> {

    @Override
    protected void initPrimary() throws NoSuchFieldException {
        primaryFieldName = getPrimaryFieldName();
        if (StringUtils.isBlank(primaryFieldName)) {
            throw new CrudTemplateException("primaryFieldName 不能为空");
        }
        primaryField = tableClass.getDeclaredField(primaryFieldName);
        if (primaryField == null) {
            throw new CrudTemplateException("primaryField 不能为空");
        }
        primaryField.setAccessible(true);
        primaryColumnName = defineColumnName(primaryFieldName, primaryField);
        if (StringUtils.isBlank(primaryColumnName)) {
            throw new CrudTemplateException("primaryColumnName 不能为空");
        }
    }

    abstract public String getPrimaryFieldName();

    public T selectByPrimaryKey(P primaryKey, String columns) {
        Sql sql = new Sql().select(StringUtils.isNotBlank(columns) ? columns : Sql.STAR).from(getFullTableName()).where(primaryColumnName + "=?").limit(1);
        log.debug(sql.toString());
        return getJdbcTemplate().query(sql.toString(), new Object[]{primaryKey}, resultSetExtractor);
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

    public int deleteByPrimaryKey(P primaryKey) {
        Sql sql = new Sql().deleteFrom(getFullTableName()).where(primaryColumnName).append(Sql.EQ).append(Sql.MARK);
        log.debug(sql.toString());
        return getJdbcTemplate().update(sql.toString(), primaryKey);
    }
}
