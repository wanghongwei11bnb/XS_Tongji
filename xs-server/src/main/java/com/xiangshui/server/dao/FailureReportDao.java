package com.xiangshui.server.dao;

import com.xiangshui.server.domain.FailureReport;
import org.springframework.stereotype.Component;

@Component
public class FailureReportDao extends BaseDynamoDao<FailureReport> {
    public String getTableName() {
        return "failure_report";
    }
}
