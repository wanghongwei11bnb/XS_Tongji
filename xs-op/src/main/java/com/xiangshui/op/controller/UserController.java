package com.xiangshui.op.controller;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.sun.org.apache.xerces.internal.xs.XSException;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.bean.PaginationResult;
import com.xiangshui.server.bean.UserSearch;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.domain.UserWallet;
import com.xiangshui.server.domain.WalletRecord;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.relation.UserInfoRelation;
import com.xiangshui.server.service.UserService;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Around;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

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
    public Result search(HttpServletRequest request, HttpServletResponse response, UserInfo criteria, Date create_date_start, Date create_date_end, Boolean fial_verifie, Boolean download) throws NoSuchFieldException, IllegalAccessException, IOException {
        if (download == null) download = false;
        if (criteria == null) criteria = new UserInfo();
        if (fial_verifie == null) fial_verifie = false;
        List<ScanFilter> filterList = userInfoDao.makeScanFilterList(criteria, new String[]{
                "uin",
                "phone",
        });

        userInfoDao.appendDateRangeFilter(filterList, "create_time", create_date_start, create_date_end);

        if (fial_verifie) {
            filterList.add(new ScanFilter("fail_count").gt(2));
            filterList.add(new ScanFilter("fail_data").exists());
            filterList.add(new ScanFilter("id_verified").ne(1));
        }

        ScanSpec scanSpec = new ScanSpec();
        if (download) scanSpec.withMaxResultSize(BaseDynamoDao.maxDownloadSize);

        if (filterList.size() > 0) {
            scanSpec.withScanFilters(filterList.toArray(new ScanFilter[filterList.size()]));
        }

        List<UserInfo> userInfoList = userInfoDao.scan(scanSpec);
        if (userInfoList == null) {
            userInfoList = new ArrayList<>();
        }
        if (download) {
            ExcelUtils.export(Arrays.asList(
                    new ExcelUtils.Column<UserInfo>("用户uin") {
                        @Override
                        public String render(UserInfo userInfo) {
                            return String.valueOf(userInfo.getUin());
                        }
                    },
                    new ExcelUtils.Column<UserInfo>("手机号") {
                        @Override
                        public String render(UserInfo userInfo) {
                            return userInfo.getPhone();
                        }
                    },
                    new ExcelUtils.Column<UserInfo>("昵称") {
                        @Override
                        public String render(UserInfo userInfo) {
                            return userInfo.getNick_name();
                        }
                    },
                    new ExcelUtils.Column<UserInfo>("注册日期") {
                        @Override
                        public String render(UserInfo userInfo) {
                            return userInfo.getCreate_time() != null ? DateUtils.format(userInfo.getCreate_time() * 1000, "yyyy-MM-dd") : null;
                        }
                    },
                    new ExcelUtils.Column<UserInfo>("是否已认证") {
                        @Override
                        public String render(UserInfo userInfo) {
                            return new Integer(1).equals(userInfo.getId_verified()) ? "是" : "否";
                        }
                    },
                    new ExcelUtils.Column<UserInfo>("认证失败次数") {
                        @Override
                        public String render(UserInfo userInfo) {
                            return String.valueOf(userInfo.getFail_count());
                        }
                    },
                    new ExcelUtils.Column<UserInfo>("认证失败数据") {
                        @Override
                        public String render(UserInfo userInfo) {
                            return JSON.toJSONString(userInfo.getFail_data());
                        }
                    },
                    new ExcelUtils.Column<UserInfo>("是否被邀请") {
                        @Override
                        public String render(UserInfo userInfo) {
                            return userInfo.getInvited_by() != null && userInfo.getInvited_by() != 0 ? "是" : "否";
                        }
                    },
                    new ExcelUtils.Column<UserInfo>("邀请人用户uin") {
                        @Override
                        public String render(UserInfo userInfo) {
                            return String.valueOf(userInfo.getInvited_by());
                        }
                    },
                    new ExcelUtils.Column<UserInfo>("是否加入黑名单") {
                        @Override
                        public String render(UserInfo userInfo) {
                            return new Integer(1).equals(userInfo.getBlock()) ? "是" : "否";
                        }
                    }
            ), userInfoList, response, "userInfoList.xlsx");
            return null;
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

    @PostMapping("/api/user/{uin:\\d+}/update/id_verified")
    @ResponseBody
    @AuthRequired("用户管理")
    public Result userInfo_update_id_verified(@PathVariable("uin") int uin, Integer id_verified) throws Exception {
        if (id_verified == null) {
            return new Result(-1, "参数不能为空");
        }
        UserInfo userInfo = userService.getUserInfoByUin(uin);
        if (userInfo == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        userInfo.setId_verified(id_verified);
        userInfoDao.updateItem(new PrimaryKey("uin", uin), userInfo, new String[]{
                "id_verified",
        });
        return new Result(CodeMsg.SUCCESS);
    }

    @PostMapping("/api/user/{uin:\\d+}/update/block")
    @ResponseBody
    @AuthRequired("用户管理")
    public Result userInfo_update_block(@PathVariable("uin") int uin, Integer block) throws Exception {
        if (block == null) {
            return new Result(-1, "参数不能为空");
        }
        UserInfo userInfo = userService.getUserInfoByUin(uin);
        if (userInfo == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        userInfo.setBlock(block);
        userInfoDao.updateItem(new PrimaryKey("uin", uin), userInfo, new String[]{
                "block",
        });
        return new Result(CodeMsg.SUCCESS);
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
        List<ScanFilter> scanFilterList = walletRecordDao.makeScanFilterList(criteria, new String[]{
                "uin",
                "phone",
                "type",
                "subject",

        });
        walletRecordDao.appendDateRangeFilter(scanFilterList,"create_time",create_date_start,create_date_end);
        ScanSpec scanSpec = new ScanSpec();
        if (scanFilterList != null && scanFilterList.size() > 0) {
            scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        }
        List<WalletRecord> walletRecordList = walletRecordDao.scan(scanSpec);
        if (walletRecordList != null && walletRecordList.size() > 0) {
            walletRecordList.sort((o1, o2) -> (int) (o2.getCreate_time() - o1.getCreate_time()));
        }
        return new Result(CodeMsg.SUCCESS).putData("walletRecordList", walletRecordList);
    }


    @AuthRequired("用户管理")
    @PostMapping("/api/user/uin_to_phone")
    @ResponseBody
    public Result uin_to_phone(@RequestBody List<Integer> uinList) {
        if (uinList == null || uinList.size() == 0) {
            throw new XiangShuiException("内容不能为空");
        }
        Set<Integer> uinSet = new HashSet<>();
        for (Integer uin : uinList) {
            if (uin != null) {
                uinSet.add(uin);
            }
        }
        if (uinSet.size() == 0) {
            throw new XiangShuiException("内容不能为空");
        }
        List<String> phoneList = new ArrayList<>();
        for (int uin : uinSet) {
            UserInfo userInfo = userService.getUserInfoByUin(uin);
            if (userInfo != null && StringUtils.isNotBlank(userInfo.getPhone())) {
                phoneList.add(userInfo.getPhone());
            }
        }
        if (phoneList.size() == 0) {
            return new Result(CodeMsg.NO_FOUND);
        } else {
            return new Result(CodeMsg.SUCCESS).putData("phoneList", phoneList);
        }
    }


    @AuthRequired("修改月卡")
    @ResponseBody
    @PostMapping("/api/user/{uin:\\d+}/monthCard/delete")
    public Result user_month_care_delete(@PathVariable("uin") int uin) throws Exception {
        monthCardService.deleteMonthCard(uin);
        return new Result(CodeMsg.SUCCESS);
    }

    @AuthRequired("修改月卡")
    @ResponseBody
    @PostMapping("/api/user/{uin:\\d+}/monthCard/appendTo")
    public Result user_month_care_delete(@PathVariable("uin") int uin, Date date) throws Exception {
        if (date == null) throw new XiangShuiException("日期不能为空");
        LocalDate localDate = new LocalDate(date);
        if (localDate.compareTo(new LocalDate()) < 0) throw new XiangShuiException("日期不能为过去");
        if (localDate.compareTo(new LocalDate().plusDays(30 * 6)) > 0) throw new XiangShuiException("日期不能超出6个月");
        monthCardService.appendMonthCardTo(uin, localDate);
        return new Result(CodeMsg.SUCCESS);
    }


    @AuthRequired("清空钱包")
    @ResponseBody
    @PostMapping("/api/user/{uin:\\d+}/wallet/clean")
    public Result user_wallet_clean(@PathVariable("uin") int uin) throws Exception {
        UserInfo userInfo = userInfoDao.getItem(new PrimaryKey("uin", uin));
        if (userInfo == null) throw new XiangShuiException("userInfo未找到");
        UserWallet userWallet = userWalletDao.getItem(new PrimaryKey("uin", userInfo.getUin()));
        if (userWallet == null) throw new XiangShuiException("userWallet未找到");
        userWallet.setBalance(0);
        userWallet.setBonus(0);
        userWallet.setCharge(0);
        userWalletDao.updateItem(new PrimaryKey("uin", uin), userWallet, new String[]{
                "balance",
                "charge",
                "bonus",
        });
        return new Result(CodeMsg.SUCCESS);
    }


}
