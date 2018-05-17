package com.xiangshui.server.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.xiangshui.util.EasyImage;
import com.xiangshui.util.MD5;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.*;

@Service
public class S3Service implements InitializingBean {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    AWSCredentials credentials = null;
    AmazonS3 s3 = null;
    private final String BUCKET_NAME_AREAIMGS = "areaimgs";

    private final int[] sizes = {227, 383, 640, 750, 957, 1080, 1615};

    public void afterPropertiesSet() {
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            e.printStackTrace();
        }
        s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion("cn-north-1")
                .build();
    }


    public String uploadAreaImg(byte[] bytes, String contentType) throws IOException {
        String key = MD5.getMD5(bytes);
        AccessControlList accessControlList = new AccessControlList();
        accessControlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        String formatName;
        if (StringUtils.isNotBlank(contentType) && contentType.indexOf("/") > -1 && contentType.indexOf("/") < contentType.length() - 1) {
            formatName = contentType.substring(contentType.indexOf("/") + 1);
        } else {
            formatName = "jpg";
        }
        for (int size : sizes) {
            EasyImage easyImage = new EasyImage(bytes);
            easyImage.resize((int) (size * 1f / easyImage.getWidth() * 100));
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            ImageIO.write(easyImage.getAsBufferedImage(), formatName, out);
            s3.putObject(new PutObjectRequest(BUCKET_NAME_AREAIMGS, key + "_" + size, new ByteArrayInputStream(out.toByteArray()), metadata).withAccessControlList(accessControlList));
        }
        return "https://s3.cn-north-1.amazonaws.com.cn/" + BUCKET_NAME_AREAIMGS + "/" + key;
    }

    public String uploadImageToAreaimgs(byte[] bs) throws IOException {
        String key = MD5.getMD5(bs);
        AccessControlList accessControlList = new AccessControlList();
        accessControlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        for (int size : sizes) {
            EasyImage easyImage = new EasyImage(bs);
            easyImage.resize((int) (size * 1f / easyImage.getWidth() * 100));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(easyImage.getAsBufferedImage(), "jpg", out);
            s3.putObject(new PutObjectRequest(BUCKET_NAME_AREAIMGS, key + "_" + size, new ByteArrayInputStream(out.toByteArray()), new ObjectMetadata()).withAccessControlList(accessControlList));
        }
        return "https://s3.cn-north-1.amazonaws.com.cn/" + BUCKET_NAME_AREAIMGS + "/" + key;
    }

    public String uploadImageToAreaimgs(File file) throws IOException {
        byte[] bs = FileUtils.readFileToByteArray(file);
        String key = MD5.getMD5(bs);

        AccessControlList accessControlList = new AccessControlList();
        accessControlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        for (int size : sizes) {
            EasyImage easyImage = new EasyImage(file);
            easyImage.resize((int) (size * 1f / easyImage.getWidth() * 100));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(easyImage.getAsBufferedImage(), "jpg", out);
            s3.putObject(new PutObjectRequest(BUCKET_NAME_AREAIMGS, key + "_" + size, new ByteArrayInputStream(out.toByteArray()), new ObjectMetadata()).withAccessControlList(accessControlList));
        }
        return "https://s3.cn-north-1.amazonaws.com.cn/" + BUCKET_NAME_AREAIMGS + "/" + key;
    }

    public void upload(String bucketName, String key, InputStream inputStream, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        AccessControlList accessControlList = new AccessControlList();
        accessControlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        s3.putObject(new PutObjectRequest(bucketName, key, inputStream, metadata).withAccessControlList(accessControlList));
    }


    public void upload(String bucketName, String key, File file) {
        AccessControlList accessControlList = new AccessControlList();
        accessControlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        s3.putObject(new PutObjectRequest(bucketName, key, file).withAccessControlList(accessControlList));
    }


}
