package com.xiangshui.op.controller;

import com.xiangshui.op.annotation.AuthPassport;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.LoginRequired;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

@Controller
public class IndexController extends BaseController {


    @GetMapping("")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "index";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request) {
        setClient(request);
        return "home";
    }

}
