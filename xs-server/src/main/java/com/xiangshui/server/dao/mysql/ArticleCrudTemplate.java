package com.xiangshui.server.dao.mysql;

import com.alibaba.fastjson.JSON;
import com.xiangshui.server.crud.assist.Example;
import com.xiangshui.server.crud.SinglePrimaryCrudTemplate;
import com.xiangshui.server.domain.mysql.Article;
import com.xiangshui.util.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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

//        Article article = new Article().setTitle("test").setRelease_time(new Date());
//        articleCrudTemplate.insertSelective(article, null);
        Article article = articleCrudTemplate.selectByPrimaryKey(1, "id,title");
        log.debug(JSON.toJSONString(article));
//        article.setTitle("12313123");
//        articleCrudTemplate.updateByPrimaryKeySelective(article, null);
        Example example = new Example();
        example.setColumns("id,title");
//        example.getCriteria().addCriterion(Criterion.eq("title", "12313123"));
        log.debug(JSON.toJSONString(articleCrudTemplate.selectByExample(example)));
    }

}
