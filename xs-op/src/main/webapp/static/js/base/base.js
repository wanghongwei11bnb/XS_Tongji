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

function nullStringReplacer(k, v) {
    return v === '' ? undefined : v;
}


function type(o, t) {
    if (t === undefined) {
        if (o !== o) return 'NaN';
        let typeStr = Object.prototype.toString.call(o);
        return typeStr.substring(8, typeStr.length - 1);
    } else if (type(t) === 'Function') {
        return o.__proto__ === t.prototype;
    } else {
        return type(o) === t;
    }
}


function typeValue(o, t, d) {
    return type(o, t) ? o : (d || null);
}

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


class LocalDate {
    constructor(year, month, day) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.check();
    }

    check() {
        let now = new Date();
        this.year = this.year || now.getFullYear();
        this.month = this.month || now.getMonth() + 1;
        this.day = this.day || now.getDate();
        if (this.month < 1) {
            this.month = 1;
        } else if (this.month > 12) {
            this.month = 12;
        }
        if (this.day < 1) {
            this.day = 1;
        } else if (this.day > this.getMaxDay(this.year, this.month)) {
            this.day = this.getMaxDay(this.year, this.month);
        }
        return this;
    }

    getMaxDay(year, month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (this.year % 4 === 0) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return;
        }
    }

    getYear() {
        return this.year;
    }

    getMonth() {
        return this.month;
    }

    getDay() {
        return this.day;
    }

    withDay(day) {
        this.day = day;
        this.check();
        return this;
    }

    withMonth(month) {
        this.month = month;
        this.check();
        return this;
    }

    withYear(year) {
        this.year = year;
        this.check();
        return this;
    }


    plusYears(n, uncheck) {
        cursor(n, () => {
            this.year++;
        });
        if (!uncheck) this.check();
        return this;
    }

    minusYears(n, uncheck) {
        cursor(n, () => {
            this.year--;
        });
        if (!uncheck) this.check();
        return this;
    }

    plusMonths(n, uncheck) {
        cursor(n, () => {
            if (this.month === 12) {
                this.plusYears(1, true);
                this.month = 1;
            } else {
                this.month++;
            }
        });
        if (!uncheck) this.check();
        return this;
    }

    minusMonths(n, uncheck) {
        cursor(n, () => {
            if (this.month === 1) {
                this.minusYears(1, true);
                this.month = 12;
            } else {
                this.month--;
            }
        });
        if (!uncheck) this.check();
        return this;
    }

    plusDays(n) {
        cursor(n, () => {
            if (this.day >= this.getMaxDay(this.year, this.month)) {
                this.plusMonths(1, true);
                this.day = 1;
            } else {
                this.day++;
            }
        });
        this.check();
        return this;
    }

    minusDays(n) {
        cursor(n, () => {
            if (this.day === 1) {
                this.minusMonths(1, true);
                this.day = this.getMaxDay(this.year, this.month);
            } else {
                this.day--;
            }
        });
        this.check();
        return this;
    }

    toDate() {
        return new Date(this.year, this.month - 1, this.day);
    }

    format(fmt) {
        return this.toDate().format(fmt || 'yyyy-MM-dd');
    }

    getTime() {
        return this.toDate().getTime();
    }

    toString() {
        return this.format('yyyy-MM-dd');
    }

    clone() {
        return new LocalDate(this.year, this.month, this.day);
    }
}

LocalDate.parseDate = function (date) {
    return type(date, 'Date') ? new LocalDate(date.getFullYear(), date.getMonth() + 1, date.getDate()) : null;
};

class LocalTime {
    constructor(hour, minute, second) {
        this.hour = hour;
        this.minute = minute;
        this.second = minute;
        this.check();
    }

