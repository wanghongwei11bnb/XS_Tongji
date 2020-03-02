class YearMonthDate {
    constructor(year, month, date) {
        this.year = year;
        this.month = month;
        this.date = date;
        this.correct();
    }

    getMaxDate(year, month) {
        switch (month) {
            case 2:
                return year % 4 == 0 ? 29 : 28;
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
            default:
                return;
        }
    }

    correct() {
        if (this.month < 1) this.month = 1;
        if (this.month > 12) this.month = 12;
        if (this.date < 1) this.date = 1;
        if (this.date > this.getMaxDate(this.year, this.month)) this.date = this.getMaxDate(this.year, this.month);
        return this;
    }

    setYear(year) {
        this.year = year;
        this.correct();
        return this;
    }

    setMonth(month) {
        this.month = month;
        this.correct();
        return this;
    }

    setDate(date) {
        this.date = date;
        this.correct();
        return this;
    }

    incrDate() {
        if (this.date + 1 > this.getMaxDate(this.month, this.month)) {
            this.incrMonth();
            this.date = 1;
        } else {
            this.date++;
        }
        return this;
    }

    decrDate() {
        if (this.date > 1) {
            this.date--;
        } else {
            this.decrMonth();
            this.date = this.getMaxDate(this.year, this.month);
        }
        return this;
    }

    addDate(n) {
        let symbol = n >= 0 ? 1 : -1;
        n = n >= 0 ? n : -n;
        for (let i = n; i > 0; i--) {
            if (symbol > 0) {
                this.incrDate();
            } else {
                this.decrDate();
            }
        }
        return this;
    }

    incrMonth() {
        if (this.month < 12) {
            this.month++;
        } else {
            this.year++;
            this.month = 1;
        }
        this.correct();
        return this;
    }

    decrMonth() {
        if (this.month > 1) {
            this.month--;
        } else {
            this.year--;
            this.month = 12;
        }
        this.correct();
        return this;
    }

    addMonth(n) {
        let symbol = n >= 0 ? 1 : -1;
        n = n >= 0 ? n : -n;
        for (let i = n; i > 0; i--) {
            if (symbol > 0) {
                this.incrMonth();
            } else {
                this.decrMonth();
            }
        }
        return this;
    }

    getDay() {
        return new Date(this.year, this.month - 1, this.date).getDay();
    }

    clone() {
        return new YearMonthDate(this.year, this.month, this.date);
    }

    toDate() {
        return new Date(this.year, this.month - 1, this.date);
    }

    format(fmt) {
        return this.toDate().format(fmt || 'yyyy-MM-dd');
    }
}

YearMonthDate.createByDate = function (date) {
    return new YearMonthDate(date.getFullYear(), date.getMonth() + 1, date.getDate());
};

YearMonthDate.create = function (year, month, date) {
    return new YearMonthDate(year, month, date);
};

class Calendar extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            nowYmd: YearMonthDate.createByDate(new Date()),
            ymd: props.ymd || YearMonthDate.createByDate(new Date()),
            hms: props.hms || HourMinuteSecond.createByDate(new Date()),
        };
    }

    isToday = (year, month, date) => {
        const {nowYmd} = this.state;
        return year == nowYmd.year && month == nowYmd.month && date == nowYmd.date;
    };

    onDateClick = (year, month, date) => {
        const ymd = YearMonthDate.create(year, month, date);
        if (this.props.onDateClick) this.props.onDateClick(ymd, this.props.withTime ? this.refs.timeSelector.getValue() : undefined);
    };

    prevMonth = () => {
        this.state.ymd.decrMonth();
        this.setState({});
    };
    nextMonth = () => {
        this.state.ymd.incrMonth();
        this.setState({});
    };

    height = () => {
        return this.refs.table.offsetHeight;
    };

    render() {

        const ymd = this.state.ymd.clone().setDate(1);
        ymd.addDate(-ymd.getDay());
        const trs = [];
        for (let i = 1; i <= 6; i++) {
            let tds = [];
            for (let j = 1; j <= 7; j++) {
                let disabled = this.props.onDateDisabled && this.props.onDateDisabled(ymd) ? true : false;
                tds.push(<td
                    className={`text-center ${!disabled ? 'hm' : ''} ${this.isToday(ymd.year, ymd.month, ymd.date) ? 'today' : ''} ${ymd.year == this.state.ymd.year && ymd.month == this.state.ymd.month && !disabled ? '' : 'out'}`}
                    onClick={!disabled ? this.onDateClick.bind(this, ymd.year, ymd.month, ymd.date) : null}>{this.isToday(ymd.year, ymd.month, ymd.date) ? '今' : ymd.date}</td>);
                ymd.incrDate();
            }
            trs.push(<tr>{tds}</tr>);
        }
        return <table ref="table" className="calendar table table-bordered">
            <thead>
            <tr>
                <th colSpan={7} className="text-center">
                    <span className="hm float-left" onClick={this.prevMonth}>&lt;</span>
                    <span className="hm float-right" onClick={this.nextMonth}>&gt;</span>
                    {this.state.ymd.year}年{this.state.ymd.month}月
                </th>
            </tr>
            <tr>
                <th className="text-center">日</th>
                <th className="text-center">一</th>
                <th className="text-center">二</th>
                <th className="text-center">三</th>
                <th className="text-center">四</th>
                <th className="text-center">五</th>
                <th className="text-center">六</th>
            </tr>
            </thead>
            <tbody>
            {trs}
            {this.props.withTime ? <tr className="text-center">
                <td colSpan={7}>
                    <TimeSelector ref="timeSelector" hms={this.props.hms}></TimeSelector>
                </td>
            </tr> : null}
            </tbody>
        </table>
    }


    makeOptions = (from, to) => {
        const options = [];
        while ((from++) <= to) {
            options.push(<option value={from}>{from}</option>);
        }
        return options;
    };

}


