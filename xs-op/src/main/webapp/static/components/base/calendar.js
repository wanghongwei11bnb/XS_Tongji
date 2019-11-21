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
        };
    }

    isToday = (year, month, date) => {
        const {nowYmd} = this.state;
        return year == nowYmd.year && month == nowYmd.month && date == nowYmd.date;
    };

    onDateClick = (year, month, date) => {
        const ymd = YearMonthDate.create(year, month, date);
        if (this.props.onDateClick) this.props.onDateClick(ymd);
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
            </tbody>
        </table>
    }

}