    check() {
        let now = new Date();
        this.hour = typeValue(this.hour, 'Number', now.getHours());
        this.minute = typeValue(this.minute, 'Number', now.getMinutes());
        this.second = typeValue(this.second, 'Number', now.getSeconds());
        if (this.hour < 0) {
            this.hour = 0;
        } else if (this.hour > 23) {
            this.hour = 23;
        }
        if (this.minute < 0) {
            this.minute = 0;
        } else if (this.minute > 59) {
            this.minute = 59;
        }
        if (this.second < 0) {
            this.second = 0
        } else if (this.second > 59) {
            this.second = 59;
        }
        return this;
    }

    getHour() {
        return this.hour;
    }

    getMinute() {
        return this.minute;
    }

    getSecond() {
        return this.second;
    }

    withHour(hour) {
        this.hour = hour;
        this.check();
        return this;
    }

    withMinute(minute) {
        this.minute = minute;
        this.check();
        return this;
    }

    withSecond(second) {
        this.second = second;
        this.check();
        return this;
    }

    plusHours(n, uncheck) {
        cursor(n, () => {
            if (this.hour === 23) {
                this.hour = 0;
            } else {
                this.hour++;
            }
        });
        if (!uncheck) this.check();
        return this;
    }

    minusHours(n, uncheck) {
        cursor(n, () => {
            if (this.hour === 0) {
                this.hour = 23;
            } else {
                this.hour--;
            }
        });
        if (!uncheck) this.check();
        return this;
    }

    plusMinutes(n, uncheck) {
        cursor(n, () => {
            if (this.minute === 59) {
                this.plusHours(1, true);
                this.minute = 0;
            } else {
                this.minute++;
            }
        });
        if (!uncheck) this.check();
        return this;
    }

    minusMinutes(n, uncheck) {
        cursor(n, () => {
            if (this.minute === 0) {
                this.minusHours(1, true);
                this.minute = 59;
            } else {
                this.minute--;
            }
        });
        if (!uncheck) this.check();
        return this;
    }

    plusSeconds(n) {
        cursor(n, () => {
            if (this.second === 59) {
                this.plusMinutes(1, true);
                this.second = 0;
            } else {
                this.second++;
            }
        });
        this.check();
        return this;
    }

    minusSeconds(n) {
        cursor(n, () => {
            if (this.second === 0) {
                this.minusMinutes(1, true);
                this.second = 59;
            } else {
                this.second--;
            }
        });
        this.check();
        return this;
    }

    toDate() {
        let now = new Date();
        return new Date(now.getFullYear(), now.getMonth(), now.getDate(), this.hour, this.minute, this.second);
    }

    format(fmt) {
        return this.toDate().format(fmt || 'hh:mm:ss');
    }

    toString() {
        return this.format('hh:mm:ss');
    }

    clone() {
        return new LocalTime(this.hour, this.minute, this.second);
    }
}

LocalTime.parseDate = function (date) {
    return type(date, 'Date') ? new LocalTime(date.getHours(), date.getMinutes(), date.getSeconds()) : null;
};

class DateTime {
    constructor(year, month, day, hour, minute, second) {
        this.localDate = new LocalDate(year, month, day);
        this.localTime = new LocalTime(hour, minute, second);
    }

    toDate() {
        return new Date(this.localDate.year, this.localDate.month - 1, this.localDate.day, this.localTime.hour, this.localTime.minute, this.localTime.second);
    }


    format(fmt) {
        return this.toDate().format(fmt);
    }

    getTime() {
        return this.toDate().getTime();
    }

    toString() {
        return this.format();
    }

}

DateTime.parseDate = function (date) {
    return type(date, 'Date') ? new DateTime(date.getFullYear(), date.getMonth() + 1, date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds()) : null;
};


function isWeiXin() {
    let ua = window.navigator.userAgent.toLowerCase();
    return ua.match(/MicroMessenger/i) === 'micromessenger';
}


