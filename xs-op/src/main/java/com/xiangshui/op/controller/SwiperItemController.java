package com.xiangshui.op.controller;

import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.crud.Condition;
import com.xiangshui.server.crud.Example;
import com.xiangshui.server.domain.SwiperItem;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class SwiperItemController extends BaseController {


//    @Menu(value = "首页Banner管理")
//    @AuthRequired("首页Banner管理")
    @GetMapping("/banner_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "banner_manage";
    }


    @GetMapping("/api/swiperItem/search")
    @ResponseBody
    public Result search(SwiperItem query) throws IllegalAccessException {
        if (query == null) query = new SwiperItem();
        Example example = new Example().setOrderByClause("sort_num desc,create_time desc,id desc");
        List<Condition> conditionList = swiperItemDao.makeConditionList(query, new String[]{
                "id",
                "status",
                "app",
        }, true);
        if (conditionList != null && conditionList.size() > 0) {
            example.getConditions().conditionList.addAll(conditionList);
        }

        if (StringUtils.isNotBlank(query.getTitle())) {
            example.getConditions().like("title", "%" + query.getTitle() + "%");
        }
        if (StringUtils.isNotBlank(query.getSub_title())) {
            example.getConditions().like("sub_title", "%" + query.getSub_title() + "%");
        }

        List<SwiperItem> swiperItemList = swiperItemDao.selectByExample(example);
        return new Result(CodeMsg.SUCCESS).putData("swiperItemList", swiperItemList);
    }

    @PostMapping("/api/swiperItem/create")
    @ResponseBody
    public Result create(SwiperItem query) throws IllegalAccessException, NoSuchFieldException {
        if (query == null) return new Result(-1, "缺少参数");
        if (StringUtils.isBlank(query.getImg())) return new Result(-1, "缺少图片网址");
        if (query.getSort_num() == null) query.setSort_num((int) (System.currentTimeMillis() / 1000));
        if (swiperItemDao.insertSelective(query, null) > 0) {
            return new Result(CodeMsg.SUCCESS).putData("swiperItem", query);
        } else {
            return new Result(-1, "保存失败");
        }
    }


    @PostMapping("/api/swiperItem/{id:\\d+}/update")
    @ResponseBody
    public Result update(@PathVariable("id") Integer id, SwiperItem query) throws IllegalAccessException, NoSuchFieldException {
        SwiperItem swiperItem = swiperItemDao.selectByPrimaryKey(id, null);
        if (swiperItem == null) return new Result(CodeMsg.NO_FOUND);
        if (query == null) return new Result(-1, "缺少参数");
        if (StringUtils.isBlank(query.getImg())) return new Result(-1, "缺少图片网址");
        if (query.getSort_num() == null) query.setSort_num((int) (System.currentTimeMillis() / 1000));
        query.setId(id);
        if (query.getSort_num() == null) query.setSort_num((int) (System.currentTimeMillis() / 1000));
        if (swiperItemDao.updateByPrimaryKey(query, new String[]{
                "title",
                "sub_title",
                "img",
                "link",
                "status",
                "sort_num",
                "app",
        }) > 0) {
            return new Result(CodeMsg.SUCCESS).putData("swiperItem", query);
        } else {
            return new Result(-1, "保存失败");
        }
    }

    @PostMapping("/api/swiperItem/{id:\\d+}/delete")
    @ResponseBody
    public Result delete(@PathVariable("id") Integer id) throws IllegalAccessException, NoSuchFieldException {
        SwiperItem swiperItem = swiperItemDao.selectByPrimaryKey(id, null);
        if (swiperItem == null) return new Result(CodeMsg.NO_FOUND);
        if (swiperItemDao.deleteByPrimaryKey(id) > 0) {
            return new Result(CodeMsg.SUCCESS);
        } else {
            return new Result(-1, "删除失败");
        }
    }


    @GetMapping("/api/swiperItem/{id:\\d+}")
    @ResponseBody
    public Result get(@PathVariable("id") Integer id) throws IllegalAccessException, NoSuchFieldException {
        SwiperItem swiperItem = swiperItemDao.selectByPrimaryKey(id, null);
        if (swiperItem == null) return new Result(CodeMsg.NO_FOUND);
        return new Result(CodeMsg.SUCCESS).putData("swiperItem", swiperItem);
    }


}
