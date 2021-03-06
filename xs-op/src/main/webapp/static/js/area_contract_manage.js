class SalerModal extends Modal {
    constructor(props) {
        super(props);
    }

    ok = () => {
        if (!this.refs.fullname.value) return Message.msg('请输入姓名');
        if (!this.refs.city.value) return Message.msg('请选择城市');
        request({
            url: '/api/op/update/saler', method: 'post', loading: true,
            data: {
                fullname: this.refs.fullname.value,
                city: this.refs.city.value,
            },
            success: resp => {
                Message.msg('操作成功');
                this.close();
                if (this.props.onSuccess) this.props.onSuccess();
            }
        });
    };

    renderBody = () => {

        return <div>
            <input ref="fullname" type="text" className="form-control d-inline-block w-auto m-3"/>
            <select ref="city" className="form-control d-inline-block w-auto m-3">
                <option value=""></option>
                {activeCityList.map(city => {
                    return <option value={city.city}>{city.city}</option>;
                })}
            </select>
        </div>
    };

    renderFooter = () => {
        return [
            <A className="btn btn-link text-primary float-right" onClick={this.ok}>确定</A>,
            <A className="btn btn-link text-secondary float-right" onClick={this.close}>取消</A>,
        ];
    };
}

class Saler extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    load = () => {
        request({
            url: '/api/getOpInfo', loading: true,
            success: resp => {
                this.setState({op: resp.data.op});
            }
        });
    };

    setting = () => {
        Modal.open(<SalerModal onSuccess={this.load}></SalerModal>);
    };

    render() {
        const {op} = this.state;
        return <div className="alert alert-primary">
            {op ? (
                op.fullname && op.city ?
                    <div>销售人员：{op.fullname}&nbsp;&nbsp;&nbsp;&nbsp;所属公司：{op.city}</div> :
                    <div className="text-danger">
                        您还没有设置姓名及城市，
                        <A className="btn btn-link text-primary" onClick={this.setting}>请设置</A>
                    </div>
            ) : null}
        </div>
    }

    componentDidMount() {
        this.load();
    }

}

class AreaContractGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {
                    field: 'area_id', title: '场地编号', render: value => {
                        return <A onClick={this.show.bind(this, value)}>{value}</A>
                    }
                },
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
                {
                    field: 'area_id', title: '投放日期', render: value => {
                        const areaCreateTimeMap = this.state.areaCreateTimeMap;
                        if (areaCreateTimeMap && type(areaCreateTimeMap[value]) === 'Number') {
                            return new Date(areaCreateTimeMap[value] * 1000).format('yyyy-MM-dd');
                        }
                    }
                },
                {
                    field: 'area_id', title: '投放数量（台）', render: value => {
                        const countGroupArea = this.state.countGroupArea;
                        if (countGroupArea && type(countGroupArea[value]) === 'Number') {
                            return countGroupArea[value];
                        }
                    }
                },
                {
                    field: 'area_id', title: '运营状态', render: value => {
                        const areaMapOptions = this.state.areaMapOptions;
                        if (areaMapOptions && areaMapOptions.get(value)) {
                            let area = areaMapOptions.get(value);
                            if (area.status == -1) {
                                return <span className="text-danger">已下线</span>;
                            } else if (area.status == -2) {
                                return <span className="text-warning">待运营</span>;
                            } else {
                                return <span className="text-success">正常</span>;
                            }
                        }
                    }
                },
                {field: 'saler_city', title: '所属公司'},
                {field: 'saler', title: '销售人员'},
                {field: 'customer', title: '客户公司名称'},
                {
                    field: 'account_ratio', title: '分账比例', render: (value, row) => {
                        let arr = [];
                        if (row.range_ratio_list && row.range_ratio_list.length > 0) {
                            row.range_ratio_list.map(range_ratio => {
                                arr.push(<div>
                                    {type(range_ratio.lte, 'Number') ? range_ratio.lte / 100 : null}
                                    ~
                                    {type(range_ratio.gte, 'Number') ? range_ratio.gte / 100 : null}
                                    :
                                    {type(range_ratio.account_ratio, 'Number') ? `${range_ratio.account_ratio}%` : null}
                                </div>);
                            });
                        }
                        if (type(value) === 'Number') {
                            arr.push(<div>{`${value}%`}</div>);
                        }
                        return arr;
                    }
                },
                // {field: 'bank_account_name', title: '客户银行付款账户'},
                // {field: 'bank_account', title: '客户银行付款帐号'},
                // {field: 'bank_branch', title: '客户银行支行信息'},
                {
                    field: 'create_time', title: '创建日期', width: 120, render: value => {
                        return type(value) === 'Number' ? new Date(value * 1000).format('yyyy-MM-dd') : value;
                    }
                },
                {field: 'remark', title: '备注'},
                {
                    field: 'status', title: '状态', render: value => {
                        if (value === 0) {
                            return <span className="text-warning">待审核</span>
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
                    title: authMapOptions.get(finalAuthMap.area_contract_verify) ?
                        <button className="btn btn-sm btn-success m-1"
                                onClick={this.createAreaContract}>新建</button> : '操作',
                    render: (value, row) => {
                        return [
                            row.status != 1 && row.status != -2 && authMapOptions.get(finalAuthMap.area_contract_saler) ?
                                <button className="btn btn-sm btn-primary m-1"
                                        onClick={this.update.bind(this, value)}>编辑</button> : null,
                            authMapOptions.get(finalAuthMap.area_contract_verify) ?
                                <button className="btn btn-sm btn-success m-1"
                                        onClick={this.verify.bind(this, value)}>审核</button> : null,
                            row.status == 1 && authMapOptions.get(finalAuthMap.area_contract_verify) ?
                                <button className="btn btn-sm btn-success m-1"
                                        onClick={this.reckon.bind(this, value)}>生成对账单</button> : null,
                            row.status == 1 && authMapOptions.get(finalAuthMap.area_bill) ?
                                <button className="btn btn-sm btn-success m-1"
                                        onClick={this.billList.bind(this, value)}>本年历史账单</button> : null,

                        ]
                    }
                },
            ],
        };
    }

    billList = (area_id) => {
        Modal.open(<AreaBillGridModal queryParams={{area_id, year: new Date().getFullYear()}}></AreaBillGridModal>);
    };

    show = (area_id) => {
        Modal.open(<AreaContractModal area_id={area_id} onSuccess={this.load}></AreaContractModal>);
    };

    update = (area_id) => {
        Modal.open(<AreaContractModal update area_id={area_id} onSuccess={this.load}></AreaContractModal>);
    };

    verify = (area_id) => {
        Modal.open(<AreaContractModal verify area_id={area_id} onSuccess={this.load}></AreaContractModal>);
    };
    reckon = (area_id) => {
        Modal.open(<YearMonthSelectModal onSuccess={(year, month) => {
            request({
                url: `/api/area_contract/${area_id}/reckon`, method: 'post', loading: true,
                data: {year, month},
                success: resp => {
                    Message.msg('操作成功');
                }
            });
        }}></YearMonthSelectModal>);
    };

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
                    countGroupArea: resp.data.countGroupArea,
                    areaCreateTimeMap: resp.data.areaCreateTimeMap,
                });
            }
        });
    };

    render() {
        return <Table columns={this.state.columns} data={this.state.data}></Table>
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

    download = () => {
        let queryParams = {
            customer: this.refs.customer.value,
        };
        queryParams.download = true;
        window.open(`/api/area_contract/search?${queryString(queryParams)}`);
    };


    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                客户公司名称：
                <input ref="customer" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="button" className="btn btn-sm btn-success ml-1" onClick={this.download}>下载</button>
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