class TimeSelector extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            hms: props.hms || HourMinuteSecond.createByDate(new Date()),
        };
    }

    getValue = () => {
        if (this.props.getValue) return this.props.getValue(this.state.hms);
        return this.state.hms;
    };

    setValue = (value) => {
        if (type(value, HourMinuteSecond)) {
            this.setState({hms: value});
        } else if (type(value, 'Date')) {
            this.setState({hms: HourMinuteSecond.createByDate(value)});
        } else if (type(value, 'Number')) {
            this.setState({hms: HourMinuteSecond.createByDate(new Date(value))});
        }
    };

    render() {
        return <div className={this.props.className || 'inline-block'}>
            <input type="number" value={this.state.hms.hour} min={0} max={23} onChange={(e) => {
                this.setState({hms: this.state.hms.setHour(e.target.value - 0)});
            }}/>
            时
            <input type="number" value={this.state.hms.minute} min={0} max={59} onChange={(e) => {
                this.setState({hms: this.state.hms.setMinute(e.target.value - 0)});
            }}/>
            分
            <input type="number" value={this.state.hms.second} min={0} max={59} onChange={(e) => {
                this.setState({hms: this.state.hms.setSecond(e.target.value - 0)});
            }}/>
            秒
        </div>
    }
}


class HourMinuteSecond {
    constructor(hour, minute, second) {
        this.hour = hour || 0;
        this.minute = minute || 0;
        this.second = second || 0;
        this.correct();
    }

    correct() {
        if (!type(this.hour, 'Number') || this.hour < 0 || this.hour > 23) {
            this.hour = 0;
        }
        if (!type(this.minute, 'Number') || this.minute < 0 || this.minute > 59) {
            this.minute = 0;
        }
        if (!type(this.second, 'Number') || this.second < 0 || this.second > 59) {
            this.second = 0;
        }
    }


    setHour(hour) {
        this.hour = hour;
        this.correct();
        return this;
    }

    setMinute(minute) {
        this.minute = minute;
        this.correct();
        return this;
    }

    setSecond(second) {
        this.second = second;
        this.correct();
        return this;
    }


    plusHour(n) {
        if (!type(n, 'Number') || n <= 0) return;
        while ((n--) > 0) {
            this.hour++;
        }
        return this;
    }

    plusMinute(n) {
        if (!type(n, 'Number') || n <= 0) return;
        while ((n--) > 0) {
            this.minute++;
            if (this.minute >= 60) {
                this.plusHour(1);
                this.minute = 0;
            }
        }
        return this;
    }


    plusSecond(n) {
        if (!type(n, 'Number') || n <= 0) return;
        while ((n--) > 0) {
            this.second++;
            if (this.second >= 60) {
                this.plusMinute(1);
                this.second = 0;
            }
        }
        return this;
    }

    minusHour(n) {
        if (!type(n, 'Number') || n <= 0) return;
        while ((n--) > 0) {
            this.hour--;
            if (this.hour < 0) {
                this.hour = 0;
            }
        }
        return this;
    }


    minusMinute(n) {
        if (!type(n, 'Number') || n <= 0) return;
        while ((n--) > 0) {
            this.minute--;
            if (this.minute < 0) {
                this.minusHour(1);
                this.minute = 59;
            }
        }
        return this;
    }


    minusSecond(n) {
        if (!type(n, 'Number') || n <= 0) return;
        while ((n--) > 0) {
            this.second--;
            if (this.second < 0) {
                this.minusMinute(1);
                this.second = 59;
            }
        }
        return this;
    }


    getTime() {
        return this.second + this.minute * 60 + this.hour * 60 * 60;
    }

    toString() {
        return `${this.hour}:${this.minute}:${this.second}`;
    }

    format() {
        return this.toString();
    }


}

HourMinuteSecond.createByDate = function (date = new Date()) {
    return new HourMinuteSecond(date.getHours(), date.getMinutes(), date.getSeconds());
};


HourMinuteSecond.createBySecond = function (second = 0) {
    if (second == 0) return new HourMinuteSecond(0, 0, 0);
    return new HourMinuteSecond(Math.floor(second / (60 * 60)), Math.floor(second % (60 * 60) / 60), second % 60);
};

