class CapsuleGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [


                GridUtils.mkBaseColumn('id', '硬件设备ID'),
                GridUtils.mkBaseColumn('city', '城市'),
                GridUtils.mkBaseColumn('area_id', '场地编号'),
                GridUtils.mkBaseColumn('area_title', '场地名称'),
                GridUtils.mkBaseColumn('capsule_id', '头等舱编号'),
                GridUtils.mkOptionColumn('device_version', '硬件设备版本', DeviceVersionOption),
                GridUtils.mkBaseColumn('area_id', '场地编号'),
                GridUtils.mkBaseColumn('area_id', '城市', value => this.state.areaMapOptions.getField(value, 'city')),
                GridUtils.mkBaseColumn('area_id', '场地名称', value => this.state.areaMapOptions.getField(value, 'title')),
                GridUtils.mkDateColumn('create_time', '创建时间'),
                GridUtils.mkBaseColumn('belong', '归属状态'),
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
            url: `/api/device/search`, loading: true,
            data: this.state.queryParams,
            success: resp => {
                this.state.data = resp.data.deviceList || [];
                const capsuleList = resp.data.capsuleList || [];
                const areaList = resp.data.areaList || [];
                const areaMapOptions = new AreaMapOptions(areaList);
                this.state.data.forEach(item => {

                    capsuleList.forEach(capsule => {
                        if (capsule.device_id === item.id) {
                            item.capsule = capsule;
                            item.device_version = capsule.device_version;
                            item.capsule_id = capsule.capsule_id;
                            item.create_time = capsule.create_time;
                            const area = areaMapOptions.get(capsule.area_id);
                            if (area) {
                                item.area = area;
                                item.area_id = area.area_id;
                                item.area_title = area.title;
                                item.city = area.city;
                            }
                        }
                    });

                });
                console.log(this.state.data);
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
        this.search();
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));