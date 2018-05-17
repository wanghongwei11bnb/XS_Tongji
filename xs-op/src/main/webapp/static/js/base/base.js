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


function type(o) {
    if (o !== o) return 'NaN';
    let typeStr = Object.prototype.toString.call(o);
    return typeStr.substring(8, typeStr.length - 1);
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


let areaColumns = [
    {
        field: 'area_id', title: '场地编号', type: 'number',
        gridField: true, formField: true,
    },
];

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
        data: opt.data,
        contentType: opt.contentType,
        dataType: opt.dataType || "json",
        success: (resp) => {
            if (resp.code == 0) {
                if (opt.success) opt.success(resp);
            } else {
                if (opt.error) opt.error(resp); else Message.error(resp.msg);
            }
        },
        error: () => {
            Message.error('网络异常');
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
    constructor() {
        this.optionMap = {};
    }


    getId(option) {
        return option ? option.id || null : null;
    }

    put(option) {
        let id = this.getId(option);
        this.optionMap[id] = option;
    }

    remove(option) {
        let id = this.getId(option);
        delete this.optionMap[id];
    }

    get(id) {
        return this.optionMap[id];
    }

}

