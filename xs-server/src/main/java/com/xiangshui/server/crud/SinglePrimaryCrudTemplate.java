package com.xiangshui.server.crud;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class SinglePrimaryCrudTemplate<T, P> extends CrudTemplate<T> {

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
        primary = true;
    }

    abstract public String getPrimaryFieldName();

    public T selectByPrimaryKey(P primaryKey, String columns) {
        if (primaryKey == null) {
            throw new CrudTemplateException("primaryFieldValue 不能为空");
        }
        Sql sql = new Sql()
                .select(StringUtils.isNotBlank(columns) ? columns : Sql.STAR)
                .from(getFullTableName())
                .where(new Sql().append(primaryColumnName).append(Sql.EQ).append(Sql.MARK).toString())
                .limit(1);
        log.debug(sql.toString());
        return getJdbcTemplate().query(sql.toString(), new Object[]{primaryKey}, resultSetExtractor);
    }

    private int updateByPrimaryKey(T record, String[] fields, boolean selective) throws IllegalAccessException {
        List paramList = new ArrayList();
        Sql setSql = new Sql();
        Sql whereSql = new Sql();
        P primaryKey = (P) primaryField.get(record);
        if (primaryKey == null) {
            throw new CrudTemplateException("primaryFieldValue 不能为空");
        }
        collectFields(record, fields, selective, new CollectCallback() {
            @Override
            public void run(String fieldName, Field field, String columnName, Object columnValue) {
                if (fieldName.equals(primaryFieldName)) {
                    return;
                }
                if (setSql.length() > 0) {
                    setSql.append(Sql.COMMA);
                }
                setSql.append(columnName).append(Sql.EQ).append(Sql.MARK);
                paramList.add(columnValue);
            }
        });
        if (setSql.length() == 0) {
            throw new CrudTemplateException("没有要更新的字段");
        }
        whereSql.append(primaryColumnName).append(Sql.EQ).append(Sql.MARK);
        paramList.add(primaryKey);
        Sql sql = new Sql().update(getFullTableName()).set(setSql.toString()).where(whereSql.toString());
        log.debug(sql.toString());
        return getJdbcTemplate().update(sql.toString(), paramList.toArray());
    }

    public int updateByPrimaryKey(T record, String[] fields) throws IllegalAccessException {
        return updateByPrimaryKey(record, fields, false);
    }

    public int updateByPrimaryKeySelective(T record, String[] fields) throws IllegalAccessException {
        return updateByPrimaryKey(record, fields, true);
    }

    public int deleteByPrimaryKey(P primaryKey) {
        if (primaryKey == null) {
            throw new CrudTemplateException("primaryFieldValue 不能为空");
        }
        Sql sql = new Sql().deleteFrom(getFullTableName()).where(primaryColumnName).append(Sql.EQ).append(Sql.MARK);
        log.debug(sql.toString());
        return getJdbcTemplate().update(sql.toString(), primaryKey);
    }


    public List<T> selectByPrimaryKeyList(List<P> primaryKeyList, String columns) {
        if (primaryKeyList == null || primaryKeyList.size() == 0) {
            return null;
        }
        Example example = new Example();
        example.setColumns(columns);
        example.getConditions().in(primaryColumnName, primaryKeyList);
        example.setLimit(primaryKeyList.size());
        return selectByExample(example);
    }

    public List<P> mapperPrimaryKeyList(List<T> data) {
        List<P> primaryKeyList = new ArrayList<>();
        if (data != null && data.size() > 0) {
            data.forEach(t -> {
                try {
                    P p = (P) primaryField.get(t);
                    if (p != null) {
                        primaryKeyList.add(p);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }
        return primaryKeyList;
    }

}
