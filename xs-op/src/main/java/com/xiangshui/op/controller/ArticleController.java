package com.xiangshui.op.controller;

import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.crud.assist.Criterion;
import com.xiangshui.server.crud.assist.Example;
import com.xiangshui.server.dao.mysql.ArticleDao;
import com.xiangshui.server.domain.mysql.Article;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.service.S3Service;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.xiangshui.op.annotation.AuthRequired.*;

@Controller
public class ArticleController extends BaseController {


    @Autowired
    S3Service s3Service;
    @Autowired
    ArticleDao articleDao;

    @Menu(auth_article)
    @AuthRequired(auth_article)
    @GetMapping("/article_manage")
    public String article_manage(HttpServletRequest request) {
        setClient(request);
        return "article_manage";
    }

    @AuthRequired(auth_article)
    @GetMapping("/api/article/search")
    @ResponseBody
    public Result api_article_search(HttpServletRequest request, HttpServletResponse response, Article criteria, Integer pageSize, Integer pageNum) throws IllegalAccessException {
        if (criteria == null) criteria = new Article();
        if (pageSize == null || pageSize < 1) {
            pageSize = 20;
        }
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        int skip = (pageNum - 1) * pageSize;
        int limit = pageSize;
        List<Criterion> criterionList = articleDao.makeCriterionList(criteria, new String[]{}, true);
        if (StringUtils.isNotBlank(criteria.getTitle())) {
            criterionList.add(Criterion.like("title", "%" + criteria.getTitle() + "%"));
        }
        Example example = new Example();
        int total = articleDao.countByExample(example);
        example.setOrderByClause("release_time desc , id desc").setSkip(skip).setLimit(limit);
        List<Article> articleList = articleDao.selectByExample(example);
        return new Result(CodeMsg.SUCCESS)
                .putData("articleList", articleList)
                .putData("total", total);
    }

    public void checkArticle(Article criteria) {
        if (criteria == null) {
            throw new XiangShuiException("参数不能为空");
        }
        if (StringUtils.isBlank(criteria.getTitle())) {
            throw new XiangShuiException("标题不能为空");
        }
        if (criteria.getType() == null) {
            throw new XiangShuiException("参数不能为空");
        }
        if (criteria.getRelease_time() == null) {
            throw new XiangShuiException("发布日期不能为空");
        }
    }


    @AuthRequired(auth_article)
    @GetMapping("/api/article/{article_id:\\d+}")
    @ResponseBody
    public Result api_article_get(HttpServletRequest request, HttpServletResponse response, @PathVariable("article_id") Integer article_id) throws IllegalAccessException, NoSuchFieldException {
        Article article = articleDao.selectByPrimaryKey(article_id, null);
        if (article == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }
        return new Result(CodeMsg.SUCCESS).putData("article", article);
    }


    @AuthRequired(auth_article)
    @PostMapping("/api/article/create")
    @ResponseBody
    public Result api_article_add(HttpServletRequest request, HttpServletResponse response, @RequestBody Article criteria) throws IllegalAccessException, NoSuchFieldException {
        checkArticle(criteria);
        criteria.setId(null);
        Date now = new Date();
        criteria.setCreate_time(now);
        criteria.setUpdate_time(now);
        int n = articleDao.insertSelective(criteria, null);
        if (n <= 0) {
            throw new XiangShuiException("保存失败");
        }
        return new Result(CodeMsg.SUCCESS).putData("article_id", criteria.getId());
    }

    @AuthRequired(auth_article)
    @PostMapping("/api/article/{article_id:\\d+}/update")
    @ResponseBody
    public Result api_article_update(HttpServletRequest request, HttpServletResponse response, @PathVariable("article_id") Integer article_id, @RequestBody Article criteria) throws IllegalAccessException, NoSuchFieldException {
        Article article = articleDao.selectByPrimaryKey(article_id, null);
        if (article == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }
        checkArticle(criteria);
        criteria.setId(article_id);
        Date now = new Date();
        criteria.setUpdate_time(now);
        int n = articleDao.updateByPrimaryKey(criteria, new String[]{
                "title",
                "sub_title",
                "author",
                "summary",
                "type",
                "category",
                "sub_cate",
                "head_img",
                "remark",
                "release_time",
                "content",
        });
        if (n <= 0) {
            throw new XiangShuiException("保存失败");
        }
        return new Result(CodeMsg.SUCCESS);
    }


    @AuthRequired(auth_article)
    @PostMapping("/api/article/img/upload")
    @ResponseBody
    public Result api_article_img_upload(MultipartFile uploadFile) throws IOException {
        uploadFile.getOriginalFilename();
        byte[] bytes = uploadFile.getBytes();
        String contentType = uploadFile.getContentType();
        String imgUrl = s3Service.uploadImage(bytes, contentType);
        if (StringUtils.isNotBlank(imgUrl)) {
            return new Result(CodeMsg.SUCCESS).putData("url", imgUrl);
        } else {
            return new Result(-1, "上传失败");
        }
    }
}
