package com.xiangshui.server.service;

import com.xiangshui.util.CallBackForResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
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


}
