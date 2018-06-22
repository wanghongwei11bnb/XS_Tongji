package com.xiangshui.server.example;

import java.util.ArrayList;
import java.util.List;

public class OpExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    private int skip = 0;

    private int limit = 5000;

    private String fields;

    public OpExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public void setSkip(int skip) {
        this.skip=skip;
    }

    public void setLimit(int limit) {
        this.limit=limit;
    }

    public int getSkip() {
        return this.skip;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setFields(String fields) {
        this.fields=fields;
    }

    public String getFields() {
        return this.fields;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        public void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        public void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        public void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andUsernameIsNull() {
            addCriterion("username is null");
            return (Criteria) this;
        }

        public Criteria andUsernameIsNotNull() {
            addCriterion("username is not null");
            return (Criteria) this;
        }

        public Criteria andUsernameEqualTo(String value) {
            addCriterion("username =", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotEqualTo(String value) {
            addCriterion("username <>", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameGreaterThan(String value) {
            addCriterion("username >", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameGreaterThanOrEqualTo(String value) {
            addCriterion("username >=", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLessThan(String value) {
            addCriterion("username <", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLessThanOrEqualTo(String value) {
            addCriterion("username <=", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLike(String value) {
            addCriterion("username like", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotLike(String value) {
            addCriterion("username not like", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameIn(List<String> values) {
            addCriterion("username in", values, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotIn(List<String> values) {
            addCriterion("username not in", values, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameBetween(String value1, String value2) {
            addCriterion("username between", value1, value2, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotBetween(String value1, String value2) {
            addCriterion("username not between", value1, value2, "username");
            return (Criteria) this;
        }

        public Criteria andPasswordIsNull() {
            addCriterion("password is null");
            return (Criteria) this;
        }

        public Criteria andPasswordIsNotNull() {
            addCriterion("password is not null");
            return (Criteria) this;
        }

        public Criteria andPasswordEqualTo(String value) {
            addCriterion("password =", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotEqualTo(String value) {
            addCriterion("password <>", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordGreaterThan(String value) {
            addCriterion("password >", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordGreaterThanOrEqualTo(String value) {
            addCriterion("password >=", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordLessThan(String value) {
            addCriterion("password <", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordLessThanOrEqualTo(String value) {
            addCriterion("password <=", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordLike(String value) {
            addCriterion("password like", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotLike(String value) {
            addCriterion("password not like", value, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordIn(List<String> values) {
            addCriterion("password in", values, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotIn(List<String> values) {
            addCriterion("password not in", values, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordBetween(String value1, String value2) {
            addCriterion("password between", value1, value2, "password");
            return (Criteria) this;
        }

        public Criteria andPasswordNotBetween(String value1, String value2) {
            addCriterion("password not between", value1, value2, "password");
            return (Criteria) this;
        }

        public Criteria andRealnameIsNull() {
            addCriterion("realname is null");
            return (Criteria) this;
        }

        public Criteria andRealnameIsNotNull() {
            addCriterion("realname is not null");
            return (Criteria) this;
        }

        public Criteria andRealnameEqualTo(String value) {
            addCriterion("realname =", value, "realname");
            return (Criteria) this;
        }

        public Criteria andRealnameNotEqualTo(String value) {
            addCriterion("realname <>", value, "realname");
            return (Criteria) this;
        }

        public Criteria andRealnameGreaterThan(String value) {
            addCriterion("realname >", value, "realname");
            return (Criteria) this;
        }

        public Criteria andRealnameGreaterThanOrEqualTo(String value) {
            addCriterion("realname >=", value, "realname");
            return (Criteria) this;
        }

        public Criteria andRealnameLessThan(String value) {
            addCriterion("realname <", value, "realname");
            return (Criteria) this;
        }

        public Criteria andRealnameLessThanOrEqualTo(String value) {
            addCriterion("realname <=", value, "realname");
            return (Criteria) this;
        }

        public Criteria andRealnameLike(String value) {
            addCriterion("realname like", value, "realname");
            return (Criteria) this;
        }

        public Criteria andRealnameNotLike(String value) {
            addCriterion("realname not like", value, "realname");
            return (Criteria) this;
        }

        public Criteria andRealnameIn(List<String> values) {
            addCriterion("realname in", values, "realname");
            return (Criteria) this;
        }

        public Criteria andRealnameNotIn(List<String> values) {
            addCriterion("realname not in", values, "realname");
            return (Criteria) this;
        }

        public Criteria andRealnameBetween(String value1, String value2) {
            addCriterion("realname between", value1, value2, "realname");
            return (Criteria) this;
        }

        public Criteria andRealnameNotBetween(String value1, String value2) {
            addCriterion("realname not between", value1, value2, "realname");
            return (Criteria) this;
        }

        public Criteria andAreasIsNull() {
            addCriterion("areas is null");
            return (Criteria) this;
        }

        public Criteria andAreasIsNotNull() {
            addCriterion("areas is not null");
            return (Criteria) this;
        }

        public Criteria andAreasEqualTo(String value) {
            addCriterion("areas =", value, "areas");
            return (Criteria) this;
        }

        public Criteria andAreasNotEqualTo(String value) {
            addCriterion("areas <>", value, "areas");
            return (Criteria) this;
        }

        public Criteria andAreasGreaterThan(String value) {
            addCriterion("areas >", value, "areas");
            return (Criteria) this;
        }

        public Criteria andAreasGreaterThanOrEqualTo(String value) {
            addCriterion("areas >=", value, "areas");
            return (Criteria) this;
        }

        public Criteria andAreasLessThan(String value) {
            addCriterion("areas <", value, "areas");
            return (Criteria) this;
        }

        public Criteria andAreasLessThanOrEqualTo(String value) {
            addCriterion("areas <=", value, "areas");
            return (Criteria) this;
        }

        public Criteria andAreasLike(String value) {
            addCriterion("areas like", value, "areas");
            return (Criteria) this;
        }

        public Criteria andAreasNotLike(String value) {
            addCriterion("areas not like", value, "areas");
            return (Criteria) this;
        }

        public Criteria andAreasIn(List<String> values) {
            addCriterion("areas in", values, "areas");
            return (Criteria) this;
        }

        public Criteria andAreasNotIn(List<String> values) {
            addCriterion("areas not in", values, "areas");
            return (Criteria) this;
        }

        public Criteria andAreasBetween(String value1, String value2) {
            addCriterion("areas between", value1, value2, "areas");
            return (Criteria) this;
        }

        public Criteria andAreasNotBetween(String value1, String value2) {
            addCriterion("areas not between", value1, value2, "areas");
            return (Criteria) this;
        }

        public Criteria andOperateExtendIsNull() {
            addCriterion("operate_extend is null");
            return (Criteria) this;
        }

        public Criteria andOperateExtendIsNotNull() {
            addCriterion("operate_extend is not null");
            return (Criteria) this;
        }

        public Criteria andOperateExtendEqualTo(String value) {
            addCriterion("operate_extend =", value, "operateExtend");
            return (Criteria) this;
        }

        public Criteria andOperateExtendNotEqualTo(String value) {
            addCriterion("operate_extend <>", value, "operateExtend");
            return (Criteria) this;
        }

        public Criteria andOperateExtendGreaterThan(String value) {
            addCriterion("operate_extend >", value, "operateExtend");
            return (Criteria) this;
        }

        public Criteria andOperateExtendGreaterThanOrEqualTo(String value) {
            addCriterion("operate_extend >=", value, "operateExtend");
            return (Criteria) this;
        }

        public Criteria andOperateExtendLessThan(String value) {
            addCriterion("operate_extend <", value, "operateExtend");
            return (Criteria) this;
        }

        public Criteria andOperateExtendLessThanOrEqualTo(String value) {
            addCriterion("operate_extend <=", value, "operateExtend");
            return (Criteria) this;
        }

        public Criteria andOperateExtendLike(String value) {
            addCriterion("operate_extend like", value, "operateExtend");
            return (Criteria) this;
        }

        public Criteria andOperateExtendNotLike(String value) {
            addCriterion("operate_extend not like", value, "operateExtend");
            return (Criteria) this;
        }

        public Criteria andOperateExtendIn(List<String> values) {
            addCriterion("operate_extend in", values, "operateExtend");
            return (Criteria) this;
        }

        public Criteria andOperateExtendNotIn(List<String> values) {
            addCriterion("operate_extend not in", values, "operateExtend");
            return (Criteria) this;
        }

        public Criteria andOperateExtendBetween(String value1, String value2) {
            addCriterion("operate_extend between", value1, value2, "operateExtend");
            return (Criteria) this;
        }

        public Criteria andOperateExtendNotBetween(String value1, String value2) {
            addCriterion("operate_extend not between", value1, value2, "operateExtend");
            return (Criteria) this;
        }

        public Criteria andOperateRemoveIsNull() {
            addCriterion("operate_remove is null");
            return (Criteria) this;
        }

        public Criteria andOperateRemoveIsNotNull() {
            addCriterion("operate_remove is not null");
            return (Criteria) this;
        }

        public Criteria andOperateRemoveEqualTo(String value) {
            addCriterion("operate_remove =", value, "operateRemove");
            return (Criteria) this;
        }

        public Criteria andOperateRemoveNotEqualTo(String value) {
            addCriterion("operate_remove <>", value, "operateRemove");
            return (Criteria) this;
        }

        public Criteria andOperateRemoveGreaterThan(String value) {
            addCriterion("operate_remove >", value, "operateRemove");
            return (Criteria) this;
        }

        public Criteria andOperateRemoveGreaterThanOrEqualTo(String value) {
            addCriterion("operate_remove >=", value, "operateRemove");
            return (Criteria) this;
        }

        public Criteria andOperateRemoveLessThan(String value) {
            addCriterion("operate_remove <", value, "operateRemove");
            return (Criteria) this;
        }

        public Criteria andOperateRemoveLessThanOrEqualTo(String value) {
            addCriterion("operate_remove <=", value, "operateRemove");
            return (Criteria) this;
        }

        public Criteria andOperateRemoveLike(String value) {
            addCriterion("operate_remove like", value, "operateRemove");
            return (Criteria) this;
        }

        public Criteria andOperateRemoveNotLike(String value) {
            addCriterion("operate_remove not like", value, "operateRemove");
            return (Criteria) this;
        }

        public Criteria andOperateRemoveIn(List<String> values) {
            addCriterion("operate_remove in", values, "operateRemove");
            return (Criteria) this;
        }

        public Criteria andOperateRemoveNotIn(List<String> values) {
            addCriterion("operate_remove not in", values, "operateRemove");
            return (Criteria) this;
        }

        public Criteria andOperateRemoveBetween(String value1, String value2) {
            addCriterion("operate_remove between", value1, value2, "operateRemove");
            return (Criteria) this;
        }

        public Criteria andOperateRemoveNotBetween(String value1, String value2) {
            addCriterion("operate_remove not between", value1, value2, "operateRemove");
            return (Criteria) this;
        }

        public Criteria andAllOperatesIsNull() {
            addCriterion("all_operates is null");
            return (Criteria) this;
        }

        public Criteria andAllOperatesIsNotNull() {
            addCriterion("all_operates is not null");
            return (Criteria) this;
        }

        public Criteria andAllOperatesEqualTo(String value) {
            addCriterion("all_operates =", value, "allOperates");
            return (Criteria) this;
        }

        public Criteria andAllOperatesNotEqualTo(String value) {
            addCriterion("all_operates <>", value, "allOperates");
            return (Criteria) this;
        }

        public Criteria andAllOperatesGreaterThan(String value) {
            addCriterion("all_operates >", value, "allOperates");
            return (Criteria) this;
        }

        public Criteria andAllOperatesGreaterThanOrEqualTo(String value) {
            addCriterion("all_operates >=", value, "allOperates");
            return (Criteria) this;
        }

        public Criteria andAllOperatesLessThan(String value) {
            addCriterion("all_operates <", value, "allOperates");
            return (Criteria) this;
        }

        public Criteria andAllOperatesLessThanOrEqualTo(String value) {
            addCriterion("all_operates <=", value, "allOperates");
            return (Criteria) this;
        }

        public Criteria andAllOperatesLike(String value) {
            addCriterion("all_operates like", value, "allOperates");
            return (Criteria) this;
        }

        public Criteria andAllOperatesNotLike(String value) {
            addCriterion("all_operates not like", value, "allOperates");
            return (Criteria) this;
        }

        public Criteria andAllOperatesIn(List<String> values) {
            addCriterion("all_operates in", values, "allOperates");
            return (Criteria) this;
        }

        public Criteria andAllOperatesNotIn(List<String> values) {
            addCriterion("all_operates not in", values, "allOperates");
            return (Criteria) this;
        }

        public Criteria andAllOperatesBetween(String value1, String value2) {
            addCriterion("all_operates between", value1, value2, "allOperates");
            return (Criteria) this;
        }

        public Criteria andAllOperatesNotBetween(String value1, String value2) {
            addCriterion("all_operates not between", value1, value2, "allOperates");
            return (Criteria) this;
        }

        public Criteria andRolesIsNull() {
            addCriterion("roles is null");
            return (Criteria) this;
        }

        public Criteria andRolesIsNotNull() {
            addCriterion("roles is not null");
            return (Criteria) this;
        }

        public Criteria andRolesEqualTo(String value) {
            addCriterion("roles =", value, "roles");
            return (Criteria) this;
        }

        public Criteria andRolesNotEqualTo(String value) {
            addCriterion("roles <>", value, "roles");
            return (Criteria) this;
        }

        public Criteria andRolesGreaterThan(String value) {
            addCriterion("roles >", value, "roles");
            return (Criteria) this;
        }

        public Criteria andRolesGreaterThanOrEqualTo(String value) {
            addCriterion("roles >=", value, "roles");
            return (Criteria) this;
        }

        public Criteria andRolesLessThan(String value) {
            addCriterion("roles <", value, "roles");
            return (Criteria) this;
        }

        public Criteria andRolesLessThanOrEqualTo(String value) {
            addCriterion("roles <=", value, "roles");
            return (Criteria) this;
        }

        public Criteria andRolesLike(String value) {
            addCriterion("roles like", value, "roles");
            return (Criteria) this;
        }

        public Criteria andRolesNotLike(String value) {
            addCriterion("roles not like", value, "roles");
            return (Criteria) this;
        }

        public Criteria andRolesIn(List<String> values) {
            addCriterion("roles in", values, "roles");
            return (Criteria) this;
        }

        public Criteria andRolesNotIn(List<String> values) {
            addCriterion("roles not in", values, "roles");
            return (Criteria) this;
        }

        public Criteria andRolesBetween(String value1, String value2) {
            addCriterion("roles between", value1, value2, "roles");
            return (Criteria) this;
        }

        public Criteria andRolesNotBetween(String value1, String value2) {
            addCriterion("roles not between", value1, value2, "roles");
            return (Criteria) this;
        }

        public Criteria andAuthsIsNull() {
            addCriterion("auths is null");
            return (Criteria) this;
        }

        public Criteria andAuthsIsNotNull() {
            addCriterion("auths is not null");
            return (Criteria) this;
        }

        public Criteria andAuthsEqualTo(String value) {
            addCriterion("auths =", value, "auths");
            return (Criteria) this;
        }

        public Criteria andAuthsNotEqualTo(String value) {
            addCriterion("auths <>", value, "auths");
            return (Criteria) this;
        }

        public Criteria andAuthsGreaterThan(String value) {
            addCriterion("auths >", value, "auths");
            return (Criteria) this;
        }

        public Criteria andAuthsGreaterThanOrEqualTo(String value) {
            addCriterion("auths >=", value, "auths");
            return (Criteria) this;
        }

        public Criteria andAuthsLessThan(String value) {
            addCriterion("auths <", value, "auths");
            return (Criteria) this;
        }

        public Criteria andAuthsLessThanOrEqualTo(String value) {
            addCriterion("auths <=", value, "auths");
            return (Criteria) this;
        }

        public Criteria andAuthsLike(String value) {
            addCriterion("auths like", value, "auths");
            return (Criteria) this;
        }

        public Criteria andAuthsNotLike(String value) {
            addCriterion("auths not like", value, "auths");
            return (Criteria) this;
        }

        public Criteria andAuthsIn(List<String> values) {
            addCriterion("auths in", values, "auths");
            return (Criteria) this;
        }

        public Criteria andAuthsNotIn(List<String> values) {
            addCriterion("auths not in", values, "auths");
            return (Criteria) this;
        }

        public Criteria andAuthsBetween(String value1, String value2) {
            addCriterion("auths between", value1, value2, "auths");
            return (Criteria) this;
        }

        public Criteria andAuthsNotBetween(String value1, String value2) {
            addCriterion("auths not between", value1, value2, "auths");
            return (Criteria) this;
        }

        public Criteria andCitysIsNull() {
            addCriterion("citys is null");
            return (Criteria) this;
        }

        public Criteria andCitysIsNotNull() {
            addCriterion("citys is not null");
            return (Criteria) this;
        }

        public Criteria andCitysEqualTo(String value) {
            addCriterion("citys =", value, "citys");
            return (Criteria) this;
        }

        public Criteria andCitysNotEqualTo(String value) {
            addCriterion("citys <>", value, "citys");
            return (Criteria) this;
        }

        public Criteria andCitysGreaterThan(String value) {
            addCriterion("citys >", value, "citys");
            return (Criteria) this;
        }

        public Criteria andCitysGreaterThanOrEqualTo(String value) {
            addCriterion("citys >=", value, "citys");
            return (Criteria) this;
        }

        public Criteria andCitysLessThan(String value) {
            addCriterion("citys <", value, "citys");
            return (Criteria) this;
        }

        public Criteria andCitysLessThanOrEqualTo(String value) {
            addCriterion("citys <=", value, "citys");
            return (Criteria) this;
        }

        public Criteria andCitysLike(String value) {
            addCriterion("citys like", value, "citys");
            return (Criteria) this;
        }

        public Criteria andCitysNotLike(String value) {
            addCriterion("citys not like", value, "citys");
            return (Criteria) this;
        }

        public Criteria andCitysIn(List<String> values) {
            addCriterion("citys in", values, "citys");
            return (Criteria) this;
        }

        public Criteria andCitysNotIn(List<String> values) {
            addCriterion("citys not in", values, "citys");
            return (Criteria) this;
        }

        public Criteria andCitysBetween(String value1, String value2) {
            addCriterion("citys between", value1, value2, "citys");
            return (Criteria) this;
        }

        public Criteria andCitysNotBetween(String value1, String value2) {
            addCriterion("citys not between", value1, value2, "citys");
            return (Criteria) this;
        }

        public Criteria andFullnameIsNull() {
            addCriterion("fullname is null");
            return (Criteria) this;
        }

        public Criteria andFullnameIsNotNull() {
            addCriterion("fullname is not null");
            return (Criteria) this;
        }

        public Criteria andFullnameEqualTo(String value) {
            addCriterion("fullname =", value, "fullname");
            return (Criteria) this;
        }

        public Criteria andFullnameNotEqualTo(String value) {
            addCriterion("fullname <>", value, "fullname");
            return (Criteria) this;
        }

        public Criteria andFullnameGreaterThan(String value) {
            addCriterion("fullname >", value, "fullname");
            return (Criteria) this;
        }

        public Criteria andFullnameGreaterThanOrEqualTo(String value) {
            addCriterion("fullname >=", value, "fullname");
            return (Criteria) this;
        }

        public Criteria andFullnameLessThan(String value) {
            addCriterion("fullname <", value, "fullname");
            return (Criteria) this;
        }

        public Criteria andFullnameLessThanOrEqualTo(String value) {
            addCriterion("fullname <=", value, "fullname");
            return (Criteria) this;
        }

        public Criteria andFullnameLike(String value) {
            addCriterion("fullname like", value, "fullname");
            return (Criteria) this;
        }

        public Criteria andFullnameNotLike(String value) {
            addCriterion("fullname not like", value, "fullname");
            return (Criteria) this;
        }

        public Criteria andFullnameIn(List<String> values) {
            addCriterion("fullname in", values, "fullname");
            return (Criteria) this;
        }

        public Criteria andFullnameNotIn(List<String> values) {
            addCriterion("fullname not in", values, "fullname");
            return (Criteria) this;
        }

        public Criteria andFullnameBetween(String value1, String value2) {
            addCriterion("fullname between", value1, value2, "fullname");
            return (Criteria) this;
        }

        public Criteria andFullnameNotBetween(String value1, String value2) {
            addCriterion("fullname not between", value1, value2, "fullname");
            return (Criteria) this;
        }

        public Criteria andCityIsNull() {
            addCriterion("city is null");
            return (Criteria) this;
        }

        public Criteria andCityIsNotNull() {
            addCriterion("city is not null");
            return (Criteria) this;
        }

        public Criteria andCityEqualTo(String value) {
            addCriterion("city =", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityNotEqualTo(String value) {
            addCriterion("city <>", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityGreaterThan(String value) {
            addCriterion("city >", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityGreaterThanOrEqualTo(String value) {
            addCriterion("city >=", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityLessThan(String value) {
            addCriterion("city <", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityLessThanOrEqualTo(String value) {
            addCriterion("city <=", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityLike(String value) {
            addCriterion("city like", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityNotLike(String value) {
            addCriterion("city not like", value, "city");
            return (Criteria) this;
        }

        public Criteria andCityIn(List<String> values) {
            addCriterion("city in", values, "city");
            return (Criteria) this;
        }

        public Criteria andCityNotIn(List<String> values) {
            addCriterion("city not in", values, "city");
            return (Criteria) this;
        }

        public Criteria andCityBetween(String value1, String value2) {
            addCriterion("city between", value1, value2, "city");
            return (Criteria) this;
        }

        public Criteria andCityNotBetween(String value1, String value2) {
            addCriterion("city not between", value1, value2, "city");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}