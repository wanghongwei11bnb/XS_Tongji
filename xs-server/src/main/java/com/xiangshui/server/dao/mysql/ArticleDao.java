package com.xiangshui.server.dao.mysql;

import com.xiangshui.server.crud.SinglePrimaryCrudTemplate;
import com.xiangshui.server.domain.mysql.Article;
import com.xiangshui.util.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component()
public class ArticleDao extends SinglePrimaryCrudTemplate<Article, Integer> {

    private static final Logger log = LoggerFactory.getLogger(ArticleDao.class);

    @Autowired
    JdbcTemplate jdbcTemplate;


    public ArticleDao() {
        this.primaryAutoIncr = true;
    }

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

}
