package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.ScanFilter;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.op.annotation.AuthRequired;
import com.xiangshui.op.annotation.Menu;
import com.xiangshui.op.threadLocal.SessionLocal;
import com.xiangshui.server.constant.CapsuleStatusOption;
import com.xiangshui.server.constant.DeviceVersionOption;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BaseDynamoDao;
import com.xiangshui.server.dao.CapsuleDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.server.relation.CapsuleRelation;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.CapsuleService;
import com.xiangshui.server.service.CityService;
import com.xiangshui.util.*;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
public class CapsuleController extends BaseController {

    @Autowired
    CityService cityService;
    @Autowired
    AreaService areaService;
    @Autowired
    AreaDao areaDao;
    @Autowired
    CapsuleDao capsuleDao;
    @Autowired
    CapsuleService capsuleService;


    @GetMapping("/api/capsule/search")
    @ResponseBody
    public Result search(Capsule criteria, Boolean download, HttpServletResponse response) throws NoSuchFieldException, IllegalAccessException, IOException {

        return new Result(CodeMsg.SUCCESS).putData("capsuleList", capsuleAuthorityTools.filterCapsule(capsuleService.search(criteria, null)));

    }


//    @GetMapping("/api/capsule/search")
//    @ResponseBody
//    public Result search(Capsule criteria, Boolean download, HttpServletResponse response) throws NoSuchFieldException, IllegalAccessException, IOException {
//
//        ScanSpec scanSpec = new ScanSpec();
//        if (Boolean.TRUE.equals(download)) scanSpec.withMaxResultSize(BaseDynamoDao.maxDownloadSize);
//
//        List<Capsule> capsuleList = capsuleDao.scan(scanSpec);
//
////        capsuleList = ListUtils.filter(capsuleList, capsule -> !ListUtils.fieldSet(ListUtils.filter(cacheScheduled.areaList, area -> new Integer(-1).equals(area.getStatus())), Area::getArea_id).contains(capsule.getArea_id()));
//
//        capsuleList.sort(Comparator.comparing(Capsule::getCapsule_id));
//
//        if (Boolean.TRUE.equals(download)) {
//
//            ExcelUtils.export(Arrays.asList(
//                    new ExcelUtils.Column<Capsule>("设备编号") {
//                        @Override
//                        public Object render(Capsule capsule) {
//                            return capsule.getCapsule_id();
//                        }
//                    },
//                    new ExcelUtils.Column<Capsule>("硬件设备ID") {
//                        @Override
//                        public Object render(Capsule capsule) {
//                            return capsule.getDevice_id();
//                        }
//                    },
//                    new ExcelUtils.Column<Capsule>("硬件设备版本") {
//                        @Override
//                        public Object render(Capsule capsule) {
//                            return Option.getActiveText(DeviceVersionOption.options, capsule.getDevice_version());
//                        }
//                    },
//                    new ExcelUtils.Column<Capsule>("场地编号") {
//                        @Override
//                        public Object render(Capsule capsule) {
//                            return capsule.getArea_id();
//                        }
//                    },
//                    new ExcelUtils.Column<Capsule>("城市") {
//                        @Override
//                        public Object render(Capsule capsule) {
//                            return cacheScheduled.areaMapOptions.tryValueForResult(capsule.getArea_id(), Area::getCity, null);
//                        }
//                    },
//                    new ExcelUtils.Column<Capsule>("场地名称") {
//                        @Override
//                        public Object render(Capsule capsule) {
//                            return cacheScheduled.areaMapOptions.tryValueForResult(capsule.getArea_id(), Area::getTitle, null);
//                        }
//                    },
//                    new ExcelUtils.Column<Capsule>("创建时间") {
//                        @Override
//                        public Object render(Capsule capsule) {
//                            return capsule.getCreate_time() != null ? DateUtils.format(capsule.getCreate_time() * 1000) : null;
//                        }
//                    },
//                    new ExcelUtils.Column<Capsule>("归属状态") {
//                        @Override
//                        public Object render(Capsule capsule) {
//                            if (cacheScheduled.badCapsuleIdSet.contains(capsule.getCapsule_id())) return "已销毁";
//                            if (cacheScheduled.giveCapsuleIdSet.contains(capsule.getCapsule_id())) return "赠予场地";
//                            return null;
//                        }
//                    },
//                    new ExcelUtils.Column<Capsule>("备注") {
//                        @Override
//                        public Object render(Capsule capsule) {
//                            return capsule.getRemark();
//                        }
//                    }
//            ), capsuleList, response, "设备列表.xlsx");
//
//
//            return null;
//        } else {
//            return new Result(CodeMsg.SUCCESS).putData("capsuleList", capsuleList);
//
//        }
//    }


