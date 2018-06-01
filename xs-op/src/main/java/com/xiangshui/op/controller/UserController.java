package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.bean.PaginationResult;
import com.xiangshui.server.bean.UserSearch;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.domain.UserWallet;
import com.xiangshui.server.domain.WalletRecord;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
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

    @Autowired
    WalletRecordDao walletRecordDao;

    @Menu("用户管理")
    @AuthRequired("用户管理")
    @GetMapping("/user_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "user_manage";
    }

    @GetMapping("/api/user/search")
    @ResponseBody
    public Result search(UserInfo criteria, Date create_date_start, Date create_date_end) {
        List<UserInfo> userInfoList = null;

        if (criteria.getUin() != null) {
            UserInfo userInfo = userService.getUserInfoByUin(criteria.getUin());
            if (userInfo == null) {
                return new Result(CodeMsg.NO_FOUND);
            }
            userInfoList = new ArrayList<>();
            userInfoList.add(userInfo);
        } else if (StringUtils.isNotBlank(criteria.getPhone())) {
            UserInfo userInfo = userService.getUserInfoByPhone(criteria.getPhone());
            if (userInfo == null) {
                return new Result(CodeMsg.NO_FOUND);
            }
            userInfoList = new ArrayList<>();
            userInfoList.add(userInfo);
        } else {
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
            userInfoList = userInfoDao.scan(scanSpec);
        }
        if (userInfoList == null) {
            userInfoList = new ArrayList<>();
        }
        return new Result(CodeMsg.SUCCESS).putData("userInfoList", userInfoList);
    }


    @GetMapping("/api/user/{uin:\\d+}")
    @ResponseBody
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

    @GetMapping("/api/user/phone/{phone:\\d+}")
    @ResponseBody
    public Result getByPhone(@PathVariable("phone") String phone) {
        UserInfo userInfo = userService.getUserInfoByPhone(phone);
        if (userInfo != null) {
            return new Result(CodeMsg.SUCCESS).putData("userInfo", userInfo);
        } else {
            return new Result(CodeMsg.NO_FOUND);
        }
    }

    @GetMapping("/api/user/{uin:\\d+}/wallet")
    @ResponseBody
    @AuthRequired("用户管理")
    public Result getWalletByUin(@PathVariable("uin") int uin) {
        UserWallet userWallet = userService.getUserWalletByUin(uin);
        if (userWallet == null) {
            return new Result(CodeMsg.NO_FOUND);
        } else {
            return new Result(CodeMsg.SUCCESS).putData("userWallet", userWallet);
        }
    }


    @PostMapping("/api/user/{uin:\\d+}/wallet/update/balance")
    @ResponseBody
    @AuthRequired("更改用户钱包")
    public Result updateWallet(HttpServletRequest request, @PathVariable("uin") Integer uin, Integer disparity, String subject) throws Exception {
        if (disparity == null || disparity == 0) return new Result(-1, "请输入变动金额");
        if (StringUtils.isBlank(subject)) return new Result(-1, "原因不能为空");
        String op_username = (String) request.getAttribute("op_username");
        userService.updateUserBalance(uin, disparity, subject, op_username);
        return new Result(CodeMsg.SUCCESS);
    }


    @GetMapping("/api/user_wallet/search")
    @ResponseBody
    public Result user_wallet_search(WalletRecord criteria, Date create_date_start, Date create_date_end) throws Exception {

        if (criteria == null) {
            criteria = new WalletRecord();
        }

        List<ScanFilter> filterList = walletRecordDao.makeScanFilterList(criteria, new String[]{
                "uin",
                "phone",
                "type",
                "subject",

        });

        if (filterList == null) {
            filterList = new ArrayList<>();
        }


        if (create_date_start != null && create_date_end != null) {
            filterList.add(new ScanFilter("create_time").between(
                    create_date_start.getTime() / 1000, (create_date_end.getTime() + 1000 * 60 * 60 * 24) / 1000
            ));
        } else if (create_date_start != null && create_date_end == null) {
            filterList.add(new ScanFilter("create_time").gt(create_date_start.getTime() / 1000 - 1));
        } else if (create_date_start == null && create_date_end != null) {
            filterList.add(new ScanFilter("create_time").lt((create_date_end.getTime() + 1000 * 60 * 60 * 24) / 1000 + 1));
        }
        ScanSpec scanSpec = new ScanSpec();
        if (filterList != null && filterList.size() > 0) {
            scanSpec.withScanFilters(filterList.toArray(new ScanFilter[filterList.size()]));
        }
        List<WalletRecord> walletRecordList = walletRecordDao.scan(scanSpec);
        if (walletRecordList != null && walletRecordList.size() > 0) {
            walletRecordList.sort(new Comparator<WalletRecord>() {
                @Override
                public int compare(WalletRecord o1, WalletRecord o2) {
                    return (int) (o2.getCreate_time() - o1.getCreate_time());
                }
            });
        }
        return new Result(CodeMsg.SUCCESS).putData("walletRecordList", walletRecordList);
    }


}
