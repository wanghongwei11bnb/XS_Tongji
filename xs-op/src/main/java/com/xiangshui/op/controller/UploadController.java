package com.xiangshui.op.controller;

import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.service.AreaService;
import com.xiangshui.server.service.CityService;
import com.xiangshui.server.service.S3Service;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
public class UploadController extends BaseController {

    @Autowired
    CityService cityService;
    @Autowired
    AreaService areaService;
    @Autowired
    AreaDao areaDao;
    @Autowired
    S3Service s3Service;


    @PostMapping("/api/upload/area_img")
    @ResponseBody
    public Result upload_area_img(MultipartFile uploadFile) throws IOException {
        uploadFile.getOriginalFilename();
        byte[] bytes = uploadFile.getBytes();
        String contentType = uploadFile.getContentType();
        String imgUrl = s3Service.uploadAreaImg(bytes, contentType);
        if (StringUtils.isNotBlank(imgUrl)) {
            return new Result(CodeMsg.SUCCESS).putData("url", imgUrl);
        } else {
            return new Result(-1, "上传失败");
        }
    }
}
