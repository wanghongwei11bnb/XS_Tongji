package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.AreaTj;
import org.springframework.stereotype.Component;

@Component
public class AreaDataManager extends DataManager<Integer, AreaTj> {
    @Override
    Integer getId(AreaTj area) {
        if (area == null) {
            return null;
        }
        return area.getArea_id();
    }
}
