package com.xiangshui.server.service;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.constant.AreaStatusOption;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.redis.AreaKeyPrefix;
import com.xiangshui.server.dao.redis.RedisService;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.domain.FailureReport;
import com.xiangshui.server.domain.fragment.CapsuleType;
import com.xiangshui.server.domain.fragment.Location;
import com.xiangshui.server.domain.fragment.RushHour;
import com.xiangshui.server.exception.XiangShuiException;
import com.xiangshui.server.relation.BookingRelation;
import com.xiangshui.server.relation.CapsuleRelation;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.web.result.CodeMsg;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.*;

@Component
public class AreaService {

    @Value("${isdebug}")
    protected boolean debug;

    @Autowired
    AreaDao areaDao;
    @Autowired
    S3Service s3Service;
    @Autowired
    RedisService redisService;

    public Area getAreaById(int area_id) {
        Area area = redisService.get(AreaKeyPrefix.cache, String.valueOf(area_id), Area.class);
        if (area == null) {
            area = areaDao.getItem(new PrimaryKey("area_id", area_id));
            if (area != null) {
                redisService.set(AreaKeyPrefix.cache, String.valueOf(area_id), area);
            }
        }
        return area;
    }


    public List<Integer> getAreaIdListByCity(String city) {
        if (StringUtils.isBlank(city)) {
            return null;
        }

        List<Area> areaList = getAreaListByCity(city, new String[]{"area_id"});
        if (areaList == null) {
            return null;
        }

        List<Integer> areaIdList = new ArrayList<Integer>(areaList.size());
        for (Area area : areaList) {
            areaIdList.add(area.getArea_id());
        }
        return areaIdList;
    }

    public List<Area> getAreaListByCity(String city, String[] attributes) {
        if (StringUtils.isBlank(city)) {
            return null;
        }
        return areaDao.scan(new ScanSpec().withScanFilters(new ScanFilter("city").eq(city)).withAttributesToGet(attributes));
    }

    public List<Area> getAreaListByIds(Integer... areaIds) {
        return areaDao.batchGetItem("area_id", areaIds);
    }

    public Map<Integer, Area> getAreaMapByIds(Integer[] areaIds) {
        List<Area> areaList = getAreaListByIds(areaIds);
        if (areaList == null || areaList.size() == 0) {
            return null;
        }
        Map<Integer, Area> areaMap = new HashMap<Integer, Area>(areaList.size());
        for (Area area : areaList) {
            areaMap.put(area.getArea_id(), area);
        }
        return areaMap;
    }


    public void matchAreaForCapsule(CapsuleRelation capsuleRelation) {
        if (capsuleRelation == null || capsuleRelation.getArea_id() == null) {
            return;
        }
        capsuleRelation.set_area(getAreaById(capsuleRelation.getArea_id()));
    }

    public void matchAreaForCapsule(List<CapsuleRelation> capsuleRelationList) {
        if (capsuleRelationList == null || capsuleRelationList.size() == 0) {
            return;
        }
        Set<Integer> areaIdSet = new HashSet<Integer>();
        for (CapsuleRelation capsuleRelation : capsuleRelationList) {
            if (capsuleRelation.getArea_id() != null) {
                areaIdSet.add(capsuleRelation.getArea_id());
            }
        }
        Map<Integer, Area> areaMap = getAreaMapByIds(areaIdSet.toArray(new Integer[0]));
        for (CapsuleRelation capsuleRelation : capsuleRelationList) {
            if (areaMap.containsKey(capsuleRelation.getArea_id())) {
                capsuleRelation.set_area(areaMap.get(capsuleRelation.getArea_id()));
            }
        }
    }

    public void matchAreaForBooking(BookingRelation bookingRelation) {
        if (bookingRelation == null) {
            return;
        }
        bookingRelation.set_area(getAreaById(bookingRelation.getArea_id()));
    }