window.Message = {
    id: 'message',
    msg: function (text, ms) {
        this.clear();
        let div = document.createElement('div');
        div.id = this.id;
        div.innerHTML = text;
        document.body.appendChild(div);
        this.sto_msg = setTimeout(() => {
            this.clear();
        }, ms || 2000);
    },
    error: function (text, ms) {
        this.msg(text, ms || 3000);
    },
    clear: function () {
        clearTimeout(this.sto_msg);
        let div = document.getElementById(this.id);
        if (div) {
            document.body.removeChild(div)
        }
    }
};
window.Loading = {
    id: 'loading',
    open: function () {
        this.close();
        let div = document.createElement('div');
        div.id = this.id;
        document.body.appendChild(div);
    },
    close: function () {
        let div = document.getElementById(this.id);
        if (div) {
            document.body.removeChild(div)
        }
    }
};


function request(opt) {
    if (opt.loading) Loading.open();
    reqwest({
        url: opt.url,
        method: opt.method || 'get',
        headers: opt.headers,
        data: opt.data,
        contentType: opt.contentType,
        dataType: opt.dataType || "json",
        success: (resp) => {
            try {
                if (resp.code == 0) {
                    if (opt.success) opt.success(resp);
                } else {
                    if (opt.error) opt.error(resp); else Message.error(resp.msg);
                }
            } catch (e) {
                console.error(e);
                Message.error(e.message);
            }
        },
        error: () => {
            try {
                Message.error('网络异常');
            } catch (e) {
                console.error(e);
                Message.error(e.message);
            }
        },
        complete: function () {
            if (opt.loading) Loading.close();
        }
    });
}


window.eventUtil = {
    addHandler: function (element, type, handler) {
        if (element.addEventListener) {
            element.addEventListener(type, handler, false);
        }
        else if (element.attachEvent) {
            element.attachEvent('on' + type, handler);
        }
        else {
            element["on" + type] = handler
            /*直接赋给事件*/
        }

    },
    removeHandler: function (element, type, handler) {
        if (element.removeEventListener) {
            /*Chrome*/
            element.removeEventListener(type, handler, false);
        } else if (element.deattachEvent) {
            /*IE*/
            element.deattachEvent('on' + type, handler);
        } else {
            /*直接赋给事件*/
            element["on" + type] = null;
        }
    }
};

window.domUtil = {
    scrollTop: function () {
        if ('pageYOffset' in window) {
            return window.pageYOffset;
        } else if (document.compatMode === 'BackCompat') {
            return document.body.scrollTop;
        } else {
            return document.documentElement.scrollTop;
        }
    },
    getX: function (obj) {
        var parObj = obj;
        var left = obj.offsetLeft;
        while (parObj = parObj.offsetParent) {
            left += parObj.offsetLeft;
        }
        return left;
    },
    getY: function (obj) {
        var parObj = obj;
        var top = obj.offsetTop;
        while (parObj = parObj.offsetParent) {
            top += parObj.offsetTop;
        }
        return top;
    },
    getCoordX: function (event, refer) {
        var left;
        left = refer ? this.getX(refer) : 0;
        return event.clientX - left + document.documentElement.scrollLeft;
    },
    getCoordY: function (event, refer) {
        var top;
        top = refer ? this.getY(refer) : 0;
        return event.clientY - top + document.documentElement.scrollTop;
    },
    getCoord: function (event, refer) {
        return {
            x: this.getCoordX(event, refer),
            y: this.getCoordY(event, refer),
        }
    },
};


class ListOptions {
    constructor() {
        this.optionList = [];
    }

    getId(option) {
        return option.id;
    }

    insertOption(option) {
        if (this.getOptionById(this.getId(option)) == null) {
            this.optionList.push(option);
        }
    }

    deleteOption(option) {
        let id = this.getId(option);
        this.removeOptionById(id);
    }

    deleteOptionById(id) {
        for (let i = 0; i < this.optionList.length; i++) {
            if (this.getId(this.optionList[i]) == id) {
                this.optionList.splice(i, 1);
                break;
            }
        }
    }

    updateOption(option) {
        let id = this.getId(option);
        for (let i = 0; i < this.optionList.length; i++) {
            if (this.getId(this.optionList[i]) == id) {
                this.optionList[i] = option;
                return;
            }
        }
    }

