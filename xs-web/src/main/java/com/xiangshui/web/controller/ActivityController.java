package com.xiangshui.web.controller;

import com.xiangshui.server.crud.assist.Criterion;
import com.xiangshui.server.crud.assist.Example;
import com.xiangshui.server.domain.SwiperItem;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ActivityController extends BaseController {
    @GetMapping("/jpi/swiperItem/search")
    @ResponseBody
    public Result swiper_item_search() {
        Example example = new Example().setOrderByClause("sort_num desc,create_time desc,id desc");
        example.getCriteria()
                .addCriterion(Criterion.eq("status", 1))
                .addCriterion(Criterion.eq("app", "ali"))
        ;
        List<SwiperItem> swiperItemList = swiperItemDao.selectByExample(example);
        return new Result(CodeMsg.SUCCESS).putData("swiperItemList", swiperItemList);
    }

}
