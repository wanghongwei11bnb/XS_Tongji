package com.xiangshui.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListUtils {


    public static <T> List<T> filter(List<T> list, CallBackForResult<T, Boolean> callBackForResult) {
        List<T> newList = new ArrayList<>();
        if (list != null) {
            list.forEach(t -> {
                if (callBackForResult != null && callBackForResult.run(t)) {
                    newList.add(t);
                }
            });
        }
        return newList;
    }

    public static <T> List<T> filter(List<T> list, CallBack2ForResult<T, Integer, Boolean> callBackForResult) {
        List<T> newList = new ArrayList<>();
        if (list != null && callBackForResult != null) {
            for (int i = 0; i < list.size(); i++) {
                if (callBackForResult.run(list.get(i), i)) {
                    newList.add(list.get(i));
                }
            }

        }
        return newList;
    }

    public static <T, R> Set<R> fieldSet(List<T> list, CallBackForResult<T, R> callBackForResult) {
        Set<R> set = new HashSet<>();
        if (list != null && callBackForResult != null) {
            for (int i = 0; i < list.size(); i++) {
                R r = callBackForResult.run(list.get(i));
                if (r != null) {
                    set.add(r);
                }
            }
        }
        return set;
    }

    public static <T> double fieldSum(List<T> list, CallBackForResult<T, Double> callBackForResult) {
        double result = 0;
        if (list != null && callBackForResult != null) {
            for (int i = 0; i < list.size(); i++) {
                Double value = callBackForResult.run(list.get(i));
                result += value != null ? value : 0;
            }
        }
        return result;
    }

    public static <T, R> List<R> map(List<T> list, CallBackForResult<T, R> callBack) {
        List<R> result = new ArrayList();
        if (list != null) {
            for (T t : list) {
                result.add(callBack.run(t));
            }
        }
        return result;
    }


}