    saveOption(option) {
        let id = this.getId(option);
        for (let i = 0; i < this.optionList.length; i++) {
            if (this.getId(this.optionList[i]) == id) {
                this.optionList[i] = option;
                return;
            }
        }
        this.optionList.push(option);
    }

    selectOptionById(id) {
        for (let i = 0; i < this.optionList.length; i++) {
            let option = this.optionList[i];
            if (this.getId(option) == id) {
                return option;
            }
        }
    }
}


class MapOptions {
    constructor(options) {
        this.optionMap = {};
        this.putAll(options);
    }

    getIdByOption(option) {
        return option ? option.id || null : null;
    }

    put(option) {
        let id = this.getIdByOption(option);
        if (id || id === 0 || id === false) {
            this.optionMap[id] = option;
        }
    }

    putAll(options) {
        if (options && options.length > 0) {
            for (let i = 0; i < options.length; i++) {
                this.put(options[i]);
            }
        }
    }

    removeById(id) {
        delete this.optionMap[id];
    }

    removeByOption(option) {
        let id = this.getIdByOption(option);
        this.removeById(id);
    }

    get(id) {
        return this.optionMap[id];
    }

    getField(id, field) {
        return this.optionMap[id] ? this.optionMap[id][field] : null;
    }
}

class AreaMapOptions extends MapOptions {
    constructor(options) {
        super(options);
    }

    getIdByOption(option) {
        return option ? option.area_id || null : null;
    }
}

class StringMapOptions extends MapOptions {
    constructor(options) {
        super(options);
    }

    getIdByOption(option) {
        return option || null;
    }
}

class AreaContractMapOptions extends MapOptions {
    constructor(options) {
        super(options);
    }

    getIdByOption(option) {
        return option ? option.area_id || null : null;
    }
}

class CapsuleMapOptions extends MapOptions {
    constructor(options) {
        super(options);
    }

    getIdByOption(option) {
        return option ? option.capsule_id || null : null;
    }
}

class UserInfoMapOptions extends MapOptions {
    constructor(options) {
        super(options);
    }

    getIdByOption(option) {
        return option ? option.uin || null : null;
    }
}

class GroupInfoMapOptions extends MapOptions {
    constructor(options) {
        super(options);
    }

    getIdByOption(option) {
        return option ? option.group_id || null : null;
    }
}

class CityMapOptions extends MapOptions {
    constructor(options) {
        super(options);
    }

    getIdByOption(option) {
        return option ? option.city || null : null;
    }
}

class AuthMapOptions extends MapOptions {
    constructor(options) {
        super(options);
    }

    getIdByOption(option) {
        return option || null;
    }
}

function queryString(json) {
    return Object.keys(json).map(function (key) {
        return `${encodeURIComponent(key)}=${encodeURIComponent(json[key])}`;
    }).join("&");
}

function clone(obj) {
    // Handle the 3 simple types, and null or undefined
    if (null == obj || "object" != typeof obj) return obj;

    // Handle Date
    if (obj instanceof Date) {
        var copy = new Date();
        copy.setTime(obj.getTime());
        return copy;
    }
    // Handle Array
    if (obj instanceof Array) {
        var copy = [];
        for (var i = 0, len = obj.length; i < len; ++i) {
            copy[i] = clone(obj[i]);
        }
        return copy;
    }
    // Handle Object
    if (obj instanceof Object) {
        var copy = {};
        for (var attr in obj) {
            if (obj.hasOwnProperty(attr)) copy[attr] = clone(obj[attr]);
        }
        return copy;
    }

    throw new Error("Unable to copy obj! Its type isn't supported.");
}

