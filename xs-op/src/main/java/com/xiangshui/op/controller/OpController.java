package com.xiangshui.op.controller;

import com.xiangshui.server.dao.redis.OpPrefix;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.server.service.OpUserService;
import com.xiangshui.util.MD5;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class OpController extends BaseController {

    @Autowired
    OpUserService opUserService;

    @Autowired
    RedisService redisService;

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public Result login(HttpServletRequest request, HttpServletResponse response, String username, String password) {
        Op op = opUserService.getOpByUsername(username, null);
        if (op == null) {
            return new Result(CodeMsg.AUTH_FAIL);
        }
        if (opUserService.authOp(op, password)) {
            String op_token = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
            redisService.set(OpPrefix.op_token, op_token, op);
            Cookie cookie = new Cookie("op_token", op_token);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 7);
            response.addCookie(cookie);
            return new Result(CodeMsg.SUCCESS).putData("op", op);
        } else {
            return new Result(CodeMsg.AUTH_FAIL);
        }
    }
}
