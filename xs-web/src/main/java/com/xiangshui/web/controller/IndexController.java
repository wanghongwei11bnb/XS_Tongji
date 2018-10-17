package com.xiangshui.web.controller;

import com.xiangshui.server.crud.assist.Example;
import com.xiangshui.server.dao.mysql.ArticleDao;
import com.xiangshui.server.domain.mysql.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {

    @Autowired
    ArticleDao articleDao;

    @GetMapping(value = {"/", "/index.html"})
    public String index() {
        return "index";
    }


    @GetMapping("/about.html")
    public String about() {
        return "about";
    }


    @GetMapping("/news/list")
    public String news_list(HttpServletRequest request, Integer pageNum) {
        return news_list_n(request, 1);
    }


    @GetMapping("/news/{article_id:\\d+}")
    public String news_detail(HttpServletRequest request, @PathVariable("article_id") Integer article_id) {
        Article article = articleDao.selectByPrimaryKey(article_id, null);
        request.setAttribute("article", article);
        return "news_detail";
    }

    @GetMapping("/news/list/{pageNum:\\d+}")
    public String news_list_n(HttpServletRequest request, @PathVariable("pageNum") Integer pageNum) {
        int pageSize = 6;
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        int skip = pageSize * (pageNum - 1);
        int limit = pageSize;
        Example example = new Example();
        int total = articleDao.countByExample(example);
        example.setOrderByClause("release_time desc , id desc").setSkip(skip).setLimit(limit);
        List<Article> articleList = articleDao.selectByExample(example);
        request.setAttribute("total", total);
        request.setAttribute("articleList", articleList);
        return "news_list";
    }


}
