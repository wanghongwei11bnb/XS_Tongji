package com.xiangshui.tj.server.service;

import com.xiangshui.tj.server.bean.Appraise;
import org.springframework.stereotype.Component;

@Component
public class AppraiseDataManager extends DataManager<Long, Appraise> {
    @Override
    Long getId(Appraise appraise) {
        if (appraise == null) {
            return null;
        }
        return appraise.getBooking_id();
    }

    @Override
    public boolean save(Appraise appraise) {
        boolean result = super.save(appraise);
        if (getMap().size() > 30) {
            Object[] ks = getMap().keySet().toArray();
            getMap().remove(ks[ks.length - 1]);
        }
        return result;
    }
}