window.dateUtils = {

    getWeek: function (date) {
        date = clone(date);
        date.setHours(0, 0, 0, 0);
        const day = date.getDay();
        const week = [];
        for (let pre = 1; day - pre >= 0; pre++) {
            week.unshift(new Date(date.getTime() - (1000 * 60 * 60 * 24 * pre)));
        }
        week.push(date);
        for (let next = 1; day + next <= 6; next++) {
            week.push(new Date(date.getTime() + (1000 * 60 * 60 * 24 * next)));
        }
        return week;
    },
    isSameDay: function (date1, date2) {
        if (!(date1 && date2)) return false;
        date1 = clone(date1);
        date2 = clone(date2);
        return date1.setHours(0, 0, 0, 0) == date2.setHours(0, 0, 0, 0);
    },
    isSameMonth: function (date1, date2) {
        if (!(date1 && date2)) return false;
        date1 = clone(date1);
        date2 = clone(date2);
        date1.setHours(0, 0, 0, 0);
        date2.setHours(0, 0, 0, 0);
        date1.setDate(1);
        date2.setDate(1);
        return date1.getTime() == date2.getTime();
    },
    isSameYear: function (date1, date2) {
        if (!(date1 && date2)) return false;
        date1 = clone(date1);
        date2 = clone(date2);
        date1.setHours(0, 0, 0, 0);
        date2.setHours(0, 0, 0, 0);
        date1.setMonth(0, 1);
        date2.setMonth(0, 1);
        return date1.getTime() == date2.getTime();
    },
    isSameWeek: function (date1, date2) {

    },
    isBetweenDay: function (date, startDate, endDate) {
        if (!(date && startDate && endDate)) {
            return false;
        }
        date = clone(date);
        startDate = clone(startDate);
        endDate = clone(endDate);
        date.setHours(0, 0, 0, 0);
        startDate.setHours(0, 0, 0, 0);
        endDate.setHours(0, 0, 0, 0);
        return startDate.getTime() <= date.getTime() && date.getTime() <= endDate.getTime()
            || startDate.getTime() >= date.getTime() && date.getTime() >= endDate.getTime()
    },
    subtractForDay: function (date1, date2) {
        if (!(date1 && date2)) {
            return 0;
        }
        date1 = clone(date1);
        date2 = clone(date2);
        date1.setHours(0, 0, 0, 0);
        date2.setHours(0, 0, 0, 0);
        return (date1.getTime() - date2.getTime()) / (1000 * 60 * 60 * 24);
    },
    subtractForMonth: function (date1, date2) {
        if (!(date1 && date2)) {
            return 0;
        }
        date1 = clone(date1);
        date2 = clone(date2);
        date1.setHours(0, 0, 0, 0);
        date2.setHours(0, 0, 0, 0);
        date1.setDate(1);
        date2.setDate(1);
        return (date1.getFullYear() - date2.getFullYear()) * 12 + (date1.getMonth() - date2.getMonth());
    },
};


function loadOpInfo() {
    request({
        url: '/api/getOpInfo',
        success: resp => {
            window.op_info = resp.data.op;
        }
    })
}


class EnumMapOptions extends MapOptions {
    constructor(options) {
        super(options);
    }

    getIdByOption(option) {
        return option ? option.value || null : null;
    }
}


class OptionWrapper {
    constructor(options) {
        this.options = options || [];
        this.initMapOptions();
    }

    initMapOptions() {
        this.mapOptions = new EnumMapOptions(this.options);
    }

    getOptionByValue(value) {
        return this.mapOptions.get(value);
    }

    getTextByValue(value) {
        return this.mapOptions.getField(value, 'text');
    }

    getColorByValue(value) {
        return this.mapOptions.getField(value, 'color');
    }

    getSpanTagByValue(value, opt = {}) {
        let option = this.mapOptions.get(value);
        if (option) {
            return <span className={`${opt.className} ${option.color ? 'text-' + option.color : ''}`}>{option.text || option.value}</span>
        } else {
            return null;
        }
    }

    getOptionTagByValue(value, opt = {}) {
        let option = this.mapOptions.get(value);
        if (option) {
            return <option value={option.value} className={opt.className}>{option.text || option.value}</option>
        } else {
            return null;
        }
    }

    getSelectTagByValue(value, opt = {}) {
        return <select ref={opt.ref} className={opt.className}>
            <option value={null}></option>
            {this.options.map(option => this.getOptionTagByValue(option.value))}
        </select>
    }

}

