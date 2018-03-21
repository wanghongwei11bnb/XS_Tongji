package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.Area;
import org.springframework.stereotype.Component;

@Component
public class AreaDataManager extends DataManager<Integer, Area> {
    @Override
    Integer getId(Area area) {
        if (area == null) {
            return null;
        }
        return area.getArea_id();
    }
}
