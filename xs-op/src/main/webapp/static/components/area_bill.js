class LetterModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            bill_id: props.bill_id || null,
        };
    }

    renderBody = () => {
        const {areaBill, area, areaContract} = this.state;
        return areaBill ?
            <div ref="box" className="p-3">
                <h1 className="text-center">企业对账函</h1>

                <p>
                    {areaContract ? areaContract.customer : null}
                </p>

                <p style={{textIndent: '2em'}}>
                    本公司与贵公司往来账，具体数据出自本公司帐薄，请贵公司核对，如无误，本公司将向贵公司付款，请您核对完回函，谢谢！
                </p>
                <table className="table table-sm table-bordered">
                    <tbody>
                    <tr>
                        <td>月份</td>
                        <td>空间名称</td>
                        <td>订单数（笔）</td>
                        <td>收款金额（元）</td>
                        <td>分账比例</td>
                        <td>分账金额（元）</td>
                    </tr>
                    <tr>
                        <td>{`${areaBill.year}年${areaBill.month}月`}</td>
                        <td>{area ? area.title : null}</td>
                        <td>{areaBill.booking_count}</td>
                        <td>{(areaBill.charge_price + areaBill.pay_price + (areaBill.month_card_price || 0)) / 100}</td>
                        <td>{`${areaBill.account_ratio}%`}</td>
                        <td>{areaBill.ratio_price / 100}</td>
                    </tr>
                    </tbody>
                </table>
                <div className="row">
                    <div className="col-sm-8">
                        <table className="table table-sm table-bordered">
                            <tbody>
                            <tr>
                                <th className="text-left" colSpan={2}>请核对银行付款账户信息</th>
                            </tr>
                            <tr>
                                <td>付款账户</td>
                                <td>{areaContract ? areaContract.bank_account_name : null}</td>
                            </tr>
                            <tr>
                                <td>付款帐号</td>
                                <td>{areaContract ? areaContract.bank_account : null}</td>
                            </tr>
                            <tr>
                                <td>支行信息</td>
                                <td>{areaContract ? areaContract.bank_branch : null}</td>
                            </tr>

                            </tbody>
                        </table>
                    </div>
                </div>
                <p className="text-right">北京享睡科技有限公司</p>
                <p className="text-right">{new Date().format('yyyy年MM月dd日')}</p>
            </div>
            : null;
    };

    load = () => {
        const {bill_id} = this.state;
        if (!bill_id) return Message.msg('请传入账单编号');
        request({
            url: `/api/area_bill/${bill_id}`, loading: true,
            success: resp => {
                this.setState({
                    areaBill: resp.data.areaBill,
                    area: resp.data.area,
                    areaContract: resp.data.areaContract,
                });
                setTimeout(() => {
                    html2canvas(this.refs.box).then((canvas) => {
                        this.refs.download.href = canvas.toDataURL('image/png');
                        this.refs.download.download = `${resp.data.area.title}_${resp.data.areaBill.year}_${resp.data.areaBill.month}.png`;
                    });
                }, 100);
            }
        });
    };

    renderFooter = () => {
        return [
            <a ref="download" className="btn btn-link text-success float-right">下载截图</a>,
            <A className="btn btn-link text-secondary float-right" onClick={this.close}>关闭</A>,
        ];
    };

    componentDidMount() {
        super.componentDidMount();
        this.refs.download.href = 'javascript:void(0);';
        this.load();
    }
}

class BillStatusUpdateModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            bill_id: props.bill_id || null,
        };
    }

    renderBody = () => {
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th>账单状态</th>
                <td>
                    <select ref="status" className="form-control">
                        <option value={0}>未结算</option>
                        <option value={1}>已结算</option>
                    </select>
                </td>
            </tr>
            </tbody>
        </table>
    };

    load = () => {
        const {bill_id} = this.state;
        if (!bill_id) return Message.msg('请传入账单编号');
        request({
            url: `/api/area_bill/${bill_id}`, loading: true,
            success: resp => {
                this.setState({
                    areaBill: resp.data.areaBill,
                    area: resp.data.area,
                    areaContract: resp.data.areaContract,
                });
            }
        });
    };

    submit = () => {
        request({
            url: `/api/area_bill/${this.state.bill_id}/update/status`, method: 'post', loading: true,
            data: {
                status: this.refs.status.value
            },
            success: resp => {
                Message.msg('操作成功');
                this.close();
                if (this.props.onSuccess) this.props.onSuccess();
            }
        });
    };

    renderFooter = () => {
        return [
            <A className="btn btn-link text-primary float-right" onClick={this.submit}>保存</A>,
            <A className="btn btn-link text-secondary float-right" onClick={this.close}>取消</A>,
        ];
    };

    componentDidMount() {
        super.componentDidMount();
        this.load();
    }
}

