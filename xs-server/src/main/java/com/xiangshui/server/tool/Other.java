package com.xiangshui.server.tool;

import com.alibaba.fastjson.JSON;
import com.xiangshui.util.CallBackForResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Other {
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 字符串模板拼接
     *
     * @param format
     * @param args
     * @return
     */
    public static String stringFormat(String format, Object... args) {
        if (StringUtils.isBlank(format)) return format;
        if (args == null || args.length == 0) return format;
        int cursor = 0;
        for (Object arg : args) {
            int index = format.indexOf("{}", cursor);
            if (index == -1) break;
            String str = String.valueOf(arg);
            format = format.substring(0, index) + str + format.substring(index + 2);
            cursor = index + str.length();
        }
        return format;
    }


    public static <T> T[] toArray(CallBackForResult<T, Boolean> filter, T... ts) {
        List<T> list = new ArrayList<>();
        for (T t : ts) {
            if (filter != null && !filter.run(t)) continue;
            list.add(t);
        }
        return (T[]) list.toArray();
    }


    public static <T> T[] toArrayNotEmpty(T... ts) {
        return toArray(t -> t != null, ts);
    }


    public static void main(String[] args) {
        log.info(JSON.toJSONString(toArrayNotEmpty(null, 2.2, 3, null, 6)));
    }


}
