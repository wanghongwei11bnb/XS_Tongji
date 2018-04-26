package com.xiangshui.op.controller;

import com.xiangshui.server.domain.mysql.Partner;
import com.xiangshui.server.mapper.PartnerMapper;
import com.xiangshui.server.relation.PartnerRelation;
import com.xiangshui.server.service.PartnerService;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PartnerController extends BaseController {


    @Autowired
    PartnerService partnerService;

    @Autowired
    PartnerMapper partnerMapper;


    @GetMapping("/partner_manage")
    public String index() {
        return "partner_manage";
    }

    @GetMapping("/api/partner/search")
    @ResponseBody
    public Result partner_search(Integer id, String phone) {
        if (id != null) {
            Partner partner = partnerService.getPartnerById(id);
            if (partner == null) {
                return new Result(CodeMsg.SUCCESS).putData("list", new Partner[]{});
            }
            PartnerRelation partnerRelation = partnerService.toRelation(partner);
            return new Result(CodeMsg.SUCCESS).putData("list", new PartnerRelation[]{partnerRelation});
        }
        if (StringUtils.isNotBlank(phone)) {
            Partner partne = partnerService.getPartnerByPhone(phone);
            if (partne != null) {
                return new Result(CodeMsg.SUCCESS).putData("list", new Partner[]{partne});
            } else {
                return new Result(CodeMsg.SUCCESS).putData("list", new Partner[]{});
            }
        }
        List<Partner> partnerList = partnerService.getPartnerList();
        return new Result(CodeMsg.SUCCESS).putData("list", partnerService.toRelation(partnerList));
    }


    @PostMapping("/api/partner/add")
    @ResponseBody
    public Result add(Partner partner) {
        partner = partnerService.addPartner(partner);
        if (partner != null) {
            return new Result(CodeMsg.SUCCESS);
        } else {
            return new Result(CodeMsg.SERVER_ERROR);
        }
    }


    @PostMapping("/api/partner/{partner_id}/edit")
    @ResponseBody
    public Result edit(@PathVariable("partner_id") Integer partner_id, Partner partner) {

        partner.setId(partner_id);
        int r = partnerMapper.updateByPrimaryKeySelective(partner);

        if (r > 0) {
            return new Result(CodeMsg.SUCCESS);
        } else {
            return new Result(CodeMsg.SERVER_ERROR);
        }
    }
}
