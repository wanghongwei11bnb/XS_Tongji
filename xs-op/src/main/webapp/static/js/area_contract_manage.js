class AreaContractGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {
                    field: 'area_id', title: '空间名称', render: value => {
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
                    title: <button className="btn btn-sm btn-success m-1" onClick={createAreaContract}>新建</button>,
                    render: (value, row) => {
                        return [
                            row.status === 0 ? <button className="btn btn-sm btn-primary m-1">编辑</button> : null,
                        ]
                    }
                },
            ],
        };
    }


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
                url: '/api/area_contract/create', method: 'post', loading: true,
                data: JSON.stringify(data),
                success: resp => {
                    Message.msg('保存成功');
                    this.close();
                    if (this.props.onSuccess) this.props.onSuccess();
                }
            });
        } else if (this.state.update) {

            request({
                url: `/api/area_contract/${this.refs.area_id.value}/update`, method: 'post', loading: true,
                data: JSON.stringify(data),
                success: resp => {
                    Message.msg('保存成功');
                    this.close();
                    if (this.props.onSuccess) this.props.onSuccess();
                }
            });
        }

    };


    renderBody = () => {
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th>场地编号</th>
                <td>
                    <div className="row">
                        <div className="col-sm-6">
                            <input ref="area_id" readOnly={true} disabled={true} type="text" className="form-control"/>
                        </div>
                        <div className="col-sm-6">
                            <button className="btn btn-success btn-sm m-1">选择场地</button>
                        </div>
                    </div>
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
            </tbody>
        </table>
    };

    renderFooter = () => {
        return [
            <A className="btn btn-link text-primary float-right" onClick={this.submit}>保存</A>,
            <A className="btn btn-link text-secondary float-right" onClick={this.close}>取消</A>,
        ];
    };

}

function createAreaContract() {
    Modal.open(<AreaContractModal create></AreaContractModal>);
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

ReactDOM.render(<Page/>, document.getElementById('root'));