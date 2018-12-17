class MinitouBillGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {
                    field: 'bill_id', title: '账单月份', render: (value, row) => `${row.year}年${row.month}月`
                },
                {field: 'capsule_id', title: '设备编号'},
                {field: 'area_id', title: '场地名称', render: value => this.state.areaMapOptions.get(value) ? this.state.areaMapOptions.get(value).title : null},
                {field: 'area_id', title: '设备数量', render: value => this.state.countGroupArea[value] || null},
                {field: 'account_ratio', title: '场地分成比例', render: value => type(value, 'Number') ? `${value}%` : null},
                {field: 'final_price', title: '收入', render: value => type(value, 'Number') ? value / 100 : null},
                {field: 'ratio_price', title: '场地分成金额', render: value => type(value, 'Number') ? value / 100 : null},
                {field: 'rent_price', title: '利润收入（扣除租金）', render: value => type(value, 'Number') ? value / 100 : null},
                {field: 'other_price', title: '其他费用', render: value => type(value, 'Number') ? value / 100 : null},
                {field: 'net_price', title: '净利润', render: value => type(value, 'Number') ? value / 100 : null},
            ]
        };
    }


    load = (queryParams) => {
        if (queryParams) this.state.queryParams = queryParams;
        request({
            url: `/api/mnt/bill/checkout`, method: 'post', loading: true,
            data: this.state.queryParams,
            success: (resp) => {
                this.setState({
                    data: resp.data.minitouBillList,
                    countGroupArea: resp.data.countGroupArea,
                    cityMapOptions: new CityMapOptions(resp.data.cityList),
                    areaMapOptions: new AreaMapOptions(resp.data.areaList),
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
        this.refs.grid.load({
            year: this.refs.year.value,
            month: this.refs.month.value,
        });
    };

    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                <select ref="year" className="form-control d-inline-block w-auto mx-1">
                    <option value={2018}>2018</option>
                </select>
                <select ref="month" className="form-control d-inline-block w-auto mx-1">
                    {(() => {
                        let options = [];
                        for (let i = 1; i <= 12; i++) {
                            options.push(<option value={i}>{i}</option>)
                        }
                        return options;
                    })()}
                </select>
                <button className="btn btn-sm btn-primary mx-1" onClick={this.search}>查看报表</button>
            </div>
            <MinitouBillGrid ref="grid"></MinitouBillGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));