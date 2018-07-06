//package com.xiangshui.op.controller;
//
//import com.xiangshui.op.annotation.AuthRequired;
//import com.xiangshui.op.annotation.Menu;
//import com.xiangshui.server.bean.Pagination;
//import com.xiangshui.server.dao.AreaDao;
//import com.xiangshui.server.dao.CapsuleDao;
//import com.xiangshui.server.domain.mysql.Partner;
//import com.xiangshui.server.example.PartnerExample;
//import com.xiangshui.server.mapper.PartnerMapper;
//import com.xiangshui.server.relation.PartnerRelation;
//import com.xiangshui.server.service.AreaService;
//import com.xiangshui.server.service.CapsuleService;
//import com.xiangshui.server.service.PartnerService;
//import com.xiangshui.util.web.result.CodeMsg;
//import com.xiangshui.util.web.result.Result;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//
//@Controller
//public class PartnerController extends BaseController {
//
//
//    @Autowired
//    PartnerService partnerService;
//
//    @Autowired
//    PartnerMapper partnerMapper;
//
//    @Autowired
//    AreaService areaService;
//    @Autowired
//    CapsuleService capsuleService;
//
//    @Autowired
//    AreaDao areaDao;
//    @Autowired
//    CapsuleDao capsuleDao;
//
//
//    @Menu(value = "场地方账号管理")
//    @AuthRequired("场地方账号管理")
//    @GetMapping("/partner_manage")
//    public String index(HttpServletRequest request) {
//        setClient(request);
//        return "partner_manage";
//    }
//
//
//    @PostMapping("/api/partner/add")
//    @ResponseBody
//    public Result add(@RequestBody Partner partner) {
//        partner = partnerService.addPartner(partner);
//        if (partner != null) {
//            return new Result(CodeMsg.SUCCESS).putData("partner", partner);
//        } else {
//            return new Result(CodeMsg.SERVER_ERROR);
//        }
//    }
//
//    @PostMapping("/api/partner/{partner_id}/del")
//    @ResponseBody
//    public Result del(@PathVariable("partner_id") int partner_id) {
//        if (partnerMapper.deleteByPrimaryKey(partner_id) > 0) {
//            return new Result(CodeMsg.SUCCESS);
//        } else {
//            return new Result(CodeMsg.SERVER_ERROR);
//        }
//    }
//
//    @PostMapping("/api/partner/{partner_id}/update")
//    @ResponseBody
//    public Result update(@PathVariable("partner_id") Integer partner_id, @RequestBody Partner partner) {
//        partner.setId(partner_id);
//        if (partnerMapper.updateByPrimaryKeySelective(partner) > 0) {
//            return new Result(CodeMsg.SUCCESS);
//        } else {
//            return new Result(CodeMsg.SERVER_ERROR);
//        }
//    }
//
//    @GetMapping("/api/partner/{partner_id}")
//    @ResponseBody
//    public Result get(@PathVariable("partner_id") Integer partner_id) {
//        Partner partner = partnerMapper.selectByPrimaryKey(partner_id, null);
//        if (partner == null) {
//            return new Result(CodeMsg.NO_FOUND);
//        } else {
//            PartnerRelation partnerRelation = partnerService.toRelation(partner);
//            return new Result(CodeMsg.SUCCESS).putData("partner", partnerRelation);
//        }
//    }
//
//
//    @GetMapping("/api/partner/search")
//    @ResponseBody
//    public Result partner_search(Partner partner, Pagination pagination) {
//
//        PartnerExample example = new PartnerExample();
//        PartnerExample.Criteria criteria = example.createCriteria();
//        if (partner != null) {
//            if (partner.getId() != null) {
//                criteria.andIdEqualTo(partner.getId());
//            }
//            if (StringUtils.isNotBlank(partner.getPhone())) {
//                criteria.andPhoneEqualTo(partner.getPhone());
//            }
//            if (StringUtils.isNotBlank(partner.getAddress())) {
//                criteria.andAddressLike("%" + partner.getAddress() + "%");
//            }
//            if (StringUtils.isNotBlank(partner.getEmail())) {
//                criteria.andEmailEqualTo(partner.getEmail());
//            }
//            if (StringUtils.isNotBlank(partner.getCity())) {
//                criteria.andCityEqualTo(partner.getCity());
//            }
//        }
//        example.setLimit(500);
//        List<Partner> partnerList = partnerMapper.selectByExample(example);
//        return new Result(CodeMsg.SUCCESS).putData("partnerList", partnerService.toRelation(partnerList));
//    }
//
//}
