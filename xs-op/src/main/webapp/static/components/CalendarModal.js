class CalendarModal extends Modal {
    constructor(props) {
        super(props);
    }

    onDateClick = (ymd) => {
        this.close();
        if (this.props.onDateClick) this.props.onDateClick(ymd);
    };

    onClean = () => {
        this.close();
        if (this.props.onClean) this.props.onClean();
    };

    renderBody = () => {
        return <div className="position-relative text-center">
            <Calendar ref="calendar" onDateClick={this.onDateClick} onDateDisabled={this.props.onDateDisabled}></Calendar>
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


class DateInput extends React.Component {
    constructor(props) {
        super(props);
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
        }
        this.refs.input.value = value;
        this.value = value;
    };

    getValue = () => {
        return this.refs.input.value;
    };


    onFocus = () => {
        this.refs.input.blur();
    };

    render() {
        return <input ref="input" type="text" className={this.props.className} onClick={this.onClick}
                      onFocus={this.onFocus}/>
    }
}

class DateTimeModal extends Modal {
    constructor(props) {
        super(props);
    }

    renderBody = () => {
        return [
            <DateInput ref="date" className="form-control w-auto m-1 d-inline-block"></DateInput>,
            <select ref="hh" className="form-control w-auto m-1 d-inline-block">
                {(() => {
                    const os = [];
                    for (let i = 0; i <= 23; i++) {
                        os.push(<option value={i}>{i}</option>);
                    }
                    return os;
                })()}
            </select>,
            <select ref="mm" className="form-control w-auto m-1 d-inline-block">
                {(() => {
                    const os = [];
                    for (let i = 0; i <= 59; i++) {
                        os.push(<option value={i}>{i}</option>);
                    }
                    return os;
                })()}
            </select>,
        ]
    };

    ok = () => {
        if (!this.refs.date.value) return Message.msg('请选择日期');
        if (!this.refs.hh.value && this.refs.hh.value != 0) return Message.msg('请选择时间');
        if (!this.refs.mm.value && this.refs.mm.value != 0) return Message.msg('请选择时间');
        this.close();
        if (this.props.ok) this.props.ok(`${this.refs.date.value} ${this.refs.hh.value}:${this.refs.mm.value}`);
    };
    renderFooter = () => {
        return [
            <A className="btn btn-link text-primary float-right" onClick={this.ok}>确定</A>,
            <A className="btn btn-link text-secondary float-right" onClick={this.close}>取消</A>,
        ];
    };
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
