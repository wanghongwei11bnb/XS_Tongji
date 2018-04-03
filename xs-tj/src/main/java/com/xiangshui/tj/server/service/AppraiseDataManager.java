package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.Appraise;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.TreeMap;

@Component
public class AppraiseDataManager {

    private TreeMap<String, Appraise> map = new TreeMap<String, Appraise>();

    public void save(Appraise appraise) {
        map.put("" + appraise.getCreatetime() + appraise.getBooking_id(), appraise);
        if (map.size() > 30) {
            map.remove(map.firstKey());
        }
    }

    public TreeMap<String, Appraise> getMap() {
        return map;
    }

    public void setMap(TreeMap<String, Appraise> map) {
        this.map = map;
    }
}
