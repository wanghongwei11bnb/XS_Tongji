package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.dao.BaseDynamoDao;
import com.xiangshui.server.dao.ChargeRecordDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.domain.ChargeRecord;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.service.UserService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class ChargeRecordController extends BaseController {

    private Set<String> subjectSet = new TreeSet<>();


    @Autowired
    ChargeRecordDao chargeRecordDao;

    @Autowired
    UserInfoDao userInfoDao;

    @Autowired
    UserService userService;

    @Menu("用户充值纪录")
    @AuthRequired("用户充值纪录")
    @GetMapping("/charge_record_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "charge_record_manage";
    }

    @AuthRequired("用户充值纪录")
    @GetMapping("/api/charge_record/subjectSet")
    @ResponseBody
    public Result getSubjectSet() {
        return new Result(CodeMsg.SUCCESS).putData("subjectSet", this.subjectSet);
    }

    @AuthRequired({"用户充值纪录", "用户管理", "月卡管理"})
    @GetMapping("/api/charge_record/search")
    @ResponseBody
    public Result search(HttpServletRequest request, HttpServletResponse response, ChargeRecord criteria, Date create_date_start, Date create_date_end, Boolean download) throws NoSuchFieldException, IllegalAccessException {
        if (download == null) download = false;
        if (criteria == null) criteria = new ChargeRecord();

        if (StringUtils.isNotBlank(criteria.getPhone())) {
            UserInfo userInfo = userService.getUserInfoByPhone(criteria.getPhone());
            if (userInfo != null) {
                criteria.setUin(userInfo.getUin());
                criteria.setPhone(null);
            }
        }

        List<ScanFilter> scanFilterList = chargeRecordDao.makeScanFilterList(criteria, new String[]{
                "out_trade_no",
                "uin",
                "phone",
                "subject",
                "city",
                "booking_id",
        });

        chargeRecordDao.appendDateRangeFilter(scanFilterList, "create_time", create_date_start, create_date_end);

        ScanSpec scanSpec = new ScanSpec();
        if (download) {
            scanSpec.withMaxResultSize(BaseDynamoDao.maxDownloadSize);
        }
        if (scanFilterList.size() > 0) {
            scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        }


        List<ChargeRecord> chargeRecordList = chargeRecordDao.scan(scanSpec);
        if (chargeRecordList.size() > 0) {
            chargeRecordList.sort((o1, o2) -> (int) (o2.getCreate_time() - o1.getCreate_time()));
        }

        return new Result(CodeMsg.SUCCESS).putData("chargeRecordList", chargeRecordList);
    }


    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void reloadSubjectSet() {
        Set<String> subjectSet = new TreeSet<>();
        List<ScanFilter> scanFilterList = new ArrayList<>();
        chargeRecordDao.appendDateRangeFilter(scanFilterList, "create_time", new LocalDate(2018, 1, 1).toDate(), null);
        List<ChargeRecord> chargeRecordList = chargeRecordDao.scan(
                new ScanSpec()
                        .withMaxResultSize(Integer.MAX_VALUE)
                        .withAttributesToGet("subject")
                        .withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]))
        );
        chargeRecordList.forEach(chargeRecord -> {
            if (chargeRecord != null && StringUtils.isNotBlank(chargeRecord.getSubject())) {
                subjectSet.add(chargeRecord.getSubject());
            }
        });
        this.subjectSet = subjectSet;
    }
}
