package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.dao.AllVerifyDao;
import com.xiangshui.server.domain.AllVerify;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Controller
public class AllVerifyController extends BaseController {

    @Autowired
    AllVerifyDao allVerifyDao;

    @Menu("优惠券兑换码")
    @AuthRequired("优惠券兑换码管理")
    @GetMapping("/all_verify_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "all_verify_manage";
    }


    @AuthRequired("优惠券兑换码管理")
    @GetMapping("/api/all_verify/search")
    @ResponseBody
    public Result search(HttpServletRequest request, HttpServletResponse response, AllVerify criteria) throws NoSuchFieldException, IllegalAccessException {
        if (criteria == null) criteria = new AllVerify();
        ScanSpec scanSpec = new ScanSpec();
        List<ScanFilter> scanFilterList = allVerifyDao.makeScanFilterList(criteria, new String[]{
                "verify_code",
        });
        if (scanFilterList != null && scanFilterList.size() > 0) {
            scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        }
        List<AllVerify> allVerifyList = allVerifyDao.scan(scanSpec);
        if (allVerifyList != null && allVerifyList.size() > 0) {
            allVerifyList.sort(new Comparator<AllVerify>() {
                @Override
                public int compare(AllVerify o1, AllVerify o2) {
                    return (int) (o2.getEnd_time() - o1.getEnd_time());
                }
            });
        }
        return new Result(CodeMsg.SUCCESS).putData("allVerifyList", allVerifyList);
    }


    @AuthRequired("优惠券兑换码管理")
    @GetMapping("/api/all_verify/{verify_code:.+}")
    @ResponseBody
    public Result all_verify(@PathVariable("verify_code") String verify_code) {
        AllVerify allVerify = allVerifyDao.getItem(new PrimaryKey("verify_code", verify_code));
        if (allVerify == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        return new Result(CodeMsg.SUCCESS).putData("allVerify", allVerify);
    }


    protected void checkAllVerify(AllVerify criteria) {
        if (criteria == null) {
            throw new XiangShuiException("参数不能为空");
        }
        if (criteria.getStart_time() == null) {
            throw new XiangShuiException("有效期不能为空");
        }
        if (criteria.getEnd_time() == null) {
            throw new XiangShuiException("有效期不能为空");
        }

        if (criteria.getType() == null) {
            throw new XiangShuiException("优惠券类型不能为空");
        }
        if (criteria.getType() == 0) {
            if (criteria.getRed_envelope() == null || criteria.getRed_envelope() <= 0) {
                throw new XiangShuiException("红包金额必须大于0");
            }
        } else if (criteria.getType() == 1) {
            if (criteria.getMin_price() == null || criteria.getMin_price() <= 0) {
                throw new XiangShuiException("最低消费金额必须大于0");
            }
            if (criteria.getCash() == null || criteria.getCash() <= 0) {
                throw new XiangShuiException("减免金额必须大于0");
            }
        } else {
            throw new XiangShuiException("优惠券类型错误");
        }
    }


    @AuthRequired("优惠券兑换码管理")
    @PostMapping("/api/all_verify/{verify_code:.+}/update")
    @ResponseBody
    public Result update(@PathVariable("verify_code") String verify_code, AllVerify criteria, Date start_time_date, Date end_time_date) throws Exception {
        AllVerify allVerify = allVerifyDao.getItem(new PrimaryKey("verify_code", verify_code));
        if (allVerify == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        if (start_time_date != null) {
            criteria.setStart_time(start_time_date.getTime() / 1000);
        }
        if (end_time_date != null) {
            criteria.setEnd_time((end_time_date.getTime() + 1000 * 60 * 60 * 24) / 1000 - 1);
        }
        checkAllVerify(criteria);
        allVerifyDao.updateItem(new PrimaryKey("verify_code", verify_code), criteria, new String[]{
                "type",
                "red_envelope",
                "cash",
                "min_price",
                "start_time",
                "end_time",
                "ban_old",
        });
        return new Result(CodeMsg.SUCCESS);
    }


    @AuthRequired("优惠券兑换码管理")
    @PostMapping("/api/all_verify/create")
    @ResponseBody
    public Result create(AllVerify criteria, Date start_time_date, Date end_time_date) throws Exception {
        if (criteria == null) {
            return new Result(-1, "参数不能为空");
        }
        if (StringUtils.isBlank(criteria.getVerify_code())) {
            return new Result(-1, "兑换码不能为空");
        }

        if (start_time_date != null) {
            criteria.setStart_time(start_time_date.getTime() / 1000);
        }
        if (end_time_date != null) {
            criteria.setEnd_time((end_time_date.getTime() + 1000 * 60 * 60 * 24) / 1000 - 1);
        }
        AllVerify allVerify = allVerifyDao.getItem(new PrimaryKey("verify_code", criteria.getVerify_code()));
        if (allVerify != null) {
            return new Result(-1, "兑换码已存在");
        }
        checkAllVerify(criteria);
        allVerifyDao.putItem(criteria);
        return new Result(CodeMsg.SUCCESS);
    }


    @AuthRequired("优惠券兑换码管理")
    @PostMapping("/api/all_verify/{verify_code:.+}/delete")
    @ResponseBody
    public Result delete(@PathVariable("verify_code") String verify_code) throws Exception {
        AllVerify allVerify = allVerifyDao.getItem(new PrimaryKey("verify_code", verify_code));
        if (allVerify == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        allVerifyDao.deleteItem(new PrimaryKey("verify_code", verify_code));
        return new Result(CodeMsg.SUCCESS);
    }

}
