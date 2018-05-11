class BookingModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            booking_id: props.booking_id,
            booking: props.booking,
            isEdit: props.isEdit || false,
            isNew: props.isNew || false,
        };
    }


    renderHeader = () => {
        return '场地信息';
    };

    renderBody = () => {


        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th colSpan={2} className="text-center text-primary">订单信息</th>
            </tr>
            <tr>
                <th>订单编号</th>
                <td>
                    <input ref="booking_id" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>创建时间</th>
                <td>
                    <input ref="create_time" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>结束时间</th>
                <td>
                    <input ref="end_time" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>订单状态</th>
                <td>
                    <select ref="status" className="form-control">
                        <option value="1">进行中</option>
                        <option value="2">待支付</option>
                        <option value="3">待支付（支付中）</option>
                        <option value="4">已支付</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th colSpan={2} className="text-center text-primary">头等舱信息</th>
            </tr>
            <tr>
                <th>头等舱编号</th>
                <td>
                    <input ref="capsule_id" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>场地编号</th>
                <td>
                    <input ref="area_id" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>场地名称</th>
                <td>
                    <input ref="area_title" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>城市</th>
                <td>
                    <input ref="area_city" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>地址</th>
                <td>
                    <input ref="area_address" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            </tbody>
        </table>
    };

}


class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {
                    field: 'booking_id', title: '订单编号', render: (value, row, index) => {
                        return <A onClick={this.showBooking.bind(this, value)}>{value}</A>
                    }
                },
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
                {
                    field: 'capsule_id', title: '头等舱编号', render: (value, row, index) => {
                        return <A onClick={this.showBooking.bind(this, value)}>{value}</A>
                    }
                },
                {
                    field: 'area_id', title: '场地编号', render: (value, row, index) => {
                        return <A onClick={this.showBooking.bind(this, value)}>{value}</A>
                    }
                },
                {
                    field: '_area', title: '场地名称', render: (value, row, index) => {
                        return value ? value.title : null;
                    }
                },
                {
                    field: '_area', title: '城市', render: (value, row, index) => {
                        return value ? value.city : null;
                    }
                },
                {
                    field: '_area', title: '地址', render: (value, row, index) => {
                        return value ? value.address : null;
                    }
                },
                {
                    field: 'uin', title: '用户UIN'
                },
                {
                    field: '_userInfo', title: '用户手机号', render: (value, row, index) => {
                        return value ? value.phone : null;
                    }
                },
                {
                    render: (value, row, index) => {
                        return [
                            <button type="button" className="btn btn-success btn-sm m-1"
                                    onClick={this.makeFailureByBooking.bind(this, row.booking_id)}>一键报修</button>
                        ]
                    }
                },
            ]
        };
    }

    makeFailureByBooking = (booking_id) => {
        Modal.open(<FailureModal isNew={true} booking_id={booking_id}></FailureModal>);
    };

    showBooking = (booking_id) => {
        Modal.open(<BookingModal></BookingModal>);
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
        const {grid} = this.refs;
        request({
            url: '/api/booking/search', loading: true,
            data: this.state.queryParams,
            success: (resp) => {
                if (resp.code == 0) {
                    this.state.data = resp.data.bookingList;
                    grid.state.data = resp.data.bookingList;
                    this.setState({});
                    grid.setState({});
                } else {
                }
            }
        });
    };

    render() {
        const {cityList, columns, data} = this.state;
        return <div className="container-fluid my-3">
            <div className="m-1">
                状态：
                <select ref="status" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value="">-- 全部 --</option>
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

                订单创建时间：
                <input ref="create_date_start" type="date"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <input ref="create_date_end" type="date"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>


                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="button" className="btn btn-sm btn-success ml-1 float-right hide"
                        onClick={this.newArea}>添加场地
                </button>
            </div>
            <div className="text-danger">查询结果条数：{data ? data.length : null}（最多返回500条数据）</div>
            <Datagrid ref="grid" columns={columns}></Datagrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
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

