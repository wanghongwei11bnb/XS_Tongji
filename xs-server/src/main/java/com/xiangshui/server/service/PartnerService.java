package com.xiangshui.server.service;

import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.mysql.Partner;
import com.xiangshui.server.domain.mysql.PartnerAreaKey;
import com.xiangshui.server.example.OpExample;
import com.xiangshui.server.example.PartnerAreaExample;
import com.xiangshui.server.example.PartnerExample;
import com.xiangshui.server.mapper.PartnerAreaMapper;
import com.xiangshui.server.mapper.PartnerMapper;
import com.xiangshui.server.relation.PartnerRelation;
import com.xiangshui.util.MD5;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class PartnerService {


    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AreaDao areaDao;
    @Autowired
    CapsuleDao capsuleDao;
    @Autowired
    BookingDao bookingDao;

    @Autowired
    PartnerMapper partnerMapper;

    @Autowired
    PartnerAreaMapper partnerAreaMapper;

    @Autowired
    CityDao cityDao;

    @Autowired
    UserInfoDao userInfoDao;


    /**
     * 获取所有常地方用户
     *
     * @return
     */
    public List<Partner> getPartnerList() {
        List<Partner> partnerList = partnerMapper.selectByExample(new PartnerExample());
        return partnerList;
    }


    public List<Integer> getAreaIdListByPartner(int partner_id) {
        PartnerAreaExample partnerAreaExample = new PartnerAreaExample();
        partnerAreaExample.createCriteria().andPartnerIdEqualTo(partner_id);
        List<PartnerAreaKey> partnerAreaList = partnerAreaMapper.selectByExample(partnerAreaExample);
        List<Integer> areaIdList = new ArrayList<Integer>();
        if (partnerAreaList != null) {
            for (PartnerAreaKey partnerAreaKey : partnerAreaList) {
                areaIdList.add(partnerAreaKey.getAreaId());
            }
        }
        return areaIdList;
    }


    public List<Area> getAreaListByPartner(Partner partner, String[] areaFields) {
        if (partner == null) {
            return null;
        }
        return getAreaListByPartner(partner.getId(), areaFields);
    }

    public List<Area> getAreaListByPartner(int partner_id, String[] areaFields) {
        List<Integer> areaIdList = getAreaIdListByPartner(partner_id);
        List<Area> areaList = new ArrayList<Area>();
        for (int area_id : areaIdList) {
            Area area = areaDao.getItem(new PrimaryKey("area_id", area_id));
            areaList.add(area);
        }
        return areaList;
    }

    public Partner addPartner(Partner partner) {

        if (partner == null) {
            throw new RuntimeException("partner对象不能为空！");
        }
        if (StringUtils.isBlank(partner.getPhone()) || !partner.getPhone().matches("\\d{11}")) {
            throw new RuntimeException("手机号码格式错误！");
        }
        if (StringUtils.isBlank(partner.getPasswd())) {
            throw new RuntimeException("密码格式错误！");
        }

        if (getPartnerByPhone(partner.getPhone()) != null) {
            throw new RuntimeException("手机号码已被注册！");
        }

        Date now = new Date();
        partner.setCreateTime(now);
        partner.setUpdateTime(now);
        partner.setId((int) (now.getTime() / 1000));
        int r = partnerMapper.insertSelective(partner);
        if (r > 0) {
            return partner;
        } else {
            return null;
        }
    }

    public Partner getPartnerById(int partner_id) {
        return partnerMapper.selectByPrimaryKey(partner_id, null);
    }


    public Partner getPartnerByPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return null;
        }
        PartnerExample example = new PartnerExample();
        example.createCriteria().andPhoneEqualTo(phone);

        List<Partner> partnerList = partnerMapper.selectByExample(example);
        if (partnerList != null && partnerList.size() > 0) {
            return partnerList.get(0);
        }
        return null;
    }


    public boolean updatePassword(int partner_id, String password) {
        Partner partner = getPartnerById(partner_id);
        if (partner == null) {
            throw new RuntimeException("帐号不存在！");
        }
        if (StringUtils.isBlank(password)) {
            throw new RuntimeException("密码不能为空！");
        }
        Partner partnerUpdate = new Partner();
        partnerUpdate.setId(partner_id);
        partnerUpdate.setPasswd(password);
        int r = partnerMapper.updateByPrimaryKey(partnerUpdate);
        return r > 0;
    }

    public boolean checkPassword(String phone, String password) {
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(password)) {
            return false;
        }
        Partner partner = getPartnerByPhone(phone);
        if (partner == null) {
            return false;
        }
        return MD5.getMD5(password).equals(partner.getPasswd());
    }


    public PartnerRelation toRelation(Partner partner) {
        if (partner == null) {
            return null;
        }
        PartnerRelation partnerRelation = new PartnerRelation();
        BeanUtils.copyProperties(partner, partnerRelation);
        partnerRelation.setAreaList(getAreaListByPartner(partner, new String[]{"area_id", "title", "address"}));
        return partnerRelation;
    }


    public List<PartnerRelation> toRelation(List<Partner> partnerList) {
        if (partnerList == null) {
            return null;
        }
        List<PartnerRelation> partnerRelationList = new ArrayList<PartnerRelation>();
        for (Partner partner : partnerList) {
            PartnerRelation partnerRelation = toRelation(partner);
            partnerRelationList.add(partnerRelation);
        }
        return partnerRelationList;
    }


}
