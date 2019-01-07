package com.xiangshui.util;

import java.util.ArrayList;
import java.util.List;

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
}
