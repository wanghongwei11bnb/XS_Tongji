class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    download = () => {
        let queryParams = {
            date_start: this.refs.date_start.value,
            date_end: this.refs.date_end.value,
            uin: this.refs.uin.value,
            bill_area_id: this.refs.bill_area_id.value,
            bill_booking_id: this.refs.bill_booking_id.value,
        };
        queryParams.download = true;
        window.open(`/api/month_card_charge_record/search?${queryString(queryParams)}`)
    };


    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                交易时间：
                <DateInput ref="date_start"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <DateInput ref="date_end"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                uin：
                <input ref="uin" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                分账场地编号：
                <input ref="bill_area_id" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                分账订单编号：
                <input ref="bill_booking_id" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <button type="button" className="btn btn-sm btn-success ml-1" onClick={this.download}>下载</button>
            </div>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));