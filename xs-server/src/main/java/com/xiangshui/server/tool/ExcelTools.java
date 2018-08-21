package com.xiangshui.server.tool;

import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BookingDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.BookingService;
import com.xiangshui.server.service.UserService;
import com.xiangshui.util.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExcelTools {

    @Autowired
    BookingService bookingService;
    @Autowired
    UserService userService;
    @Autowired
    AreaService areaService;
    @Autowired
    BookingDao bookingDao;
    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    AreaDao areaDao;

    public void exportBookingList(List<Booking> bookingList) {
    }

}
