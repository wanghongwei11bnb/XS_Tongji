package com.xiangshui.op.controller;

import com.alibaba.fastjson.JSON;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.bean.WebMenu;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.server.mapper.OpMapper;
import com.xiangshui.server.service.OpUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

@Controller
public class IndexController extends BaseController {

    private List<WebMenu> authMenuList;


    @Autowired
    OpMapper opMapper;
    @Autowired
    OpUserService opUserService;

    @GetMapping("")
    public String index(HttpServletRequest request) {
        setClient(request);
        if (this.authMenuList == null) {
            List<WebMenu> authMenuList = new ArrayList<>();
            WebApplicationContext webApplicationContext =
                    (WebApplicationContext) request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            RequestMappingHandlerMapping bean = webApplicationContext.getBean(RequestMappingHandlerMapping.class);
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = bean.getHandlerMethods();
            for (RequestMappingInfo requestMappingInfo : handlerMethods.keySet()) {
                HandlerMethod handlerMethod = handlerMethods.get(requestMappingInfo);
                Method method = handlerMethod.getMethod();
                Menu menu = method.getAnnotation(Menu.class);
                if (menu != null) {
                    WebMenu webMenu = new WebMenu();
                    webMenu.setTitle(menu.value());
                    webMenu.setPath(requestMappingInfo.getPatternsCondition().getPatterns().iterator().next());
                    webMenu.setSort(menu.sort());
                    AuthRequired authRequired = method.getAnnotation(AuthRequired.class);
                    if (authRequired != null) {
                        webMenu.setAuth(authRequired.value());
                    }
                    authMenuList.add(webMenu);
                }
            }
            authMenuList.sort(new Comparator<WebMenu>() {
                @Override
                public int compare(WebMenu o1, WebMenu o2) {
                    return o1.getSort() - o2.getSort();
                }
            });
            this.authMenuList = authMenuList;
        }
        String op_username = (String) request.getAttribute("op_username");
        Set<String> authSet = opUserService.getAuthSet(op_username);


        List<WebMenu> webMenuListActive = new ArrayList<>();
        this.authMenuList.forEach(new Consumer<WebMenu>() {
            @Override
            public void accept(WebMenu webMenu) {
                if (StringUtils.isBlank(webMenu.getAuth())) {
                    webMenuListActive.add(webMenu);
                } else if (authSet.contains(webMenu.getAuth())) {
                    webMenuListActive.add(webMenu);
                }
            }
        });
        Set<String> citySet = opUserService.getCitySet(op_username);
        if (citySet != null && citySet.size() > 0) {
            citySet.forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    WebMenu webMenu = new WebMenu();
                    webMenu.setTitle(s + "-场地管理");
                    webMenu.setPath("/city/" + s + "/area_manage");
                    webMenuListActive.add(webMenu);
                }
            });
        }
        Set<Integer> areaSet = opUserService.getAreaSet(op_username);
        if (areaSet != null && areaSet.size() > 0) {
            WebMenu webMenu = new WebMenu();
            webMenu.setTitle("我的场地");
            webMenu.setPath("/main_area_manage");
            webMenuListActive.add(webMenu);
        }
        request.setAttribute("webMenuList", JSON.toJSONString(webMenuListActive));
        return "index";
    }

    //    @Menu(value = "首页", sort = 901)
    @GetMapping("/home")
    public String home(HttpServletRequest request) {
        setClient(request);
        return "home";
    }

}
