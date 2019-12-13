package com.xiangshui.web.scheduled;

import com.xiangshui.server.dao.mysql.ArticleDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ArticleScheduled {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ArticleDao articleDao;

    public volatile int countArticle = 0;

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void updateCountArticle() {
        countArticle = articleDao.countByConditions(null);
    }

}
