class AreaModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            area_id: props.area_id,
            area: props.area,
            onSuccess: props.onSuccess,
            cityList: props.cityList || [],
        };
    }

    onSubmit = () => {
        const {area_id, onSuccess} = this.state;
        let data = {
            area_id: this.refs.area_id.value,
            title: this.refs.title.value,
            // city: this.refs.city.value,
            address: this.refs.address.value,
            contact: this.refs.contact.value,
            notification: this.refs.notification.value,
            minute_start: this.refs.minute_start.value,
            rushHours: this.refs.rushHours.getData(),
            location: {
                latitude: this.refs.latitude.value,
                longitude: this.refs.longitude.value,
            },
            area_img: this.refs.area_img.value,
            imgs: this.refs.imgs.getData(),
            status: this.refs.status.value ? this.refs.status.value - 0 : null,
            is_external: this.refs.is_external.value - 0,
        };
        if (area_id) {
            request({
                url: `/api/area/${area_id}/update`, method: 'post', contentType: 'application/json', loading: true,
                data: JSON.stringify(data),
                success: (resp) => {
                    if (resp.code == 0) {
                        Message.msg('保存成功');
                        this.close();
                        if (onSuccess) onSuccess();
                    }
                }
            });
        } else {
            request({
                url: `/api/area/add`, method: 'post', contentType: 'application/json', loading: true,
                data: JSON.stringify(data),
                success: (resp) => {
                    if (resp.code == 0) {
                        Message.msg('保存成功');
                        this.close();
                        if (onSuccess) onSuccess();
                    }
                }
            });
        }


    };
    renderHeader = () => {
        return '场地信息';
    };
    renderFooter = () => {
        return <span className="float-right">
                <button type="button" className="btn btn-link text-primary"
                        onClick={this.onSubmit}>保存</button>
                <button type="button" className="btn btn-link text-secondary" onClick={this.close}>取消</button>
            </span>;
    };
    renderBody = () => {
        const area = this.state.area || {};
        const cityList = this.props.cityList || [];
        return <div>
            <table className="table table-bordered">
                <tbody>
                <tr>
                    <th>场地编号</th>
                    <td>
                        <input ref="area_id" type="text" disabled={true} readOnly={true} className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>标题</th>
                    <td>
                        <input ref="title" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>城市</th>
                    <td>
                        <select ref="city" disabled={true} className="form-control">
                            <option value=""></option>
                            {cityList.map((city) => {
                                return <option value={city.city}>{city.city}</option>
                            })}
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>地址</th>
                    <td>
                        <input ref="address" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>最少时长</th>
                    <td>
                        <input ref="minute_start" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>联系方式</th>
                    <td>
                        <input ref="contact" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>提醒文案</th>
                    <td>
                        <textarea ref="notification" className="form-control"></textarea>
                    </td>
                </tr>
                <tr>
                    <th>地图URL</th>
                    <td>
                        <input ref="area_img" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>图片URL</th>
                    <td>
                        <ListEditor ref="imgs" itemRender={(item, index, itemUpdate) => {
                            return [<img src={`${item}_227`} alt=""/>,
                                <input type="text" className="form-control" value={item} onChange={(e) => {
                                    itemUpdate(e.target.value)
                                }}/>];
                        }}></ListEditor>
                    </td>
                </tr>
                <tr>
                    <th>经纬度</th>
                    <td>
                        <div className="row">
                            <div className="col">
                                longitude
                                <input ref="longitude" type="text" className="form-control"/>
                            </div>
                            <div className="col">
                                latitude
                                <input ref="latitude" type="text" className="form-control"/>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>高峰时段</th>
                    <td>
                        <ListEditor ref="rushHours" itemRender={(item, index, itemUpdate) => {
                            return <div>
                                开始时间：<input type="text" className="form-control d-inline-block w-auto"
                                            value={item ? item.start_time : null}
                                            onChange={(e) => {
                                                itemUpdate({start_time: e.target.value, end_time: item.end_time})
                                            }}/>
                                结束时间：<input type="text" className="form-control d-inline-block w-auto"
                                            value={item ? item.end_time : null}
                                            onChange={(e) => {
                                                itemUpdate({end_time: e.target.value, start_time: item.start_time})
                                            }}/>
                            </div>;
                        }}></ListEditor>
                    </td>
                </tr>
                <tr>
                    <th>是否场地对外开放</th>
                    <td>
                        <select ref="is_external" className="form-control">
                            <option value="0">否</option>
                            <option value="1">是</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>状态</th>
                    <td>
                        <select ref="status" className="form-control">
                            <option value="">正常</option>
                            <option value="-1">已下架</option>
                            <option value="-2">待运营</option>
                        </select>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>;
    };

    componentDidMount() {
        super.componentDidMount();
        const {area} = this.state;
        if (area) {
            area.status = area.status || 0;
            area.is_external = area.is_external || 0;
            this.refs.area_id.value = area.area_id;
            this.refs.title.value = area.title;
            this.refs.city.value = area.city;
            this.refs.address.value = area.address;
            this.refs.status.value = area.status;
            this.refs.is_external.value = area.is_external;

            this.refs.contact.value = area.contact;
            this.refs.notification.value = area.notification;

            this.refs.area_img.value = area.area_img;
            this.refs.minute_start.value = area.minute_start;
            this.refs.imgs.setData(area.imgs);

            if (area.location) {
                this.refs.longitude.value = area.location.longitude;
                this.refs.latitude.value = area.location.latitude;
            }


            if (area.rushHours) {
                this.refs.rushHours.setData(area.rushHours);
            }


        }
    }
}

class CapsuleManageModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'capsule_id', title: '头等舱编号'},
                {field: 'device_id', title: '设备id'},
                {field: 'area_id', title: '店铺id'},
                {field: 'type', title: '设备类型'},
                {
                    field: 'status', title: '设备状态', render: (value) => {
                        switch (value) {
                            case 1:
                                return <span className="text-success">空闲</span>;
                            case 2:
                                return <span className="text-danger">占用</span>;
                            default:
                                break;
                        }
                    }
                },
                {
                    field: 'create_time', title: '创建时间', render: (value) => {
                        return value ? new Date(value * 1000).format('yyyy-MM-dd') : null;
                    }
                },
                {
                    field: 'update_time', title: '更新时间', render: (value) => {
                        return value ? new Date(value * 1000).format('yyyy-MM-dd') : null;
                    }
                },
                {
                    field: 'is_downline', title: '是否标记下线', render: (value) => {
                        return value == 1 ? <span className="text-danger">已下线</span> : null;
                    }
                },
                {
                    title: <A className="btn btn-sm m-1 btn-success" onClick={this.openCreateModal}>创建头等舱</A>,
                    render: (value, row, index) => {
                        return [
                            <button type="button" className="btn btn-sm m-1 btn-primary"
                                    onClick={this.openUpdateModal.bind(this, row.capsule_id)}>编辑</button>,
                            <button type="button" className="btn btn-sm m-1 btn-success"
                                    onClick={this.makeFailureByCapsule.bind(this, row.capsule_id)}>一键报修</button>,

                        ];
                    }
                },
            ],
            area_id: props.area_id,
            data: props.data || [],
        };
    }


    makeFailureByCapsule = (capsule_id) => {
        Modal.open(<FailureModal isNew={true} capsule_id={capsule_id}></FailureModal>);
    };


    openCreateModal = () => {
        Modal.open(<CapsuleModal create onSuccess={this.load}></CapsuleModal>);
    };


    openUpdateModal = (capsule_id) => {
        Modal.open(<CapsuleModal capsule_id={capsule_id} update onSuccess={this.load}></CapsuleModal>);
    };

    renderBody = () => {
        const {columns, data} = this.state;
        return <Table {...{columns, data}}></Table>
    };

    load = () => {
        const {area_id} = this.state;
        request({
            url: `/api/capsule/search`, loading: true,
            data: {area_id},
            success: (resp) => {
                if (resp.code == 0) {
                    this.setState({data: resp.data.list || []});
                }
            }
        });
    };

    componentDidMount() {
        super.componentDidMount();
        this.load();
    }
}


class CapsuleModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            capsule_id: props.capsule_id,
            create: props.create || false,
            update: props.update || false,
            show: props.show || false,
            capsule: props.capsule,
            onSuccess: props.onSuccess,
        };
    }


    submit = () => {
        const {capsule_id, create, update, show} = this.state;
        if (update) {
            request({
                url: `/api/capsule/${capsule_id}/update`,
                loading: true,
                method: 'post',
                contentType: 'application/json',
                data: JSON.stringify({
                    device_id: this.refs.device_id.value,
                    is_downline: this.refs.is_downline.value,
                    type: this.refs.type.value,
                    status: this.refs.status.value,
                }, nullStringReplacer),
                success: resp => {
                    Message.msg('保存成功');
                    this.close();
                    if (this.state.onSuccess) this.state.onSuccess();
                }
            });
        } else if (create) {
            request({
                url: `/api/capsule/create`, loading: true, method: 'post', contentType: 'application/json',
                data: JSON.stringify({
                    capsule_id: this.refs.capsule_id.value,
                    area_id: this.refs.area_id.value,
                    device_id: this.refs.device_id.value,
                    type: this.refs.type.value,
                    is_downline: this.refs.is_downline.value,
                    status: this.refs.status.value,
                }, nullStringReplacer),
                success: resp => {
                    Message.msg('保存成功');
                    this.close();
                    if (this.state.onSuccess) this.state.onSuccess();
                }
            });
        }
    };

    renderFooter = () => {
        return [
            <button type="button" className="btn btn-link btn-sm btn-primary float-right"
                    onClick={this.submit}>保存</button>,
            <button type="button" className="btn btn-link btn-sm btn-secondary float-right"
                    onClick={this.close}>取消</button>,
        ];
    };

    renderBody = () => {
        const {create, update, show} = this.state;
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th>头等舱编号</th>
                <td>
                    <input ref="capsule_id" readOnly={!create} disabled={!create} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>场地编号</th>
                <td>
                    <input ref="area_id" readOnly={!create} disabled={!create} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>类型</th>
                <td>
                    <input ref="type" readOnly={show} disabled={show} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>设备ID</th>
                <td>
                    <input ref="device_id" readOnly={show} disabled={show} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>设备状态</th>
                <td>
                    <select ref="status" disabled={show} className="form-control">
                        <option value=""></option>
                        <option value="1">空闲</option>
                        <option value="2">占用</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>创建时间</th>
                <td>
                    <input ref="create_time" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>更新时间</th>
                <td>
                    <input ref="update_time" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>是否标记下线</th>
                <td>
                    <select ref="is_downline" disabled={show} className="form-control">
                        <option value=""></option>
                        <option value="0">否</option>
                        <option value="1">是</option>
                    </select>
                </td>
            </tr>
            </tbody>
        </table>
    };

    reViewData = (capsule) => {
        if (capsule) {
            this.refs.capsule_id.value = capsule.capsule_id;
            this.refs.area_id.value = capsule.area_id;
            this.refs.create_time.value = capsule.create_time;
            this.refs.update_time.value = capsule.update_time;
            this.refs.status.value = capsule.status;
            this.refs.type.value = capsule.type;
            this.refs.device_id.value = capsule.device_id;
            this.refs.is_downline.value = capsule.is_downline;
        }
    };

    componentDidMount() {
        super.componentDidMount();
        const {capsule_id, capsule} = this.state;
        if (capsule) {
            this.reViewData(capsule);
        } else if (capsule_id) {
            request({
                url: `/api/capsule/${capsule_id}`, loading: true,
                success: resp => {
                    if (resp.code == 0 && resp.data.capsule) {
                        this.reViewData(resp.data.capsule);
                    }
                }
            });
        }
    }
}


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
            {field: 'minute_start', title: '最少时长'},
            {
                field: 'rushHours', title: '高峰时段', render: (value, row, index) => {
                    return value ? value.map((item) => {
                        return <div>开始时间：{item.start_time}，结束时间：{item.end_time}</div>
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
                render: (value, row, index) => {
                    return [
                        <button type="button" className="btn btn-sm btn-primary m-1"
                                onClick={this.showCapsuleModal.bind(this, row.area_id)}>管理头等舱</button>,
                        <button type="button" className="btn btn-sm btn-primary m-1"
                                onClick={this.editTypes.bind(this, row)}>编辑类型</button>
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
        Modal.open(<AreaModal cityList={this.state.cityList}></AreaModal>);
    };

    showArea = (area_id) => {
        request({
            url: '/api/area/' + area_id, loading: true,
            success: (resp) => {
                if (resp.code == 0) {
                    Modal.open(
                        <AreaModal area_id={area_id} area={resp.data.area} cityList={this.state.cityList}
                                   onSuccess={this.load}></AreaModal>
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
                    <option value="0">否</option>
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

