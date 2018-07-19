package com.xiangshui.server.constant;

import com.xiangshui.util.Option;

import java.util.List;

public class CityRegionOption extends Option<String> {

    public CityRegionOption(String value, String text) {
        super(value, text);
    }

    public static final CityRegionOption huadong = new CityRegionOption("华东", "华东");
    public static final CityRegionOption huanan = new CityRegionOption("华南", "华南");
    public static final CityRegionOption huabei = new CityRegionOption("华北", "华北");
    public static final CityRegionOption huazhong = new CityRegionOption("华中", "华中");

    public static final List<Option> options = getOptions(CityRegionOption.class);
}