class AreaBillGrid extends Grid {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {
                    field: 'area_id', title: '账单月份', render: (value, row) => {
                        return `${row.year}年${row.month}月`;
                    }
                },
                {
                    field: 'area_id', title: '场地编号', render: value => {
                        return <A onClick={this.showAreaContractModal.bind(this, value)}>{value}</A>
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
                    field: 'area_id', title: '场地投放城市', render: value => {
                        const areaMapOptions = this.state.areaMapOptions;
                        if (areaMapOptions && areaMapOptions.get(value)) {
                            return areaMapOptions.get(value).city;
                        }
                    }
                },
                {
                    field: 'area_id', title: '客户公司名称', render: value => {
                        const areaContractMapOptions = this.state.areaContractMapOptions;
                        if (areaContractMapOptions && areaContractMapOptions.get(value)) {
                            return areaContractMapOptions.get(value).customer;
                        }
                    }
                },
                {field: 'booking_count', title: '订单数量（笔）'},
                {
                    field: 'pay_price', title: '收款金额（元）', render: (value, row) => {
                        return (
                            (type(row.charge_price) === 'Number' ? row.charge_price : 0)
                            + (type(row.pay_price) === 'Number' ? row.pay_price : 0)
                            + (type(row.month_card_price) === 'Number' ? row.month_card_price : 0)
                        ) / 100;
                    }
                },
                {
                    field: 'account_ratio', title: '分账比例', render: value => {
                        if (type(value) === 'Number') {
                            return `${value}%`;
                        }
                    }
                },
                {
                    field: 'ratio_price', title: '分账金额（元）', render: value => {
                        if (type(value) === 'Number') {
                            return value / 100;
                        }
                    }
                },
                {
                    field: 'update_time', title: '账单生成时间', render: value => {
                        if (type(value) === 'Number') {
                            return new Date(value * 1000).format();
                        }
                    }
                },
                {
                    field: 'status', title: '状态', render: value => {
                        if (value == 1) {
                            return <span className="text-success">已付款</span>;
                        } else {
                            return <span className="text-danger">未付款</span>;
                        }
                    }
                },
                {
                    field: 'bill_id', title: '操作', render: (value, row) => {
                        if (value == 1) {
                        } else {
                            return [
                                <button className="btn btn-sm btn-primary m-1"
                                        onClick={this.updateStatus.bind(this, value)}>修改状态</button>,
                                <button className="btn btn-sm btn-success m-1"
                                        onClick={this.openLetter.bind(this, value)}>制作对账函</button>,
                                <button className="btn btn-sm btn-success m-1"
                                        onClick={this.download.bind(this, row.area_id, row.year, row.month)}>下载订单</button>,
                            ];
                        }
                    }
                },
            ],
        };
    }

    download = (area_id, year, month) => {
        let queryParams = {year, month};
        queryParams.download = true;
        window.open(`/api/area_contract/${area_id}/reckon/download?${queryString(queryParams)}`)
    };

    showAreaContractModal = (area_id) => {
        Modal.open(<AreaContractModal area_id={area_id}></AreaContractModal>);
    };

    updateStatus = (bill_id) => {
        Modal.open(<BillStatusUpdateModal bill_id={bill_id} onSuccess={this.load}></BillStatusUpdateModal>);
    };

    openLetter = (bill_id) => {
        Modal.open(<LetterModal bill_id={bill_id}></LetterModal>);
    };

    load = (queryParams) => {
        if (queryParams) {
            this.state.queryParams = queryParams;
        }
        request({
            url: '/api/area_bill/search', loading: true,
            data: this.state.queryParams,
            success: resp => {
                this.setState({
                    data: resp.data.areaBillList,
                    areaMapOptions: resp.data.areaList ? new AreaMapOptions(resp.data.areaList) : null,
                    areaContractMapOptions: resp.data.areaContractList ? new AreaContractMapOptions(resp.data.areaContractList) : null,
                });
            }
        });

    };

    render() {
        return <Table columns={this.state.columns} data={this.state.data}></Table>
    }
}


class AreaBillGridModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {queryParams: props.queryParams};
    }

    renderBody = () => {
        return <AreaBillGrid ref="grid"></AreaBillGrid>;
    };

    componentDidMount() {
        super.componentDidMount();
        this.refs.grid.load(this.state.queryParams);
    }
}

