class CapsuleGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                GridUtils.mkBaseColumn('capsule_id', '设备编号'),
                GridUtils.mkBaseColumn('device_id', '硬件设备ID'),
                GridUtils.mkOptionColumn('device_version', '硬件设备版本', DeviceVersionOption),
                GridUtils.mkBaseColumn('area_id', '场地编号'),
                GridUtils.mkBaseColumn('area_id', '城市', value => this.state.areaMapOptions.getField(value, 'city')),
                GridUtils.mkBaseColumn('area_id', '场地名称', value => this.state.areaMapOptions.getField(value, 'title')),
                GridUtils.mkOptionColumn('status', '设备状态', [{value: 1, text: '空闲', color: 'success'}, {value: 2, text: '占用', color: 'danger'}]),
                GridUtils.mkDateColumn('create_time', '创建时间'),
                GridUtils.mkBaseColumn('capsule_id', '归属状态', value => {
                    if (badCapsuleIdSet.indexOf(value) > -1) {
                        return <span className="text-danger">已销毁</span>;
                    } else if (giveCapsuleIdSet.indexOf(value) > -1) {
                        return <span className="text-warning">赠予场地</span>;
                    }
                }),

                GridUtils.mkBaseColumn('remark', '备注'),
            ],
            areaMapOptions: new AreaMapOptions(),
        };
    }

    render() {
        return <Table ref="table" columns={this.state.columns} data={this.state.data}></Table>;
    }


    load = (queryParams) => {
        if (queryParams) this.state.queryParams = queryParams;
        request({
            url: `/api/capsule/search`, loading: true,
            data: this.state.queryParams,
            success: resp => {
                this.state.data = resp.data.capsuleList || [];
                this.setState({});
            }
        });
    };


    componentDidMount() {
        request({
            url: `/api/area/search`, loading: true,
            success: resp => {
                this.state.areaMapOptions.putAll(resp.data.areaList);
                this.setState({});
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
        this.refs.grid.load({});
    };


    download = () => {
        window.open(`/api/capsule/search?${queryString({
            download: true,
        })}`)
    };


    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="button" className="btn btn-sm btn-success ml-1" onClick={this.download}>下载</button>
            </div>
            <div className="text-danger">最多返回{maxResultSize}条数据</div>
            <CapsuleGrid ref="grid"></CapsuleGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
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