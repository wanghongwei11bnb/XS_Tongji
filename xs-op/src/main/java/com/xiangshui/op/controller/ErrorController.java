package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.op.annotation.AuthPassport;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.CapsuleService;
import com.xiangshui.server.service.CityService;
import com.xiangshui.server.service.S3Service;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;

@Controller
public class ErrorController extends BaseController {
    @GetMapping("/error/no_login")
    public String no_login(HttpServletRequest request) {
        setClient(request);
        return "error/no_login";
    }

    @GetMapping("/error/no_auth")
    public String no_auth(HttpServletRequest request) {
        setClient(request);
        return "error/no_auth";
    }
}
