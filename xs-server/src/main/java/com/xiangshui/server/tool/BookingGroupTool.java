package com.xiangshui.server.tool;

import com.xiangshui.server.domain.Booking;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class BookingGroupTool {

    @Data
    public static abstract class GroupItem {
        private String title;

        private Map<Object, List<Booking>> keyMap = new TreeMap();

        public GroupItem(String title) {
            this.title = title;
        }

        public abstract Object groupKey(Booking booking);

    }

    @Data
    public static abstract class SelectItem<T> {
        private String title;
        protected Map<Object, T> keyMap = new HashMap<>();

        public SelectItem(String title) {
            this.title = title;
        }

        public abstract T initialValue();

        public T count(List<Booking> bookingList) {
            T value = this.initialValue();
            if (bookingList != null) {
                for (Booking booking : bookingList) {
                    value = this.reduce(value, booking);
                }
            }
            return value;
        }

        protected abstract T reduce(T value, Booking booking);

        public Object render(Object key) {
            return this.keyMap.get(key);
        }
    }


    public static GroupItem mkGroupItem(String type) {
        switch (type) {
            case "month":
                return new GroupItem("月份") {
                    @Override
                    public Object groupKey(Booking booking) {
                        return DateUtils.format(booking.getCreate_time() * 1000, "yyyyMM");
                    }
                };
            case "date":
                return new GroupItem("日期") {
                    @Override
                    public Object groupKey(Booking booking) {
                        return DateUtils.format(booking.getCreate_time() * 1000, "yyyyMMdd");
                    }
                };
            case "area":
                return new GroupItem("场地") {
                    @Override
                    public Object groupKey(Booking booking) {
                        return booking.getArea_id();
                    }
                };
            case "capsule":
                return new GroupItem("设备") {
                    @Override
                    public Object groupKey(Booking booking) {
                        return booking.getCapsule_id();
                    }
                };
            default:
                return null;
        }
    }

    public static SelectItem mkSelectItem(String type) {
        switch (type) {
            case "count":
                return new SelectItem<Integer>("订单数") {

                    @Override
                    public Integer initialValue() {
                        return 0;
                    }

                    @Override
                    protected Integer reduce(Integer value, Booking booking) {
                        return value + 1;
                    }
                };
            case "count_user":
                return new SelectItem<Integer>("用户数") {
                    private Set<Integer> uinSet = new HashSet<>();

                    @Override
                    public Integer initialValue() {
                        uinSet = new HashSet<>();
                        return 0;
                    }


                    @Override
                    protected Integer reduce(Integer value, Booking booking) {
                        uinSet.add(booking.getUin());
                        return uinSet.size();
                    }
                };
            case "sum_use_pay":
                return new SelectItem<Integer>("非会员付费金额") {
                    @Override
                    public Integer initialValue() {
                        return 0;
                    }

                    @Override
                    protected Integer reduce(Integer value, Booking booking) {
                        return value + (booking.getUse_pay() != null ? booking.getUse_pay() : 0);
                    }

                    @Override
                    public Object render(Object key) {
                        return this.keyMap.get(key) / 100f;
                    }
                };
            case "sum_from_charge":
                return new SelectItem<Integer>("充值部分") {
                    @Override
                    public Integer initialValue() {
                        return 0;
                    }

                    @Override
                    protected Integer reduce(Integer value, Booking booking) {
                        return value + (booking.getFrom_charge() != null ? booking.getFrom_charge() : 0);
                    }

                    @Override
                    public Object render(Object key) {
                        return this.keyMap.get(key) / 100f;
                    }
                };
            case "count_month_card_flag":
                return new SelectItem<Integer>("月卡订单") {
                    @Override
                    public Integer initialValue() {
                        return 0;
                    }

                    @Override
                    protected Integer reduce(Integer value, Booking booking) {
                        return value + (new Integer(1).equals(booking.getMonth_card_flag()) ? 1 : 0);
                    }
                };
            default:
                return null;
        }
    }


    public static void group(List<Booking> bookingList, GroupItem groupItem, List<SelectItem> selectItemList) {
        if (bookingList != null) {
            for (Booking booking : bookingList) {
                Object key = groupItem.groupKey(booking);
                if (key == null) continue;
                if (!groupItem.keyMap.containsKey(key)) {
                    groupItem.keyMap.put(key, new ArrayList<>());
                }
                groupItem.keyMap.get(key).add(booking);
            }
        }
        for (Object key : groupItem.keyMap.keySet()) {
            for (SelectItem selectItem : selectItemList) {
                selectItem.keyMap.put(key, selectItem.count(groupItem.keyMap.get(key)));
            }
        }
    }

    public static void group(List<Booking> bookingList, GroupItem groupItem, List<SelectItem> selectItemList, HttpServletResponse response, String fileName) throws IOException {
        group(bookingList, groupItem, selectItemList);
        List<ExcelUtils.Column<Object>> columnList = new ArrayList<>();
        columnList.add(new ExcelUtils.Column(groupItem.getTitle()) {
            @Override
            public Object render(Object o) {
                return o;
            }
        });
        for (SelectItem selectItem : selectItemList) {
            columnList.add(new ExcelUtils.Column(selectItem.getTitle()) {
                @Override
                public Object render(Object o) {
                    return selectItem.render(o);
                }
            });
        }
        ExcelUtils.export(columnList, groupItem.keyMap.keySet(), response, fileName);
    }
}
