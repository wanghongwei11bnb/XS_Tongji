package com.xiangshui.server.crud;


import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class Conditions {
    public final List<Condition> conditionList = new ArrayList<>();


    public Conditions single(String column) {
        conditionList.add(new Condition(Condition.Type.single, column, null, null, null, null));
        return this;
    }

    public Conditions eq(String column, Object value) {
        conditionList.add(new Condition(Condition.Type.eq, column, value, null, null, null));
        return this;
    }


    public Conditions not_eq(String column, Object value) {
        conditionList.add(new Condition(Condition.Type.not_eq, column, value, null, null, null));
        return this;
    }

    public Conditions gt(String column, Object value) {
        conditionList.add(new Condition(Condition.Type.gt, column, value, null, null, null));
        return this;
    }

    public Conditions gte(String column, Object value) {
        conditionList.add(new Condition(Condition.Type.gte, column, value, null, null, null));
        return this;
    }

    public Conditions lt(String column, Object value) {
        conditionList.add(new Condition(Condition.Type.lt, column, value, null, null, null));
        return this;
    }

    public Conditions lte(String column, Object value) {
        conditionList.add(new Condition(Condition.Type.lte, column, value, null, null, null));
        return this;
    }

    public Conditions like(String column, Object value) {
        conditionList.add(new Condition(Condition.Type.like, column, value, null, null, null));
        return this;
    }

    public Conditions not_like(String column, Object value) {
        conditionList.add(new Condition(Condition.Type.not_like, column, value, null, null, null));
        return this;
    }


    public Conditions between(String column, Object value, Object secondValue) {
        conditionList.add(new Condition(Condition.Type.between, column, value, secondValue, null, null));
        return this;
    }


    public Conditions in(String column, List listValue) {
        conditionList.add(new Condition(Condition.Type.in, column, null, null, listValue, null));
        return this;
    }


    public Conditions not_in(String column, List listValue) {
        conditionList.add(new Condition(Condition.Type.not_in, column, null, null, listValue, null));
        return this;
    }


    public Conditions is_null(String column) {
        conditionList.add(new Condition(Condition.Type.is_null, column, null, null, null, null));
        return this;
    }


    public Conditions is_not_null(String column) {
        conditionList.add(new Condition(Condition.Type.is_not_null, column, null, null, null, null));
        return this;
    }


    public Conditions and(Conditions conditions) {
        conditionList.add(new Condition(Condition.Type.and, null, null, null, null, conditions));
        return this;
    }


    public Conditions or(Conditions conditions) {
        conditionList.add(new Condition(Condition.Type.or, null, null, null, null, conditions));
        return this;
    }


    public void export(ConditionsResult result) {
        if (conditionList.size() > 0) {
            result.sql.append(Sql.BL);
            for (int i = 0; i < conditionList.size(); i++) {
                Condition condition = conditionList.get(i);
                if (i > 0) {
                    result.sql.append(condition.type == Condition.Type.or ? Sql.OR : Sql.AND);
                }
                condition.export(result);
            }
            result.sql.append(Sql.BR);
        }
    }


    public ConditionsResult export() {
        ConditionsResult result = new ConditionsResult();
        export(result);
        return result;
    }


    @Override
    public String toString() {
        return super.toString();
    }

    public static void main(String[] args) {
        ConditionsResult result = new Conditions()
                .and(new Conditions().eq("id", 1).eq("name", 234))
                .or(new Conditions().eq("id", 2).eq("name", 67))
                .export();
        System.out.println(result.sql);
        System.out.println(JSON.toJSONString(result.params));
    }

}
