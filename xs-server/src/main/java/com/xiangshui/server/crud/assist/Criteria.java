package com.xiangshui.server.crud.assist;

import java.util.ArrayList;
import java.util.List;

public class Criteria {
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
                if (criterion.type == CriterionType.or) {
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
                    sql.append(criterion.type == CriterionType.in ? Sql.IN : Sql.NIN);
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
                case eq:
                case not_eq:
                case gt:
                case gte:
                case lt:
                case lte:
                case like:
                case not_like:
                    paramList.add(criterion.value);
                    break;
                case between:
                    paramList.add(criterion.value);
                    paramList.add(criterion.secondValue);
                    break;
                case in:
                case not_in:
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

