package com.xiangshui.web.controller;

import com.xiangshui.server.crud.assist.Example;
import com.xiangshui.server.dao.mysql.ArticleDao;
import com.xiangshui.server.dao.mysql.HomeMediaDao;
import com.xiangshui.server.domain.mysql.Article;
import com.xiangshui.server.domain.mysql.HomeMedia;
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
    @Autowired
    HomeMediaDao homeMediaDao;

    @GetMapping({"/iframe"})
    public String iframe() {
        return "iframe";
    }

    @GetMapping({"/", "/index.html"})
    public String index(HttpServletRequest request) {
        setClient(request);
        Example example = new Example();
        example.setOrderByClause("serial desc,id desc");
        example.setSkip(0).setLimit(5);
        List<HomeMedia> homeMediaList = homeMediaDao.selectByExample(example);
        request.setAttribute("homeMediaList", homeMediaList);
        return "index";
    }


    @GetMapping({"/about", "/about.html"})
    public String about(HttpServletRequest request) {
        setClient(request);
        return "about";
    }


    @GetMapping("/news/list")
    public String news_list(HttpServletRequest request, Integer pageNum) {
        request.setAttribute("pageNum", 1);
        return news_list_n(request, 1);
    }


    @GetMapping("/news/list/{pageNum:\\d+}")
    public String news_list_n(HttpServletRequest request, @PathVariable("pageNum") Integer pageNum) {
        setClient(request);
        int pageSize = 6;
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        int skip = pageSize * (pageNum - 1);
        Example example = new Example();
        int total = articleDao.countByExample(example);
        example.setOrderByClause("release_time desc , id desc").setSkip(skip).setLimit(pageSize);
        List<Article> articleList = articleDao.selectByExample(example);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("total", total);
        request.setAttribute("articleList", articleList);
        return "news_list";
    }

    @GetMapping("/news/{article_id:\\d+}")
    public String news_detail(HttpServletRequest request, @PathVariable("article_id") Integer article_id) {
        setClient(request);
        Article article = articleDao.selectByPrimaryKey(article_id, null);
        request.setAttribute("article", article);
        return "news_detail";
    }

}
