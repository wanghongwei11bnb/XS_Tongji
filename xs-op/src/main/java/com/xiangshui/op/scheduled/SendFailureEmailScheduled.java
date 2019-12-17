package com.xiangshui.op.scheduled;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.CityDao;
import com.xiangshui.server.dao.FailureReportDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.City;
import com.xiangshui.server.domain.FailureReport;
import com.xiangshui.server.service.MailService;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.MapOptions;
import com.xiangshui.util.spring.SpringUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class SendFailureEmailScheduled implements InitializingBean {

    @Autowired
    CacheScheduled cacheScheduled;
    @Autowired
    AreaDao areaDao;

    @Autowired
    CityDao cityDao;
    @Autowired
    FailureReportDao failureReportDao;


    @Override
    public void afterPropertiesSet() throws Exception {
    }

    /**
     * 每分钟处理上一分钟的故障报修，发送给相关人员邮箱
     */
    @Scheduled(cron = "0 * * * * ?")
    public void mark() {
        DateTime dateTime = new DateTime().withMillisOfSecond(0).withSecondOfMinute(0).minusMinutes(1);
        mark(dateTime);
    }


    public void mark(DateTime dateTime) {
        List<FailureReport> failureReportList = failureReportDao.scan(new ScanSpec().withScanFilters(
                new ScanFilter("create_time").between(
                        dateTime.toDate().getTime() / 1000,
                        dateTime.plusMinutes(1).toDate().getTime() / 1000 - 1
                ),
                new ScanFilter("create_from_role").notExist()
        ));
        if (failureReportList != null && failureReportList.size() > 0) {
            for (FailureReport failureReport : failureReportList) {
                try {
                    send(failureReport);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void send(FailureReport failureReport) throws IOException, MessagingException {
        if (failureReport == null) {
            return;
        }
        Area area = cacheScheduled.areaMapOptions.get(failureReport.getArea_id());
        if (area == null) {
            return;
        }
        City city = cacheScheduled.cityMapOptions.get(area.getCity());
        if (city == null) {
            return;
        }

        String[] toAccounts = null;


        if (new HashSet<>(Arrays.asList(
                2401, 1604, 2410, 3505, 3502, 3402, 1601, 1100
        )).contains(failureReport.getArea_id() / 10000)) {
            toAccounts = new String[]{
                    "xiangui@xiangshuispace.com",
            };
        } else if ("华南".equals(city.getRegion())) {
            toAccounts = new String[]{
                    "xiangui@xiangshuispace.com",
            };
        } else if ("北京市".equals(area.getCity())) {
            toAccounts = new String[]{
                    "guobin@xiangshuispace.com",
                    "wangxin@xiangshuispace.com",
                    "yinlin@xiangshuispace.com",
            };
        } else if ("华北".equals(city.getRegion())) {
            toAccounts = new String[]{
                    "guobin@xiangshuispace.com",
            };
        } else if ("华东".equals(city.getRegion())) {
            toAccounts = new String[]{
                    "zhaomin@xiangshuispace.com",
                    "mengfan@xiangshuispace.com",
            };
        } else if ("成都市".equals(area.getCity())) {
            toAccounts = new String[]{
                    "mengfan@xiangshuispace.com",
                    "guangrui@xiangshuispace.com",
            };
        } else if ("重庆市".equals(area.getCity())) {
            toAccounts = new String[]{
                    "Shukang@xiangshuispace.com",
            };
        } else if ("西安市".equals(area.getCity())) {
            toAccounts = new String[]{
                    "fengfeng@xiangshuispace.com",
            };
        } else if ("深圳市".equals(area.getCity())
                || "泉州市".equals(area.getCity())
                || "厦门市".equals(area.getCity())
                || "衡阳市".equals(area.getCity())
                || "江门市".equals(area.getCity())
                || "长沙市".equals(area.getCity())
                ) {
            toAccounts = new String[]{
                    "Jitao@xiangshuispace.com",
            };
        } else if ("东莞市".equals(area.getCity())) {
            toAccounts = new String[]{
                    "yongchao@xiangshuispace.com",
                    "Jitao@xiangshuispace.com",
            };
        } else if ("广州市".equals(area.getCity())) {
            toAccounts = new String[]{
                    "chuanwen@xiangshuispace.com",
                    "Jitao@xiangshuispace.com",
            };
        } else {

            toAccounts = new String[]{
                    "mengwei@xiangshuispace.com",
            };
        }


        MailService.send(
                toAccounts,
                new String[]{
                        "hongwei@xiangshuispace.com",
                },
                "故障报修－" + city.getRegion() + "－" + city.getCity() + "－" + area.getTitle(),
                "<style>" + IOUtils.toString(this.getClass().getResourceAsStream("/bootstrap.min.css"), "UTF-8") + "</style>" +
                        new StringBuilder()
                                .append("<table>")
                                .append("<tbody>")

                                .append("<tr><th>地址</th><td>" + area.getAddress() + "</td></tr>")
                                .append("<tr><th>场地编号</th><td>" + failureReport.getArea_id() + "</td></tr>")
                                .append("<tr><th>头等舱编号</th><td>" + failureReport.getCapsule_id() + "</td></tr>")
                                .append("<tr><th>订单编号</th><td>" + failureReport.getBooking_id() + "</td></tr>")
                                .append("<tr><th>用户编号</th><td>" + failureReport.getUin() + "</td></tr>")
                                .append("<tr><th>用户手机号</th><td>" + failureReport.getPhone() + "</td></tr>")
                                .append("<tr><th>报修时间</th><td>" + DateUtils.format(failureReport.getCreate_time() * 1000) + "</td></tr>")
                                .append("<tr><th>客户端</th><td>" + failureReport.getReq_from() + failureReport.getApp_version() + "</td></tr>")
                                .append("<tr><th>tags</th><td>" + JSON.toJSONString(failureReport.getTags()) + "</td></tr>")
                                .append("<tr><th>用户描述</th><td>" + failureReport.getDescription() + "</td></tr>")

                                .append("</tbody>")
                                .append("</table>")
                                .toString()
        );
    }


    public void test() throws Exception {
//        send(failureReportDao.getItem(new KeyAttribute("capsule_id", 3301008003l), new KeyAttribute("create_time", 1528440897l)));
//        mark(new DateTime(2019,8,12,15,35));
    }


    public static void main(String[] args) throws Exception {
        SpringUtils.init();
        SpringUtils.getBean(SendFailureEmailScheduled.class).test();
    }


}
