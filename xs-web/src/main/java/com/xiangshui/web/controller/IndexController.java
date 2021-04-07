package com.xiangshui.web.controller;

import com.xiangshui.server.crud.Example;
import com.xiangshui.server.dao.MonthCardRecodeDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.dao.UserWalletDao;
import com.xiangshui.server.dao.mysql.ArticleDao;
import com.xiangshui.server.dao.mysql.HomeMediaDao;
import com.xiangshui.server.domain.MonthCardRecode;
import com.xiangshui.server.domain.mysql.Article;
import com.xiangshui.server.domain.mysql.HomeMedia;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.service.MonthCardService;
import com.xiangshui.server.service.UserService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import com.xiangshui.web.Other;
import com.xiangshui.web.weixin.FluentMap;
import com.xiangshui.web.weixin.PayUtils;
import com.xiangshui.web.weixin.UUID;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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


    @GetMapping({"/buy_month_card", "/buy_month_card.html"})
    public String buy_month_card(HttpServletRequest request) {
        setClient(request);
        return "buy_month_card";
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
        int total = articleDao.countByConditions(null);
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


    @Value("${weixin.gzh.AppID}")
    String weixin_gzh_AppID;

    @Value("${weixin.gzh.AppSecret}")
    String weixin_gzh_AppSecret;

    @Value("${weixin.mch_id}")
    String mch_id;

    @Value("${weixin.key}")
    String key;

    @Value("${notify_url}")
    String notify_url;


    @Autowired
    UserInfoDao userInfoDao;

    @Autowired
    UserWalletDao userWalletDao;


    @Autowired
    MonthCardRecodeDao monthCardRecodeDao;


    @Autowired
    MonthCardService monthCardService;

    @Autowired
    UserService userService;


    @PostMapping("/jpi/buy_month_card")
    @ResponseBody
    public Result buy_month_card(Integer mode, String phone) throws IOException {

        if (mode == null) throw new XiangShuiException("参数错误");
        if (mode != 1 && mode != 3) throw new XiangShuiException("参数错误");
        if (StringUtils.isBlank(phone) || !phone.matches("^1\\d{10}$")) throw new XiangShuiException("参数错误");

        String out_trade_no = Other.stringFormat("MC_{}_{}_{}", phone, mode, UUID.get(4));

        FluentMap map = new FluentMap()
                .fluentPut("appid", weixin_gzh_AppID)
                .fluentPut("mch_id", mch_id)
                .fluentPut("body", "月卡")
                .fluentPut("out_trade_no", out_trade_no)
                .fluentPut("total_fee", mode == 1 ? "1" : "3")
                .fluentPut("spbill_create_ip", "")
                .fluentPut("notify_url", "https://www.xiangshuispace.com/jpi/buy_month_card/notify_url")
                .fluentPut("trade_type", "NATIVE");
        Map result = PayUtils.unifiedorder(map, key);


        return new Result(CodeMsg.SUCCESS)
                .putData("result", result)
                ;
    }


    @RequestMapping("/jpi/buy_month_card/notify_url")
    @ResponseBody
    public String buy_month_card_notify_url(@RequestBody String body) throws Exception {
        TreeMap<String, String> map = PayUtils.parseXml(body);
        if (StringUtils.isBlank(map.get("sign")) || !PayUtils.makeSign(map, key).equals(map.get("sign"))) {
            return PayUtils.makeXml(new FluentMap()
                    .fluentPut("return_code", map.get("return_code"))
                    .fluentPut("return_msg", map.get("签名错误")));
        }
        if (!"SUCCESS".equals(map.get("return_code"))) {
            return PayUtils.makeXml(new FluentMap()
                    .fluentPut("return_code", map.get("return_code"))
                    .fluentPut("return_msg", map.get("return_msg")));
        }
        String out_trade_no = map.get("out_trade_no");
        String phone = out_trade_no.split("_")[1];
        int mode = Integer.valueOf(out_trade_no.split("_")[2]);
        MonthCardRecode monthCardRecode = monthCardService.getMonthCardRecodeByPhone(phone, null);
        long month_card_init_date;
        if (monthCardRecode != null && monthCardRecode.getEnd_time() > System.currentTimeMillis() / 1000) {
            month_card_init_date = monthCardRecode.getEnd_time();
        } else {
            month_card_init_date = System.currentTimeMillis() / 1000;
        }
        monthCardService.appendMonthCardTo(phone, new LocalDate((month_card_init_date + 60 * 60 * 24 * 30 * mode) * 1000));
        return PayUtils.makeXml(new FluentMap()
                .fluentPut("return_code", map.get("SUCCESS"))
                .fluentPut("return_msg", map.get("OK")));
    }


}
