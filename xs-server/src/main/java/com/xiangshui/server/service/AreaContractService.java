package com.xiangshui.server.service;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.server.constant.AreaContractStatusOption;
import com.xiangshui.server.dao.AreaContractDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.AreaContract;
import com.xiangshui.server.domain.fragment.RangeRatio;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.util.web.result.CodeMsg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AreaContractService {

    @Autowired
    OpUserService opUserService;

    @Autowired
    AreaContractDao areaContractDao;

    @Autowired
    AreaService areaService;

    public AreaContract getByAreaId(int area_id) {
        return areaContractDao.getItem(new PrimaryKey("area_id", area_id));
    }


    public void validateCustomer(AreaContract criteria) {
        if (StringUtils.isBlank(criteria.getCustomer())) throw new XiangShuiException("客户公司名称不能为空");
        if (StringUtils.isBlank(criteria.getBank_account_name())) throw new XiangShuiException("客户银行付款账户不能为空");
        if (StringUtils.isBlank(criteria.getBank_account())) throw new XiangShuiException("客户银行付款帐号不能为空");
        if (StringUtils.isBlank(criteria.getBank_branch())) throw new XiangShuiException("客户银行支行信息不能为空");
        if (criteria.getAccount_ratio() == null) throw new XiangShuiException("分账比例不能为空");
        if (!(0 <= criteria.getAccount_ratio() && criteria.getAccount_ratio() < 100)) {
            throw new XiangShuiException("分账比例必须在0～100之间");
        }

    }


    public void updateForSaler(AreaContract criteria, String saler_username) throws Exception {

        if (criteria == null) throw new XiangShuiException("参数不能为空");
        if (criteria.getArea_id() == null) throw new XiangShuiException("场地编号不能为空");
        AreaContract areaContract = getByAreaId(criteria.getArea_id());
        if (areaContract == null) throw new XiangShuiException(CodeMsg.NO_FOUND);

        if (areaContract.getStatus() != null && areaContract.getStatus().equals(AreaContractStatusOption.adopt.value)) {
            throw new XiangShuiException("已审核通过，不能再次修改");
        }

        if (StringUtils.isNotBlank(saler_username)) {
            Op op = opUserService.getOpByUsername(saler_username, null);
            if (StringUtils.isBlank(op.getFullname())) throw new XiangShuiException("请设置您的姓名");
            if (StringUtils.isBlank(op.getCity())) throw new XiangShuiException("请设置您的城市");
            if (!op.getCity().equals(areaContract.getSaler_city())) throw new XiangShuiException(CodeMsg.AUTH_FAIL);
            if (!op.getFullname().equals(areaContract.getSaler())) throw new XiangShuiException(CodeMsg.AUTH_FAIL);
        }
        validateCustomer(criteria);


        Date now = new Date();
        criteria.setUpdate_time(now.getTime() / 1000);
        areaContractDao.updateItem(new PrimaryKey("area_id", criteria.getArea_id()), criteria, new String[]{
                "saler",
                "saler_city",
                "customer",
                "customer_email",
                "customer_contact",
                "account_ratio",
                "range_ratio_list",
                "bank_account",
                "bank_branch",
                "remark",
                "status",
        });
    }


    public Integer checkAccountRatio(AreaContract areaContract, int count_price) {
        Integer account_ratio = areaContract.getAccount_ratio();
        List<RangeRatio> rangeRatioList = areaContract.getRange_ratio_list();
        if (rangeRatioList != null && rangeRatioList.size() > 0) {
            for (RangeRatio rangeRatio : rangeRatioList) {
                if (rangeRatio != null && rangeRatio.getAccount_ratio() != null) {
                    if ((rangeRatio.getLte() == null || rangeRatio.getLte() <= count_price)
                            && (rangeRatio.getGte() == null || count_price <= rangeRatio.getGte())) {
                        account_ratio = rangeRatio.getAccount_ratio();
                        break;
                    }
                }
            }
        }
        return account_ratio;
    }

}
