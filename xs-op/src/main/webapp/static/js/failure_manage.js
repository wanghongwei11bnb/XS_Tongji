class FailureModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            failure: props.failure,
            onSuccess: props.onSuccess,
        };
    }

    renderHeader = () => {
        return '故障保修审批处理';
    };
    renderFooter = () => {
        return <span className="float-right">
                <button type="button" className="btn btn-link text-primary" onClick={this.onSubmit}>保存</button>
                <button type="button" className="btn btn-link text-secondary" onClick={this.close}>取消</button>
            </span>;
    };

    onSubmit = () => {
        const {failure, onSuccess} = this.state;
        request({
            url: `/api/failure/${failure.capsule_id}/${failure.create_time}/update/review`,
            method: 'post',
            loading: true,
            contentType: "application/json",
            data: JSON.stringify({
                op_description: this.refs.op_description.value,
                op_status: this.refs.op_status.value,
            }),
            success: (resp) => {
                this.close();
                if (onSuccess) onSuccess();
            }
        });
    };


    renderBody = () => {
        const {failure} = this.state;
        return <div>
            <table className="table">
                <tbody>
                <tr>
                    <th>场地</th>
                    <td>
                        {failure.areaObj ? failure.areaObj.title : null}
                    </td>
                </tr>
                <tr>
                    <th>城市</th>
                    <td>
                        {failure.areaObj ? failure.areaObj.city : null}
                    </td>
                </tr>
                <tr>
                    <th>地址</th>
                    <td>
                        {failure.areaObj ? failure.areaObj.address : null}
                    </td>
                </tr>
                <tr>
                    <th>头等舱编号</th>
                    <td>
                        {failure.capsule_id}
                    </td>
                </tr>
                <tr>
                    <th>订单编号</th>
                    <td>
                        {failure.booking_id}
                    </td>
                </tr>
                <tr>
                    <th>用户</th>
                    <td>
                        uin：{failure.uin}<br/>手机号：{failure.phone}
                    </td>
                </tr>
                <tr>
                    <th>报修时间</th>
                    <td>
                        {failure.create_time ? new Date(failure.create_time * 1000).format('yyyy-MM-dd hh:mm') : null}
                    </td>
                </tr>
                <tr>
                    <th>客户端</th>
                    <td>
                        {failure.app_version}<br/>
                        {failure.client_type}<br/>
                        {failure.client_version}<br/>
                        {failure.req_from}<br/>
                    </td>
                </tr>
                <tr>
                    <th>tags</th>
                    <td>
                        {failure.tags ? failure.tags.map((tag) => {
                            return [<span className="badge badge-pill badge-primary">{tag}</span>, <br/>]
                        }) : null}
                    </td>
                </tr>
                <tr>
                    <th>用户描述</th>
                    <td>
                        {failure.description}
                    </td>
                </tr>
                <tr>
                    <th>OP描述</th>
                    <td>
                        <textarea ref="op_description" className="form-control"></textarea>
                    </td>
                </tr>
                <tr>
                    <th>OP处理状态</th>
                    <td>
                        <select ref="op_status" className="form-control">
                            <option value="0">未处理</option>
                            <option value="1">已解决</option>
                            <option value="-1">未解决</option>
                            <option value="2">处理中</option>
                        </select>
                    </td>
                </tr>

                </tbody>
            </table>
        </div>;
    };

    componentDidMount() {
        super.componentDidMount();
        const {failure} = this.state;
        this.refs.op_description.value = failure.op_description;
        if (failure.op_status == 1) {
            this.refs.op_status.value = 1;
        } else if (failure.op_status == -1) {
            this.refs.op_status.value = -1;
        } else {
            this.refs.op_status.value = 0;
        }
    }
}

class FailureAddNewModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            failure: props.failure,
            onSuccess: props.onSuccess,
        };
    }

    renderHeader = () => {
        return '创建故障保修';
    };
    renderFooter = () => {
        return <span className="float-right">
                <button type="button" className="btn btn-link text-primary" onClick={this.onSubmit}>保存</button>
                <button type="button" className="btn btn-link text-secondary" onClick={this.close}>取消</button>
            </span>;
    };

    onSubmit = () => {
        const {failure, onSuccess} = this.state;
        request({
            url: `/api/failure/${failure.capsule_id}/${failure.create_time}/update/review`,
            method: 'post',
            loading: true,
            contentType: "application/json",
            data: JSON.stringify({
                op_description: this.refs.op_description.value,
                op_status: this.refs.op_status.value,
            }),
            success: (resp) => {
                this.close();
                if (onSuccess) onSuccess();
            }
        });
    };


    renderBody = () => {
        return <div>
            <table className="table">
                <tbody>
                <tr>
                    <th>场地</th>
                    <td>
                        {failure.areaObj ? failure.areaObj.title : null}
                    </td>
                </tr>
                <tr>
                    <th>城市</th>
                    <td>
                        {failure.areaObj ? failure.areaObj.city : null}
                    </td>
                </tr>
                <tr>
                    <th>地址</th>
                    <td>
                        {failure.areaObj ? failure.areaObj.address : null}
                    </td>
                </tr>
                <tr>
                    <th>头等舱编号</th>
                    <td>
                        {failure.capsule_id}
                    </td>
                </tr>
                <tr>
                    <th>订单编号</th>
                    <td>
                        {failure.booking_id}
                    </td>
                </tr>
                <tr>
                    <th>用户</th>
                    <td>
                        uin：{failure.uin}<br/>手机号：{failure.phone}
                    </td>
                </tr>
                <tr>
                    <th>报修时间</th>
                    <td>
                        {failure.create_time ? new Date(failure.create_time * 1000).format('yyyy-MM-dd hh:mm') : null}
                    </td>
                </tr>
                <tr>
                    <th>客户端</th>
                    <td>
                        {failure.app_version}<br/>
                        {failure.client_type}<br/>
                        {failure.client_version}<br/>
                        {failure.req_from}<br/>
                    </td>
                </tr>
                <tr>
                    <th>tags</th>
                    <td>
                        {failure.tags ? failure.tags.map((tag) => {
                            return [<span className="badge badge-pill badge-primary">{tag}</span>, <br/>]
                        }) : null}
                    </td>
                </tr>
                <tr>
                    <th>用户描述</th>
                    <td>
                        {failure.description}
                    </td>
                </tr>
                <tr>
                    <th>OP描述</th>
                    <td>
                        <textarea ref="op_description" className="form-control"></textarea>
                    </td>
                </tr>
                <tr>
                    <th>OP处理状态</th>
                    <td>
                        <select ref="op_status" className="form-control">
                            <option value="0">未处理</option>
                            <option value="1">已解决</option>
                            <option value="-1">未解决</option>
                        </select>
                    </td>
                </tr>

                </tbody>
            </table>
            <ModalContainer ref="modal"></ModalContainer>
        </div>;
    };

    componentDidMount() {
        this.refs.op_description.value = failure.op_description;
        if (failure.op_status == 1) {
            this.refs.op_status.value = 1;
        } else if (failure.op_status == -1) {
            this.refs.op_status.value = -1;
        } else {
            this.refs.op_status.value = 0;
        }
    }
}


class Page extends React.Component {
    constructor(props) {
        super(props);
        const columns = [
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
                field: 'areaObj', title: '场地标题', render: (value, row, index) => {
                    if (value) {
                        return value.title;
                    } else {
                        return null;
                    }
                }
            },
            {
                field: 'areaObj', title: '城市/地址', width: 200, render: (value, row, index) => {
                    if (value) {
                        return [value.city, <br/>, value.address];
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
                field: 'create_time', title: '保修时间', width: 120, render: (value, row, index) => {
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
            {field: 'create_from_role', title: 'create_from_role'},
            {field: 'op_description', title: 'OP描述', width: 200},
            {
                field: 'op_status', title: '处理状态', width: 100, render: (value, row, index) => {
                    if (value == -1) {
                        return <span className="text-danger">未解决</span>
                    } else if (value == 1) {
                        return <span className="text-success">已解决</span>
                    } else {
                        return <span className="text-secondary">未处理</span>
                    }
                }
            },
            {
                width: 80, render: (value, row, index) => {
                    return [
                        <button type="button" className="btn btn-sm bg-primary text-white"
                                onClick={this.edit.bind(this, row)}>编辑</button>
                    ];
                }
            },
        ];
        this.state = {
            columns
            , now: new Date()
        };
    }


    edit = (failure) => {
        ModalContainer.modal.open(<FailureModal failure={failure} onSuccess={this.load}></FailureModal>);
    };

    addNew = (failure) => {
        ModalContainer.modal.open(<FailureAddNewModal onSuccess={this.load}></FailureAddNewModal>);
    };


    search = () => {
        this.state.queryParams = {
            start_date: this.refs.start_date.value,
            end_date: this.refs.end_date.value,
            op_status: this.refs.op_status.value,
        };
        this.load();
    };


    load = () => {
        const {queryParams} = this.state;
        const {grid} = this.refs;
        request({
            url: '/api/failure/search', loading: true,
            data: queryParams,
            success: (resp) => {
                if (resp.code == 0) {
                    this.state.data = resp.data.failureList;
                    grid.state.data = resp.data.failureList;
                    this.setState({});
                } else {
                }
            }
        });
    };

    render() {
        const {cityList, columns, data, now} = this.state;
        return <div className="container-fluid my-3">
            <div className="m-1">
                保修时间：
                <input ref="start_date" type="date" className="form-control d-inline-block mx-3 w-auto"/>
                <input ref="end_date" type="date" className="form-control d-inline-block mx-3 w-auto"/>
                OP处理状态：
                <select ref="op_status" className="form-control d-inline-block mx-3 w-auto">
                    <option value=""></option>
                    <option value="1">已解决</option>
                    <option value="-1">未解决</option>
                </select>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="button" className="btn btn-sm btn-success ml-1 float-right" onClick={this.addNew}>创建报修
                </button>
            </div>
            <div className="text-danger">查询结果条数：{data ? data.length : null}（最多返回100条）</div>
            <div className="table-responsive">
                <Datagrid ref="grid" columns={columns}></Datagrid>
            </div>
            <ModalContainer id="modal"></ModalContainer>
        </div>;
    }

    componentDidMount() {
        const {now} = this.state;
        this.refs.start_date.value = new Date(now.getTime() - 1000 * 60 * 60 * 24 * 7).format('yyyy-MM-dd');
        this.refs.end_date.value = now.format('yyyy-MM-dd');
        this.search();
        reqwest({
            url: '/api/cityList',
            success: (resp) => {
                if (resp.code == 0) {
                    this.setState({cityList: resp.data.cityList});
                }
            }
        });
    }
}


ReactDOM.render(<Page/>, document.getElementById('root'));

