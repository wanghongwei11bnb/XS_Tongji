package com.xiangshui.op.controller;

import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.domain.MonthCardRecode;
import com.xiangshui.server.service.MonthCardService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
public class MonthCardController extends BaseController {

    @Autowired
    MonthCardService monthCardService;

    @Menu("月卡管理")
    @AuthRequired("月卡管理")
    @GetMapping("/month_card_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "month_card_manage";
    }


    @AuthRequired("月卡管理")
    @GetMapping("/api/month_card_recode/search")
    @ResponseBody
    public Result search(MonthCardRecode criteria, Date create_date_start, Date create_date_end, Date end_time_start, Date end_time_end) throws NoSuchFieldException, IllegalAccessException {
        List<MonthCardRecode> monthCardRecodeList = monthCardService.search(criteria, create_date_start, create_date_end, end_time_start, end_time_end, null);
        return new Result(CodeMsg.SUCCESS).putData("monthCardRecodeList", monthCardRecodeList);
    }


    @AuthRequired("月卡管理")
    @GetMapping("/api/month_card_recode/uin_{uin:\\d+}")
    @ResponseBody
    public Result getByUin(@PathVariable("uin") int uin) {
        MonthCardRecode monthCardRecode = monthCardService.getMonthCardRecodeByUin(uin, null);
        if (monthCardRecode == null) {
            return new Result(CodeMsg.NO_FOUND);
        } else {
            return new Result(CodeMsg.SUCCESS).putData("monthCardRecode", monthCardRecode);
        }
    }


    @AuthRequired("月卡管理")
    @GetMapping("/api/month_card_recode/uin_{phone:\\d+}")
    @ResponseBody
    public Result getByUin(@PathVariable("phone") String phone) {
        MonthCardRecode monthCardRecode = monthCardService.getMonthCardRecodeByPhone(phone, null);
        if (monthCardRecode == null) {
            return new Result(CodeMsg.NO_FOUND);
        } else {
            return new Result(CodeMsg.SUCCESS).putData("monthCardRecode", monthCardRecode);
        }
    }


}
