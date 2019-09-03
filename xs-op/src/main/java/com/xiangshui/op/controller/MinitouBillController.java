package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.scheduled.CacheScheduled;
import com.xiangshui.op.scheduled.CountCapsuleScheduled;
import com.xiangshui.op.scheduled.MinitouBillScheduled;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.domain.MinitouBill;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.ServiceUtils;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

import static com.xiangshui.op.annotation.AuthRequired.auth_minitou_investor;
import static com.xiangshui.op.annotation.AuthRequired.auth_minitou_op;

@Controller
public class MinitouBillController extends BaseController {

    @Autowired
    CacheScheduled cacheScheduled;

    @Autowired
    AreaDao areaDao;
    @Autowired
    CapsuleDao capsuleDao;
    @Autowired
    BookingDao bookingDao;
    @Autowired
    MinitouBillDao minitouBillDao;
    @Autowired
    CountCapsuleScheduled countCapsuleScheduled;
    @Autowired
    MinitouBillScheduled minitouBillScheduled;


    @Autowired
    AreaService areaService;


    @Menu("迷你投设备列表")
    @GetMapping("/mnt_capsule_manage")
    @AuthRequired(auth_minitou_investor)
    public String mnt_capsule_manage(HttpServletRequest request) {
        setClient(request);
        return "mnt_capsule_manage";
    }

    @Menu("迷你投订单列表")
    @GetMapping("/mnt_booking_manage")
    @AuthRequired(auth_minitou_investor)
    public String mnt_booking_manage(HttpServletRequest request) {
        setClient(request);
        return "mnt_booking_manage";
    }

    @Menu("迷你投设备报表")
    @GetMapping("/mnt_bill_manage")
    @AuthRequired(auth_minitou_investor)
    public String mnt_bill_manage(HttpServletRequest request) {
        setClient(request);
        return "mnt_bill_manage";
    }


    @GetMapping("/api/mnt/capsule/search")
    @AuthRequired(auth_minitou_investor)
    @ResponseBody
    public Result capsule_search() {
        List<Capsule> capsuleList = capsuleDao.scan(new ScanSpec().withScanFilters(new ScanFilter("capsule_id").in(minitouBillScheduled.capsuleIdSet.toArray())));
        if (capsuleList != null && capsuleList.size() > 0) {
            capsuleList.sort((o1, o2) -> (int) (o2.getCapsule_id() - o1.getCapsule_id()));
        }
        List<Area> areaList = areaService.getAreaListByCapsule(capsuleList, null);
        return new Result(CodeMsg.SUCCESS)
                .putData("capsuleList", capsuleList)
                .putData("areaList", areaList);
    }


    @GetMapping("/api/mnt/booking/search")
    @AuthRequired(auth_minitou_investor)
    @ResponseBody
    public Result booking_search(Date create_date_start, Date create_date_end, Long capsule_id) {
        if (create_date_start == null || create_date_end == null) {
            return new Result(-1, "请选择日期");
        }
        if (create_date_start.compareTo(create_date_end) > 0) {
            return new Result(-1, "开始如期不能大于结束日期");
        }
        if (capsule_id != null && !minitouBillScheduled.capsuleIdSet.contains(capsule_id)) {
            return new Result(-1, "设备编号错误");
        }

        List<ScanFilter> scanFilterList = new ArrayList<>();
        capsuleDao.appendDateRangeFilter(scanFilterList, "create_time", create_date_start, create_date_end);

        if (capsule_id != null) {
            scanFilterList.add(new ScanFilter("capsule_id").eq(capsule_id));
        } else {
            scanFilterList.add(new ScanFilter("capsule_id").in(minitouBillScheduled.capsuleIdSet.toArray()));
        }
        ScanSpec scanSpec = new ScanSpec()
                .withMaxResultSize(Integer.MAX_VALUE)
                .withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        List<Booking> bookingList = bookingDao.scan(scanSpec);
        if (bookingList != null && bookingList.size() > 0) {
            bookingList.sort((o1, o2) -> o2.getCreate_time().compareTo(o1.getCreate_time()));
        }
        return new Result(CodeMsg.SUCCESS)
                .putData("bookingList", bookingList)
                .putData("areaList", areaService.getAreaListByBooking(bookingList, null))
                .putData("countGroupArea", countCapsuleScheduled.countGroupArea);
    }


