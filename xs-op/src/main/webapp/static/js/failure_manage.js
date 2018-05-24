class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {
                    title: 'ID', width: 220, render: (value, row, index) => {
                        return <div>
                            uin：{row.uin}<br/>
                            手机号：{row.phone}<br/>
                            场地编号：{row.area_id}<br/>
                            头等舱编号：{row.capsule_id}<br/>
                            订单号：{row.booking_id}<br/>
                        </div>
                    }
                },
                {
                    field: 'area_id', title: '场地标题', render: (value, row, index) => {
                        if (value && this.state.areaMapOptions.get(value)) {
                            return this.state.areaMapOptions.get(value).title;
                        } else {
                            return null;
                        }
                    }
                },
                {
                    field: 'area_id', title: '城市/地址', width: 200, render: (value, row, index) => {
                        if (value && this.state.areaMapOptions.get(value)) {
                            return [this.state.areaMapOptions.get(value).city,
                                <br/>, this.state.areaMapOptions.get(value).address];
                        } else {
                            return null;
                        }
                    }
                },
                {
                    title: '客户端', width: 120, render: (value, row, index) => {
                        return <div>{row.req_from}<br/>{row.app_version}</div>;
                    }
                },
                {
                    field: 'create_time', title: '报修时间', width: 120, render: (value, row, index) => {
                        if (value) {
                            let create_date = new Date(value * 1000);
                            return create_date.format('yyyy-MM-dd hh:mm');
                        }
                        return null;
                    }
                },
                {
                    field: 'tags', title: 'tags', render: (value, row, index) => {
                        if (value) {
                            return value.map((tag) => {
                                return [<span className="badge badge-pill badge-primary">{tag}</span>, <br/>]
                            })
                        }
                        return null;
                    }
                },
                {field: 'description', title: '用户描述', width: 200},
                {field: 'create_from_role', title: '报修创建来源'},
                {field: 'op_description', title: '处理结果', width: 200},
                {
                    field: 'op_status', title: '处理状态', width: 100, render: (value, row, index) => {
                        if (value == -1) {
                            return <span className="text-danger">未解决</span>
                        } else if (value == 1) {
                            return <span className="text-warning">处理中</span>
                        } else if (value == 2) {
                            return <span className="text-success">已解决</span>
                        } else {
                            return <span className="text-secondary">未处理</span>
                        }
                    }
                },
                {
                    title: <A className="btn btn-sm btn-success ml-1 float-right" onClick={this.addNew}>创建报修</A>,
                    width: 80, render: (value, row, index) => {
                        return [
                            <button type="button" className="btn btn-sm bg-primary text-white"
                                    onClick={this.edit.bind(this, row)}>编辑处理结果</button>
                        ];
                    }
                },
            ], now: new Date()
        };
    }

    edit = (failure) => {
        Modal.open(<FailureModal failure={failure} onSuccess={this.load}></FailureModal>);
    };

    addNew = () => {
        Modal.open(<FailureModal isNew={true} onSuccess={this.load}></FailureModal>);
    };

    search = () => {
        this.state.queryParams = {
            start_date: this.refs.start_date.value,
            end_date: this.refs.end_date.value,
            op_status: this.refs.op_status.value,
            capsule_id: this.refs.capsule_id.value,
            booking_id: this.refs.booking_id.value,
        };
        this.load();
    };

    load = () => {
        const {queryParams} = this.state;
        request({
            url: '/api/failure/search', loading: true,
            data: queryParams,
            success: (resp) => {
                if (resp.code == 0) {
                    this.state.data = resp.data.failureList;
                    this.state.areaMapOptions = new AreaMapOptions(resp.data.areaList);
                    this.setState({});
                } else {
                }
            }
        });
    };

    render() {
        const {columns, data} = this.state;
        return <div className="container-fluid my-3">
            <div className="m-1">
                报修时间：
                <DateInput ref="start_date"
                           className="form-control  form-control-sm d-inline-block mx-3 w-auto"/>
                <DateInput ref="end_date" className="form-control form-control-sm  d-inline-block mx-3 w-auto"/>
                OP处理状态：
                <select ref="op_status" className="form-control form-control-sm  d-inline-block mx-3 w-auto">
                    <option value=""></option>
                    <option value="1">处理中</option>
                    <option value="2">已解决</option>
                    <option value="-1">未解决</option>
                </select>
                头等舱编号：
                <input ref="capsule_id" type="text"
                       className="form-control form-control-sm  d-inline-block mx-3 w-auto"/>
                订单编号：
                <input ref="booking_id" type="text"
                       className="form-control form-control-sm  d-inline-block mx-3 w-auto"/>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
            </div>
            <div className="text-danger">最多返回{maxResultSize}条</div>
            <div className="table-responsive">
                <Table columns={columns} data={data}></Table>
            </div>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        const {now} = this.state;
        this.refs.start_date.setValue(new Date(now.getTime() - 1000 * 60 * 60 * 24 * 7).format('yyyy-MM-dd'));
        this.refs.end_date.setValue(now.format('yyyy-MM-dd'));
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
