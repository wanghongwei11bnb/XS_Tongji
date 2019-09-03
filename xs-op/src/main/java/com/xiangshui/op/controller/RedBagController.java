package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.domain.RedBag;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
    public Result search(RedBag demo, String phone, Date create_date_start, Date create_date_end) throws NoSuchFieldException, IllegalAccessException {
        List<ScanFilter> scanFilterList = redBagDao.makeScanFilterList(demo, new String[]{
                "uin",
                "booking_id",
                "type",
                "price_title",
                "status",
        });
        redBagDao.appendDateRangeFilter(scanFilterList, "create_time", create_date_start, create_date_end);
        ScanSpec scanSpec = new ScanSpec();
        scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        List<RedBag> redBagList = redBagDao.scan(scanSpec);
        return new Result(CodeMsg.SUCCESS)
                .putData("redBagList", redBagList)
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
