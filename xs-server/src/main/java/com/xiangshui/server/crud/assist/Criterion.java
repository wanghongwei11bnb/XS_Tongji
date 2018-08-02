package com.xiangshui.server.crud.assist;

import java.util.Arrays;
import java.util.List;

public class Criterion {
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
        return new Criterion(CriterionType.or, null, null, null, null, whereItemList);
    }

    public static Criterion or(Criterion... whereItems) {
        return or(Arrays.asList(whereItems));
    }

    public static Criterion and(List<Criterion> whereItemList) {
        return new Criterion(CriterionType.and, null, null, null, null, whereItemList);
    }

    public static Criterion and(Criterion... whereItems) {
        return and(Arrays.asList(whereItems));
    }

    public static Criterion single(String column) {
        return new Criterion(CriterionType.eq, column, null, null, null, null);
    }

    public static Criterion is_null(String column) {
        return new Criterion(CriterionType.is_null, column, null, null, null, null);
    }

    public static Criterion is_not_null(String column) {
        return new Criterion(CriterionType.is_null, column, null, null, null, null);
    }


    public static Criterion eq(String column, Object value) {
        return new Criterion(CriterionType.eq, column, value, null, null, null);
    }

    public static Criterion not_eq(String column, Object value) {
        return new Criterion(CriterionType.not_eq, column, value, null, null, null);
    }

    public static Criterion gt(String column, Object value) {
        return new Criterion(CriterionType.gt, column, value, null, null, null);
    }

    public static Criterion gte(String column, Object value) {
        return new Criterion(CriterionType.gte, column, value, null, null, null);
    }

    public static Criterion lt(String column, Object value) {
        return new Criterion(CriterionType.lt, column, value, null, null, null);
    }

    public static Criterion lte(String column, Object value) {
        return new Criterion(CriterionType.lte, column, value, null, null, null);
    }

    public static Criterion like(String column, Object value) {
        return new Criterion(CriterionType.like, column, value, null, null, null);
    }

    public static Criterion not_like(String column, Object value) {
        return new Criterion(CriterionType.not_like, column, value, null, null, null);
    }

    public static Criterion between(String column, Object value, Object secondValue) {
        return new Criterion(CriterionType.between, column, value, secondValue, null, null);
    }

    public static Criterion in(String column, List listValue) {
        return new Criterion(CriterionType.in, column, null, null, listValue, null);
    }

    public static Criterion not_in(String column, List listValue) {
        return new Criterion(CriterionType.not_in, column, null, null, listValue, null);
    }
}
