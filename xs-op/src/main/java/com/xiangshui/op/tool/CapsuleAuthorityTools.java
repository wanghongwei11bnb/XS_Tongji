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
        authorityMap.put("guobin@xiangshuispace.com", new CapsuleAuthority(new String[]{"北京市"}, new int[]{
                1100031,
                1100058,
                1100033,
                1100036,
                1100038,
                2702001,
                1100077,
                1100144,
                1100094,
                1100150,
                1100146,
                1100034,
                1100139,
                1100109,
                1100137,
                3121001,
                1100081,
                1100116,
                1100059,
                1100110,
                1100111,
                1100073,
                1100103,
                1100117,
                1100130,
                1100133,
                1100134,
                1100127,
                1100064,
                1100128,
                1100128,
                1200005,
                1200006,
        }));
        authorityMap.put("weihao@xiangshuispace.com", new CapsuleAuthority(null, new int[]{
                2501031,
                2501006,
                2501032,
                2501042,
                2501043,
                2501034,
                2501037,
                2501001,
                2501039,
                2501041,
                2501016,
                2501018,
        }));
        authorityMap.put("hongyang@xiangshuispace.com", new CapsuleAuthority(null, new int[]{
                3100037,
                3100061,
                3100049,
                3100029,
                3100002,
                3100018,
                3100019,
                3100044,
                3100056,
                3100069,
                3301021,
                3301020,
                3100014,
                3100046,
                3100021,
                3100039,
                3100012,
                3100058,
                3100050,
                3100071,
                2201003,
                3100060,
                3100070,
        }));
        authorityMap.put("haijiang@xiangshuispace.com", new CapsuleAuthority(null, new int[]{
                1100021,
                1100048,
                1100051,
                1100148,
                1100149,
                1100102,
                1100065,
                1100147,
                1100071,
                1100078,
                1100098,
                1100120,
                1100040,
                1100112,
                1100121,
                1100122,
                1100119,
                1100141,
                1100138,
                1100090,
                1100142,
                1100002,
                1100009,
                1100060,
                1100118,
                1100136,
                1100107,
                1100140,
                1100143,
                1100044,
                1100074,
        }));
//        authorityMap.put("wanghongwei@xiangshuispace.com", new CapsuleAuthority(
//                new String[]{"成都市"},
//                null
//        ));
    }


    public boolean auth(Area area, String username, boolean throwException) {
        if (area == null) throw new XiangShuiException(CodeMsg.NO_FOUND);
        boolean result = false;
        if (!StringUtils.isBlank(username)) {
            if (!authorityMap.containsKey(username)) {
                result = true;
            } else {
                CapsuleAuthority authority = authorityMap.get(username);
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
            }
        }
        if (!result && throwException) {
            throw new XiangShuiException("没有权限");
        }
        return result;
    }


    public boolean auth(Capsule capsule, String username, boolean throwException) {
        if (capsule == null) throw new XiangShuiException(CodeMsg.NO_FOUND);
        Area area = cacheScheduled.areaMapOptions.get(capsule.getArea_id());
        if (area == null) throw new XiangShuiException(CodeMsg.NO_FOUND);
        return auth(area, username, throwException);
    }


    public boolean auth(Booking booking, String username, boolean throwException) {
        if (booking == null) throw new XiangShuiException(CodeMsg.NO_FOUND);
        Area area = cacheScheduled.areaMapOptions.get(booking.getArea_id());
        if (area == null) throw new XiangShuiException(CodeMsg.NO_FOUND);
        return auth(area, username, throwException);
    }


    public List<Area> filterArea(List<Area> areaList) {
        return ListUtils.filter(areaList, area -> auth(area, SessionLocal.getUsername(), false));
    }

    public List<Capsule> filterCapsule(List<Capsule> capsuleList) {
        return ListUtils.filter(capsuleList, capsule -> auth(capsule, SessionLocal.getUsername(), false));
    }

    public List<Booking> filterBooking(List<Booking> bookingList) {
        return ListUtils.filter(bookingList, booking -> auth(booking, SessionLocal.getUsername(), false));
    }

    public List<DeviceStatus> filterDeviceStatus(List<DeviceStatus> deviceStatusList) {
        return ListUtils.filter(deviceStatusList, deviceStatus -> {
            Area area = cacheScheduled.areaMapOptions.get(deviceStatus.getArea_id());
            return this.auth(area, SessionLocal.getUsername(), false);
        });
    }

    public List<AreaContract> filterAreaContract(List<AreaContract> areaContractList) {
        return ListUtils.filter(areaContractList, areaContract -> {
            Area area = cacheScheduled.areaMapOptions.get(areaContract.getArea_id());
            return this.auth(area, SessionLocal.getUsername(), false);
        });
    }


    public List<AreaBill> filterAreaBill(List<AreaBill> areaBillList) {
        return ListUtils.filter(areaBillList, areaBill -> {
            Area area = cacheScheduled.areaMapOptions.get(areaBill.getArea_id());
            return this.auth(area, SessionLocal.getUsername(), false);
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
