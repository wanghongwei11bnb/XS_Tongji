package com.xiangshui.server.job;

import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BookingDao;
import com.xiangshui.server.dao.CapsuleDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.util.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Count {




    @Autowired
    UserInfoDao userInfoDao;

    @Autowired
    AreaDao areaDao;

    @Autowired
    BookingDao bookingDao;

    @Autowired
    CapsuleDao capsuleDao;








    public void test(){



    }


    public static void main(String[] args) throws Exception {
        SpringUtils.init();
        SpringUtils.getBean(Count.class).test();

        log.info("finish");
    }


}
