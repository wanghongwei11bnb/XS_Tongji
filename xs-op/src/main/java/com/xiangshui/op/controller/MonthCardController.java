package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.threadLocal.UsernameLocal;
import com.xiangshui.server.dao.BaseDynamoDao;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.ChargeRecord;
import com.xiangshui.server.domain.MonthCardRecode;
import com.xiangshui.server.service.MonthCardService;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

@Controller
public class MonthCardController extends BaseController {

    @Autowired
    MonthCardService monthCardService;

    @Menu("月卡管理")
    @AuthRequired("月卡管理")
    @GetMapping("/month_card_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "month_card_manage";
    }


    @AuthRequired("月卡管理")
    @GetMapping("/api/month_card_recode/search")
    @ResponseBody
    public Result search(HttpServletResponse response, MonthCardRecode criteria, Date create_date_start, Date create_date_end, Date end_time_start, Date end_time_end, Boolean download) throws NoSuchFieldException, IllegalAccessException, IOException {
        if (download == null) {
            download = false;
        }
        if (download) {
            String op_username = UsernameLocal.get();
            boolean auth_month_card_download = opUserService.getAuthSet(op_username).contains(AuthRequired.auth_month_card_download);
            if (!auth_month_card_download) {
                return new Result(CodeMsg.AUTH_FAIL);
            }
        }
        List<MonthCardRecode> monthCardRecodeList = monthCardService.search(criteria, create_date_start, create_date_end, end_time_start, end_time_end, null, download);
        if (download) {
            Date now = new Date();
            List<List<Object>> data = new ArrayList<>();
            List<Object> headRow = new ArrayList<>();
            headRow.add("用户uin");
            headRow.add("卡号／手机号");
            headRow.add("城市");
            headRow.add("当天剩余月卡时长");
            headRow.add("截止日期");
            headRow.add("月卡状态");
            data.add(headRow);
            if (monthCardRecodeList != null && monthCardRecodeList.size() > 0) {
                monthCardRecodeList.forEach(new Consumer<MonthCardRecode>() {
                    @Override
                    public void accept(MonthCardRecode monthCardRecode) {
                        if (monthCardRecode == null) {
                            return;
                        }
                        List<Object> row = new ArrayList<>();
                        row.add(String.valueOf(monthCardRecode.getUin()));
                        row.add(String.valueOf(monthCardRecode.getCard_no()));
                        row.add(monthCardRecode.getCity());
                        Date end_date = new Date(monthCardRecode.getEnd_time() * 1000);
                        if (end_date.getTime() - now.getTime() > 0) {
                            long left_seconds = monthCardRecode.getLeft_seconds() != null ? monthCardRecode.getLeft_seconds() : 0;
                            if (!(
                                    monthCardRecode.getUpdate_time() != null
                                            && DateUtils.format(new Date(monthCardRecode.getUpdate_time() * 1000), "yyyy-MM-dd").equals(DateUtils.format(now, "yyyy-MM-dd"))
                            )) {
                                left_seconds = (long) (60 * 60);
                            }
                            row.add(left_seconds / 60 + "分钟");

                        } else {
                            row.add("");
                        }
                        row.add(DateUtils.format(end_date, "yyyy-MM-dd"));
                        row.add(end_date.getTime() - now.getTime() > 0 ? "生效中" : "已过期");

                        data.add(row);
                    }
                });
            }

            XSSFWorkbook workbook = ExcelUtils.export(data);
            response.addHeader("Content-Disposition", "attachment;filename=" + new String("monthCard.xlsx".getBytes()));
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            workbook.close();
            return null;
        } else {
            return new Result(CodeMsg.SUCCESS).putData("monthCardRecodeList", monthCardRecodeList);
        }
    }


    @AuthRequired("月卡管理")
    @GetMapping("/api/month_card_recode/uin_{uin:\\d+}")
    @ResponseBody
    public Result getByUin(@PathVariable("uin") int uin) {
        MonthCardRecode monthCardRecode = monthCardService.getMonthCardRecodeByUin(uin, null);
        if (monthCardRecode == null) {
            return new Result(CodeMsg.NO_FOUND);
        } else {
            return new Result(CodeMsg.SUCCESS).putData("monthCardRecode", monthCardRecode);
        }
    }


