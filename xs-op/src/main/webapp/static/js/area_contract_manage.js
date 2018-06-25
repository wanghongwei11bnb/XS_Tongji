class AreaContractGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'area_id', title: '场地编号'},
                {
                    field: 'area_id', title: '场地名称', render: value => {
                        const areaMapOptions = this.state.areaMapOptions;
                        if (areaMapOptions && areaMapOptions.get(value)) {
                            return areaMapOptions.get(value).title;
                        }
                    }
                },
                {
                    field: 'area_id', title: '投放城市', render: value => {
                        const areaMapOptions = this.state.areaMapOptions;
                        if (areaMapOptions && areaMapOptions.get(value)) {
                            return areaMapOptions.get(value).city;
                        }
                    }
                },
                {
                    field: 'area_id', title: '投放地址', render: value => {
                        const areaMapOptions = this.state.areaMapOptions;
                        if (areaMapOptions && areaMapOptions.get(value)) {
                            return areaMapOptions.get(value).address;
                        }
                    }
                },
                {field: 'saler_city', title: '所属公司'},
                {field: 'saler', title: '销售人员'},
                {field: 'customer', title: '客户公司名称'},
                {
                    field: 'account_ratio', title: '分账比例', render: value => {
                        if (type(value) === 'Number') {
                            return `${value}%`;
                        }
                    }
                },
                {field: 'bank_account', title: '客户公司银行账号'},
                {field: 'bank_branch', title: '客户公司银行支行信息'},
                {field: 'remark', title: '备注'},
                {
                    field: 'status', title: '状态', render: value => {
                        if (value === 0) {
                            return <span className="text-warning">审核中</span>
                        } else if (value === 1) {
                            return <span className="text-success">审核通过</span>
                        } else if (value === -1) {
                            return <span className="text-danger">审核未通过</span>
                        } else if (value === -2) {
                            return <span className="text-secondary">废弃</span>
                        }
                    }
                },
                {
                    field: 'area_id',
                    title: <button className="btn btn-sm btn-success m-1" onClick={this.createAreaContract}>新建</button>,
                    render: (value, row) => {
                        return [
                            row.status === 0 ? <button className="btn btn-sm btn-primary m-1">编辑</button> : null,
                        ]
                    }
                },
            ],
        };
    }

    createAreaContract = () => {
        Modal.open(<AreaContractModal create onSuccess={this.load}></AreaContractModal>);
    };

    load = (queryParams) => {
        if (queryParams) {
            this.state.queryParams = queryParams;
        }
        request({
            url: '/api/area_contract/search', loading: true,
            data: this.state.queryParams,
            success: resp => {
                this.setState({
                    data: resp.data.areaContractList,
                    areaMapOptions: resp.data.areaList ? new AreaMapOptions(resp.data.areaList) : null,
                });
            }
        });

    };

    render() {
        return <Table columns={this.state.columns} data={this.state.data}></Table>
    }
}


class AreaContractModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            create: props.create || false,
            update: props.update || false,
            area_id: props.area_id,
        };
    }

    submit = () => {

        if (!this.refs.area_id.value) return Message.msg('请选择场地');

        let data = {
            area_id: this.refs.area_id.value,
            saler: this.refs.saler.value,
            saler_city: this.refs.saler_city.value,
            customer: this.refs.customer.value,
            customer_email: this.refs.customer_email.value,
            customer_contact: this.refs.customer_contact.value,
            account_ratio: this.refs.account_ratio.value,
            bank_account: this.refs.bank_account.value,
            bank_branch: this.refs.bank_branch.value,
            remark: this.refs.remark.value,
            status: this.refs.status.value,
        };
        if (this.state.create) {
            request({
                url: '/api/area_contract/create', contentType: 'application/json', method: 'post', loading: true,
                data: JSON.stringify(data, nullStringReplacer),
                success: resp => {
                    Message.msg('保存成功');
                    this.close();
                    if (this.props.onSuccess) this.props.onSuccess();
                }
            });
        } else if (this.state.update) {
            request({
                url: `/api/area_contract/${this.refs.area_id.value}/update`, method: 'post', loading: true,
                data: JSON.stringify(data, nullStringReplacer),
                success: resp => {
                    Message.msg('保存成功');
                    this.close();
                    if (this.props.onSuccess) this.props.onSuccess();
                }
            });
        }

    };

    selectArea = () => {
        Modal.open(<AreaSelectModal onSuccess={area => {
            this.refs.area_id.value = area.area_id;
            this.refs.area_title.value = area.title;
            this.refs.area_city.value = area.city;
            this.refs.area_address.value = area.address;
        }}></AreaSelectModal>);
    };

    renderBody = () => {
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <td colSpan={2} className="text-center text-danger">场地信息</td>
            </tr>
            <tr>
                <th>场地编号</th>
                <td>
                    <div className="row">
                        <div className="col-sm-6">
                            <input ref="area_id" readOnly={true} disabled={true} type="text" className="form-control"/>
                        </div>
                        <div className="col-sm-6">
                            <button className="btn btn-success btn-sm m-1" onClick={this.selectArea}>选择场地</button>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <th>场地名称</th>
                <td>
                    <input ref="area_title" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>投放城市</th>
                <td>
                    <input ref="area_city" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>投放地址</th>
                <td>
                    <input ref="area_address" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <td colSpan={2} className="text-center text-danger">销售人员</td>
            </tr>
            <tr>
                <th>销售人员</th>
                <td>
                    <input ref="saler" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>所属公司</th>
                <td>
                    <input ref="saler_city" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <td colSpan={2} className="text-center text-danger">客户信息</td>
            </tr>
            <tr>
                <th>客户公司名称</th>
                <td>
                    <input ref="customer" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>客户公司邮箱</th>
                <td>
                    <input ref="customer_email" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>客户公司联系方式</th>
                <td>
                    <input ref="customer_contact" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>客户公司银行账号</th>
                <td>
                    <input ref="bank_account" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>客户公司银行支行信息</th>
                <td>
                    <input ref="bank_branch" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>分账比例</th>
                <td>
                    <div className="row">
                        <div className="col-sm-6">
                            <input ref="account_ratio" type="text" className="form-control"/>
                        </div>
                        <div className="col-sm-6 pt-1 pl-0">
                            %
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td colSpan={2} className="text-center text-danger">审核状态</td>
            </tr>
            <tr>
                <th>创建日期</th>
                <td>
                    <input ref="create_time" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>备注</th>
                <td>
                    <textarea ref="remark" className="form-control"></textarea>
                </td>
            </tr>
            <tr>
                <th>状态</th>
                <td>
                    <select ref="status" className="form-control">
                        <option value=""></option>
                        {AreaContractStatusOption.map(option => {
                            return <option value={option.value}>{option.text}</option>
                        })}
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
        const {areaContract} = this.props;
        if (areaContract) {
            if (areaContract.saler) this.refs.saler.value = areaContract.saler;
            if (areaContract.saler_city) this.refs.saler_city.value = areaContract.saler_city;


        }
    }

}

class AreaSelectModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {};
    }

    handleSelect = (area) => {
        if (this.props.onSuccess) this.props.onSuccess(area);
        this.close();
    };


    search = () => {
        this.refs.grid.load({
            city: this.refs.city.value,
            title: this.refs.title.value,
            address: this.refs.address.value,
            area_id: this.refs.area_id.value,
        });
    };

    renderBody = () => {
        return <div>
            <div>
                城市：
                <select ref="city" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value=""></option>
                    {activeCityList ? activeCityList.map((city) => {
                        return <option value={city.city}>{city.city}</option>
                    }) : null}
                </select>
                标题：
                <input ref="title" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                地址：
                <input ref="address" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                场地编号：
                <input ref="area_id" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <button className="btn btn-primary btn-sm m-1" onClick={this.search}>搜索</button>
            </div>
            <AreaSelectGrid ref="grid" onSuccess={this.handleSelect}></AreaSelectGrid>
        </div>
    };

    componentDidMount() {
        super.componentDidMount();
        this.search();
    }

}

class AreaSelectGrid extends AreaGrid {
    constructor(props) {
        super(props);
        this.state.columns.push({
            title: '操作',
            render: (value, row, index) => {
                return <button className="btn btn-sm btn-primary m-1" onClick={() => {
                    if (this.props.onSuccess) this.props.onSuccess(row);
                }}>选择</button>;
            }
        });
    }
}


class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }


    search = () => {
        this.refs.grid.load({
            customer: this.refs.customer.value,
        });
    };


    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                客户公司名称：
                <input ref="customer" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
            </div>
            <AreaContractGrid ref="grid"></AreaContractGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.search();
    }
}

loadOpInfo();

request({
    url: '/api/activeCityList',
    success: (resp) => {
        window.activeCityList = resp.data.cityList;
    }
});

ReactDOM.render(<Page/>, document.getElementById('root'));