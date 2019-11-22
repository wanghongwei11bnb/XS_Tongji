package com.xiangshui.server.service;


import com.alibaba.fastjson.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtils {


    public static String iv_key="\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";

    public static String encrypt(String str, String key) throws Exception {
        if (key == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (key.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw = key.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"
        IvParameterSpec iv = new IvParameterSpec(iv_key.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(str.getBytes());
        return  Base64.getEncoder().encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }



    public static String decrypt(String str, String key) throws Exception {
        try {
            // 判断Key是否正确
            if (key == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (key.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = key.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(iv_key.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = Base64.getDecoder().decode(str);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }




    public static void main(String[] args) throws Exception {
        String key = "v0Xw6cqlDO1cOehG";
        JSONObject json = new JSONObject()
                .fluentPut("appid", "ovQLFBjxHucSOqPrL8")
                .fluentPut("store_code", "018054000252-,全积分官方,四川省成都市,成都双流国际机场T1")
                .fluentPut("confirm_id", "123")
                .fluentPut("amount", 100);
        String jsonString=json.toJSONString();
        String base64= Base64.getEncoder().encodeToString(jsonString.getBytes());
        String encryptStr = encrypt(base64, key);
        System.out.println("https://pays.jifen360.com/china_air/third_index.html?param="+encryptStr);
    }


}
