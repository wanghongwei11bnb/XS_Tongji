package com.xiangshui.server.service;

import com.alibaba.fastjson.JSON;
import com.xiangshui.util.CallBackForResult;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ServiceUtils {

    public static <I, T> List<T> division(I[] is, int max, CallBackForResult<I[], List<T>> callBackForResult, I[] is2) {
        if (is == null || is.length == 0) return null;
        List<T> tList = new ArrayList<T>(is.length);
        List<I> iList = new ArrayList<I>(max);
        for (int i = 0; i < is.length; i++) {
            iList.add(is[i]);
            if (iList.size() >= max) {
                List<T> resultList = callBackForResult.run(iList.toArray(is2));
                if (resultList != null && resultList.size() > 0) {
                    tList.addAll(resultList);
                }
                iList.clear();
            }
        }
        if (iList != null && iList.size() > 0) {
            List<T> resultList = callBackForResult.run(iList.toArray(is2));
            if (resultList != null && resultList.size() > 0) {
                tList.addAll(resultList);
            }
        }
        return tList;
    }


    public static <I, T> List<T> division(I[] is, int max, CallBackForResult<I[], List<T>> callBackForResult) {
        List<T> tList = new ArrayList<>();
        if (is == null || is.length == 0) return tList;
        Class<I> iClass = (Class<I>) is[0].getClass();
        I[] isTemp = (I[]) Array.newInstance(iClass, 0);
        List<I> iList = new ArrayList<>();
        for (int i = 0; i < is.length; i++) {
            iList.add(is[i]);
            if (iList.size() >= max) {
                List<T> resultList = callBackForResult.run(iList.toArray(isTemp));
                if (resultList != null && resultList.size() > 0) {
                    tList.addAll(resultList);
                }
                iList.clear();
            }
        }
        if (iList != null && iList.size() > 0) {
            List<T> resultList = callBackForResult.run(iList.toArray(isTemp));
            if (resultList != null && resultList.size() > 0) {
                tList.addAll(resultList);
            }
        }
        return tList;
    }

    public static void main(String[] args) {
        division(new Integer[]{
                1, 2, 3, 454, 6, 7, 8, 9
        }, 2, (CallBackForResult<Integer[], List<Integer>>) integers -> {
            System.out.println(JSON.toJSONString(integers));
            return null;
        });
    }

}
