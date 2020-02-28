package com.xiangshui.op.controller;

import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.crud.Example;
import com.xiangshui.server.dao.mysql.PourBookingDao;
import com.xiangshui.server.domain.mysql.qingsu.PourBooking;
import com.xiangshui.server.service.PourService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Controller
public class PourController extends BaseController {


    @Autowired
    PourBookingDao pourBookingDao;

    @Autowired
    PourService pourService;

    @Menu(value = "倾诉订单")
    @AuthRequired("倾诉订单")
    @GetMapping("/pour_manage")
    public String pour_manage(HttpServletRequest request) {
        setClient(request);
        return "pour_manage";
    }


    @GetMapping("/api/pour/booking/search")
    @ResponseBody
    public Result search(PourBooking booking, Date date_start, Date date_end, Integer page) throws IllegalAccessException {
        if (booking == null) booking = new PourBooking();
        if (page == null || page < 1) page = 1;
        Example example = new Example();
        example.getConditions().conditionList.addAll(pourBookingDao.makeConditionList(booking, new String[]{
                "id",
                "uin",
                "status",
                "pay_id",
        }, true));
        if (StringUtils.isNotBlank(booking.getPhone())) {
            example.getConditions().eq("phone", booking.getPhone());
        }
        if (date_start != null && date_end != null) {
            example.getConditions().between("start_time", new LocalDate(date_start).toDate().getTime() / 1000, new LocalDate(date_end).plusDays(1).toDate().getTime() / 1000 - 1);
        } else if (date_start == null && date_end != null) {
            example.getConditions().lte("start_time", new LocalDate(date_end).plusDays(1).toDate().getTime() / 1000 - 1);
        } else if (date_start != null && date_end == null) {
            example.getConditions().gte("start_time", new LocalDate(date_start).toDate().getTime() / 1000);
        }
        int count = pourBookingDao.countByConditions(example.getConditions());
        example.setPageParams(page, 50);
        List<PourBooking> bookingList = pourBookingDao.selectByExample(example);
        return new Result(CodeMsg.SUCCESS)
                .putData("bookingList", bookingList)
                .putData("page", page)
                .putData("count", count);
    }

    @GetMapping("/api/pour/booking/{id:\\d+}")
    @ResponseBody
    public Result get(@PathVariable("id") Long id) {
        PourBooking booking = pourBookingDao.selectByPrimaryKey(id, null);
        if (booking == null) return new Result(CodeMsg.NO_FOUND);
        return new Result(CodeMsg.SUCCESS)
                .putData("booking", booking);
    }

    @PostMapping("/api/pour/booking/{id:\\d+}/delete")
    @ResponseBody
    public Result delete(@PathVariable("id") Long id) {
        PourBooking booking = pourBookingDao.selectByPrimaryKey(id, null);
        if (booking == null) return new Result(CodeMsg.NO_FOUND);
        if (pourBookingDao.deleteByPrimaryKey(id) > 0) return new Result(CodeMsg.SUCCESS);
        else return new Result(-1, "操作失败");
    }


    @PostMapping("/api/pour/booking/{id:\\d+}/update")
    @ResponseBody
    public Result update_price(@PathVariable("id") Long id, Long start_time, Long talk_time, Integer final_price) throws IllegalAccessException, NoSuchFieldException {
        if (final_price == null) return new Result(-1, "缺少金额");
        pourService.updateBookingForOp(id, start_time, talk_time, final_price);
        return new Result(CodeMsg.SUCCESS);
    }


    @PostMapping("/api/pour/booking/create")
    @ResponseBody
    public Result create(PourBooking booking) throws IllegalAccessException, NoSuchFieldException {
        if (booking == null) return new Result(-1, "缺少参数");
        pourService.createBookingForOp(booking);
        return new Result(CodeMsg.SUCCESS).putData("booking", booking);
    }


}
