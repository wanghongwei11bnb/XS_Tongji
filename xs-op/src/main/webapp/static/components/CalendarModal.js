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