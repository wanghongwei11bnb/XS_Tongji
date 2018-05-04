package com.xiangshui.server.service;

import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.FailureReportDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.FailureReport;
import com.xiangshui.server.relation.FailureReportRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FailureReportService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AreaService areaService;


    @Autowired
    FailureReportDao failureReportDao;

    public List<FailureReportRelation> mapperArea(List<FailureReport> failureReportList) {
        Map<Integer, Area> areaMap = new HashMap<Integer, Area>();
        List<FailureReportRelation> failureReportRelationList = new ArrayList<FailureReportRelation>();
        for (FailureReport failureReport : failureReportList) {
            FailureReportRelation failureReportRelation = new FailureReportRelation();
            failureReportRelationList.add(failureReportRelation);
            BeanUtils.copyProperties(failureReport, failureReportRelation);
            if (failureReport.getArea_id() != null) {
                if (areaMap.containsKey(failureReport.getArea_id())) {
                    failureReportRelation.setAreaObj(areaMap.get(failureReport.getArea_id()));
                } else {
                    Area area = areaService.getAreaById(failureReport.getArea_id());
                    if (area != null) {
                        areaMap.put(area.getArea_id(), area);
                        failureReportRelation.setAreaObj(area);
                    }
                }
            }
        }
        return failureReportRelationList;
    }

    public FailureReportRelation mapperArea(FailureReport failureReport) {
        FailureReportRelation failureReportRelation = new FailureReportRelation();
        BeanUtils.copyProperties(failureReport, failureReportRelation);
        if (failureReport.getArea_id() != null) {
            Area area = areaService.getAreaById(failureReport.getArea_id());
            if (area != null) {
                failureReportRelation.setAreaObj(area);
            }
        }
        return failureReportRelation;
    }
}
