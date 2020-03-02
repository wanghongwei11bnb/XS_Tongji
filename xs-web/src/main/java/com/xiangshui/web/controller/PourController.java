package com.xiangshui.web.controller;

import com.xiangshui.server.crud.Conditions;
import com.xiangshui.server.crud.Example;
import com.xiangshui.server.dao.mysql.PourBookingDao;
import com.xiangshui.server.domain.mysql.qingsu.PourBooking;
import com.xiangshui.server.service.PourService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import com.xiangshui.util.weixin.FluentMap;
import com.xiangshui.util.weixin.PayUtils;
import com.xiangshui.web.annotation.LoginRequired;
import com.xiangshui.web.bean.Session;
import com.xiangshui.web.threadLocal.SessionLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

@Slf4j
@Controller
public class PourController extends BaseController {


    @Autowired
    PourBookingDao pourBookingDao;
    @Autowired
    PourService pourService;

    //    String columns = "id,uin,phone,hear_phone,start_time,end_time,talk_time,status,star,final_price";
    String columns = "*";

    @GetMapping("/jpi/pour/booking/list")
    @ResponseBody
    @LoginRequired
    public Result booking_list(HttpServletRequest request) {
        Session session = SessionLocal.get();
        int uin = session.getUin();
        Example example = new Example();
        example.getConditions()
                .eq("uin", uin)
                .in("status", Arrays.asList(1, 2, 3, 4));
        example.setOrderByClause("start_time desc");
        example.setColumns(columns);
        List<PourBooking> bookingList = pourBookingDao.selectByExample(example);
        return new Result(CodeMsg.SUCCESS).putData("bookingList", bookingList);
    }


    @GetMapping("/jpi/pour/booking/running")
    @ResponseBody
    @LoginRequired
    public Result booking_running(HttpServletRequest request) {
        Session session = SessionLocal.get();
        int uin = session.getUin();
        Conditions conditions = new Conditions();
        conditions
                .eq("uin", uin)
                .in("status", Arrays.asList(1, 2, 3));
        PourBooking booking = pourBookingDao.selectOne(conditions, null, columns);
        return new Result(CodeMsg.SUCCESS).putData("booking", booking);
    }

    @GetMapping("/jpi/pour/booking/{id:\\d+}")
    @ResponseBody
    @LoginRequired
    public Result booking_get(HttpServletRequest request, @PathVariable("id") Integer id) {
        Session session = SessionLocal.get();
        int uin = session.getUin();
        Conditions conditions = new Conditions();
        conditions.eq("uin", uin).eq("id", id);
        PourBooking booking = pourBookingDao.selectOne(conditions, null, columns);
        if (booking == null) return new Result(CodeMsg.NO_FOUND);
        return new Result(CodeMsg.SUCCESS).putData("booking", booking);
    }


    @PostMapping("/jpi/pour/booking/{id:\\d+}/prepay")
    @ResponseBody
    @LoginRequired
    public Result booking_prepay(HttpServletRequest request, @PathVariable("id") Long id) throws IOException {
        Session session = SessionLocal.get();
        int uin = session.getUin();
        Conditions conditions = new Conditions();
        conditions.eq("uin", uin).eq("id", id);
        PourBooking booking = pourBookingDao.selectOne(conditions, null, columns);
        if (booking == null) return new Result(CodeMsg.NO_FOUND);
        if (new Integer(4).equals(booking.getStatus())) return new Result(-1, "重复支付！");

        if (booking.getFinal_price() == null || booking.getFinal_price() <= 0) {
            return new Result(-1, "价格错误");
        }
        TreeMap<String, String> data = pourService.unifiedorder(booking, session.getUserWallet().getOpenID());
        return new Result(CodeMsg.SUCCESS).putData("booking", booking).putData("data", data);
    }

    @RequestMapping("/jpi/pour/booking/notify_url")
    public String notify_url(@RequestBody String body) throws IllegalAccessException {
        Date now = new Date();
        log.info("［支付结果通知］{}", body);
        TreeMap<String, String> treeMap = PayUtils.parseXml(body);
        if (!PayUtils.makeSign(treeMap, "MB9pL3BqYeJ5rzkI1RufmNCHd7KJGUfF").equals(treeMap.get("sign"))) {
            log.info("［支付结果通知——签名错误］");
            return new StringBuilder()
                    .append("<xml>")
                    .append("<return_code>FAIL</return_code>")
                    .append("<return_msg>签名失败</return_msg>")
                    .append("</xml>").toString();
        }
        if (!"SUCCESS".equals(treeMap.get("return_code"))) {
            log.info("［支付结果通知——通信失败］");
            return new StringBuilder()
                    .append("<xml>")
                    .append("<return_code>FAIL</return_code>")
                    .append("<return_msg></return_msg>")
                    .append("</xml>").toString();
        }
        if (!"SUCCESS".equals(treeMap.get("result_code"))) {
            log.info("［支付结果通知——业务失败］err_code={},err_code_des={}", treeMap.get("err_code"), treeMap.get("err_code_des"));
            return new StringBuilder()
                    .append("<xml>")
                    .append("<return_code>FAIL</return_code>")
                    .append("<return_msg></return_msg>")
                    .append("</xml>").toString();
        }
        String out_trade_no = treeMap.get("out_trade_no");
        if (out_trade_no.matches("^pour_\\d+_[0-9a-zA-Z]+$")) {

            Long booking_id = Long.parseLong(out_trade_no.split("_")[1]);
            String cash_fee = treeMap.get("cash_fee");
            int pay_amount = Integer.valueOf(cash_fee);

            PourBooking booking = pourBookingDao.selectByPrimaryKey(booking_id, null);

            booking.setPay_type(9);
            booking.setPay_id(out_trade_no);
            booking.setPay_price(pay_amount);
            booking.setStatus(4);

            pourBookingDao.updateByPrimaryKey(booking, new String[]{
                    "pay_type",
                    "pay_id",
                    "pay_price",
                    "status",
            });
        }


        return new StringBuilder()
                .append("<xml>")
                .append("<return_code>SUCCESS</return_code>")
                .append("<return_msg>OK</return_msg>")
                .append("</xml>").toString();
    }


    @PostMapping("/jpi/pour/makePhoneCall")
    @ResponseBody
    @LoginRequired
    public Result makePhoneCall() {
        Session session = SessionLocal.get();
        int uin = session.getUin();
        Conditions conditions = new Conditions();
        conditions
                .eq("uin", uin)
                .in("status", Arrays.asList(1, 2, 3));
        PourBooking booking = pourBookingDao.selectOne(conditions, null, columns);
        if (booking != null) {
            return new Result(-3001, "您有未支付的订单！").putData("booking", booking);
        }
        return new Result(CodeMsg.SUCCESS).putData("phone", "400-688-9960");
    }

}
