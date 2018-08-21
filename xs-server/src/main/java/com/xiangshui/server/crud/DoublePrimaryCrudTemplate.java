package com.xiangshui.server.crud;

import com.xiangshui.server.crud.assist.CollectCallback;
import com.xiangshui.server.crud.assist.CrudTemplateException;
import com.xiangshui.server.crud.assist.Sql;
import com.xiangshui.server.exception.XiangShuiException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class DoublePrimaryCrudTemplate<P1, P2, T> extends CrudTemplate<T> {

    @Override
    public void initPrimary() throws NoSuchFieldException {
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

    @Override
    protected void initSecondPrimary() throws NoSuchFieldException {
        if (!primary) {
            throw new CrudTemplateException("primary 不能为空");
        }
        secondPrimaryFieldName = getSecondPrimaryFieldName();
        if (StringUtils.isBlank(secondPrimaryFieldName)) {
            throw new CrudTemplateException("secondPrimaryFieldName 不能为空");
        }
        secondPrimaryField = tableClass.getDeclaredField(secondPrimaryFieldName);
        if (secondPrimaryField == null) {
            throw new CrudTemplateException("secondPrimaryField 不能为空");
        }
        secondPrimaryField.setAccessible(true);
        secondPrimaryColumnName = defineColumnName(secondPrimaryFieldName, secondPrimaryField);
        if (StringUtils.isBlank(secondPrimaryColumnName)) {
            throw new CrudTemplateException("secondPrimaryColumnName 不能为空");
        }
        secondPrimary = true;
    }

    abstract public String getPrimaryFieldName();

    abstract public String getSecondPrimaryFieldName();

    public T selectByPrimaryKey(P1 primaryKey, P2 secondPrimaryKey, String columns) {
        if (primaryKey == null) {
            throw new CrudTemplateException("primaryFieldValue 不能为空");
        }
        if (secondPrimaryKey == null) {
            throw new CrudTemplateException("secondPrimaryKey 不能为空");
        }
        Sql sql = new Sql().select(StringUtils.isNotBlank(columns) ? columns : Sql.STAR).from(getFullTableName()).where(primaryColumnName + "=? and " + secondPrimaryColumnName + "=?").limit(1);
        log.debug(sql.toString());
        return getJdbcTemplate().query(sql.toString(), new Object[]{primaryKey, secondPrimaryKey}, resultSetExtractor);
    }

    public List<T> selectByPrimaryKey(P1 primaryKey, String columns, int skip, int limit) {
        if (primaryKey == null) {
            throw new CrudTemplateException("primaryFieldValue 不能为空");
        }
        if (skip > 0) {
            throw new CrudTemplateException("skip 必须大于等于0");
        }
        if (limit > 1) {
            throw new CrudTemplateException("limit 必须大于等于1");
        }
        Sql sql = new Sql()
                .select(StringUtils.isNotBlank(columns) ? columns : Sql.STAR)
                .from(getFullTableName())
                .where(new Sql().append(primaryColumnName).append(Sql.EQ).append(Sql.MARK).toString())
                .limit(skip, limit);
        log.debug(sql.toString());
        return getJdbcTemplate().query(sql.toString(), new Object[]{primaryKey}, rowMapper);
    }

    private int updateByPrimaryKey(T record, String[] fields, boolean selective) throws NoSuchFieldException, IllegalAccessException {
        List paramList = new ArrayList();
        Sql setSql = new Sql();
        Sql whereSql = new Sql();
        P1 primaryKey = (P1) primaryField.get(record);
        if (primaryKey == null) {
            throw new CrudTemplateException("primaryFieldValue 不能为空");
        }
        P2 secondPrimaryKey = (P2) secondPrimaryField.get(record);
        if (secondPrimaryKey == null) {
            throw new CrudTemplateException("secondPrimaryKey 不能为空");
        }
        collectFields(record, fields, selective, new CollectCallback() {
            @Override
            public void run(String fieldName, Field field, String columnName, Object columnValue) {
                if (fieldName.equals(primaryFieldName)) {
                    throw new CrudTemplateException("不能更新 primaryFieldName 字段");
                }
                if (fieldName.equals(secondPrimaryFieldName)) {
                    throw new CrudTemplateException("不能更新 secondPrimaryFieldName 字段");
                }
                if (setSql.length() > 0) {
                    setSql.append(Sql.COMMA);
                }
                setSql.append(columnName).append(Sql.EQ).append(Sql.MARK);
                paramList.add(columnValue);
            }
        });
        if (setSql.length() == 0) {
            throw new XiangShuiException("没有要更新的字段");
        }
        whereSql.append(primaryColumnName).append(Sql.EQ).append(Sql.MARK).append(Sql.AND).append(secondPrimaryColumnName).append(Sql.EQ).append(Sql.MARK);
        paramList.add(primaryKey);
        paramList.add(secondPrimaryKey);
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

    public int deleteByPrimaryKey(P1 primaryKey, P2 secondPrimaryKey) {
        if (primaryKey == null) {
            throw new CrudTemplateException("primaryFieldValue 不能为空");
        }
        if (secondPrimaryKey == null) {
            throw new CrudTemplateException("secondPrimaryKey 不能为空");
        }
        Sql sql = new Sql().deleteFrom(getFullTableName()).where(primaryColumnName).append(Sql.EQ).append(Sql.MARK).append(Sql.AND).append(secondPrimaryColumnName).append(Sql.EQ).append(Sql.MARK);
        log.debug(sql.toString());
        return getJdbcTemplate().update(sql.toString(), new Object[]{primaryKey, secondPrimaryKey});
    }
}
