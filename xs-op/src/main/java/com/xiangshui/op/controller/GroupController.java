package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.dao.GroupBookingDao;
import com.xiangshui.server.dao.GroupInfoDao;
import com.xiangshui.server.domain.GroupBooking;
import com.xiangshui.server.domain.GroupInfo;
import com.xiangshui.server.service.ServiceUtils;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class GroupController extends BaseController {

    @Autowired
    GroupInfoDao groupInfoDao;
    @Autowired
    GroupBookingDao groupBookingDao;


    @GetMapping("/group_manage")
    @Menu("拼团管理")
    @AuthRequired(AuthRequired.group)
    public String index(HttpServletRequest request) {
        setClient(request);
        return "group_manage";
    }


    @GetMapping("/api/group/search")
    @ResponseBody
    @AuthRequired(AuthRequired.group)
    public Result search(GroupInfo criteria, Date create_date_start, Date create_date_end) throws NoSuchFieldException, IllegalAccessException {
        if (criteria == null) {
            criteria = new GroupInfo();
        }
        List<ScanFilter> scanFilterList = groupInfoDao.makeScanFilterList(criteria, new String[]{
                "group_status",
                "group_amount",
                "group_type",
                "group_master",
                "group_id",
        });
        if (criteria.getGroup_status() == null) {
            scanFilterList.add(new ScanFilter("group_status").ne(0));
        }
        groupInfoDao.appendDateRangeFilter(scanFilterList, "create_time", create_date_start, create_date_end);
        ScanSpec scanSpec = new ScanSpec();
        if (scanFilterList.size() > 0) {
            scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        }
        List<GroupInfo> groupInfoList = groupInfoDao.scan(scanSpec);
        groupInfoList.sort(new Comparator<GroupInfo>() {
            @Override
            public int compare(GroupInfo o1, GroupInfo o2) {
                return (int) (o2.getCreate_time() - o1.getCreate_time());
            }
        });
        List<GroupBooking> groupBookingList = null;
        if (groupInfoList.size() > 0) {
            Set<Long> groupIdSet = new HashSet<>();
            groupInfoList.forEach(groupInfo -> groupIdSet.add(groupInfo.getGroup_id()));
            if (groupIdSet.size() > 0) {
                groupBookingList = ServiceUtils.division(groupIdSet.toArray(new Long[groupIdSet.size()]), 100, longs -> {
                    return groupBookingDao.scan(new ScanSpec().withScanFilters(new ScanFilter("group_id").in(longs)));
                }, new Long[0]);
            }
        }
        return new Result(CodeMsg.SUCCESS)
                .putData("groupInfoList", groupInfoList)
                .putData("groupBookingList", groupBookingList);
    }
}
