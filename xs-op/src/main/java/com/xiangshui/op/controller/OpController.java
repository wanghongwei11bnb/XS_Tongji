package com.xiangshui.op.controller;

import com.xiangshui.op.bean.Session;
import com.xiangshui.op.threadLocal.UsernameLocal;
import com.xiangshui.server.dao.redis.OpPrefix;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.server.service.OpUserService;
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
import java.util.Set;
import java.util.UUID;

@Controller
public class OpController extends BaseController {

    @Autowired
    OpUserService opUserService;

    @Autowired
    RedisService redisService;

    @GetMapping("/login")
    public String loginView(HttpServletRequest request) {
        setClient(request);
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
            String op_session = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
            Session session = new Session();
            session.setUsername(username);
            redisService.set(OpPrefix.session, op_session, session);
            Cookie cookie = new Cookie("op_session", op_session);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24);
            response.addCookie(cookie);
            return new Result(CodeMsg.SUCCESS).putData("op", op);
        } else {
            return new Result(CodeMsg.AUTH_FAIL);
        }
    }

    @PostMapping("/api/logout")
    @ResponseBody
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("op_session".equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    redisService.del(OpPrefix.session, cookie.getValue());
                    break;
                }
            }
        }
        return new Result(CodeMsg.SUCCESS);
    }

    @GetMapping("/api/authSet")
    @ResponseBody
    public Result authSet() {
        String op_username = UsernameLocal.get();
        Set<String> authSet = opUserService.getAuthSet(op_username);
        return new Result(CodeMsg.SUCCESS).putData("authSet", authSet);
    }

    @GetMapping("/api/getOpInfo")
    @ResponseBody
    public Result getOpInfo() {
        String op_username = UsernameLocal.get();
        Op op = opUserService.getOpByUsername(op_username, null);
        if (op == null) {
            return new Result(CodeMsg.NO_FOUND);
        } else {
            return new Result(CodeMsg.SUCCESS).putData("op", op);
        }
    }


    @PostMapping("/api/op/update/saler")
    @ResponseBody
    public Result update_saler(Op criteria) {
        String op_username = UsernameLocal.get();
        Op op = opUserService.getOpByUsername(op_username, null);
        if (op == null) {
            return new Result(CodeMsg.NO_FOUND);
        }

        return null;
    }
}
