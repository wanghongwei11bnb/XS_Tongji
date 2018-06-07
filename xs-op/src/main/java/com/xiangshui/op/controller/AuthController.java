package com.xiangshui.op.controller;

import com.alibaba.fastjson.JSON;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.LoginRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.dao.redis.OpPrefix;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.server.example.OpExample;
import com.xiangshui.server.mapper.OpMapper;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.OpUserService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

@Controller
public class AuthController extends BaseController {

    @Autowired
    OpMapper opMapper;
    @Autowired
    OpUserService opUserService;
    Set<String> authSet;

    @Autowired
    RedisService redisService;

    @Autowired
    AreaService areaService;


    public Set<String> getAuthSet(HttpServletRequest request) {
        if (this.authSet == null) {
            this.authSet = new HashSet<String>();
            WebApplicationContext webApplicationContext = (WebApplicationContext) request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            RequestMappingHandlerMapping bean = webApplicationContext.getBean(RequestMappingHandlerMapping.class);
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = bean.getHandlerMethods();
            for (RequestMappingInfo requestMappingInfo : handlerMethods.keySet()) {
                HandlerMethod handlerMethod = handlerMethods.get(requestMappingInfo);
                Method method = handlerMethod.getMethod();
                AuthRequired authRequired = method.getAnnotation(AuthRequired.class);
                if (authRequired != null) {
                    authSet.add(authRequired.value());
                }
            }
        }
        return this.authSet;
    }

    @Menu(value = "OP权限管理")
    @AuthRequired("OP权限管理")
    @GetMapping("/auth_manage")
    public String auth_manage(HttpServletRequest request, HttpServletResponse response) {
        setClient(request);
        request.setAttribute("authList", JSON.toJSONString(getAuthSet(request)));
        return "auth_manage";
    }

    @AuthRequired("OP权限管理")
    @GetMapping("/api/allAuth")
    @ResponseBody
    public Result getAllUrl(HttpServletRequest request) {
        Set<String> authSet = getAuthSet(request);
        return new Result(CodeMsg.SUCCESS).putData("authList", authSet.toArray(new String[authSet.size()]));
    }


    @AuthRequired("OP权限管理")
    @GetMapping("/api/op/list")
    @ResponseBody
    public Result op_list() {
        OpExample example = new OpExample();
        example.setLimit(1000);
        return new Result(CodeMsg.SUCCESS).putData("opList", opMapper.selectByExample(example));
    }

    @AuthRequired("OP权限管理")
    @GetMapping("/api/op/get")
    @ResponseBody
    public Result getOp(String username) {
        Op op = opMapper.selectByPrimaryKey(username, null);
        if (op == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        return new Result(CodeMsg.SUCCESS).putData("op", op);
    }

    @AuthRequired("OP权限管理")
    @GetMapping("/api/op/auths")
    @ResponseBody
    public Result auths(String username) {
        Op op = opMapper.selectByPrimaryKey(username, null);
        if (op == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        return new Result(CodeMsg.SUCCESS).putData("auths", opUserService.getAuthSet(username));
    }


    @AuthRequired("OP权限管理")
    @PostMapping("/api/op/update/auths")
    @ResponseBody
    public Result update_auths(String username, String auths) {
        Op op = opMapper.selectByPrimaryKey(username, "username");
        if (op == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        if (StringUtils.isBlank(auths)) {
            auths = "";
        }
        op.setAuths(auths);
        opMapper.updateByPrimaryKeySelective(op);
        opUserService.cleanCache(username);
        return new Result(CodeMsg.SUCCESS);
    }


    @AuthRequired("OP权限管理")
    @GetMapping("/api/op/citys")
    @ResponseBody
    public Result citys(String username) {
        Op op = opMapper.selectByPrimaryKey(username, null);
        if (op == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        return new Result(CodeMsg.SUCCESS).putData("citys", opUserService.getCitySet(username));
    }

    @AuthRequired("OP权限管理")
    @PostMapping("/api/op/update/citys")
    @ResponseBody
    public Result update_citys(String username, String citys) {
        Op op = opMapper.selectByPrimaryKey(username, "username");
        if (op == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        if (StringUtils.isBlank(citys)) {
            citys = "";
        }
        op.setCitys(citys);
        opMapper.updateByPrimaryKeySelective(op);
        opUserService.cleanCache(username);
        return new Result(CodeMsg.SUCCESS);
    }


    @AuthRequired("OP权限管理")
    @GetMapping("/api/op/areas/options")
    @ResponseBody
    public Result areas_options() throws NoSuchFieldException, IllegalAccessException {
        List<Area> areaList = areaService.search(null, new String[]{"area_id", "title", "city", "address"});
        if (areaList != null && areaList.size() > 0) {
            areaList.sort((o1, o2) -> o2.getArea_id() - o1.getArea_id());
        }
        return new Result(CodeMsg.SUCCESS).putData("areaList", areaList);
    }

    @AuthRequired("OP权限管理")
    @GetMapping("/api/op/areas")
    @ResponseBody
    public Result areas(String username) {
        Op op = opMapper.selectByPrimaryKey(username, null);
        if (op == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        return new Result(CodeMsg.SUCCESS).putData("areas", opUserService.getAreaSet(username));
    }

    @AuthRequired("OP权限管理")
    @PostMapping("/api/op/update/areas")
    @ResponseBody
    public Result update_areas(String username, String areas) {
        Op op = opMapper.selectByPrimaryKey(username, "username");
        if (op == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        if (StringUtils.isBlank(areas)) {
            areas = "";
        }
        op.setAreas(areas);
        opMapper.updateByPrimaryKeySelective(op);
        opUserService.cleanCache(username);
        return new Result(CodeMsg.SUCCESS);
    }


}
