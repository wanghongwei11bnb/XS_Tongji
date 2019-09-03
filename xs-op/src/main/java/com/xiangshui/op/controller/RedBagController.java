package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.domain.RedBag;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.ListUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Controller
public class RedBagController extends BaseController {


    @Menu("周年活动奖品记录")
    @AuthRequired(AuthRequired.auth_red_bag)
    @GetMapping("/red_bag_manage")
    public String view(HttpServletRequest request) {
        setClient(request);
        return "red_bag_manage";
    }

    @GetMapping("/api/red_bag/search")
    @ResponseBody
    public Result search(HttpServletResponse response, RedBag demo, String phone, Date create_date_start, Date create_date_end, Boolean download) throws NoSuchFieldException, IllegalAccessException, IOException {
        if (download == null) download = false;
        if (demo == null) demo = new RedBag();
        List<ScanFilter> scanFilterList = redBagDao.makeScanFilterList(demo, new String[]{
                "uin",
                "booking_id",
                "type",
                "price_title",
                "status",
        });
        redBagDao.appendDateRangeFilter(scanFilterList, "create_time", create_date_start, create_date_end);
        if (demo.getUin() == null && StringUtils.isNotBlank(phone) && cacheScheduled.phoneUinMap.containsKey(phone)) {
            scanFilterList.add(new ScanFilter("uin").eq(cacheScheduled.phoneUinMap.get(phone)));
        }
        ScanSpec scanSpec = new ScanSpec();
        scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        List<RedBag> redBagList = redBagDao.scan(scanSpec);
        redBagList.sort((o1, o2) -> o2.getCreate_time().compareTo(o1.getCreate_time()));

        if (download) {
            ExcelUtils.export(Arrays.asList(
                    new ExcelUtils.Column<RedBag>("id") {
                        @Override
                        public Object render(RedBag redBag) {
                            return redBag.getId();
                        }
                    },
                    new ExcelUtils.Column<RedBag>("活动名称") {
                        @Override
                        public Object render(RedBag redBag) {
                            switch (redBag.getType()) {
                                case 1:
                                    return "单单返现金";
                                case 2:
                                    return "幸运大转盘";
                                default:
                                    return redBag.getType();
                            }
                        }
                    },
                    new ExcelUtils.Column<RedBag>("用户编号") {
                        @Override
                        public Object render(RedBag redBag) {
                            return redBag.getUin();
                        }
                    },
                    new ExcelUtils.Column<RedBag>("用户手机号") {
                        @Override
                        public Object render(RedBag redBag) {
                            return cacheScheduled.phoneUinMap.containsKey(redBag.getUin()) ? cacheScheduled.phoneUinMap.get(redBag.getUin()) : null;
                        }
                    },
                    new ExcelUtils.Column<RedBag>("奖品") {
                        @Override
                        public Object render(RedBag redBag) {
                            return redBag.getPrice_title();
                        }
                    },
                    new ExcelUtils.Column<RedBag>("奖品") {
                        @Override
                        public Object render(RedBag redBag) {
                            if ("现金红包".equals(redBag.getPrice_title())) {
                                return redBag.getPrice() / 100 + "元现金红包";
                            } else if ("雨露均沾奖".equals(redBag.getPrice_title())) {
                                return "满" + redBag.getMin_price() / 100 + "减" + redBag.getCash() / 100 + "优惠券";
                            }
                            return null;
                        }
                    },
                    new ExcelUtils.Column<RedBag>("领取状态") {
                        @Override
                        public Object render(RedBag redBag) {
                            if (new Integer(1).equals(redBag.getStatus())) return "已领取";
                            return "未领取";
                        }
                    },
                    new ExcelUtils.Column<RedBag>("领取时间") {
                        @Override
                        public Object render(RedBag redBag) {
                            return redBag.getReceive_time() != null ? DateUtils.format(redBag.getReceive_time() * 1000) : null;
                        }
                    },
                    new ExcelUtils.Column<RedBag>("订单编号") {
                        @Override
                        public Object render(RedBag redBag) {
                            return redBag.getBooking_id();
                        }
                    }
            ), redBagList, response, "red_bag.xlsx");
            return null;
        }
        return new Result(CodeMsg.SUCCESS)
                .putData("redBagList", redBagList)
                .putData("userInfoList", cacheScheduled.userInfoMapOptions.selectByPrimarys(ListUtils.fieldSet(redBagList, RedBag::getUin)))
                ;
    }

    @GetMapping("/api/red_bag/{red_bag_id:\\d+}")
    @ResponseBody
    public Result get(@PathVariable("red_bag_id") Long red_bag_id) {
        RedBag redBag = redBagDao.getItem(new PrimaryKey("id", red_bag_id));
        if (redBag == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        return new Result(CodeMsg.SUCCESS).putData("redBag", redBag);
    }


    @PostMapping("/api/red_bag/{red_bag_id:\\d+}/update")
    @ResponseBody
    public Result update(@PathVariable("red_bag_id") Long red_bag_id, RedBag demo) throws Exception {
        RedBag redBag = redBagDao.getItem(new PrimaryKey("id", red_bag_id));
        if (redBag == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        redBagDao.updateItem(new PrimaryKey("id", red_bag_id), demo, new String[]{
                "status",
        });
        return new Result(CodeMsg.SUCCESS);
    }


    @PostMapping("/api/red_bag/{red_bag_id:\\d+}/delete")
    @ResponseBody
    public Result delete(@PathVariable("red_bag_id") Long red_bag_id) throws Exception {
        RedBag redBag = redBagDao.getItem(new PrimaryKey("id", red_bag_id));
        if (redBag == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        redBagDao.deleteItem(new PrimaryKey("id", red_bag_id));
        return new Result(CodeMsg.SUCCESS);
    }

}