    public void matchAreaForBooking(List<BookingRelation> bookingRelationList) {
        if (bookingRelationList == null || bookingRelationList.size() == 0) {
            return;
        }
        Set<Integer> areaIdSet = new HashSet<Integer>();
        for (BookingRelation bookingRelation : bookingRelationList) {
            if (bookingRelation.getArea_id() != null) {
                areaIdSet.add(bookingRelation.getArea_id());
            }
        }
        Map<Integer, Area> areaMap = getAreaMapByIds(areaIdSet.toArray(new Integer[0]));
        for (BookingRelation bookingRelation : bookingRelationList) {
            if (areaMap.containsKey(bookingRelation.getArea_id())) {
                bookingRelation.set_area(areaMap.get(bookingRelation.getArea_id()));
            }
        }
    }


    public void fillLocation(Area area) throws IOException {
        if (area == null) {
            throw new XiangShuiException("方法参数不能为空");
        }
        String string = Jsoup.connect("http://api.map.baidu.com/geocoder/v2/?address=" + area.getCity() + " " + area.getAddress() + "&output=json&ak=" + "71UPECanchHaS66O2KsxPBSetZkCV7wW").execute().body();
        JSONObject resp = JSONObject.parseObject(string);
        if (resp.getIntValue("status") == 0) {
            JSONObject locationJson = resp.getJSONObject("result").getJSONObject("location");
            float lat = locationJson.getFloatValue("lat");
            float lng = locationJson.getFloatValue("lng");
            if (lat > 0 && lng > 0) {
                Location location = new Location();
                location.setLatitude((int) (lat * 1000000));
                location.setLongitude((int) (lng * 1000000));
                area.setLocation(location);
                String mapImgUrl = "http://api.map.baidu.com/staticimage/v2?ak=71UPECanchHaS66O2KsxPBSetZkCV7wW&width=800&height=500&markers=" + lng + "," + lat + "&zoom=15&markerStyles=l,|l,";
                String imgurl = s3Service.uploadImageToAreaimgs(IOUtils.toByteArray(new URL(mapImgUrl)));
                area.setArea_img(imgurl);
            } else {
                throw new XiangShuiException("获取经纬度失败，请修改地址重试");
            }
        } else {
            throw new XiangShuiException("获取经纬度失败，请修改地址重试");
        }
    }

