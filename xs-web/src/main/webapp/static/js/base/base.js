window.UUID = {
    cs: '012346789abcdef'.toUpperCase().split(''),
    get: function (n) {
        n = n || 32;
        let _uuid = "";
        for (let i = 0; i < n; i++) {
            let index = Math.floor(Math.random() * this.cs.length);
            _uuid += this.cs[index];
        }
        return _uuid;
    }
};

function type(o, t) {
    if (t === undefined) {
        if (o !== o) return 'NaN';
        let typeStr = Object.prototype.toString.call(o);
        return typeStr.substring(8, typeStr.length - 1);
    } else {
        return type(o) === t;
    }
}

function typeValue(o, t, d) {
    return type(o, t) ? o : (d || null);
}

Date.prototype.format = function (fmt) {
    if (!fmt) fmt = "yyyy-MM-dd hh:mm:ss";
    let o = {
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
    for (let k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
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
    let header = {
        'content-type': 'application/json',
        'User-Uin': Cookies.get('User-Uin') || 100000,
        'Req-From': 'web',
        'Client-Token': Cookies.get('Client-Token'),
        ...opt.header
    };
    if (opt.loading) Loading.open();
    $.ajax({
        url: opt.url, method: opt.method || 'get', data: opt.data,
        contentType: opt.contentType, dataType: opt.dataType || "json", headers: header,
        success: (resp) => {
            try {
                if (resp.ret === 0) {
                    if (opt.success) opt.success(resp);
                } else {
                    if (opt.error) opt.error(resp); else Message.error(resp.err);
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

window.EventUtil = {
    addHandler: function (element, type, handler) {
        if (element.addEventListener) {
            element.addEventListener(type, handler, false);
        } else if (element.attachEvent) {
            element.attachEvent('on' + type, handler);
        } else {
            element["on" + type] = handler
        }
    },
    removeHandler: function (element, type, handler) {
        if (element.removeEventListener) {
            element.removeEventListener(type, handler, false);
        } else if (element.deattachEvent) {
            element.deattachEvent('on' + type, handler);
        } else {
            element["on" + type] = null;
        }
    }
};

window.DomUtil = {
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
        let parObj = obj;
        let left = obj.offsetLeft;
        while (parObj = parObj.offsetParent) {
            left += parObj.offsetLeft;
        }
        return left;
    },
    getY: function (obj) {
        let parObj = obj;
        let top = obj.offsetTop;
        while (parObj = parObj.offsetParent) {
            top += parObj.offsetTop;
        }
        return top;
    },
    getCoordX: function (event, refer) {
        let left;
        left = refer ? this.getX(refer) : 0;
        return event.clientX - left + document.documentElement.scrollLeft;
    },
    getCoordY: function (event, refer) {
        let top;
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
}

class StringMapOptions extends MapOptions {
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

function getParameter(key) {
    let reg = new RegExp(`(^|&)${key}=([^&]*)(&|$)`, "i");
    let r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

function getBigFactor(a, b) {
    if (b == 0) {
        return a;
    }
    return getBigFactor(b, a % b)
}

window.ImageUtils = {
    ratio: function (w, h) {
        if (typeof window !== 'undefined') {
            let bigFactor = getBigFactor(w, h);
            let canvas = document.createElement('canvas');
            canvas.width = w / bigFactor;
            canvas.height = h / bigFactor;
            return canvas.toDataURL("image/png");
        } else {
            return '';
        }
    },
};