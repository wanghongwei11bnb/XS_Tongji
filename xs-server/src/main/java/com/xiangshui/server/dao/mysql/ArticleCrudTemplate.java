package com.xiangshui.server.dao.mysql;

import com.alibaba.fastjson.JSON;
import com.xiangshui.server.crud.assist.Criterion;
import com.xiangshui.server.crud.assist.Example;
import com.xiangshui.server.crud.SinglePrimaryCrudTemplate;
import com.xiangshui.server.domain.mysql.Article;
import com.xiangshui.util.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component()
public class ArticleCrudTemplate extends SinglePrimaryCrudTemplate<Integer, Article> {

    private static final Logger log = LoggerFactory.getLogger(ArticleCrudTemplate.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public String getTableName() {
        return "article";
    }

    @Override
    public String getPrimaryFieldName() {
        return "id";
    }


    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        SpringUtils.init();
        ArticleCrudTemplate articleCrudTemplate = SpringUtils.getBean(ArticleCrudTemplate.class);

        Article article = new Article().setTitle("test").setRelease_time(new Date());
        Example example = new Example();
        example.setColumns("id,title");
        example.getCriteria().addCriterion(Criterion.and(articleCrudTemplate.makeCriterionList(article,new String[]{
                "id",
                "title",
        },false)));
        log.debug(example.getCriteria().makeSql());
    }

}