    @GetMapping("/api/mnt/bill/search")
    @AuthRequired(auth_minitou_investor)
    @ResponseBody
    public Result bill_search(HttpServletRequest request, HttpServletResponse response, Integer year, Integer month, Boolean download) throws IOException, NoSuchFieldException, IllegalAccessException {
        if (download == null) download = false;
        if (year == null || month == null) {
            return new Result(-1, "请选择月份");
        }
        List<ScanFilter> scanFilterList = minitouBillDao.makeScanFilterList(new MinitouBill().setYear(year).setMonth(month), new String[]{
                "year",
                "month",
        });
        List<MinitouBill> minitouBillList = minitouBillDao.scan(new ScanSpec()
                .withMaxResultSize(Integer.MAX_VALUE)
                .withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]))
        );
        if (download) {
            ExcelUtils.export(Arrays.asList(
                    new ExcelUtils.Column<MinitouBill>("设备编号") {
                        @Override
                        public String render(MinitouBill minitouBill) {
                            return String.valueOf(minitouBill.getCapsule_id());
                        }
                    },
//                    new ExcelUtils.Column<MinitouBill>("场地名称") {
//                        @Override
//                        public String render(MinitouBill minitouBill) {
//                            return cacheScheduled.areaMapOptions.containsKey(minitouBill.getArea_id()) ? cacheScheduled.areaMapOptions.get(minitouBill.getArea_id()).getTitle() : null;
//                        }
//                    },
                    new ExcelUtils.Column<MinitouBill>("场地分成比例") {
                        @Override
                        public Object render(MinitouBill minitouBill) {
                            return minitouBill.getAccount_ratio() + "%";
                        }
                    },
                    new ExcelUtils.Column<MinitouBill>("经营收入") {
                        @Override
                        public Object render(MinitouBill minitouBill) {
                            return minitouBill.getFinal_price() / 100f;
                        }
                    },
                    new ExcelUtils.Column<MinitouBill>("场地分成金额") {
                        @Override
                        public Object render(MinitouBill minitouBill) {
                            return minitouBill.getRatio_price() / 100f;
                        }
                    },
                    new ExcelUtils.Column<MinitouBill>("利润收入（扣除租金）") {
                        @Override
                        public Object render(MinitouBill minitouBill) {
                            return minitouBill.getRent_price() / 100f;
                        }
                    }
            ), minitouBillList, response, "bill_" + year + month + ".xlsx");
            return null;
        }
        return new Result(CodeMsg.SUCCESS)
                .putData("minitouBillList", minitouBillList)
                .putData("countGroupArea", countCapsuleScheduled.countGroupArea)
                .putData("areaList", cacheScheduled.areaMapOptions.values());
    }


    @PostMapping("/api/mnt/bill/checkout")
    @AuthRequired(auth_minitou_op)
    @ResponseBody
    public Result bill_checkout(HttpServletRequest request, HttpServletResponse response, Long capsule_id, Integer year, Integer month, Boolean preview) throws Exception {
        if (preview == null) preview = false;
        if (year == null || month == null) {
            return new Result(-1, "请选择月份");
        }
        if (capsule_id != null) {
            MinitouBill minitouBill = minitouBillScheduled.makeBill(capsule_id, year, month);
            if (preview) {
                return new Result(CodeMsg.SUCCESS)
                        .putData("minitouBillList", minitouBill != null ? new MinitouBill[]{minitouBill} : null)
                        .putData("countGroupArea", countCapsuleScheduled.countGroupArea)
                        .putData("areaList", cacheScheduled.areaMapOptions.values());
            } else {
                minitouBillScheduled.upsetMinitouBill(minitouBill);
                return new Result(CodeMsg.SUCCESS);
            }
        } else {
            List<MinitouBill> minitouBillList = minitouBillScheduled.makeBill(year, month);
            if (preview) {
                return new Result(CodeMsg.SUCCESS)
                        .putData("minitouBillList", minitouBillList)
                        .putData("countGroupArea", countCapsuleScheduled.countGroupArea)
                        .putData("areaList", cacheScheduled.areaMapOptions.values());
            } else {
                minitouBillScheduled.upsetMinitouBill(minitouBillList);
                return new Result(CodeMsg.SUCCESS);
            }
        }
    }
}
