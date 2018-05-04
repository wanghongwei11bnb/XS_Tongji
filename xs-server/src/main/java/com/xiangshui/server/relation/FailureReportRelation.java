package com.xiangshui.server.relation;

import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.FailureReport;

public class FailureReportRelation extends FailureReport {

    private Area areaObj;

    public Area getAreaObj() {
        return areaObj;
    }

    public void setAreaObj(Area areaObj) {
        this.areaObj = areaObj;
    }
}
