package com.xiangshui.server.tool;

import com.xiangshui.server.cache.BaseCache;
import com.xiangshui.server.domain.Booking;
import com.xiangshui.server.job.ReportFormJob;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.ExcelUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
public class BookingGroupTool {

    @Autowired
    ReportFormJob reportFormJob;

    @Autowired
    public BaseCache cache;

    @Data
    public static abstract class GroupItem {
        private String title;

        private Map<Object, List<Booking>> keyMap = new TreeMap();

        protected List<DeriveItem> deriveItemList = this.deriveItemList();

        protected List<DeriveItem> deriveItemList() {
            return null;
        }

        public GroupItem(String title) {
            this.title = title;
        }

        public abstract Object groupKey(Booking booking);


        public void mapDeriveItemList() {
            if (this.deriveItemList != null) {
                this.deriveItemList.forEach(deriveItem -> this.keyMap.forEach((key, bookingList) -> {
                    deriveItem.keyMap.put(key, deriveItem.map(key, bookingList));
                }));
            }
        }
    }

    @Data
    public static abstract class DeriveItem<T> {
        private String title;
        private Map<Object, T> keyMap = new HashMap<>();

        public DeriveItem(String title) {
            this.title = title;
        }

        public T render(Object key) {
            return keyMap.get(key);
        }


        public abstract T map(Object key, List<Booking> bookingList);
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
                    value = this.map(value, booking);
                }
            }
            return value;
        }

        protected abstract T map(T value, Booking booking);

        public Object render(Object key) {
            return this.keyMap.get(key);
        }
    }


    public GroupItem mkGroupItem(String type) {
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
                    protected List<DeriveItem> deriveItemList() {
                        return Arrays.asList(
                                new DeriveItem<String>("场地名称") {
                                    @Override
                                    public String map(Object key, List<Booking> bookingList) {
                                        return cache.areaMapOptions.containsKey(key) ? cache.areaMapOptions.get(key).getTitle() : null;
                                    }
                                }
                        );
                    }

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

    public SelectItem mkSelectItem(String type) {
        switch (type) {
            case "count":
                return new SelectItem<Integer>("订单数") {

                    @Override
                    public Integer initialValue() {
                        return 0;
                    }

                    @Override
                    protected Integer map(Integer value, Booking booking) {
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
                    protected Integer map(Integer value, Booking booking) {
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
                    protected Integer map(Integer value, Booking booking) {
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
                    protected Integer map(Integer value, Booking booking) {
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
                    protected Integer map(Integer value, Booking booking) {
                        return value + (new Integer(1).equals(booking.getMonth_card_flag()) ? 1 : 0);
                    }
                };
            case "month_card_income":
                return new SelectItem<Integer>("月卡收入") {
                    @Override
                    public Integer initialValue() {
                        return 0;
                    }

                    @Override
                    protected Integer map(Integer value, Booking booking) {
                        return value + (booking.getMonthCardPrice() != null && booking.getMonthCardPrice() > 0 ? booking.getMonthCardPrice() : 0);
                    }

                    @Override
                    public Object render(Object key) {
                        return this.keyMap.get(key) / 100f;
                    }
                };
            default:
                return null;
        }
    }


    public void group(List<Booking> bookingList, GroupItem groupItem, List<SelectItem> selectItemList) {
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
        groupItem.mapDeriveItemList();
        for (Object key : groupItem.keyMap.keySet()) {
            for (SelectItem selectItem : selectItemList) {
                selectItem.keyMap.put(key, selectItem.count(groupItem.keyMap.get(key)));
            }
        }
    }

    public void group(List<Booking> bookingList, GroupItem groupItem, List<SelectItem> selectItemList, HttpServletResponse response, String fileName) throws IOException {
        group(bookingList, groupItem, selectItemList);
        List<ExcelUtils.Column<Object>> columnList = new ArrayList<>();
        columnList.add(new ExcelUtils.Column(groupItem.getTitle()) {
            @Override
            public Object render(Object o) {
                return o;
            }
        });
        if (groupItem.deriveItemList != null) {
            groupItem.deriveItemList.forEach(deriveItem -> columnList.add(new ExcelUtils.Column(deriveItem.getTitle()) {
                @Override
                public Object render(Object o) {
                    return deriveItem.render(o);
                }
            }));
        }
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
