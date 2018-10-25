package com.xiangshui.web.controller.act;

import com.xiangshui.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class MinIPOController extends BaseController {


    @GetMapping("/act/invite/MinIPO")
    public String index(HttpServletRequest request, HttpServletResponse response,
                        String uid, String time, String sign) {
        setClient(request);
        return "act_invite_MinIPO";
    }
}
