package com.xiangshui.op.controller;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.relation.FailureReportRelation;
import com.xiangshui.server.service.FailureReportService;
import com.xiangshui.server.service.UserService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class FailureReportController extends BaseController {

    @Autowired
    FailureReportDao failureReportDao;

    @Autowired
    FailureReportService failureReportService;

    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    AreaDao areaDao;
    @Autowired
    CapsuleDao capsuleDao;
    @Autowired
    BookingDao bookingDao;

    @Autowired
    UserService userService;


    @GetMapping("/failure_manage")
    public String index() {
        return "failure_manage";
    }


    @GetMapping("/api/failure/search")
    @ResponseBody
    public Result search(String req_from, String phone, Integer uin, Long booking_id, Integer area_id, Long capsule_id, Integer op_status, Date start_date, Date end_date, String app_version, String client_type, String client_version) {

        ScanSpec scanSpec = new ScanSpec();
        List<ScanFilter> filterList = new ArrayList<ScanFilter>();


        if (start_date != null && end_date != null) {
            filterList.add(new ScanFilter("create_time").between(start_date.getTime() / 1000, (end_date.getTime() + 1000 * 60 * 60 * 24) / 1000));
        } else if (start_date != null && end_date == null) {
            filterList.add(new ScanFilter("create_time").gt(start_date.getTime() / 1000 - 1));
        } else if (start_date == null && end_date != null) {
            filterList.add(new ScanFilter("create_time").lt((end_date.getTime() + 1000 * 60 * 60 * 24) / 1000));
        }


        if (StringUtils.isNotBlank(req_from)) {
            filterList.add(new ScanFilter("req_from").eq(req_from));
        }
        if (StringUtils.isNotBlank(phone)) {
            filterList.add(new ScanFilter("phone").eq(phone));
        }

        if (booking_id != null) {
            filterList.add(new ScanFilter("booking_id").eq(booking_id));
        }

        if (area_id != null) {
            filterList.add(new ScanFilter("area_id").eq(area_id));
        }

        if (capsule_id != null) {
            filterList.add(new ScanFilter("capsule_id").eq(capsule_id));
        }

        if (uin != null) {
            filterList.add(new ScanFilter("uin").eq(uin));
        }

        if (StringUtils.isNotBlank(app_version)) {
            filterList.add(new ScanFilter("app_version").eq(app_version));
        }

        if (StringUtils.isNotBlank(client_type)) {
            filterList.add(new ScanFilter("client_type").eq(client_type));
        }

        if (StringUtils.isNotBlank(client_version)) {
            filterList.add(new ScanFilter("client_version").eq(client_version));
        }


        if (op_status != null) {
            filterList.add(new ScanFilter("op_status").eq(op_status));
        }

        scanSpec.withScanFilters(filterList.toArray(new ScanFilter[]{}));

        scanSpec.withMaxResultSize(100);

        List<FailureReport> failureReportList = failureReportDao.scan(scanSpec);

        if (failureReportList != null) {
            return new Result(CodeMsg.SUCCESS).putData("failureList", failureReportService.mapperArea(failureReportList));
        } else {
            return new Result(CodeMsg.SUCCESS).putData("failureList", new FailureReport[0]);
        }
    }


    @GetMapping("/api/failure/{capsule_id}/{create_time}")
    @ResponseBody
    public Result get(@PathVariable("capsule_id") long capsule_id, @PathVariable("create_time") long create_time) {
        FailureReport failureReport = failureReportDao.getItem(new KeyAttribute("capsule_id", capsule_id), new KeyAttribute("create_time", create_time));
        if (failureReport == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        FailureReportRelation failureReportRelation = failureReportService.mapperArea(failureReport);
        return new Result(CodeMsg.SUCCESS).putData("failure", failureReportRelation);
    }


    @PostMapping("/api/failure/{capsule_id:\\d+}/{create_time:\\d+}/update/review")
    @ResponseBody
    public Result review(@PathVariable("capsule_id") long capsule_id, @PathVariable("create_time") long create_time, @RequestBody FailureReport failureReport) throws Exception {
        UpdateItemSpec updateItemSpec = new UpdateItemSpec();
        updateItemSpec.withPrimaryKey(new KeyAttribute("capsule_id", capsule_id), new KeyAttribute("create_time", create_time));
        updateItemSpec.withAttributeUpdate(
                StringUtils.isNotBlank(failureReport.getOp_description()) ?
                        new AttributeUpdate("op_description").put(failureReport.getOp_description()) :
                        new AttributeUpdate("op_description").delete(),
                new AttributeUpdate("op_status").put(failureReport.getOp_status())
        );
        failureReportDao.updateItem(updateItemSpec);
        return new Result(CodeMsg.SUCCESS);
    }


    @PostMapping("/api/failure/create")
    @ResponseBody
    public Result create(@RequestBody FailureReport failureReport) throws Exception {
        if (failureReport.getCapsule_id() == null) {
            return new Result(-1, "头等舱编号不能为空");
        }
        Date now = new Date();
        failureReport.setCreate_time(now.getTime() / 1000);
        failureReport.setCreate_from_role("op");
        failureReportDao.putItem(failureReport);
        return new Result(CodeMsg.SUCCESS);
    }


    @GetMapping("/api/failure/makeByUin/{uin:\\d+}")
    @ResponseBody
    public Result makeByUin(@PathVariable("uin") Integer uin) throws Exception {
        UserInfo userInfo = userInfoDao.getItem(new PrimaryKey("uin", uin));
        if (userInfo == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        FailureReportRelation failureReportRelation = new FailureReportRelation();
        failureReportRelation.setUin(userInfo.getUin());
        failureReportRelation.setPhone(userInfo.getPhone());
        return new Result(CodeMsg.SUCCESS).putData("failure", failureReportRelation);
    }

    @GetMapping("/api/failure/makeByPhone/{phone:\\d+}")
    @ResponseBody
    public Result makeByPhone(@PathVariable("phone") String phone) throws Exception {
        List<UserInfo> userInfoList = userInfoDao.scan(new ScanSpec().withScanFilters(new ScanFilter("phone").eq(phone)));
        if (userInfoList == null || userInfoList.size() == 0) {
            return new Result(CodeMsg.NO_FOUND);
        }
        UserInfo userInfo = userInfoList.get(0);
        FailureReportRelation failureReportRelation = new FailureReportRelation();
        failureReportRelation.setUin(userInfo.getUin());
        failureReportRelation.setPhone(userInfo.getPhone());
        return new Result(CodeMsg.SUCCESS).putData("failure", failureReportRelation);
    }

    @GetMapping("/api/failure/makeByArea/{area_id:\\d+}")
    @ResponseBody
    public Result makeByArea(@PathVariable("area_id") Integer area_id) throws Exception {

        Area area = areaDao.getItem(new PrimaryKey("area_id", area_id));
        if (area == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        FailureReportRelation failureReportRelation = new FailureReportRelation();
        failureReportRelation.setArea_id(area_id);
        failureReportRelation.setAreaObj(area);
        return new Result(CodeMsg.SUCCESS).putData("failure", failureReportRelation);
    }

    @GetMapping("/api/failure/makeByCapsule/{capsule_id:\\d+}")
    @ResponseBody
    public Result makeByCapsule(@PathVariable("capsule_id") Long capsule_id) throws Exception {

        Capsule capsule = capsuleDao.getItem(new PrimaryKey("capsule_id", capsule_id));
        if (capsule == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        FailureReportRelation failureReportRelation = new FailureReportRelation();
        failureReportRelation.setCapsule_id(capsule_id);
        Area area = areaDao.getItem(new PrimaryKey("area_id", capsule.getArea_id()));
        if (area != null) {
            failureReportRelation.setArea_id(capsule.getArea_id());
            failureReportRelation.setAreaObj(area);
        }
        return new Result(CodeMsg.SUCCESS).putData("failure", failureReportRelation);
    }

    @GetMapping("/api/failure/makeByBooking/{booking_id:\\d+}")
    @ResponseBody
    public Result makeByBooking(@PathVariable("booking_id") Long booking_id) throws Exception {
        Booking booking = bookingDao.getItem(new PrimaryKey("booking_id", booking_id));
        if (booking == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        FailureReportRelation failureReportRelation = new FailureReportRelation();
        failureReportRelation.setBooking_id(booking_id);
        failureReportRelation.setUin(booking.getUin());
        failureReportRelation.setCapsule_id(booking.getCapsule_id());
        failureReportRelation.setArea_id(booking.getArea_id());
        Area area = areaDao.getItem(new PrimaryKey("area_id", booking.getArea_id()));
        if (area != null) {
            failureReportRelation.setArea_id(booking.getArea_id());
            failureReportRelation.setAreaObj(area);
        }
        return new Result(CodeMsg.SUCCESS).putData("failure", failureReportRelation);
    }

}
