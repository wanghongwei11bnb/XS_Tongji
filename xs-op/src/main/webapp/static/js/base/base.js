var UUID = {
    cs: '012346789abcdef'.toUpperCase().split(''),
    get: function (n) {
        n = n || 32;
        var _uuid = "";
        for (var i = 0; i < n; i++) {
            var index = Math.floor(Math.random() * this.cs.length);
            _uuid += this.cs[index];
        }
        return _uuid;
    }
};


Date.prototype.format = function (fmt) {
    if (!fmt) fmt = "yyyy-MM-dd hh:mm:ss";
    var o = {
        "M+": this.getMonth() + 1,//月份
        "W": (function (date) {
            switch (date.getDay()) {
                case 0:
                    return "日";
                case 1:
                    return "一";
                case 2:
                    return "二";
                case 3:
                    return "三";
                case 4:
                    return "四";
                case 5:
                    return "五";
                case 6:
                    return "六";
                default:
                    return "";
            }
        })(this),//星期
        "d+": this.getDate(),//日
        "h+": this.getHours(),//小时
        "m+": this.getMinutes(),//分
        "s+": this.getSeconds(),//秒
        "q+": Math.floor((this.getMonth() + 3) / 3),//季度
        "S": this.getMilliseconds()//毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
};


let areaColumns = [
    {
        field: 'area_id', title: '场地编号', type: 'number',
        gridField: true, formField: true,
    },
];