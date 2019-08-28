package com.xiangshui.server.job;

import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.RedVerifyRecordDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.domain.RedVerifyRecord;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.service.UserService;
import com.xiangshui.util.*;
import com.xiangshui.util.spring.SpringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

@Component
public class Coupon {

    public static void main(String[] args) throws Exception {
        SpringUtils.init();
        UserInfoDao userInfoDao = SpringUtils.getBean(UserInfoDao.class);
        UserService userService = SpringUtils.getBean(UserService.class);
        RedVerifyRecordDao redVerifyRecordDao = SpringUtils.getBean(RedVerifyRecordDao.class);

        List<RedVerifyRecord> recordList = redVerifyRecordDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE).withScanFilters(
                new ScanFilter("verify_code").in(new String[]{
                        "享+头等舱",
                        "太库头等舱",
                        "无界头额舱",
                        "启迪头等舱",
                        "58头等舱",
                        "零秒头等舱",
                        "共享头等舱",
                        "头等舱体验金",
                        "岛里头等舱",
                })
        ));

        if (recordList != null) {
            recordList.sort(new Comparator<RedVerifyRecord>() {
                @Override
                public int compare(RedVerifyRecord o1, RedVerifyRecord o2) {
                    if (o1.getVerify_code().compareTo(o2.getVerify_code()) != 0) {
                        return o1.getVerify_code().compareTo(o2.getVerify_code());
                    } else {
                        return o2.getTime().compareTo(o1.getTime());
                    }
                }
            });
        }

        Set<Integer> uinSet = ListUtils.fieldSet(recordList, new CallBackForResult<RedVerifyRecord, Integer>() {
            @Override
            public Integer run(RedVerifyRecord record) {
                return record.getUin();
            }
        });

        List<UserInfo> userInfoList = userService.getUserInfoList(uinSet.toArray(new Integer[uinSet.size()]), null);

        MapOptions<Integer, UserInfo> userInfoMapOptions = new MapOptions<Integer, UserInfo>(userInfoList) {
            @Override
            public Integer getPrimary(UserInfo userInfo) {
                return userInfo.getUin();
            }
        };


        XSSFWorkbook workbook = ExcelUtils.export(Arrays.asList(
                new ExcelUtils.Column<RedVerifyRecord>("兑换码") {
                    @Override
                    public Object render(RedVerifyRecord redVerifyRecord) {
                        return redVerifyRecord.getVerify_code();
                    }
                },
                new ExcelUtils.Column<RedVerifyRecord>("用户编号") {
                    @Override
                    public Object render(RedVerifyRecord redVerifyRecord) {
                        return redVerifyRecord.getUin();
                    }
                },
                new ExcelUtils.Column<RedVerifyRecord>("用户手机号") {
                    @Override
                    public Object render(RedVerifyRecord redVerifyRecord) {
                        return userInfoMapOptions.containsKey(redVerifyRecord.getUin()) ? userInfoMapOptions.get(redVerifyRecord.getUin()).getPhone() : null;
                    }
                },
                new ExcelUtils.Column<RedVerifyRecord>("兑换时间") {
                    @Override
                    public Object render(RedVerifyRecord redVerifyRecord) {
                        return DateUtils.format(redVerifyRecord.getTime() * 1000);
                    }
                }
        ), recordList);
        OutputStream outputStream = new FileOutputStream(new File("/Users/whw/Downloads/红包兑换记录.xlsx"));
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
        outputStream.flush();
        outputStream.close();

    }

}
