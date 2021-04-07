package com.xiangshui.web;

import com.alibaba.fastjson.JSON;
import com.xiangshui.util.CallBackForResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String filterEmoji(String source) {
        if (source == null) {
            return null;
        }
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher emojiMatcher = emoji.matcher(source);
        if (emojiMatcher.find()) {
            source = emojiMatcher.replaceAll("");
            return source;
        }
        return source;
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
        return toArray(Objects::nonNull, ts);
    }


    public static List<Integer> split(int amount, int num, boolean random) {
//        int max_item = 100000;
//        int min_item = 30;
//        int max_amount = 1000000;
//        int min_num = 1;
//        int max_num = 500;
        if (amount < 100) throw new RuntimeException("红包总金额不得少于1元");
        if (amount > 1000000) throw new RuntimeException("红包总金额不得高于10000元");
        if (num < 1) throw new RuntimeException("红包个数不得少于1个");
        if (amount / num < 30) throw new RuntimeException("平均每个红包不能低于0.30元");
        if (amount / num > 100000) throw new RuntimeException("平均每个红包不能高于1000元");
        List<Integer> list = new ArrayList<>();
        if (random) {
            amount -= 30 * num;
            list.add(amount);
            while (list.size() < num) {
                int max = list.remove(list.size() - 1);
                int v1 = (int) (Math.random() * max);
                int v2 = max - v1;
                list.add(v1);
                list.add(v2);
                Collections.sort(list);
            }
            for (int i = 0; i < list.size(); i++) {
                list.set(i, list.get(i) + 30);
            }
            while (list.size() > 1 && list.get(list.size() - 1) > 100000) {
                int max = list.remove(list.size() - 1);
                int min = list.remove(0);
                int v1 = 100000;
                int v2 = min + max - 100000;
                list.add(v1);
                list.add(v2);
                Collections.sort(list);
            }
            Collections.shuffle(list);
        } else {
            int multiple = amount / num;
            int remainder = amount % num;
            for (int i = num; i > 0; i--) {
                if (remainder > 0) {
                    remainder--;
                    list.add(multiple + 1);
                } else {
                    list.add(multiple);
                }
            }
        }
        return list;
    }


    public static String getNotBlankValue(String defaultValue, String... values) {
        if (values == null || values.length == 0) return defaultValue;
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) return value.trim();
        }
        return defaultValue;
    }

    public static String verifyPhone(String phone, boolean throwException) {
        String error = null;
        if (StringUtils.isBlank(phone)) error = "手机号码不能为空";

        if (error == null && !phone.matches("^1\\d{10}$")) error = "手机号码格式有误";

        if (throwException && StringUtils.isNotBlank(error)) {
            throw new RuntimeException(error);
        }
        return error;
    }


    public static void main(String[] args) {
        log.info(JSON.toJSONString(split(1000000, 11, true)));
    }
}
