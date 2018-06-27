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
            <Calendar onDateClick={this.onDateClick}></Calendar>
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
}


class DateInput extends React.Component {
    constructor(props) {
        super(props);
    }

    onClick = () => {
        Modal.open(<CalendarModal
            onDateClick={(ymd) => {
                this.setValue(`${ymd.year}-${ymd.month}-${ymd.date}`);
            }}
            onClean={() => {
                this.setValue(null);
            }}></CalendarModal>);
    };

    setValue = (value) => {
        this.refs.input.value = value;
        this.value = value;
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