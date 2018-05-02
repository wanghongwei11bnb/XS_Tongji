package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.AppraiseTj;
import org.springframework.stereotype.Component;

import java.util.TreeMap;

@Component
public class AppraiseDataManager {

    private TreeMap<String, AppraiseTj> map = new TreeMap<String, AppraiseTj>();

    public void save(AppraiseTj appraise) {
        map.put("" + appraise.getCreatetime() + appraise.getBooking_id(), appraise);
        if (map.size() > 30) {
            map.remove(map.firstKey());
        }
    }

    public TreeMap<String, AppraiseTj> getMap() {
        return map;
    }

    public void setMap(TreeMap<String, AppraiseTj> map) {
        this.map = map;
    }
}
