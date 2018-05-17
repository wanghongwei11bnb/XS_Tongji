package com.xiangshui.server.service;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
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

import java.util.*;

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
                    failureReportRelation.set_area(areaMap.get(failureReport.getArea_id()));
                } else {
                    Area area = areaService.getAreaById(failureReport.getArea_id());
                    if (area != null) {
                        areaMap.put(area.getArea_id(), area);
                        failureReportRelation.set_area(area);
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
                failureReportRelation.set_area(area);
            }
        }
        return failureReportRelation;
    }


    public List<FailureReport> search(FailureReport criteria, Date start_date, Date end_date, Integer maxResultSize) throws NoSuchFieldException, IllegalAccessException {
        ScanSpec scanSpec = new ScanSpec();
        List<ScanFilter> filterList;
        if (criteria != null) {
            filterList = failureReportDao.makeScanFilterList(criteria, new String[]{
                    "uin",
                    "phone",
                    "req_from",
                    "booking_id",
                    "area_id",
                    "capsule_id",
                    "app_version",
                    "op_status",
            });
        } else {
            filterList = new ArrayList<ScanFilter>();
        }
        if (start_date != null && end_date != null) {
            filterList.add(new ScanFilter("create_time").between(start_date.getTime() / 1000, (end_date.getTime() + 1000 * 60 * 60 * 24) / 1000));
        } else if (start_date != null && end_date == null) {
            filterList.add(new ScanFilter("create_time").gt(start_date.getTime() / 1000 - 1));
        } else if (start_date == null && end_date != null) {
            filterList.add(new ScanFilter("create_time").lt((end_date.getTime() + 1000 * 60 * 60 * 24) / 1000));
        }
        if (maxResultSize != null) {
            scanSpec.withMaxResultSize(maxResultSize);
        }
        return failureReportDao.scan(scanSpec.withScanFilters(filterList.toArray(new ScanFilter[0])));
    }


}
