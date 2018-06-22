package com.xiangshui.server.service;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.server.dao.AreaContractDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.AreaContract;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.util.web.result.CodeMsg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AreaContractService {


    @Autowired
    AreaContractDao areaContractDao;

    @Autowired
    AreaService areaService;

    public AreaContract getByAreaId(int area_id) {
        return areaContractDao.getItem(new PrimaryKey("area_id", area_id));
    }


    public void create(AreaContract criteria) {
        if (criteria == null) throw new XiangShuiException("参数不能为空");
        if (criteria.getArea_id() == null) throw new XiangShuiException("场地编号不能为空");
        if (getByAreaId(criteria.getArea_id()) != null) throw new XiangShuiException("该场地合同已存在");
        Area area = areaService.getAreaById(criteria.getArea_id());
        if (area == null) throw new XiangShuiException("场地不存在");
        if (criteria.getAccount_ratio() == null) throw new XiangShuiException("分账比例不能为空");
        if (!(0 <= criteria.getAccount_ratio() && criteria.getAccount_ratio() < 100))
            throw new XiangShuiException("分账比例必须在0～100之间");
        Date now = new Date();
        criteria.setCreate_time(now.getTime() / 1000);
        criteria.setUpdate_time(now.getTime() / 1000);
        areaContractDao.putItem(criteria);
    }

    public void update(AreaContract criteria) throws Exception {
        if (criteria == null) throw new XiangShuiException("参数不能为空");
        if (criteria.getArea_id() == null) throw new XiangShuiException("场地编号不能为空");
        AreaContract areaContract = getByAreaId(criteria.getArea_id());
        if (areaContract == null) throw new XiangShuiException(CodeMsg.NO_FOUND);
        if (criteria.getAccount_ratio() == null) throw new XiangShuiException("分账比例不能为空");
        if (!(0 <= criteria.getAccount_ratio() && criteria.getAccount_ratio() < 100))
            throw new XiangShuiException("分账比例必须在0～100之间");
        Date now = new Date();
        criteria.setUpdate_time(now.getTime() / 1000);
        areaContractDao.updateItem(new PrimaryKey("area_id", criteria.getArea_id()), criteria, new String[]{
                "saler",
                "saler_city",
                "customer",
                "customer_email",
                "customer_contact",
                "account_ratio",
                "bank_account",
                "bank_branch",
                "remark",
                "status",
        });
    }


}
