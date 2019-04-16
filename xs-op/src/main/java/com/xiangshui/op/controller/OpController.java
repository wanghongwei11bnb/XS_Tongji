package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.bean.Session;
import com.xiangshui.op.threadLocal.UsernameLocal;
import com.xiangshui.server.dao.AreaContractDao;
import com.xiangshui.server.dao.redis.OpPrefix;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.AreaContract;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.mapper.OpMapper;
import com.xiangshui.server.service.AreaContractService;
import com.xiangshui.server.service.OpUserService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
public class OpController extends BaseController {

    @Autowired
    OpUserService opUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    OpMapper opMapper;

    @Autowired
    AreaContractService areaContractService;

    @Autowired
    AreaContractDao areaContractDao;

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
            cookie.setMaxAge(60 * 60 * 24 * 7);
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
        if (op == null) return new Result(CodeMsg.NO_FOUND);

        if (StringUtils.isNotBlank(op.getFullname()) && StringUtils.isNotBlank(op.getCity())) {
            List<AreaContract> areaContractList = areaContractDao.scan(new ScanSpec()
                    .withMaxResultSize(1)
                    .withScanFilters(
                            new ScanFilter("saler").eq(op.getFullname())
                            , new ScanFilter("saler_city").eq(op.getCity())
                    ));
            if (areaContractList != null && areaContractList.size() > 0) {
                throw new XiangShuiException("不能修改了！！！");
            }
        }

        if (criteria == null) throw new XiangShuiException("内容不能为空");
        if (StringUtils.isBlank(criteria.getFullname())) throw new XiangShuiException("姓名不能为空");
        if (StringUtils.isBlank(criteria.getCity())) throw new XiangShuiException("城市不能为空");
        Op update = new Op();
        update.setFullname(criteria.getFullname());
        update.setCity(criteria.getCity());
        update.setUsername(op_username);
        opMapper.updateByPrimaryKeySelective(update);
        opUserService.cleanCache(op_username);
        return new Result(CodeMsg.SUCCESS).putData("op_info", opUserService.getOpByUsername(op_username, null));
    }


    //    @AuthRequired(AuthRequired.area_contract_operate)
    @GetMapping("/api/saler/list")
    @ResponseBody
    public Result salerList() {
        return new Result(CodeMsg.SUCCESS)
                .putData("salerList", opUserService.getSalerList());
    }


    @AuthRequired(AuthRequired.auth_op_auth)
    @PostMapping("/api/op/create")
    @ResponseBody
    public Result create(@RequestBody Op criteria) {
        if (criteria == null) {
            return new Result(-1, "参数不能为空");
        }
        if (StringUtils.isBlank(criteria.getUsername())) {
            return new Result(-1, "帐号不能为空");
        }
        if (StringUtils.isBlank(criteria.getPassword())) {
            return new Result(-1, "密码不能为空");
        }
        Op op = opUserService.getOpByUsername(criteria.getUsername(), null);
        if (op != null) {
            return new Result(-1, "帐号已存在");
        }
        criteria.setPassword(opUserService.passwordMd5(criteria.getPassword()));
        opMapper.insert(criteria);
        return new Result(CodeMsg.SUCCESS);
    }
}
