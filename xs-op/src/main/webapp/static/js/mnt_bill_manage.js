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

    download = () => {
        window.open(`/api/mnt/bill/search?${queryString({
            year: this.refs.year.value,
            month: this.refs.month.value,
            download: true
        })}`);
    };

    checkout = () => {
        Modal.open(<YearMonthSelectModal onSuccess={(year, month) => {
            request({
                url: `/api/mnt/bill/checkout`, method: 'post', loading: true,
                data: {year, month},
                success: resp => {
                    Message.msg('操作成功');
                }
            });
        }}></YearMonthSelectModal>);
    };

    preview = () => {
        Modal.open(<YearMonthSelectModal onSuccess={(year, month) => {
            request({
                url: `/api/mnt/bill/checkout`, method: 'post', loading: true,
                data: {year, month, preview: true},
                success: resp => {
                    Message.msg('操作成功');
                    this.refs.grid.setState({
                        data: resp.data.minitouBillList,
                        cityMapOptions: new CityMapOptions(resp.data.cityList),
                        areaMapOptions: new AreaMapOptions(resp.data.areaList),
                    });
                }
            });
        }}></YearMonthSelectModal>);
    };

    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                <select ref="year" className="form-control d-inline-block w-auto mx-1" onChange={this.setState.bind(this, {})}>
                    <option></option>
                    <option value={2018}>2018</option>
                    <option value={2019}>2019</option>
                </select>
                <select ref="month" className="form-control d-inline-block w-auto mx-1">
                    {(() => {
                        if (this.refs && this.refs.year && this.refs.year.value) {
                            if (this.refs.year.value == 2018) {
                                return <option value={12}>12</option>;
                            } else if (this.refs.year.value == 2019) {
                                let options = [];
                                for (let i = 1; i <= 12; i++) {
                                    options.push(<option value={i}>{i}</option>);
                                }
                                return options;
                            }
                        }
                    })()}
                </select>
                <button className="btn btn-sm btn-primary mx-1" onClick={this.search}>查看报表</button>
                <button className="btn btn-sm btn-success mx-1" onClick={this.download}>下载</button>
                {authMapOptions.get(finalAuthMap.auth_minitou_op) ?
                    [
                        <button className="btn btn-sm btn-danger mx-1" onClick={this.checkout}>生成账单(耗能／慎用)</button>,
                        <button className="btn btn-sm btn-warning mx-1" onClick={this.preview}>生成账单(预览)</button>,
                    ]
                    : null}
            </div>
            <MinitouBillGrid renderHeader={(headerHtml, data, columns) => {
                return [
                    headerHtml,
                    <span className="text-danger pl-3">本月净利润{(columns[2].total - columns[3].total - 22000).toFixed(2)}元（扣除固定成本2.2万元）</span>
                ];
            }} ref="grid"></MinitouBillGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));