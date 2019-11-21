class RedBagModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            red_bag_id: props.red_bag_id,
            create: !!props.create,
            update: !!props.update,
        };
    }

    renderBody = () => {
        const {red_bag_id, create, update} = this.state;
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th>id</th>
                <td>
                    <input ref="id" type="text" className="form-control" readOnly={true} disabled={true}/>
                </td>
            </tr>
            <tr>
                <th>活动名称</th>
                <td>
                    <select ref="type" className="form-control" disabled={true}>
                        <option value=""></option>
                        <option value={1}>单单返现金</option>
                        <option value={2}>幸运大转盘</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>用户编号</th>
                <td>
                    <input ref="uin" type="text" className="form-control" readOnly={true} disabled={true}/>
                </td>
            </tr>
            <tr>
                <th>奖品</th>
                <td>
                    <input ref="price_title" type="text" className="form-control" readOnly={true} disabled={true}/>
                </td>
            </tr>
            <tr>
                <th>领取状态</th>
                <td>
                    <select ref="status" className="form-control" disabled={!create && !update}>
                        <option value=""></option>
                        <option value={0}>未领取</option>
                        <option value={1}>已领取</option>
                    </select>
                </td>
            </tr>
            </tbody>
        </table>
    };

    submit = () => {
        const {red_bag_id, create, update} = this.state;
        let data = {
            status: this.refs.status.value || 0,
        };

        if (create) {
            // request({
            //     url: `/api/red_bag/create`, method: 'post', data,
            //     success: resp => {
            //
            //     }
            // });
        } else if (update) {
            request({
                url: `/api/red_bag/${red_bag_id}/update`, method: 'post', data,
                success: resp => {
                    Message.msg('保存成功！');
                    this.close();
                    if (this.props.onSuccess) this.props.onSuccess();
                }
            });
        }


    };


    reView = (redBag) => {
        if (redBag) {
            this.refs.id.value = redBag.id || null;
            this.refs.uin.value = redBag.uin || null;
            this.refs.price_title.value = redBag.price_title || null;
            this.refs.status.value = redBag.status || null;
            this.refs.type.value = redBag.type || null;

        }
    };

    componentDidMount() {
        super.componentDidMount();
        const {red_bag_id, create, update} = this.state;
        if (red_bag_id) {
            request({
                url: `/api/red_bag/${red_bag_id}`, loading: true,
                success: resp => {
                    this.reView(resp.data.redBag);
                }
            });
        }
    }


}


class RedBagGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'id',},
                {
                    field: 'type', title: '活动名称', render: value => {
                        if (value === 1) return '单单返现金';
                        if (value === 2) return '幸运大转盘';
                        return value;
                    }
                },
                {field: 'uin', title: '用户编号'},
                {field: 'uin', title: '用户手机号', render: value => this.state.userInfoMapOptions.get(value) ? this.state.userInfoMapOptions.get(value).phone : null},
                {field: 'price_title', title: '奖品'},
                {
                    field: 'id', title: '奖品', render: (value, row, index) => {
                        if (row.price_title === '现金红包') return `${row.price / 100}元现金红包`;
                        if (row.price_title === '雨露均沾奖') return `满${row.min_price / 100}减${row.cash / 100}优惠券`;
                        return value;
                    }
                },
                {
                    field: 'status', title: '领取状态', render: value => {
                        if (value === 1) return '已领取';
                        return <span className="text-danger">未领取</span>;
                    }
                },
                {field: 'receive_time', title: '领取时间', render: value => type(value, 'Number') ? new Date(value * 1000).format() : null},
                {field: 'booking_id', title: '订单编号'},
                {
                    field: 'id', title: <span className="btn btn-sm m-1 btn-success hide" onClick={this.create}>新建</span>, render: (value, row, index) => [
                        <span className="btn btn-sm m-1 btn-danger hide" onClick={this.delete.bind(this, value)}>删除</span>,
                        <span className="btn btn-sm m-1 btn-primary hide" onClick={this.update.bind(this, value)}>编辑</span>,
                    ]
                },
            ],
            queryParams: props.queryParams,
        };
    }

    load = (queryParams) => {
        if (type(queryParams) == 'Object') this.state.queryParams = queryParams;
        request({
            url: '/api/red_bag/search', loading: true,
            data: this.state.queryParams,
            success: (resp) => {
                this.setState({
                    data: resp.data.redBagList,
                    userInfoMapOptions: new UserInfoMapOptions(resp.data.userInfoList),
                });
            }
        });
    };

    create = () => {


    };
    delete = () => {


    };
    update = () => {


    };


    render() {
        const {columns, data} = this.state;
        return <Table columns={columns} data={data}></Table>;
    }

    componentDidMount() {
    }
}


class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    getQueryParams = () => {
        return {
            uin: this.refs.uin.value,
            phone: this.refs.phone.value,
            create_date_start: this.refs.create_date_start.value,
            create_date_end: this.refs.create_date_end.value,
        };
    };

    search = () => {
        this.refs.grid.load(this.getQueryParams());
    };
    download = () => {
        let queryParams = this.getQueryParams();
        queryParams.download = true;
        window.open(`/api/red_bag/search?${queryString(queryParams)}`)
    };


    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                创建日期：
                <DateInput ref="create_date_start"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <DateInput ref="create_date_end"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                用户编号：
                <input ref="uin" type="text" className="form-control d-inline-block mx-3 w-auto"/>
                用户手机号：
                <input ref="phone" type="text" className="form-control d-inline-block mx-3 w-auto"/>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="button" className="btn btn-sm btn-success ml-1" onClick={this.download}>下载</button>
            </div>
            <div className="table-responsive">
                <RedBagGrid ref="grid"></RedBagGrid>
            </div>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.refs.create_date_start.setValue(new Date().format('yyyy-MM-dd'));
        this.refs.create_date_end.setValue(new Date().format('yyyy-MM-dd'));
        this.search();
    }
}


ReactDOM.render(<Page/>, document.getElementById('root'));

