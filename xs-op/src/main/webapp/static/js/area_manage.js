class Page extends React.Component {
    constructor(props) {
        super(props);
        const columns = [
            {
                field: 'area_id', title: '场地编号',
                render: (value, row, index) => {
                    return <A onClick={this.showArea.bind(this, value)}>{value}</A>;
                }
            },
            {field: 'title', title: '标题'},
            {field: 'city', title: '城市'},
            {field: 'address', title: '地址'},
            {
                field: 'is_external', title: '是否对外开放', render: (value, row, index) => {
                    return value == 1 ? <span className="text-success">是</span> :
                        <span className="text-danger">否</span>;
                }
            },
            {field: 'contact', title: '联系方式'},
            {field: 'minute_start', title: '最少时长（分钟）'},
            {
                field: 'rushHours', title: '高峰时段', render: (value, row, index) => {
                    return value ? value.map((item) => {
                        return item ? <div>开始时间：{item.start_time}，结束时间：{item.end_time}</div> : null;
                    }) : null;
                }
            },
            {
                field: 'location', title: '经纬度', render: (value, row, index) => {
                    return value ?
                        <div>经度：{value.longitude}，纬度：{value.latitude}</div>
                        : null;
                }
            },
            {
                field: 'status', title: '状态',
                render: (value, row, index) => {
                    if (value == -1) {
                        return <span className="text-danger">已下线</span>;
                    } else if (value == -2) {
                        return <span className="text-warning">待运营</span>;
                    } else {
                        return <span className="text-success">正常</span>;
                    }
                }
            },
            {
                title: <button type="button" className="btn btn-sm btn-success m-1"
                               onClick={this.newArea}>新建场地</button>,
                render: (value, row, index) => {
                    return [
                        <button type="button" className="btn btn-sm btn-primary m-1"
                                onClick={this.showCapsuleModal.bind(this, row.area_id)}>管理头等舱</button>,
                        <button type="button" className="btn btn-sm btn-primary m-1"
                                onClick={this.editTypes.bind(this, row)}>编辑类型</button>,
                    ];
                }
            },
        ];
        this.state = {columns};
    }


    showCapsuleModal = (area_id) => {
        Modal.open(<CapsuleManageModal area_id={area_id}></CapsuleManageModal>);
    };


    editTypes = (area) => {
        Modal.open(<CapsuleTypeGridModal area_id={area.area_id} onSuccess={this.load}></CapsuleTypeGridModal>);
    };

    newArea = () => {
        Modal.open(<AreaIdCreateModal onSuccess={(area) => {
            Modal.open(<AreaCreateModal area={area} onSuccess={this.load}></AreaCreateModal>);
        }}></AreaIdCreateModal>);
    };

    showArea = (area_id) => {
        request({
            url: '/api/area/' + area_id, loading: true,
            success: (resp) => {
                if (resp.code == 0) {
                    Modal.open(
                        <AreaUpdateModal update area_id={area_id} area={resp.data.area} cityList={this.state.cityList}
                                         onSuccess={this.load}></AreaUpdateModal>
                    );
                } else {
                }
            }
        });
    };


    search = () => {
        this.state.queryParams = {
            city: this.refs.city.value,
            status: this.refs.status.value,
        };
        this.load();
    };
    load = () => {
        const {grid} = this.refs;
        request({
            url: '/api/area/search', loading: true,
            data: {
                city: this.refs.city.value,
                status: this.refs.status.value,
                is_external: this.refs.is_external.value,
            },
            success: (resp) => {
                if (resp.code == 0) {
                    this.state.data = resp.data.areaList;
                    grid.state.data = resp.data.areaList;
                    this.setState({});
                    grid.setState({});
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
                    <option value="1">是</option>
                </select>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="button" className="btn btn-sm btn-success ml-1 float-right hide"
                        onClick={this.newArea}>添加场地
                </button>
            </div>
            <div className="text-danger">查询结果条数：{data ? data.length : null}（最多返回500条数据）</div>
            <Datagrid ref="grid" columns={columns}></Datagrid>
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

