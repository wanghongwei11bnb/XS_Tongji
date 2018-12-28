class MinitouCapsuleGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'capsule_id', title: '设备编号'},
                // {field: 'area_id', title: '场地编号编号'},
                {field: 'area_id', title: '场地名称', render: value => this.state.areaMapOptions.get(value) ? this.state.areaMapOptions.get(value).title : null},
                {field: 'area_id', title: '城市', render: value => this.state.areaMapOptions.get(value) ? this.state.areaMapOptions.get(value).city : null},
                {field: 'area_id', title: '地址', render: value => this.state.areaMapOptions.get(value) ? this.state.areaMapOptions.get(value).address : null},
                {
                    field: 'capsule_id', title: '操作', render: value => [
                        <button className="btn btn-sm btn-primary m-1" onClick={this.showBookingFor30Days.bind(this, value)}>30日内订单</button>,
                    ]
                },
            ]
        };
    }

    load = (queryParams) => {
        if (queryParams) this.state.queryParams = queryParams;
        request({
            url: `/api/mnt/capsule/search`, loading: true,
            data: this.state.queryParams,
            success: (resp) => {
                this.setState({
                    data: resp.data.capsuleList,
                    areaMapOptions: new AreaMapOptions(resp.data.areaList),
                });
            }
        });
    };

    showBookingFor30Days = (capsule_id) => {
        Modal.open(<MinitouBookingGridModal queryParams={{
            capsule_id: capsule_id,
            create_date_start: new Date(Date.now() - (1000 * 60 * 60 * 24 * 30)).format('yyyy-MM-dd'),
            create_date_end: new Date().format('yyyy-MM-dd')
        }}></MinitouBookingGridModal>);
    };

    render() {
        return <Table columns={this.state.columns} data={this.state.data}></Table>
    }
}


class MinitouBookingGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            payType: {5: '公众号支付', 7: '支付宝移动页面支付', 9: '微信小程序支付', 30: '新用户注册赠送', 1: '微信支付', 2: '支付宝支付', 20: '钱包余额支付'},
            columns: [
                {field: 'booking_id', title: '订单编号', width: '8em'},
                {
                    field: 'create_time', title: '创建时间', width: '8em', render: (value, row, index) => {
                        return value ? new Date(value * 1000).format('yyyy-MM-dd hh:mm') : value;
                    }
                },
                {
                    field: 'end_time', title: '结束时间', width: '8em', render: (value, row, index) => {
                        return value ? new Date(value * 1000).format('yyyy-MM-dd hh:mm') : value;
                    }
                },
                {
                    field: 'status', title: '订单状态', width: '6em', render: (value, row, index) => {
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
                {
                    field: 'final_price', title: '订单总金额',
                    render: value => type(value) == 'Number' ? value / 100 : value,
                },
                {
                    field: 'use_pay', title: '非会员付费金额',
                    render: value => type(value) == 'Number' ? value / 100 : value,
                }, {
                    field: 'from_charge', title: '充值部分',
                    render: value => type(value) == 'Number' ? value / 100 : value,
                }, {
                    field: 'from_bonus', title: '赠送部分',
                    render: value => type(value) == 'Number' ? value / 100 : value,
                },
                {
                    field: 'pay_type', title: '支付方式', width: '8em',
                    render: value => type(value) == 'Number' && this.state.payType[value] ? this.state.payType[value] : value
                },
                {field: 'capsule_id', title: '头等舱编号'},
                {
                    field: 'area_id', title: '场地名称', render: (value, row, index) => {
                        return value && this.state.areaMapOptions.get(value) ? this.state.areaMapOptions.get(value).title : null;
                    }
                },
                {field: 'uin', title: '用户编号'},
                {field: 'req_from', title: '订单来源'},
            ],
        };
    }

    load = (queryParams) => {
        if (queryParams) this.state.queryParams = queryParams;
        request({
            url: '/api/mnt/booking/search', loading: true,
            data: this.state.queryParams,
            success: resp => {
                this.state.bookingList = resp.data.bookingList;
                this.setState({
                    data: resp.data.bookingList,
                    areaMapOptions: new AreaMapOptions(resp.data.areaList),
                });
            }
        });
    };

    render() {
        return <Table columns={this.state.columns} data={this.state.data}></Table>
    }
}

class MinitouBookingGridModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            queryParams: props.queryParams
        };
    }

    renderBody = () => {
        return <MinitouBookingGrid ref="grid"></MinitouBookingGrid>
    };

    componentDidMount() {
        super.componentDidMount();
        this.refs.grid.load(this.state.queryParams);
    }
}


class MinitouBillGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'capsule_id', title: '设备编号'},
                {field: 'area_id', title: '场地名称', render: value => this.state.areaMapOptions.get(value) ? this.state.areaMapOptions.get(value).title : null},
                {field: 'account_ratio', title: '场地分成比例', render: value => type(value, 'Number') ? `${value}%` : null},
                {
                    field: 'final_price', title: '经营收入', render: value => type(value, 'Number') ? value / 100 : null, totalHandle: (total, value) => {
                        if (type(total) !== 'Number') total = 0;
                        if (type(value) !== 'Number') value = 0;
                        return (total + value / 100).toFixed(2) - 0;
                    }
                },
                {
                    field: 'ratio_price', title: '场地分成金额', render: value => type(value, 'Number') ? value / 100 : null, totalHandle: (total, value) => {
                        if (type(total) !== 'Number') total = 0;
                        if (type(value) !== 'Number') value = 0;
                        return (total + value / 100).toFixed(2) - 0;
                    }
                },
                {
                    field: 'rent_price', title: '利润收入（扣除租金）', render: value => type(value, 'Number') ? value / 100 : null, totalHandle: (total, value) => {
                        if (type(total) !== 'Number') total = 0;
                        if (type(value) !== 'Number') value = 0;
                        return (total + value / 100).toFixed(2) - 0;
                    }
                },
            ]
        };
    }


    load = (queryParams) => {
        if (queryParams) this.state.queryParams = queryParams;
        request({
            url: `/api/mnt/bill/checkout`, method: 'post', loading: true,
            data: this.state.queryParams,
            success: (resp) => {
                this.setState({
                    data: resp.data.minitouBillList,
                    cityMapOptions: new CityMapOptions(resp.data.cityList),
                    areaMapOptions: new AreaMapOptions(resp.data.areaList),
                });
            }
        });
    };


    render() {
        return <Table renderHeader={this.props.renderHeader} columns={this.state.columns} data={this.state.data}></Table>
    }


}
