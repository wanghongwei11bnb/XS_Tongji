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

class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
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
                {field: 'final_price', title: '订单总金额', render: value => value ? value / 100 : value},
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
                {
                    render: (value, row, index) => {
                        return [
                            debug ? <button type="button" className="btn btn-primary btn-sm m-1"
                                            onClick={this.update.bind(this, row.booking_id)}>更改订单信息</button> : null,
                            <button type="button" className="btn btn-success btn-sm m-1"
                                    onClick={this.makeFailureByBooking.bind(this, row.booking_id)}>创建报修</button>,
                        ]
                    }
                },
            ]
        };
    }

    update = (booking_id) => {
        Modal.open(<BookingUpdateModal booking_id={booking_id} onSuccess={this.load}></BookingUpdateModal>);
    };

    makeFailureByBooking = (booking_id) => {
        Modal.open(<FailureModal isNew={true} booking_id={booking_id}></FailureModal>);
    };

    search = () => {
        this.state.queryParams = {
            status: this.refs.status.value,
            booking_id: this.refs.booking_id.value,
            area_id: this.refs.area_id.value,
            capsule_id: this.refs.capsule_id.value,
            create_date_start: this.refs.create_date_start.value,
            create_date_end: this.refs.create_date_end.value,
        };
        this.load();
    };
    load = () => {
        request({
            url: '/api/booking/search', loading: true,
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


                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="button" className="btn btn-sm btn-success ml-1 float-right hide"
                        onClick={this.newArea}>添加场地
                </button>
            </div>
            <div className="text-danger">最多返回{maxResultSize}条数据</div>
            <Table columns={columns} data={data}></Table>
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

