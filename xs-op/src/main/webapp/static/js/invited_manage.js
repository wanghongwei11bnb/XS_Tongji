class InvitedGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'uin', title: '被邀请人uin'},
                {field: 'phone', title: '被邀请人手机号'},
                {field: 'create_time', title: '被邀请时间', render: value => type(value, 'Number') ? new Date(value * 1000).format() : null},
                {field: 'invited_by', title: '邀请人uin'},
                {
                    field: 'uin', title: '被邀请人订单数', render: value => {
                        return this.state.countBookingMap[value] || 0;
                    }
                },
            ],
        };
    }

    render() {
        return <Table columns={this.state.columns} data={this.state.data}></Table>
    }

    load = (queryParams) => {
        if (queryParams) this.state.queryParams = queryParams;
        request({
            url: '/api/invited/search', method: 'post', loading: true,
            data: this.state.queryParams,
            success: resp => {
                let countBookingMap = {};
                (resp.data.bookingList || []).forEach(booking => {
                    countBookingMap[booking.uin] = (countBookingMap[booking.uin] || 0) + 1;
                });
                this.setState({
                    data: resp.data.userInfoList || [],
                    bookingList: resp.data.bookingList || [],
                    countBookingMap,
                });
            }
        });
    };

}

class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    getQueryParams = () => {
        return {
            invited_bys: this.refs.invited_bys.value,
            invited_date_start: this.refs.invited_date_start.value,
            invited_date_end: this.refs.invited_date_end.value,
            booking_status: this.refs.booking_status.value,
            booking_date_start: this.refs.booking_date_start.value,
            booking_date_end: this.refs.booking_date_end.value,
        };
    };

    search = () => {
        this.refs.grid.load(this.getQueryParams());
    };

    download = () => {
        let queryParams = this.getQueryParams();
        queryParams.download = true;
        queryParams.payMonth = this.refs.payMonth.value;
        window.open(`/api/booking/search?${queryString(queryParams)}`)
    };

    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                邀请人uin：
                <textarea ref="invited_bys" placeholder="一行一个uin"
                          className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                邀请日期：
                <DateInput ref="invited_date_start"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <DateInput ref="invited_date_end"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                统计订单状态：
                <select ref="booking_status" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value=""></option>
                    <option value="1">进行中</option>
                    <option value="2">待支付</option>
                    <option value="3">待支付（支付中）</option>
                    <option value="4">已支付</option>
                </select>
                统计订单日期：
                <DateInput ref="booking_date_start"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <DateInput ref="booking_date_end"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
            </div>
            <div className="text-danger">最多返回{maxResultSize}条数据</div>
            <InvitedGrid ref="grid"></InvitedGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.refs.invited_date_start.setValue(new Date().format('yyyy-MM-dd'));
        this.refs.invited_date_end.setValue(new Date().format('yyyy-MM-dd'));
        this.refs.booking_date_start.setValue(new Date().format('yyyy-MM-dd'));
        this.refs.booking_date_end.setValue(new Date().format('yyyy-MM-dd'));
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));