    public boolean validateRushHours(Area criteria) {
        if (criteria != null) {
            if (criteria.getRushHours() != null && criteria.getRushHours().size() > 0) {
                for (RushHour rushHour : criteria.getRushHours()) {
                    if (rushHour == null || rushHour.getStart_time() <= 0 || rushHour.getEnd_time() <= 0) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public boolean validateImgs(Area criteria) {
        if (criteria == null) {
            return false;
        } else {
            if (criteria.getImgs() == null || criteria.getImgs().size() == 0) {
                return false;
            }
            for (String img : criteria.getImgs()) {
                if (StringUtils.isBlank(img)) {
                    return false;
                }
            }
            return true;
        }
    }


    public void updateArea(Area criteria) throws Exception {
        if (criteria == null) {
            throw new XiangShuiException("方法参数不能为空");
        }
        if (criteria.getArea_id() == null || criteria.getArea_id() <= 0) {
            throw new XiangShuiException("场地编号不能为空并且大于0");
        }

        Area area = getAreaById(criteria.getArea_id());
        if (area == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }

        if (StringUtils.isBlank(criteria.getTitle())) {
            throw new XiangShuiException("场地名称不能为空");
        }

        if (StringUtils.isBlank(criteria.getAddress())) {
            throw new XiangShuiException("地址不能为空");
        }

        if (!validateImgs(criteria)) {
            throw new XiangShuiException("图片Url不能为空");
        }

        if (StringUtils.isBlank(criteria.getContact())) {
            throw new XiangShuiException("联系方式不能为空");
        }

//        if (StringUtils.isBlank(criteria.getNotification())) {
//            throw new XiangShuiException("注意事项不能为空");
//        }
        if (criteria.getMinute_start() == null || criteria.getMinute_start() < 1) {
            throw new XiangShuiException("最少时长不能小于1");
        }

        if (!validateRushHours(criteria)) {
            throw new XiangShuiException("高峰时段输入有误");
        }
        if (!(criteria.getCity().equals(area.getCity()) && criteria.getAddress().equals(area.getAddress()))) {
            fillLocation(criteria);
            areaDao.updateItem(new PrimaryKey("area_id", criteria.getArea_id()), criteria, new String[]{
                    "title",
                    "address",
                    "contact",
                    "notification",
                    "area_img",
                    "status",
                    "minute_start",
                    "imgs",
                    "location",
                    "rushHours",
                    "is_external",
                    "is_time_limit",
            });
        } else {
            areaDao.updateItem(new PrimaryKey("area_id", criteria.getArea_id()), criteria, new String[]{
                    "title",
                    "address",
                    "contact",
                    "notification",
                    "status",
                    "minute_start",
                    "imgs",
                    "rushHours",
                    "is_external",
                    "is_time_limit",
            });
        }
        cleanCache(area.getArea_id());
        clean_area_cache_notification();
    }


    public void createArea(Area criteria) throws IOException {
        if (criteria == null) {
            throw new XiangShuiException("方法参数不能为空");
        }
        if (criteria.getArea_id() == null || criteria.getArea_id() <= 0) {
            throw new XiangShuiException("场地编号不能为空并且大于0");
        }

        Area area = getAreaById(criteria.getArea_id());
        if (area != null) {
            throw new XiangShuiException("场地编号已存在");
        }

        if (StringUtils.isBlank(criteria.getTitle())) {
            throw new XiangShuiException("场地名称不能为空");
        }

        if (StringUtils.isBlank(criteria.getCity())) {
            throw new XiangShuiException("城市不能为空");
        }
        if (StringUtils.isBlank(criteria.getAddress())) {
            throw new XiangShuiException("地址不能为空");
        }

        if (!validateImgs(criteria)) {
            throw new XiangShuiException("图片Url不能为空");
        }
        if (StringUtils.isBlank(criteria.getContact())) {
            throw new XiangShuiException("联系方式不能为空");
        }

//        if (StringUtils.isBlank(criteria.getNotification())) {
//            throw new XiangShuiException("注意事项不能为空");
//        }
        if (criteria.getMinute_start() == null || criteria.getMinute_start() < 1) {
            throw new XiangShuiException("最少时长不能小于1");
        }

        if (!validateRushHours(criteria)) {
            throw new XiangShuiException("高峰时段输入有误");
        }
        fillLocation(criteria);
        if (criteria.getStatus() == null) {
            criteria.setStatus(AreaStatusOption.stay.value);
        }
        areaDao.putItem(criteria);
        cleanCache(area.getArea_id());
        clean_area_cache_notification();
    }


    public void updateTypes(Area criteria) throws Exception {
        if (criteria == null) {
            throw new XiangShuiException("方法参数不能为空");
        }
        if (criteria.getArea_id() == null || criteria.getArea_id() <= 0) {
            throw new XiangShuiException("场地编号不能为空并且大于0");
        }
        Area area = getAreaById(criteria.getArea_id());
        if (area == null) {
            throw new XiangShuiException(CodeMsg.NO_FOUND);
        }

        if (criteria.getTypes() == null || criteria.getTypes().size() == 0) {
            throw new XiangShuiException("头等舱类型不能为空");
        }

        for (CapsuleType capsuleType : criteria.getTypes()) {


            if (capsuleType.getPrice() == null || capsuleType.getPrice() <= 0) {
                throw new XiangShuiException("价格必须大于0");
            }

            if (capsuleType.getDay_max_price() == null || capsuleType.getDay_max_price() <= 0) {
                throw new XiangShuiException("每日最高费用必须大于0");
            }

            if (capsuleType.getRush_hour_price() == null || capsuleType.getRush_hour_price() <= 0) {
                throw new XiangShuiException("高峰期价格必须大于0");
            }


//            if (StringUtils.isBlank(capsuleType.getPrice_rule_text())) {
//                throw new XiangShuiException("价格文案不能为空");
//            }


            capsuleType.setType_id(1);
            capsuleType.setSize(1);
            capsuleType.setTypeTitle("共享头等舱");
            capsuleType.setTypeDesc("共享头等舱");


        }
        areaDao.updateItem(new PrimaryKey("area_id", criteria.getArea_id()), criteria, new String[]{
                "types",
        });
        cleanCache(area.getArea_id());
    }

    public List<Area> search(Area criteria, String[] attributes) throws NoSuchFieldException, IllegalAccessException {
        if (criteria == null) {
            return areaDao.scan();
        }
        if (criteria.getArea_id() != null) {
            List<Area> areaList = new ArrayList<Area>();
            Area area = getAreaById(criteria.getArea_id());
            if (area != null) {
                areaList.add(area);
            }
            return areaList;
        } else {
            ScanSpec scanSpec = new ScanSpec();
            List<ScanFilter> filterList = areaDao.makeScanFilterList(criteria, new String[]{
                    "city", "status",
            });
            if (filterList == null) filterList = new ArrayList<ScanFilter>();
            if (StringUtils.isNotBlank(criteria.getTitle())) {
                filterList.add(new ScanFilter("title").contains(criteria.getTitle()));
            }
            if (StringUtils.isNotBlank(criteria.getAddress())) {
                filterList.add(new ScanFilter("address").contains(criteria.getAddress()));
            }
            scanSpec.withScanFilters(filterList.toArray(new ScanFilter[]{}));
            List<Area> areaList = areaDao.scan(scanSpec);
            return areaList;
        }

    }

    public void clean_area_cache_notification() {
        try {
            Jsoup.connect(debug ? "http://devop.xiangshuispace.com/op/clean_area_cache_notification" : "http://op.xiangshuispace.com/op/clean_area_cache_notification").execute();
        } catch (Exception e) {
        }
    }

    public void cleanCache(int area_id) {
        redisService.del(AreaKeyPrefix.cache, String.valueOf(area_id));
    }


    public Set<Integer> getAreaIdSet(List<Booking> bookingList) {
        if (bookingList == null) {
            return null;
        }
        Set<Integer> areaIdSet = new HashSet<Integer>();
        for (Booking booking : bookingList) {
            if (booking != null && booking.getArea_id() != null) {
                areaIdSet.add(booking.getArea_id());
            }
        }
        return areaIdSet;
    }

    public List<Area> getAreaListByBooking(List<Booking> bookingList, final String[] attributes) {
        if (bookingList == null) {
            return null;
        }
        final Set<Integer> areaIdSet = getAreaIdSet(bookingList);
        if (areaIdSet == null || areaIdSet.size() == 0) {
            return null;
        }
        return ServiceUtils.division(areaIdSet.toArray(new Integer[0]), 100, new CallBackForResult<Integer[], List<Area>>() {
            public List<Area> run(Integer[] object) {
                return areaDao.batchGetItem("area_id", object, attributes);
            }
        }, new Integer[0]);

    }

    public List<Area> getAreaListByFailure(List<FailureReport> failureReportList, final String[] attributes) {
        if (failureReportList == null) {
            return null;
        }
        final Set<Integer> areaIdSet = new HashSet<Integer>();
        for (FailureReport failureReport : failureReportList) {
            if (failureReport != null && failureReport.getArea_id() != null) {
                areaIdSet.add(failureReport.getArea_id());
            }
        }
        if (areaIdSet == null || areaIdSet.size() == 0) {
            return null;
        }
        return ServiceUtils.division(areaIdSet.toArray(new Integer[0]), 100, new CallBackForResult<Integer[], List<Area>>() {
            public List<Area> run(Integer[] object) {
                return areaDao.batchGetItem("area_id", object, attributes);
            }
        }, new Integer[0]);

    }

    public JSONObject deviceStatus(String device_id) throws IOException {
        if (StringUtils.isBlank(device_id)) {
            throw new XiangShuiException("device_id 不能为空");
        }
        String body = Jsoup.connect((debug ? "http://dev.xiangshuispace.com:8855" : "http://52.80.56.139:8877") + "/api/device/" + device_id + "/status")
                .header("User-Uin", "100000").ignoreContentType(true).execute().body();
        return JSONObject.parseObject(body);
    }

}
