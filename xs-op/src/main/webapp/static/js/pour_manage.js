class PourBookingModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            booking_id: props.booking_id,
            create: !!props.create,
            update: !!props.update,
            booking: props.booking,
        };
    }


    renderBody = () => {
        return <table className="table table-bordered">
            <tr>
                <th>用户编号</th>
                <td>{this.state.booking && this.state.booking.uin}</td>
            </tr>
            <tr>
                <th>用户手机号码</th>
                <td>{this.state.booking && this.state.booking.phone}</td>
            </tr>
            <tr>
                <th>接通时间</th>
                <td><DateTimeInput ref="start_time" getValue={(ymd, hms) => Math.floor(new DateTime(ymd.year, ymd.month, ymd.day, hms.hour, hms.minute, hms.second).toDate().getTime() / 1000)}></DateTimeInput></td>
            </tr>
            <tr>
                <th>通话时长</th>
                <td><TimeSelector ref="talk_time" getValue={hms => hms ? hms.getTime() : 0} hms={new HourMinuteSecond(0, 0, 0)}></TimeSelector></td>
            </tr>
            <tr>
                <th>计费金额</th>
                <td><PriceInput ref="final_price"></PriceInput></td>
            </tr>
        </table>
    };


    submit = () => {
        const {create, update, booking_id, booking} = this.state;
        const data = {
            uin: booking.uin,
            phone: booking.phone,
            hear_phone: '400',
            start_time: this.refs.start_time.getValue(),
            talk_time: this.refs.talk_time.getValue(),
            final_price: this.refs.final_price.getValue(),
        };
        data.end_time = data.start_time + data.talk_time;
        data.status = 3;

        if (create) {
            request({
                url: '/api/pour/booking/create', method: 'post', loading: true, data,
                success: resp => {
                    Message.msg('操作成功');
                    if (this.props.onSuccess) this.props.onSuccess();
                    this.close();
                }
            });

        } else if (update) {
            request({
                url: `/api/pour/booking/${booking_id}/update`, method: 'post', loading: true, data,
                success: resp => {
                    Message.msg('操作成功');
                    if (this.props.onSuccess) this.props.onSuccess();
                    this.close();
                }
            });
        }


    };

    renderFooter = () => [
        <span className="btn btn-sm btn-primary mx-1" onClick={this.submit}>保存</span>,
        <span className="btn btn-sm btn-secondary mx-1" onClick={this.close}>取消</span>,
    ];


    componentDidMount() {
        const {booking_id, create, update, booking} = this.state;

        if (update && booking_id) {
            request({
                url: `/api/pour/booking/${booking_id}`, loading: true,
                success: resp => {
                    this.setState({booking: resp.data.booking});
                    this.refs.start_time.setValue((resp.data.booking.start_time || 0) * 1000);
                    this.refs.talk_time.setValue(HourMinuteSecond.createBySecond(resp.data.booking.talk_time));
                    this.refs.final_price.setValue(resp.data.booking.final_price);
                }
            })
        }
    }
}


class PourBookingGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'id', title: '订单编号'},
                {
                    field: 'status', title: '订单状态', render: value => {
                        switch (value) {
                            case 0:
                            case 1:
                            case 2:
                                return <span className="text-secondary">未完成</span>
                            case 3:
                                return <span className="text-warning">待支付</span>
                            case 4:
                                return <span className="text-success">支付完成</span>
                            default:
                                return null;
                        }
                    }
                },
                {field: 'uin', title: '用户编号'},
                {field: 'phone', title: '用户手机号'},
                {field: 'start_time', title: '接通时间', render: (value) => type(value, 'Number') ? new Date(value * 1000).format() : null},
                {field: 'talk_time', title: '通话时长', render: (value) => type(value, 'Number') ? `${Math.floor(value / 60)}分${value % 60}秒` : null},
                {field: 'final_price', title: '计费金额', render: value => type(value, 'Number') ? value / 100 : null},
                {field: 'pay_price', title: '实际支付金额', render: value => type(value, 'Number') ? value / 100 : null},
                {field: 'remark', title: '备注'},
                {
                    field: 'id', title: <span className="btn btn-sm btn-success" onClick={this.create}>新建</span>, render: value =>
                        [
                            <span className="btn btn-sm btn-primary ml-1 mb-1" onClick={this.update.bind(this, value)}>编辑</span>,
                            <span className="btn btn-sm btn-danger ml-1 mb-1" onClick={this.delete.bind(this, value)}>删除</span>,
                        ]
                },
            ],
            queryParams: props.queryParams,
            page: 1,
            count: 0,
        };
    }


    load = (queryParams, page) => {
        if (type(queryParams) == 'Object') this.state.queryParams = queryParams;
        request({
            url: '/api/pour/booking/search', loading: true,
            data: {
                ...this.state.queryParams,
                page: page || this.state.page,
            },
            success: (resp) => {
                this.setState({
                    data: resp.data.bookingList,
                    page: page || this.state.page,
                    count: resp.data.count,
                });
            }
        });
    };


    create = () => {
        Modal.open(<PromptModal title="请输入用户手机号" ok={phone => {
            if (!/^1\d{10}$/.test(phone)) return Message.error('手机号码格式错误');
            request({
                url: `/api/user/phone/${phone}`, loading: true,
                success: resp => {
                    let userInfo = resp.data.userInfo;
                    Modal.open(<PourBookingModal create booking={{uin: userInfo.uin, phone: userInfo.phone}} onSuccess={() => {
                        this.load();
                    }}></PourBookingModal>)
                }
            })
        }}></PromptModal>);
    };

    update = (booking_id) => {
        Modal.open(<PourBookingModal update booking_id={booking_id} onSuccess={() => {
            this.load();
        }}></PourBookingModal>)
    };

    delete = (booking_id) => {
        Modal.open(<ConfirmModal ok={() => {
            request({
                url: `/api/pour/booking/${booking_id}/delete`, method: 'post', loading: true,
                success: resp => {
                    this.load();
                }
            })
        }}>确定删除？</ConfirmModal>)
    };


    render() {
        const {columns, data} = this.state;
        return <div>
            <Table columns={columns} data={data}></Table>
            <div className="text-center mt-1">
                <span className="btn btn-sm mx-1 btn-primary">上一页</span>
                第{this.state.page}页
                <span className="btn btn-sm mx-1 btn-primary">下一页</span>
                共{this.state.count}条
            </div>
        </div>;
    }

}


class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            payType: {5: '公众号支付', 7: '支付宝移动页面支付', 9: '微信小程序支付', 30: '新用户注册赠送', 1: '微信支付', 2: '支付宝支付', 20: '钱包余额支付'},

        };
    }

    getQueryParams = () => {
        return {
            date_start: this.refs.date_start.getValue(),
            date_end: this.refs.date_end.getValue(),
            status: this.refs.status.value,
            id: this.refs.id.value,
            uin: this.refs.uin.value,
            phone: this.refs.phone.value,
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
                接通时间：
                <DateInput ref="date_start" getValue={value => value ? value.format() : null}
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <DateInput ref="date_end" getValue={value => value ? value.format() : null}
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                状态：
                <select ref="status" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value=""></option>
                    <option value="3">待支付</option>
                    <option value="4">支付完成</option>
                </select>
                订单编号：
                <input ref="id" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                uin：
                <input ref="uin" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                手机号：
                <input ref="phone" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>

            </div>
            <PourBookingGrid ref="grid"></PourBookingGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.refs.date_start.setValue(new Date());
        this.refs.date_end.setValue(new Date());
        this.search();
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));