    @GetMapping("/api/capsule/{capsule_id:\\d+}")
    @ResponseBody
    public Result get(@PathVariable("capsule_id") Long capsule_id) {

        Capsule capsule = capsuleService.getCapsuleById(capsule_id);
        if (capsule == null) {
            return new Result(CodeMsg.NO_FOUND);
        }
        capsuleAuthorityTools.auth(capsule, SessionLocal.getUsername(),true);
        Area area = areaDao.getItem(new PrimaryKey("area_id", capsule.getArea_id()));
        if (area != null) {
            CapsuleRelation capsuleRelation = new CapsuleRelation();
            BeanUtils.copyProperties(capsule, capsuleRelation);
            capsuleRelation.set_area(area);
            return new Result(CodeMsg.SUCCESS).putData("capsule", capsuleRelation);
        } else {
            return new Result(CodeMsg.SUCCESS).putData("capsule", capsule);
        }
    }

    @GetMapping("/api/capsule/{capsule_id:\\d+}/validateForCreate")
    @ResponseBody
    public Result validateForCreate(@PathVariable("capsule_id") Long capsule_id) {
        Capsule capsule = capsuleDao.getItem(new PrimaryKey("capsule_id", capsule_id));
        if (capsule == null) {
//            capsuleAuthorityTools.authForException(capsule);
            return new Result(CodeMsg.SUCCESS);
        } else {
            return new Result(-1, "头等舱编号已存在");
        }
    }

    @GetMapping("/api/capsule/{capsule_id:\\d+}/validateDeviceIdForSave/{device_id}")
    @ResponseBody
    public Result validateDeviceIdForSave(@PathVariable("capsule_id") Long capsule_id, @PathVariable("device_id") String device_id) {
        List<Capsule> capsuleList = capsuleDao.scan(new ScanSpec().withScanFilters(new ScanFilter("device_id").eq(device_id), new ScanFilter("capsule_id").ne(capsule_id)));
        if (capsuleList == null || capsuleList.size() == 0) {
            return new Result(CodeMsg.SUCCESS);
        } else {
            Set<Integer> areaIdSet = new HashSet<>();
            for (Capsule capsule : capsuleList) {
                areaIdSet.add(capsule.getArea_id());
            }
            List<Area> areaList = areaDao.batchGetItem("area_id", areaIdSet.toArray(), null);
            return new Result(-1, "硬件设备ID已占用").putData("capsuleList", capsuleList).putData("areaList", areaList);
        }
    }

    @PostMapping("/api/capsule/{capsule_id:\\d+}/update")
    @ResponseBody
    public Result update(@PathVariable("capsule_id") Long capsule_id, @RequestBody Capsule criteria) throws Exception {
        Capsule capsule = capsuleDao.getItem(new PrimaryKey("capsule_id", capsule_id));
        capsuleAuthorityTools.auth(capsule, SessionLocal.getUsername(),true);
        criteria.setCapsule_id(capsule_id);
        capsuleService.updateCapsule(criteria);
        return new Result(CodeMsg.SUCCESS);
    }

    @PostMapping("/api/capsule/create")
    @ResponseBody
    public Result create(@RequestBody Capsule criteria) throws Exception {
        capsuleAuthorityTools.auth(criteria, SessionLocal.getUsername(),true);
        capsuleService.createCapsule(criteria);
        return new Result(CodeMsg.SUCCESS);
    }


}
