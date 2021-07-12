package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.scheduled.CacheScheduled;
import com.xiangshui.op.threadLocal.SessionLocal;
import com.xiangshui.op.threadLocal.UsernameLocal;
import com.xiangshui.op.tool.ExcelTools;
import com.xiangshui.server.constant.BookingStatusOption;
import com.xiangshui.server.constant.CapsuleStatusOption;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.job.ReportFormJob;
import com.xiangshui.server.relation.BookingRelation;
import com.xiangshui.server.service.*;
import com.xiangshui.server.tool.BookingGroupTool;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.ListUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class ReportFormController extends BaseController {

    @Autowired
    ReportFormJob reportFormJob;

    @GetMapping("/api/report_form/export")
    @ResponseBody
    @AuthRequired("报表导出")
    public void export(@RequestParam LocalDate start, @RequestParam LocalDate end, HttpServletResponse response) throws IOException {


        ExcelUtils.export(Arrays.asList(
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("区域") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getRegion();
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("城市") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getCity();
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("场地编号") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getArea_id();
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("场地名称") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getArea_title();
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("舱数") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getCapsule_count();
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("订单总收入") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getIncome_total() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("单舱总收入") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getIncome_total_each_capsule() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("订单量") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getBooking_count();
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("真实订单收入") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getActive_income_total() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("真实单舱收入") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getActive_income_total_each_capsule() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("真实订单量") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getActive_booking_count();
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("真实单舱订单量") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getActive_booking_count_each_capsule();
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("现金收入") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getPrice_income_total() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("单舱现金收入") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getPrice_income_each_capsule() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("订单现金收入(不含月卡押金）") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getPrice_income_booking() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("月卡") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getPrice_income_card_1() / 100f;
                    }
                },
                new ExcelUtils.Column<ReportFormJob.ReportFormRow>("季卡") {
                    @Override
                    public Object render(ReportFormJob.ReportFormRow reportFormRow) {
                        return reportFormRow.getPrice_income_card_3() / 100f;
                    }
                }
        ), reportFormJob.makeReportForm(start, end), response, "reportFormRowList.xlsx");


    }


}
