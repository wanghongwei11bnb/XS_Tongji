package com.xiangshui.op.tool;

import com.xiangshui.op.bean.DeviceStatus;
import com.xiangshui.op.scheduled.CacheScheduled;
import com.xiangshui.op.threadLocal.SessionLocal;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.util.ListUtils;
import com.xiangshui.util.web.result.CodeMsg;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CapsuleAuthorityTools {


    @Autowired
    CacheScheduled cacheScheduled;

    public final Map<String, CapsuleAuthority> authorityMap = new HashMap<>();

    {
        authorityMap.put("weiyu@xiangshuispace.com", new CapsuleAuthority(
                new String[]{"成都市"},
                null
        ));
//        authorityMap.put("xubo@xiangshuispace.com", new CapsuleAuthority(
//                new String[]{"成都市"},
//                null
//        ));
//        authorityMap.put("wanghongwei@xiangshuispace.com", new CapsuleAuthority(
//                new String[]{"成都市"},
//                null
//        ));
    }

    public String getOp() {
        if (SessionLocal.get() == null) return null;
        return SessionLocal.get().getUsername();
    }

    public boolean auth(Area area) {
        String op = this.getOp();
        if (StringUtils.isBlank(op)) return false;
        if (!authorityMap.containsKey(op)) return true;
        CapsuleAuthority authority = authorityMap.get(op);
        if (authority.citys != null) {
            for (String city : authority.citys) {
                if (city.equals(area.getCity())) return true;
            }
        }
        if (authority.area_ids != null) {
            for (int area_id : authority.area_ids) {
                if (area_id == area.getArea_id()) return true;
            }
        }
        return false;
    }


    public boolean auth(Capsule capsule) {
        if (capsule == null) return false;
        Area area = cacheScheduled.areaMapOptions.get(capsule.getArea_id());
        return area != null && auth(area);
    }


    public boolean auth(Booking booking) {
        if (booking == null) return false;
        Area area = cacheScheduled.areaMapOptions.get(booking.getArea_id());
        return area != null && auth(area);
    }

    public void authForException(Area area) {
        if (!this.auth(area)) throw new XiangShuiException(CodeMsg.OPAUTH_FAIL);
    }


    public void authForException(Capsule capsule) {
        if (!this.auth(capsule)) throw new XiangShuiException(CodeMsg.OPAUTH_FAIL);
    }


    public void authForException(Booking booking) {
        if (!this.auth(booking)) throw new XiangShuiException(CodeMsg.OPAUTH_FAIL);
    }


    public List<Area> filterArea(List<Area> areaList) {
        return ListUtils.filter(areaList, this::auth);
    }

    public List<Capsule> filterCapsule(List<Capsule> capsuleList) {
        return ListUtils.filter(capsuleList, this::auth);
    }

    public List<Booking> filterBooking(List<Booking> bookingList) {
        return ListUtils.filter(bookingList, this::auth);
    }

    public List<DeviceStatus> filterDeviceStatus(List<DeviceStatus> deviceStatusList) {
        return ListUtils.filter(deviceStatusList, deviceStatus -> {
            Area area = cacheScheduled.areaMapOptions.get(deviceStatus.getArea_id());
            return this.auth(area);
        });
    }

    public List<AreaContract> filterAreaContract(List<AreaContract> areaContractList) {
        return ListUtils.filter(areaContractList, areaContract -> {
            Area area = cacheScheduled.areaMapOptions.get(areaContract.getArea_id());
            return this.auth(area);
        });
    }


    public List<AreaBill> filterAreaBill(List<AreaBill> areaBillList) {
        return ListUtils.filter(areaBillList, areaBill -> {
            Area area = cacheScheduled.areaMapOptions.get(areaBill.getArea_id());
            return this.auth(area);
        });
    }


    @Data
    public static class CapsuleAuthority {
        private String[] citys;
        private int[] area_ids;

        public CapsuleAuthority(String[] citys, int[] area_ids) {
            this.citys = citys;
            this.area_ids = area_ids;
        }
    }

}
