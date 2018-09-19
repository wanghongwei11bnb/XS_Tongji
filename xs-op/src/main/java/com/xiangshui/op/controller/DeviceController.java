package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.bean.DeviceStatus;
import com.xiangshui.op.scheduled.DeviceStatusScheduled;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.service.*;
import com.xiangshui.util.CallBackForResult;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Controller
public class DeviceController extends BaseController {

    @Autowired
    CityService cityService;
    @Autowired
    AreaService areaService;
    @Autowired
    AreaDao areaDao;
    @Autowired
    S3Service s3Service;

    @Autowired
    CapsuleService capsuleService;

    @Autowired
    DeviceStatusScheduled deviceStatusScheduled;

    @Menu(value = "实时设备状态")
    @AuthRequired("实时设备状态")
    @GetMapping("/device_status_manage")
    public String device_status_manage(HttpServletRequest request) {
        setClient(request);
        return "device_status_manage";
    }


    @GetMapping("/api/device_status/search")
    @ResponseBody
    public Result search(HttpServletRequest request, HttpServletResponse response, Boolean download) throws IOException {
        if (download == null) download = false;
        List<DeviceStatus> deviceStatusList = new ArrayList<>();
        deviceStatusScheduled.statusMap.forEach((aLong, deviceStatus) -> {
            deviceStatusList.add(deviceStatus);
        });
        deviceStatusList.sort(Comparator.comparingInt(DeviceStatus::getArea_id));
        Set<Integer> areaIdSet = new HashSet<>();
        for (DeviceStatus deviceStatus : deviceStatusList) {
            areaIdSet.add(deviceStatus.getArea_id());
        }
        List<Area> areaList = null;
        if (areaIdSet.size() > 0) {
            areaList = ServiceUtils.division(areaIdSet.toArray(new Integer[areaIdSet.size()]), 100, new CallBackForResult<Integer[], List<Area>>() {
                @Override
                public List<Area> run(Integer[] object) {
                    return areaService.getAreaListByIds(object);
                }
            }, new Integer[0]);
        }
        if (download) {

            Map<Integer, Area> areaMap = new HashMap<>();
            if (areaList != null) {
                for (Area area : areaList) {
                    if (area != null) {
                        areaMap.put(area.getArea_id(), area);
                    }
                }
            }

            List<List<String>> data = new ArrayList<>();
            List<String> headRow = new ArrayList<>();
            headRow.add("场地编号");
            headRow.add("场地名称");
            headRow.add("城市");
            headRow.add("地址");
            headRow.add("头等舱编号");
            headRow.add("设备ID");
            headRow.add("门状态");
            headRow.add("WIFI状态");
            headRow.add("其他");
            headRow.add("获取时间");
            data.add(headRow);
            deviceStatusList.forEach(new Consumer<DeviceStatus>() {
                @Override
                public void accept(DeviceStatus deviceStatus) {
                    Area area = areaMap.get(deviceStatus.getArea_id());
                    if (area == null) {
                        return;
                    }
                    List<String> row = new ArrayList<>();
                    row.add(String.valueOf(deviceStatus.getArea_id()));
                    row.add(area.getTitle());
                    row.add(area.getCity());
                    row.add(area.getAddress());
                    row.add(String.valueOf(deviceStatus.getCapsule_id()));
                    row.add(deviceStatus.getDevice_id());
                    row.add(deviceStatus.getStatus() != null && (deviceStatus.getStatus() & 1) == 0 ? "关闭" : "打开");
                    row.add(deviceStatus.getWifi_flag() != null && deviceStatus.getWifi_flag() == 1 ? "链接成功" : "链接失败");
                    row.add(deviceStatus.getStatus_text());
                    row.add(DateUtils.format(deviceStatus.getUpdate_time()));
                    data.add(row);
                }
            });

            XSSFWorkbook workbook = ExcelUtils.export(data);
            response.addHeader("Content-Disposition", "attachment;filename=" + new String("status.xlsx".getBytes()));
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            workbook.close();
            return null;

        } else {
            return new Result(CodeMsg.SUCCESS)
                    .putData("deviceStatusList", deviceStatusList)
                    .putData("areaList", areaList)
                    .putData("cityList", cityService.getCityList())
                    ;
        }
    }

    @Menu("实时设备状态——更新")
    @AuthRequired("实时设备状态——更新")
    @PostMapping("/api/device_status/refresh")
    @ResponseBody
    public Result refresh() {
        deviceStatusScheduled.put();
        return new Result(CodeMsg.SUCCESS).setMsg("操作成功!最新结果正在获取中，请等一等!");
    }


    @PostMapping("/api/device/{device_id}/status")
    @ResponseBody
    public Result device_status(@PathVariable("device_id") String device_id) throws IOException {
        return new Result(CodeMsg.SUCCESS).putData("resp", areaService.deviceStatus(device_id));
    }


    @PostMapping("/api/capsule/{capsule_id:\\d+}/device/status")
    @ResponseBody
    public Result capsule_device_status(@PathVariable("capsule_id") Long capsule_id) throws IOException {
        Capsule capsule = capsuleService.getCapsuleById(capsule_id);
        if (capsule == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        if (StringUtils.isBlank(capsule.getDevice_id())) {
            return new Result(-1, "头等舱没有配置设备编号");
        }
        return device_status(capsule.getDevice_id());
    }


}
