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
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

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
    public Result search() {
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
        return new Result(CodeMsg.SUCCESS)
                .putData("deviceStatusList", deviceStatusList)
                .putData("areaList", areaList)
                ;
    }

    @Menu("实时设备状态——更新")
    @AuthRequired("实时设备状态——更新")
    @PostMapping("/api/device_status/refresh")
    @ResponseBody
    public Result refresh() {
        deviceStatusScheduled.put();
        return new Result(CodeMsg.SUCCESS).setMsg("操作成功!最新结果正在获取中，请等一等!");
    }


}
