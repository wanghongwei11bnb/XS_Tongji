class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'area_id', title: '场地编号'},
                {
                    field: 'area_id', title: '场地名称', render: (value) => {
                        return value && this.state.areaMapOptions && this.state.areaMapOptions.get(value) ?
                            this.state.areaMapOptions.get(value).title : null;
                    }
                },
                {
                    field: 'area_id', title: '城市', render: (value) => {
                        return value && this.state.areaMapOptions && this.state.areaMapOptions.get(value) ?
                            this.state.areaMapOptions.get(value).city : null;
                    }
                },
                {
                    field: 'area_id', title: '地址', render: (value) => {
                        return value && this.state.areaMapOptions && this.state.areaMapOptions.get(value) ?
                            this.state.areaMapOptions.get(value).address : null;
                    }
                },
                {field: 'capsule_id', title: '头等舱编号'},
                {field: 'device_id', title: '设备ID'},
                {
                    field: 'status', title: '门状态', render: (value, row) => {
                        if (type(value) === 'Number') {
                            return (value & 1) === 0 ? '关闭' : <span className="text-danger">打开</span>;
                        }
                    }
                },
                {
                    field: 'wifi_flag', title: 'WIFI状态', render: (value, row) => {
                        if (type(value) === 'Number') {
                            return value === 1 ? '链接成功' : <span className="text-danger">链接失败</span>;
                        }
                    }
                },
                {
                    field: 'status_text', title: '其他', render: value => {
                        return <span className="text-danger">{value}</span>;
                    }
                },
                {
                    field: 'update_time', title: '获取时间', render: value => {
                        return value ? new Date(value).format() : value;
                    }
                },
            ],
        };
    }

    load = () => {
        request({
            url: '/api/device_status/search', loading: true,
            success: resp => {
                this.setState({
                    data: resp.data.deviceStatusList,
                    areaMapOptions: new AreaMapOptions(resp.data.areaList),
                });
            }
        });
    };

    refresh = () => {
        request({
            url: '/api/device_status/refresh', method: 'post', loading: true,
            success: resp => {
                Modal.open(<AlertModal>
                    <h1>操作成功</h1>
                    <span className="text-danger">最新结果正在获取中，请等一等</span>
                </AlertModal>);
            }
        });
    };

    render() {
        const {columns, data} = this.state;
        return <div className="container-fluid my-3">
            <button className="btn btn-success btn-sm m-1" type="button" onClick={this.load}>刷新</button>
            <button className="btn btn-primary btn-sm m-1" type="button" onClick={this.refresh}>请求后台更新</button>
            <Table columns={columns} data={data}></Table>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.load();
    }

}

ReactDOM.render(<Page/>, document.getElementById('root'));