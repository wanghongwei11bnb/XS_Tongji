package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.bean.PaginationResult;
import com.xiangshui.server.bean.UserSearch;
import com.xiangshui.server.dao.UserFaceDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.dao.UserRegisterDao;
import com.xiangshui.server.dao.UserWalletDao;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.relation.UserInfoRelation;
import com.xiangshui.server.service.UserService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class UserController extends BaseController {


    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    UserWalletDao userWalletDao;
    @Autowired
    UserRegisterDao userRegisterDao;
    @Autowired
    UserFaceDao userFaceDao;

    @Autowired
    UserService userService;

    @GetMapping("/user_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "user_manage";
    }

    @GetMapping("/api/user/search")
    @ResponseBody
    public Result search(UserInfo criteria, Date create_date_start, Date create_date_end) {
        if (criteria.getUin() != null || StringUtils.isNotBlank(criteria.getPhone())) {
            UserInfo userInfo = null;
            if (criteria.getUin() != null) {
                userInfo = userService.getUserInfoByUin(criteria.getUin());
            } else if (StringUtils.isNotBlank(criteria.getPhone())) {
                userInfo = userService.getUserInfoByPhone(criteria.getPhone());
            }
            if (userInfo == null) {
                return new Result(CodeMsg.SUCCESS);
            } else {
                UserInfoRelation userInfoRelation = userService.toRelation(userInfo);
                return new Result(CodeMsg.SUCCESS).putData("userInfoList", new UserInfoRelation[]{userInfoRelation});
            }
        }

        ScanSpec scanSpec = new ScanSpec();
        List<ScanFilter> filterList = new ArrayList<ScanFilter>();
        if (create_date_start != null && create_date_end != null) {
            filterList.add(new ScanFilter("create_time").between(
                    create_date_start.getTime() / 1000, (create_date_end.getTime() + 1000 * 60 * 60 * 24) / 1000
            ));
        } else if (create_date_start != null && create_date_end == null) {
            filterList.add(new ScanFilter("create_time").gt(create_date_start.getTime() / 1000 - 1));
        } else if (create_date_start == null && create_date_end != null) {
            filterList.add(new ScanFilter("create_time").lt((create_date_end.getTime() + 1000 * 60 * 60 * 24) / 1000 + 1));
        }
        if (filterList.size() > 0) {
            scanSpec.withScanFilters(filterList.toArray(new ScanFilter[0]));
        }
        List<UserInfo> userInfoList = userInfoDao.scan(scanSpec);
        if (userInfoList == null || userInfoList.size() == 0) {
            return new Result(CodeMsg.SUCCESS);
        } else {
            List<UserInfoRelation> userInfoRelationList = userService.toRelation(userInfoList);
            return new Result(CodeMsg.SUCCESS).putData("userInfoList", userInfoRelationList);
        }
    }


    @GetMapping("/api/user/getByUin/{uin:\\d+}")
    public Result getByUin(@PathVariable("uin") int uin) {
        UserInfo userInfo = userService.getUserInfoByUin(uin);
        if (userInfo != null) {
            return new Result(CodeMsg.SUCCESS)
                    .putData("userInfo", userInfo)
                    .putData("userWallet", userService.getUserWalletByUin(userInfo.getUin()));
        } else {
            return new Result(CodeMsg.NO_FOUND);
        }
    }

    @GetMapping("/api/user/getByPhone/{phone:\\d+}")
    public Result getByPhone(@PathVariable("phone") String phone) {
        UserInfo userInfo = userService.getUserInfoByPhone(phone);
        if (userInfo != null) {
            return new Result(CodeMsg.SUCCESS).putData("userInfo", userInfo);
        } else {
            return new Result(CodeMsg.NO_FOUND);
        }
    }


}
