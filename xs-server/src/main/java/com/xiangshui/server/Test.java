package com.xiangshui.server;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.AreaContract;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.domain.UserInfo;
import com.xiangshui.server.domain.mysql.Op;
import com.xiangshui.server.example.OpExample;
import com.xiangshui.server.mapper.OpMapper;
import com.xiangshui.server.service.PartnerService;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.spring.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class Test {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    AreaDao areaDao;

    @Autowired
    CapsuleDao capsuleDao;

    @Autowired
    PartnerService partnerService;


    @Autowired
    CityDao cityDao;

    @Autowired
    OpMapper opMapper;

    @Autowired
    AreaContractDao areaContractDao;


    public void testSelect() throws Exception {
        List<UserInfo> userInfoList = userInfoDao.scan(new ScanSpec().withFilterExpression("phone = :phone").withValueMap(new ValueMap().withString(":phone", "11000000014")));
        log.debug(JSON.toJSONString(userInfoList));

        List<Area> areaList = areaDao.scan(new ScanSpec());
        log.debug(JSON.toJSONString(areaList));
    }

    public void test2() throws Exception {

        boolean success = userInfoDao.updateItem(new PrimaryKey("uin", 1601519873), new AttributeUpdate("nick_name").put("sfsfsdttttttfs"));
//        log.debug(success+"");
        UserInfo userInfo = userInfoDao.getItem(new PrimaryKey("uin", 1601519873));
        log.debug(JSON.toJSONString(userInfo));


    }

    public void test() throws Exception {

        Date now=new Date();

        List<List<String>> data = ExcelUtils.read(new FileInputStream("/Users/whw/Downloads/分账信息.xlsx"), 0);
        for (List<String> row : data) {
            try {
                String area_title = row.get(0);
                String saler = row.get(3);
                String customer = row.get(4);
                String account_ratio_str = row.get(5);
                String bank_account = row.get(6);
                String bank_branch = row.get(7);

                if (StringUtils.isBlank(area_title)) {
                    continue;
                }
                if (StringUtils.isBlank(saler)) {
                    continue;
                }

                OpExample example = new OpExample();
                example.setLimit(1);
                example.createCriteria().andFullnameEqualTo(saler);
                List<Op> opList = opMapper.selectByExample(example);
                if (opList != null && opList.size() > 0) {
                    Op op = opList.get(0);

                    List<Area> areaList = areaDao.scan(new ScanSpec().withScanFilters(new ScanFilter("title").eq(area_title)));

                    if (areaList != null && areaList.size() > 0) {
                        Area area = areaList.get(0);
                        int account_ratio = (int) (Float.valueOf(account_ratio_str) * 100);
                        AreaContract areaContract = new AreaContract();
                        areaContract.setArea_id(area.getArea_id());
                        areaContract.setSaler(op.getFullname());
                        areaContract.setSaler_city(op.getCity());
                        areaContract.setCustomer(customer);
                        areaContract.setBank_account_name(customer);
                        areaContract.setBank_account(bank_account);
                        areaContract.setBank_branch(bank_branch);
                        areaContract.setAccount_ratio(account_ratio);
                        areaContract.setStatus(0);
                        areaContract.setCreate_time(now.getTime()/1000);
                        areaContract.setUpdate_time(now.getTime()/1000);
                        areaContractDao.putItem(areaContract);
                        continue;
                    }
                }
                System.out.println(area_title);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws Exception {


        SpringUtils.init();
        SpringUtils.getBean(Test.class).test();


    }
}
