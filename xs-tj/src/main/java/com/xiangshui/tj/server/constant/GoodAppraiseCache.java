package com.xiangshui.tj.server.constant;


public class GoodAppraiseCache {
    public static final String[] ts = {
            "还可以，我可能有鼻炎，感觉空气稍微有点闷，新东西，新体验。",
            "非常完美",
            "棒棒哒",
            "不错",
            "除了不隔音都挺好的",
            "很舒适",
            "很不错哈，门帘上孔如果能挡住就好了",
            "体验效果还不错，可舒服的睡个好觉。",
            "舒服！！！",
            "价格略贵",
            "宽敞明亮",
            "体验都不错，就是关门半天关不上",
            "比较高大上",
            "nice",
            "感觉还可以",
            "建议仓里顺手地方放个小垃圾桶",
            "不错\uD83D\uDE0A",
    };

    public static String random() {
        return ts[(int) (Math.random() * ts.length)];
    }
}