    @AuthRequired("月卡管理")
    @GetMapping("/api/month_card_recode/uin_{phone:\\d+}")
    @ResponseBody
    public Result getByUin(@PathVariable("phone") String phone) {
        MonthCardRecode monthCardRecode = monthCardService.getMonthCardRecodeByPhone(phone, null);
        if (monthCardRecode == null) {
            return new Result(CodeMsg.NO_FOUND);
        } else {
            return new Result(CodeMsg.SUCCESS).putData("monthCardRecode", monthCardRecode);
        }
    }


    @Menu("月卡购买记录")
    @AuthRequired("月卡管理")
    @GetMapping("/month_card_charge_record_manage")
    public String month_card_charge_record_manage(HttpServletRequest request) {
        setClient(request);
        return "month_card_charge_record_manage";
    }


    @AuthRequired("月卡管理")
    @GetMapping("/api/month_card_charge_record/search")
    @ResponseBody
    public Result month_card_charge_record_search(HttpServletResponse response, ChargeRecord criteria, Date date_start, Date date_end, Boolean download) throws NoSuchFieldException, IllegalAccessException, IOException {
        if (download == null) download = false;
        List<ScanFilter> scanFilterList = chargeRecordDao.makeScanFilterList(criteria, new String[]{
                "uin",
                "bill_area_id",
                "bill_booking_id",
        });
        scanFilterList.add(new ScanFilter("status").eq(1));
        scanFilterList.add(new ScanFilter("subject").in(new String[]{"享+-月卡充值", "享+-季卡充值"}));
        chargeRecordDao.appendDateRangeFilter(scanFilterList, "update_time", date_start, date_end);
        ScanSpec scanSpec = new ScanSpec().withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        if (download) scanSpec.withMaxResultSize(BaseDynamoDao.maxDownloadSize);
        List<ChargeRecord> chargeRecordList = chargeRecordDao.scan(scanSpec);
        if (chargeRecordList != null && chargeRecordList.size() > 0) {
            chargeRecordList.sort((o1, o2) -> o2.getUpdate_time().compareTo(o1.getUpdate_time()));
        }
        if (download) {
            ExcelUtils.export(Arrays.asList(
                    new ExcelUtils.Column<ChargeRecord>("out_trade_no") {
                        @Override
                        public Object render(ChargeRecord chargeRecord) {
                            return chargeRecord.getOut_trade_no();
                        }
                    },
                    new ExcelUtils.Column<ChargeRecord>("交易时间") {
                        @Override
                        public Object render(ChargeRecord chargeRecord) {
                            return chargeRecord.getUpdate_time() != null ? DateUtils.format(chargeRecord.getUpdate_time() * 1000) : null;
                        }
                    },
                    new ExcelUtils.Column<ChargeRecord>("业务类型") {
                        @Override
                        public Object render(ChargeRecord chargeRecord) {
                            return chargeRecord.getSubject();
                        }
                    },
                    new ExcelUtils.Column<ChargeRecord>("交易金额") {
                        @Override
                        public Object render(ChargeRecord chargeRecord) {
                            return chargeRecord.getPrice() != null ? chargeRecord.getPrice() / 100f : null;
                        }
                    },
                    new ExcelUtils.Column<ChargeRecord>("定位城市") {
                        @Override
                        public Object render(ChargeRecord chargeRecord) {
                            return chargeRecord.getCity();
                        }
                    },
                    new ExcelUtils.Column<ChargeRecord>("用户编号") {
                        @Override
                        public Object render(ChargeRecord chargeRecord) {
                            return chargeRecord.getUin();
                        }
                    },
                    new ExcelUtils.Column<ChargeRecord>("用户手机号") {
                        @Override
                        public Object render(ChargeRecord chargeRecord) {
                            return chargeRecord.getPhone();
                        }
                    },
                    new ExcelUtils.Column<ChargeRecord>("场地编号") {
                        @Override
                        public Object render(ChargeRecord chargeRecord) {
                            return chargeRecord.getBill_area_id();
                        }
                    },
                    new ExcelUtils.Column<ChargeRecord>("订单编号") {
                        @Override
                        public Object render(ChargeRecord chargeRecord) {
                            return chargeRecord.getBill_booking_id();
                        }
                    }
            ), chargeRecordList, response, "chargeRecordList.xlsx");
            return null;
        } else {
            return new Result(CodeMsg.SUCCESS).putData("chargeRecordList", chargeRecordList);
        }
    }


}
