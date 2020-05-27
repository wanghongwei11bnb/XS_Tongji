class CalendarModal extends Modal {
    constructor(props) {
        super(props);
    }

    onDateClick = (ymd, hms) => {
        this.close();
        if (this.props.onDateClick) this.props.onDateClick(ymd, hms);
    };

    onClean = () => {
        this.close();
        if (this.props.onClean) this.props.onClean();
    };

    renderBody = () => {
        return <div className="position-relative text-center">
            <Calendar ref="calendar" withTime={this.props.withTime} onDateClick={this.onDateClick} onDateDisabled={this.props.onDateDisabled}></Calendar>
        </div>
    };

    renderFooter = () => {
        return [
            <A className="btn btn-link text-danger"
               onClick={this.onDateClick.bind(this, YearMonthDate.createByDate(new Date()))}>今天</A>,
            <A className="btn btn-link text-primary" onClick={this.onClean}>清除</A>,
            <A className="btn btn-link text-secondary" onClick={this.close}>关闭</A>,
        ];
    };
    reSize = () => {
        this.refs.dialog.style.maxWidth = this.refs.dialog.offsetHeight + "px";
        this.refs.dialog.style.minWidth = "0px";
    };

    componentDidMount() {
        super.componentDidMount();
        this.reSize();
    }
}


class DateRangeModal extends Modal {
    constructor(props) {
        super(props);
    }

    renderBody = () => {
        return <div className="row">
            <div className="col-sm-6">
                开始日期：
                <DateInput ref="start" className="form-control"></DateInput>
            </div>
            <div className="col-sm-6">
                结束日期：
                <DateInput ref="end" className="form-control"></DateInput>
            </div>
        </div>
    };

    ok = () => {
        if (!this.refs.start.value) return Message.msg('请选择开始日期');
        if (!this.refs.end.value) return Message.msg('请选择结束日期');
        this.close();
        if (this.props.ok) this.props.ok(this.refs.start.value, this.refs.end.value);
    };
    renderFooter = () => {
        return [
            <A className="btn btn-link text-primary float-right" onClick={this.ok}>确定</A>,
            <A className="btn btn-link text-secondary float-right" onClick={this.close}>取消</A>,
        ];
    };
}

class YearMonthSelectModal extends Modal {
    constructor(props) {
        super(props);
    }

    renderHeader = () => '请选择月份';
    renderBody = () => {
        return <div>

            <select ref="year" className="form-control d-inline-block w-auto m-1">
                <option value=""></option>
                {(() => {
                    let os = [];
                    for (let i = 2017; i <= 2020; i++) {
                        os.push(<option value={i}>{i}</option>);
                    }
                    return os;
                })()}
            </select>
            <select ref="month" className="form-control d-inline-block w-auto m-1">
                <option value=""></option>
                {(() => {
                    let os = [];
                    for (let i = 1; i <= 12; i++) {
                        os.push(<option value={i}>{i}</option>);
                    }
                    return os;
                })()}
            </select>
        </div>
    };
    ok = () => {
        if (!this.refs.year.value) return Message.msg('请选择年份');
        if (!this.refs.month.value) return Message.msg('请选择月份');
        this.close();
        if (this.props.onSuccess) this.props.onSuccess(this.refs.year.value, this.refs.month.value);
    };
    renderFooter = () => {
        return [
            <A className="btn btn-link text-primary float-right" onClick={this.ok}>确定</A>,
            <A className="btn btn-link text-secondary float-right" onClick={this.close}>取消</A>,
        ];
    };
}


class DateInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            ymd: this.props.initialValue || null,
        };
    }

    onClick = () => {
        Modal.open(<CalendarModal
            onDateClick={(ymd) => {
                this.setValue(ymd.toDate().format('yyyy-MM-dd'));
            }}
            onClean={() => {
                this.setValue(null);
            }}
            onDateDisabled={this.props.onDateDisabled}></CalendarModal>);
    };

    setValue = (value) => {
        if (type(value, 'Number')) {
            value = new Date(value).format('yyyy-MM-dd');
        } else if (type(value, 'Date')) {
            value = value.format('yyyy-MM-dd');
        }
        this.refs.input.value = value;
        this.value = value;
        this.state.value = value;
    };

    getValue = () => {
        if (this.props.getValue) return this.props.getValue(this.state.value);
        return this.state.value;
    };


    onFocus = () => {
        this.refs.input.blur();
    };

    render() {
        return <input ref="input" type="text" className={this.props.className} onClick={this.onClick}
                      onFocus={this.onFocus} value={this.state.value || null}/>
    }
}


class DateTimeInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            ymd: this.props.ymd || null,
            hms: this.props.hms || null,
        };
    }

    onClick = () => {
        Modal.open(<CalendarModal
            withTime={true}
            onDateClick={(ymd, hms) => {
                this.setValue(ymd, hms);
            }}
            onClean={() => {
                this.setValue(null, null);
            }}
            onDateDisabled={this.props.onDateDisabled}></CalendarModal>);
    };

    setValue = (ymd, hms) => {
        if (type(ymd, YearMonthDate)) {
            if (type(hms, HourMinuteSecond)) {
                this.setState({ymd, hms});
            } else {
                this.setState({ymd, hms: new HourMinuteSecond()});
            }
        } else if (type(ymd, 'Date')) {
            this.setState({ymd: YearMonthDate.createByDate(ymd), hms: HourMinuteSecond.createByDate(ymd)});
        } else if (type(ymd, 'Number')) {
            const date = new Date(ymd);
            this.setState({ymd: YearMonthDate.createByDate(date), hms: HourMinuteSecond.createByDate(date)});
        }
    };

    getValue = () => {
        if (this.props.getValue) return this.props.getValue(this.state.ymd, this.state.hms);
        return this.textValue();
    };

    textValue = () => {
        if (this.state.ymd == null) return null;
        if (this.state.hms != null) {
            return `${this.state.ymd.format()} ${this.state.hms.format()}`;
        } else {
            return this.state.ymd.format();
        }
    };


    onFocus = () => {
        this.refs.input.blur();
    };

    render() {
        return <input ref="input" type="text" className={this.props.className} onClick={this.onClick}
                      onFocus={this.onFocus} value={this.textValue()}/>
    }
}

