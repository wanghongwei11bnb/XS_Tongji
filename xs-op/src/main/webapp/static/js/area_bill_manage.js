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
                {field: 'booking_count', title: '订单数量'},
                {
                    field: 'final_price', title: '订单总金额', render: value => {
                        if (type(value) === 'Number') {
                            return value / 100;
                        }
                    }
                },
                {
                    field: 'charge_price', title: '收款金额（充值部分）', render: value => {
                        if (type(value) === 'Number') {
                            return value / 100;
                        }
                    }
                },
                {
                    field: 'pay_price', title: '收款金额（现金部分）', render: value => {
                        if (type(value) === 'Number') {
                            return value / 100;
                        }
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
                    field: 'ratio_price', title: '分账金额', render: value => {
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
                                        onClick={this.updateStatus.bind(this, value)}>修改状态</button>
                            ];
                        }
                    }
                },
            ],
        };
    }

    updateStatus = (bill_id) => {

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

class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }


    search = () => {
        this.refs.grid.load({});
    };


    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
            </div>
            <AreaBillGrid ref="grid"></AreaBillGrid>
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