class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {columns: []};
    }

    search = () => {
        this.state.queryParams = {};
        this.load();
    };
    load = () => {
        request({
            url: '/api/user/search', loading: true,
            data: this.state.queryParams,
            success: (resp) => {
                if (resp.code == 0) {
                    this.setState({data: resp.data.areaList});
                } else {
                }
            }
        });
    };

    render() {
        const {cityList, columns, data} = this.state;
        return <div className="container-fluid my-3">
            <div className="m-1">
                城市：
                <select ref="city" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value="">-- 全部城市 --</option>
                    {cityList ? cityList.map((city) => {
                        return <option value={city.city}>{city.city}</option>
                    }) : null}
                </select>
                状态：
                <select ref="status" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value="">-- 全部 --</option>
                    <option value="-1">已下线</option>
                    <option value="-2">待运营</option>
                </select>
                是否对外开放：
                <select ref="is_external" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value="">-- 全部 --</option>
                    <option value="0">否</option>
                    <option value="1">是</option>
                </select>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="button" className="btn btn-sm btn-success ml-1 float-right hide"
                        onClick={this.newArea}>添加场地
                </button>
            </div>
            <div className="text-danger">查询结果条数：{data ? data.length : null}（最多返回500条数据）</div>
            <Table columns={columns} data={data}></Table>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.search();
        reqwest({
            url: '/api/activeCityList',
            success: (resp) => {
                if (resp.code == 0) {
                    this.setState({cityList: resp.data.cityList});
                }
            }
        });
    }
}


ReactDOM.render(
    <Page/>
    , document.getElementById('root'));

