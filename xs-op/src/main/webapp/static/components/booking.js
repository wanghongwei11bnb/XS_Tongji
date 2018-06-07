class BookingUpdateModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            booking_id: props.booking_id,
        };
    }

    submit = () => {
        request({
            url: `/api/booking/${this.state.booking_id}/update/op`, loading: true, method: 'post',
            data: {
                final_price: this.refs.final_price.value ? Math.floor(this.refs.final_price.value * 100) : null,
                status: this.refs.status.value,
            },
            success: (resp) => {
                Message.msg('保存成功');
                this.close();
                if (this.props.onSuccess) this.props.onSuccess();
            }
        });
    };

    renderBody = () => {
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th>订单金额</th>
                <td>
                    <input ref="final_price" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>订单状态</th>
                <td>
                    <select ref="status" className="form-control">
                        <option value="2">待支付</option>
                    </select>
                </td>
            </tr>
            </tbody>
        </table>
    };

    renderFooter = () => {
        return [
            <A className="btn btn-link text-primary float-right" onClick={this.submit}>保存</A>,
            <A className="btn btn-link text-secondary float-right" onClick={this.close}>取消</A>,
        ];
    };

    componentDidMount() {
        super.componentDidMount();
        this.refs.status.value = 2;
        request({
            url: `/api/booking/${this.state.booking_id}`, loading: true,
            success: (resp) => {
                if (resp.code == 0) {
                    if (resp.data.booking) {
                        this.refs.final_price.value = resp.data.booking.final_price ? resp.data.booking.final_price / 100 : null;

                    }
                }
            }
        });
    }

}


class BookingGrid extends React.Component {
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
                {field: 'use_pay', title: '实际付款金额', render: value => type(value) == 'Number' ? value / 100 : value},
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
            ],
        };
    }

    update = (booking_id) => {
        Modal.open(<BookingUpdateModal booking_id={booking_id} onSuccess={this.load}></BookingUpdateModal>);
    };


    makeFailureByBooking = (booking_id) => {
        Modal.open(<FailureModal isNew={true} booking_id={booking_id}></FailureModal>);
    };


    load = (queryParams) => {
        if (queryParams) {
            this.state.queryParams = queryParams;
        }
        request({
            url: this.props.url || '/api/booking/search', loading: true,
            data: this.state.queryParams,
            success: (resp) => {
                if (resp.code == 0) {
                    this.state.bookingList = resp.data.bookingList;
                    this.setState({
                        data: resp.data.bookingList,
                        areaMapOptions: new AreaMapOptions(resp.data.areaList),
                        userInfoMapOptions: new UserInfoMapOptions(resp.data.userInfoList),
                    });
                } else {
                }
            }
        });
    };

    render() {
        const {columns, data} = this.state;
        return <Table columns={columns} data={data}></Table>;
    }
}

class BookingGridModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            queryParams: props.queryParams
        };
    }

    renderHeader = () => {
        return '订单列表';
    };

    renderBody = () => {
        return <BookingGrid ref="bookingGrid" url={this.props.url}></BookingGrid>;
    };

    componentDidMount() {
        super.componentDidMount();
        this.refs.bookingGrid.load(this.state.queryParams);
    }
}

class BookingGridView extends BookingGrid {
    constructor(props) {
        super(props);
        this.state.columns.splice(this.state.columns.length - 1, 1);
    }
}


class BookingGridViewModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            queryParams: props.queryParams
        };
    }

    renderHeader = () => {
        return '订单列表';
    };

    renderBody = () => {
        return <BookingGridView ref="bookingGrid" url={this.props.url}></BookingGridView>;
    };

    componentDidMount() {
        super.componentDidMount();
        this.refs.bookingGrid.load(this.state.queryParams);
    }
}

class BookingModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            booking_id: props.booking_id,
        };
    }

    renderHeader = () => {
        return '订单信息';
    };
    renderBody = () => {
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th>订单编号</th>
                <td>
                    <input ref="booking_id" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>订单编号</th>
                <td>
                    <input ref="booking_id" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>订单编号</th>
                <td>
                    <input ref="booking_id" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>订单编号</th>
                <td>
                    <input ref="booking_id" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>订单编号</th>
                <td>
                    <input ref="booking_id" type="text" className="form-control"/>
                </td>
            </tr>
            </tbody>
        </table>;
    };
}