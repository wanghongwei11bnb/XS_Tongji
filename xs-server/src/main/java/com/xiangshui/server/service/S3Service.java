package com.xiangshui.server.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.xiangshui.util.MD5;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

@Service
public class S3Service {


    AWSCredentials credentials = null;


    public static void main(String[] args) throws Exception {
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider().getCredentials();
        } catch (Exception e) {
            e.printStackTrace();
        }
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion("cn-north-1")
                .build();


        String bucketName = "areaimgs";
        File file = new File("/Users/whw/Downloads/vivo应用商店展示图.jpg");
        byte[] bs = FileUtils.readFileToByteArray(file);
        String key = MD5.getMD5(bs);
        System.out.println(key);


        s3.putObject(new PutObjectRequest(bucketName, key, file).withAccessControlList(new AccessControlList()));

//        s3.putObject(bucketName, key, file);

//        s3.deleteObject(bucketName, key);

    }


}
