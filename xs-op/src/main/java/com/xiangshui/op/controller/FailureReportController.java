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
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

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
    public String index(HttpServletRequest request) {
        setClient(request);
        return "failure_manage";
    }


    @GetMapping("/api/failure/search")
    @ResponseBody
    public Result search(HttpServletRequest request, HttpServletResponse response,
                         FailureReport criteria, Date start_date, Date end_date, Boolean download) throws NoSuchFieldException, IllegalAccessException, IOException {
        if (download == null) {
            download = false;
        }
        List<FailureReport> failureReportList = failureReportService.search(criteria, start_date, end_date, download ? 5000 : null);
        List<FailureReportRelation> failureReportRelationList;
        if (failureReportList != null) {
            failureReportRelationList = failureReportService.mapperArea(failureReportList);
        } else {
            failureReportRelationList = new ArrayList<FailureReportRelation>();
        }
        if (download) {
            List<List<String>> data = new ArrayList<List<String>>(failureReportRelationList.size() + 1);
            List<String> headRow = new ArrayList<String>();
            headRow.add("头等舱编号");
            headRow.add("场地编号	");
            headRow.add("场地名称	");
            headRow.add("城市");
            headRow.add("地址");
            headRow.add("用户uin	");
            headRow.add("用户手机号");
            headRow.add("订单编号	");
            headRow.add("报修时间	");
            headRow.add("req_from");
            headRow.add("app_version");
            headRow.add("client_type");
            headRow.add("client_version");
            headRow.add("tags");
            headRow.add("用户描述");
            headRow.add("处理结果	");
            headRow.add("处理状态	");
            data.add(headRow);
            failureReportRelationList.forEach(new Consumer<FailureReportRelation>() {
                public void accept(FailureReportRelation failureReportRelation) {
                    List<String> row = new ArrayList<String>();
                    row.add(failureReportRelation.getCapsule_id() + "");
                    row.add(failureReportRelation.getArea_id() + "");
                    if (failureReportRelation.get_area() != null) {
                        row.add(failureReportRelation.get_area().getTitle() + "");
                        row.add(failureReportRelation.get_area().getCity() + "");
                        row.add(failureReportRelation.get_area().getAddress() + "");
                    } else {
                        row.add("");
                        row.add("");
                        row.add("");
                    }
                    row.add(failureReportRelation.getUin() + "");
                    row.add(failureReportRelation.getPhone() + "");
                    row.add(failureReportRelation.getBooking_id() + "");
                    row.add(failureReportRelation.getCreate_time() + "");
                    row.add(failureReportRelation.getReq_from() + "");
                    row.add(failureReportRelation.getApp_version() + "");
                    row.add(failureReportRelation.getClient_type() + "");
                    row.add(failureReportRelation.getClient_version() + "");
                    row.add(failureReportRelation.getTags() + "");
                    row.add(failureReportRelation.getDescription() + "");
                    row.add(failureReportRelation.getOp_description() + "");
                    row.add(failureReportRelation.getOp_status() + "");
                    data.add(row);
                }
            });
            HSSFWorkbook workbook = ExcelUtils.export(data);
            response.addHeader("Content-Disposition", "attachment;filename=" + new String("故障报修.xlsx".getBytes()));
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            workbook.close();
            return null;
        } else {
            return new Result(CodeMsg.SUCCESS).putData("failureList", failureReportRelationList);
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
        failureReportRelation.set_area(area);
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
            failureReportRelation.set_area(area);
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
        failureReportRelation.setCapsule_id(booking.getCapsule_id());
        failureReportRelation.setArea_id(booking.getArea_id());
        failureReportRelation.setUin(booking.getUin());
        Area area = areaDao.getItem(new PrimaryKey("area_id", booking.getArea_id()));
        if (area != null) {
            failureReportRelation.setArea_id(booking.getArea_id());
            failureReportRelation.set_area(area);
        }
        UserInfo userInfo = userService.getUserInfoByUin(booking.getUin());
        if (userInfo != null) {
            failureReportRelation.setPhone(userInfo.getPhone());
        }
        return new Result(CodeMsg.SUCCESS).putData("failure", failureReportRelation);
    }

}
