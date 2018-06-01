class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            payType: {5: '公众号支付', 7: '支付宝移动页面支付', 9: '微信小程序支付', 30: '新用户注册赠送', 1: '微信支付', 2: '支付宝支付', 20: '钱包余额支付'},
            columns: [
                {field: 'booking_id', title: '订单编号'},
                {
                    field: 'create_time', title: '创建时间', render: (value, row, index) => {
                        return value ? new Date(value * 1000).format('yyyy-MM-dd hh:mm') : value;
                    }
                },
                {
                    field: 'end_time', title: '结束时间', render: (value, row, index) => {
                        return value ? new Date(value * 1000).format('yyyy-MM-dd hh:mm') : value;
                    }
                },
                {
                    field: 'status', title: '订单状态', render: (value, row, index) => {
                        switch (value) {
                            case 1:
                                return <span className="text-success">进行中</span>;
                            case 2:
                                return <span className="text-warning">待支付</span>;
                            case 3:
                                return <span className="text-warning">待支付（支付中）</span>;
                            case 4:
                                return <span className="text-secondary">已支付</span>;
                            default:
                                return value;
                        }
                    }
                },
                {field: 'final_price', title: '订单总金额', render: value => type(value) == 'Number' ? value / 100 : value},
                {field: 'use_pay', title: '现金支付金额', render: value => type(value) == 'Number' ? value / 100 : value},
                {
                    field: 'pay_type', title: '支付方式',
                    render: value => type(value) == 'Number' && this.state.payType[value] ? this.state.payType[value] : value
                },
                {field: 'capsule_id', title: '头等舱编号'},
                {field: 'area_id', title: '场地编号'},
                {
                    field: 'area_id', title: '场地名称', render: (value, row, index) => {
                        return value && this.state.areaMapOptions.get(value) ? this.state.areaMapOptions.get(value).title : null;
                    }
                },
                {
                    field: 'area_id', title: '城市', render: (value, row, index) => {
                        return value && this.state.areaMapOptions.get(value) ? this.state.areaMapOptions.get(value).city : null;
                    }
                },
                {
                    field: 'area_id', title: '地址', render: (value, row, index) => {
                        return value && this.state.areaMapOptions.get(value) ? this.state.areaMapOptions.get(value).address : null;
                    }
                },
                {field: 'uin', title: '用户UIN'},
                {
                    field: 'uin', title: '用户手机号', render: (value, row, index) => {
                        return value && this.state.userInfoMapOptions.get(value) ? this.state.userInfoMapOptions.get(value).phone : null;
                    }
                },
                {field: 'req_from', title: '订单来源'},
                {
                    render: (value, row, index) => {
                        return [
                            <button type="button" className="btn btn-primary btn-sm m-1"
                                    onClick={this.update.bind(this, row.booking_id)}>更改订单信息</button>,
                            <button type="button" className="btn btn-success btn-sm m-1"
                                    onClick={this.makeFailureByBooking.bind(this, row.booking_id)}>创建报修</button>,
                        ]
                    }
                },
            ]
        };
    }


    getQueryParams = () => {
        return {
            status: this.refs.status.value,
            booking_id: this.refs.booking_id.value,
            area_id: this.refs.area_id.value,
            capsule_id: this.refs.capsule_id.value,
            uin: this.refs.uin.value,
            phone: this.refs.phone.value,
            create_date_start: this.refs.create_date_start.value,
            create_date_end: this.refs.create_date_end.value,
        };
    };

    search = () => {
        this.refs.bookingGrid.load(this.getQueryParams());
    };
    download = () => {
        let queryParams = this.getQueryParams();
        queryParams.download = true;
        window.open(`/api/booking/search?${queryString(queryParams)}`)
    };


    render() {
        const {columns, data} = this.state;
        return <div className="container-fluid my-3">
            <div className="m-1">
                订单创建时间：
                <DateInput ref="create_date_start"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <DateInput ref="create_date_end"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                状态：
                <select ref="status" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value=""></option>
                    <option value="1">进行中</option>
                    <option value="2">待支付</option>
                    <option value="3">待支付（支付中）</option>
                    <option value="4">已支付</option>
                </select>
                订单编号：
                <input ref="booking_id" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                场地编号：
                <input ref="area_id" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                头等舱编号：
                <input ref="capsule_id" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>

                uin：
                <input ref="uin" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>

                手机号：
                <input ref="phone" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>


                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="button" className="btn btn-sm btn-success ml-1" onClick={this.download}>下载</button>

            </div>
            <div className="text-danger">最多返回{maxResultSize}条数据</div>
            <BookingGrid ref="bookingGrid"></BookingGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.refs.create_date_start.setValue(new Date().format('yyyy-MM-dd'));
        this.refs.create_date_end.setValue(new Date().format('yyyy-MM-dd'));
        this.search();
        request({
            url: '/api/activeCityList',
            success: (resp) => {
                if (resp.code == 0) {
                    this.setState({cityList: resp.data.cityList});
                }
            }
        });
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));

