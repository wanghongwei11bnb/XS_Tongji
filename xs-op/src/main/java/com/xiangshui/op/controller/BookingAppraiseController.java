package com.xiangshui.op.controller;

import com.alibaba.fastjson.JSON;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.server.constant.AreaStatusOption;
import com.xiangshui.server.constant.BookingStatusOption;
import com.xiangshui.server.constant.PayTypeOption;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BookingAppraiseDao;
import com.xiangshui.server.dao.UserInfoDao;
import com.xiangshui.server.domain.*;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.service.CityService;
import com.xiangshui.server.service.ServiceUtils;
import com.xiangshui.server.service.UserService;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.Option;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Controller
public class BookingAppraiseController extends BaseController {


    @Autowired
    BookingAppraiseDao bookingAppraiseDao;

    @Autowired
    UserService userService;

    @Autowired
    CityService cityService;
    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    AreaDao areaDao;

    @Menu("用户评论列表")
    @AuthRequired("用户评论列表")
    @GetMapping("/booking_appraise_manage")
    public String index(HttpServletRequest request) {
        setClient(request);
        return "booking_appraise_manage";
    }

    @GetMapping("/api/booking_appraise/search")
    @ResponseBody
    public Result search(HttpServletRequest request, HttpServletResponse response, String phone,
                         BookingAppraise criteria, Date create_date_start, Date create_date_end, Boolean download) throws NoSuchFieldException, IllegalAccessException, IOException {

        if (download == null) download = false;
        if (criteria == null) criteria = new BookingAppraise();

        if (StringUtils.isNotBlank(phone)) {
            UserInfo userInfo = userService.getUserInfoByPhone(phone);
            if (userInfo == null) {
                throw new XiangShuiException("手机号对应的用户不存在");
            } else {
                criteria.setUin(userInfo.getUin());
            }
        }

        List<ScanFilter> scanFilterList = bookingAppraiseDao.makeScanFilterList(criteria, new String[]{
                "booking_id",
                "area_id",
                "score",
                "uin",
        });
        if (scanFilterList == null) {
            scanFilterList = new ArrayList<>();
        }
        if (StringUtils.isNotBlank(criteria.getSuggest())) {
            scanFilterList.add(new ScanFilter("suggest").contains(criteria.getSuggest()));
        }

        bookingAppraiseDao.appendDateRangeFilter(scanFilterList, "createtime", create_date_start, create_date_end);

        ScanSpec scanSpec = new ScanSpec();
        if (scanFilterList != null && scanFilterList.size() > 0) {
            scanSpec.withScanFilters(scanFilterList.toArray(new ScanFilter[scanFilterList.size()]));
        }

        List<BookingAppraise> bookingAppraiseList = bookingAppraiseDao.scan(scanSpec);

        List<Area> areaList = null;
        List<UserInfo> userInfoList = null;

        if (bookingAppraiseList != null && bookingAppraiseList.size() > 0) {

            Set<Integer> uinSet = new HashSet<>();
            Set<Integer> areaIdSet = new HashSet<>();

            bookingAppraiseList.forEach(new Consumer<BookingAppraise>() {
                @Override
                public void accept(BookingAppraise bookingAppraise) {
                    if (bookingAppraise == null) return;
                    if (bookingAppraise.getUin() != null) {
                        uinSet.add(bookingAppraise.getUin());
                    }
                    if (bookingAppraise.getArea_id() != null) {
                        areaIdSet.add(bookingAppraise.getArea_id());
                    }
                }
            });

            if (uinSet.size() > 0) {
                userInfoList = ServiceUtils.division(uinSet.toArray(new Integer[uinSet.size()]), 100, new CallBackForResult<Integer[], List<UserInfo>>() {
                    @Override
                    public List<UserInfo> run(Integer[] uins) {
                        return userInfoDao.batchGetItem("uin", uins, new String[]{
                                "uin",
                                "phone",
                        });
                    }
                }, new Integer[0]);
            }
            if (areaIdSet.size() > 0) {
                areaList = ServiceUtils.division(areaIdSet.toArray(new Integer[areaIdSet.size()]), 100, new CallBackForResult<Integer[], List<Area>>() {
                    @Override
                    public List<Area> run(Integer[] areaIds) {
                        return areaDao.batchGetItem("area_id", areaIds, new String[]{
                                "area_id",
                                "title",
                                "city",
                                "address",
                        });
                    }
                }, new Integer[0]);
            }

        }

        if (download) {
            Map<String,City> cityMap=new HashMap<>();
            List<City> cityList=cityService.getCityList();
            cityList.forEach(city -> cityMap.put(city.getCity(),city));
            Map<Integer, Area> areaMap = new HashMap<>();
            Map<Integer, UserInfo> userInfoMap = new HashMap<>();
            if (userInfoList != null && userInfoList.size() > 0) {
                userInfoList.forEach(new Consumer<UserInfo>() {
                    @Override
                    public void accept(UserInfo userInfo) {
                        if (userInfo != null) {
                            userInfoMap.put(userInfo.getUin(), userInfo);
                        }
                    }
                });
            }
            if (areaList != null && areaList.size() > 0) {
                areaList.forEach(new Consumer<Area>() {
                    @Override
                    public void accept(Area area) {
                        if (area != null) {
                            areaMap.put(area.getArea_id(), area);
                        }
                    }
                });
            }

            List<List<Object>> data = new ArrayList<>();
            List<Object> headRow = new ArrayList<>();
            headRow.add("订单编号");
            headRow.add("场地编号");
            headRow.add("场地名称");
            headRow.add("场地区域");
            headRow.add("场地城市");
            headRow.add("场地详细地址");
            headRow.add("用户编号");
            headRow.add("用户手机号");
            headRow.add("评论时间");
            headRow.add("评论星级");
            headRow.add("描述标签");
            headRow.add("用户描述");
            data.add(headRow);


            if (bookingAppraiseList != null && bookingAppraiseList.size() > 0) {
                bookingAppraiseList.forEach(new Consumer<BookingAppraise>() {
                    @Override
                    public void accept(BookingAppraise bookingAppraise) {
                        if (bookingAppraise == null) {
                            return;
                        }
                        List<Object> row = new ArrayList<>();

                        row.add(String.valueOf(bookingAppraise.getBooking_id()));
                        row.add(String.valueOf(bookingAppraise.getArea_id()));
                        Area area = areaMap.get(bookingAppraise.getArea_id());
                        if (area != null) {
                            row.add(area.getTitle());
                            row.add(cityMap.containsKey(area.getCity())  ? cityMap.get(area.getCity()).getRegion() : null);
                            row.add(area.getCity());
                            row.add(area.getAddress());
                        } else {
                            row.add(null);
                            row.add(null);
                            row.add(null);
                            row.add(null);
                        }
                        row.add(String.valueOf(bookingAppraise.getUin()));
                        UserInfo userInfo = userInfoMap.get(bookingAppraise.getUin());
                        if (userInfo != null) {
                            row.add(userInfo.getPhone());
                        } else {
                            row.add(null);
                        }
                        if (bookingAppraise.getCreatetime() != null) {
                            row.add(DateUtils.format(bookingAppraise.getCreatetime() * 1000, "yyyy-MM-dd hh:mm"));
                        } else {
                            row.add(null);
                        }
                        row.add(String.valueOf(bookingAppraise.getScore()));
                        if (bookingAppraise.getAppraise() != null && bookingAppraise.getAppraise().size() > 0) {
                            row.add(JSON.toJSONString(bookingAppraise.getAppraise()));
                        } else {
                            row.add(null);
                        }
                        row.add(bookingAppraise.getSuggest());

                        data.add(row);
                    }
                });
            }

            XSSFWorkbook workbook = ExcelUtils.export(data);
            response.addHeader("Content-Disposition", "attachment;filename=" + new String("bookingAppraise.xlsx".getBytes()));
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            workbook.close();
            return null;


        } else {
            return new Result(CodeMsg.SUCCESS)
                    .putData("bookingAppraiseList", bookingAppraiseList)
                    .putData("areaList", areaList)
                    .putData("userInfoList", userInfoList)
                    .putData("cityList", cityService.getCityList());
        }
    }


    @GetMapping("/api/booking_appraise/{booking_id:\\d+}")
    @ResponseBody
    public Result getByBookingId(@PathVariable("booking_id") long booking_id) {
        BookingAppraise bookingAppraise = bookingAppraiseDao.getItem(new PrimaryKey("booking_id", booking_id));
        if (bookingAppraise == null) {
            return new Result(CodeMsg.NO_FOUND);
        } else {
            return new Result(CodeMsg.SUCCESS)
                    .putData("bookingAppraise", bookingAppraise)
                    .putData("userInfo", userService.getUserInfoByUin(bookingAppraise.getUin()))
                    .putData("area", bookingAppraise.getArea_id() != null ? areaDao.getItem(new PrimaryKey("area_id", bookingAppraise.getArea_id())) : null);
        }
    }


}
