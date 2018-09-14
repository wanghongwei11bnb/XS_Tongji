package com.xiangshui.server.service;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.constant.CapsuleStatusOption;
import com.xiangshui.server.dao.CapsuleDao;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CapsuleService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CapsuleDao capsuleDao;
    @Autowired
    AreaService areaService;


    public Capsule getCapsuleById(long capsule_id, String[] attributes) {
        if (attributes == null || attributes.length == 0) {
            return capsuleDao.getItem(new PrimaryKey("capsule_id", capsule_id));
        } else {
            return capsuleDao.getItem(new GetItemSpec().withPrimaryKey(new PrimaryKey("capsule_id", capsule_id)).withAttributesToGet(attributes));
        }

    }

    public Capsule getCapsuleById(long capsule_id) {
        return getCapsuleById(capsule_id, null);
    }


    public List<Capsule> search(Capsule criteria, String[] attributes) throws NoSuchFieldException, IllegalAccessException {
        if (criteria == null) {
            if (attributes == null || attributes.length == 0) {
                return capsuleDao.scan();
            } else {
                return capsuleDao.scan(new ScanSpec().withAttributesToGet(attributes));
            }
        }
        if (criteria.getCapsule_id() != null) {
            Capsule capsule = getCapsuleById(criteria.getCapsule_id(), attributes);
            List<Capsule> capsuleList = new ArrayList<Capsule>(1);
            if (capsule != null) {
                capsuleList.add(capsule);
            }
            return capsuleList;
        }


        List<ScanFilter> filterList = capsuleDao.makeScanFilterList(criteria, new String[]{
                "area_id",
                "status",
                "type",
                "status",
                "device_id",
                "is_downline",
        });
        ScanSpec scanSpec = new ScanSpec();
        if (filterList != null && filterList.size() > 0) {
            scanSpec.withScanFilters(filterList.toArray(new ScanFilter[0]));
        }
        if (attributes != null && attributes.length > 0) {
            scanSpec.withAttributesToGet(attributes);
        }
        return capsuleDao.scan(scanSpec);
    }

    public void createCapsule(Capsule criteria) {
        if (criteria == null) {
            throw new XiangShuiException("方法参数不能为空");
        }
        if (criteria.getCapsule_id() == null || criteria.getCapsule_id() == 0) {
            throw new XiangShuiException("头等舱编号不能为空");
        }
        if (criteria.getArea_id() == null || criteria.getArea_id() == 0) {
            throw new XiangShuiException("场地编号不能为空");
        }
        if (criteria.getStatus() == null) {
            throw new XiangShuiException("设备状态不能为空");
        }
        if (StringUtils.isBlank(criteria.getDevice_id())) {
            throw new XiangShuiException("设备ID不能为空");
        }
        if (criteria.getDevice_version() == null) {
            throw new XiangShuiException("头等舱版本不能为空");
        }
        if (capsuleDao.getItem(new PrimaryKey("capsule_id", criteria.getCapsule_id())) != null) {
            throw new XiangShuiException("头等舱编号已存在");
        }

        if (!validateDeviceIdForSave(criteria.getCapsule_id(), criteria.getDevice_id())) {
            throw new XiangShuiException("硬件设备ID已占用");
        }
        Date now = new Date();
        criteria.setCreate_time(now.getTime() / 1000);
        criteria.setUpdate_time(now.getTime() / 1000);
        criteria.setIs_downline(0);
        criteria.setType(1);
        criteria.setStatus(CapsuleStatusOption.free.value);
        capsuleDao.putItem(criteria);
    }


    public void updateCapsule(Capsule criteria) throws Exception {
        if (criteria == null) {
            throw new XiangShuiException("方法参数不能为空");
        }
        if (criteria.getCapsule_id() == null || criteria.getCapsule_id() == 0) {
            throw new XiangShuiException("头等舱编号不能为空");
        }
        Capsule capsule = getCapsuleById(criteria.getCapsule_id());
        if (capsule == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND.msg);
        }
        if (criteria.getStatus() == null) {
            throw new XiangShuiException("设备状态不能为空");
        }
        if (StringUtils.isBlank(criteria.getDevice_id())) {
            throw new XiangShuiException("设备ID不能为空");
        }
        if (criteria.getDevice_version() == null) {
            throw new XiangShuiException("头等舱版本不能为空");
        }

        if (!validateDeviceIdForSave(criteria.getCapsule_id(), criteria.getDevice_id())) {
            throw new XiangShuiException("硬件设备ID已占用");
        }

        criteria.setUpdate_time(System.currentTimeMillis() / 1000);
        capsuleDao.updateItem(new PrimaryKey("capsule_id", criteria.getCapsule_id()), criteria, new String[]{
                "update_time",
                "status",
                "device_id",
                "is_downline",
                "device_version",
                "remark",
        });
    }

    public boolean validateDeviceIdForSave(long capsule_id, String device_id) {
        List<Capsule> capsuleList = capsuleDao.scan(new ScanSpec().withScanFilters(new ScanFilter("device_id").eq(device_id), new ScanFilter("capsule_id").ne(capsule_id)));
        if (capsuleList == null || capsuleList.size() == 0) {
            return true;
        } else {
            return false;
        }
    }


}
