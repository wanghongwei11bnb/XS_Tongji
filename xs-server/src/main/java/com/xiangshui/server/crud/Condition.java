package com.xiangshui.server.crud;

import java.util.List;

public class Condition {

    public final Type type;
    public final String column;
    public final Object value;
    public final Object secondValue;
    public final List listValue;
    public final Conditions conditions;

    public Condition(Type type, String column, Object value, Object secondValue, List listValue, Conditions conditions) {
        this.type = type;
        this.column = column;
        this.value = value;
        this.secondValue = secondValue;
        this.listValue = listValue;
        this.conditions = conditions;
    }

    public void export(ConditionsResult result) {
        switch (type) {
            case single:
                result.sql.append(column);
                return;
            case eq:
                result.sql.append(column).append(Sql.EQ).append(Sql.MARK);
                result.params.add(value);
                return;
            case not_eq:
                result.sql.append(column).append(Sql.NOT_EQ).append(Sql.MARK);
                result.params.add(value);
                break;
            case is_null:
                result.sql.append(column).append(Sql.IS_NULL);
                break;
            case is_not_null:
                result.sql.append(column).append(Sql.IS_NOT_NULL);
                break;
            case gt:
                result.sql.append(column).append(Sql.GT).append(Sql.MARK);
                result.params.add(value);
                break;
            case gte:
                result.sql.append(column).append(Sql.GTE).append(Sql.MARK);
                result.params.add(value);
                break;
            case lt:
                result.sql.append(column).append(Sql.LT).append(Sql.MARK);
                result.params.add(value);
                break;
            case lte:
                result.sql.append(column).append(Sql.LTE).append(Sql.MARK);
                result.params.add(value);
                break;
            case like:
                result.sql.append(column).append(Sql.LIKE).append(Sql.MARK);
                result.params.add(value);
                break;
            case not_like:
                result.sql.append(column).append(Sql.NOT_LIKE).append(Sql.MARK);
                result.params.add(value);
                break;
            case between:
                result.sql.append(column).append(Sql.BETWEEN).append(Sql.MARK).append(Sql.AND).append(Sql.MARK);
                result.params.add(value);
                result.params.add(secondValue);
                break;
            case in:
            case not_in:
                result.sql.append(column);
                result.sql.append(type == Type.in ? Sql.IN : Sql.NOT_IN);
                result.sql.append(Sql.BL);
                for (int i = 0; i < listValue.size(); i++) {
                    if (i > 0) {
                        result.sql.append(Sql.COMMA);
                    }
                    result.sql.append(Sql.MARK);
                    result.params.add(listValue.get(i));
                }
                result.sql.append(Sql.BR);
                break;
            case and:
            case or:
                result.sql.append(Sql.BL);
                conditions.export(result);
                result.sql.append(Sql.BR);
                break;
            default:
                break;
        }
    }


    public static enum Type {
        single,
        eq, not_eq,
        gt, gte,
        lt, lte,
        like, not_like,
        between,
        in, not_in,
        is_null, is_not_null,
        and, or,
    }
}
