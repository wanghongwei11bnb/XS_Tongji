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
            city: this.refs.city.value,
            address: this.refs.address.value,
            contact: this.refs.contact.value,
            notification: this.refs.notification.value,
            minute_start: this.refs.minute_start.value,
            // rushHours: this.refs.rushHours.value,
            location: {
                latitude: this.refs.latitude.value,
                longitude: this.refs.longitude.value,
            },
            area_img: this.refs.area_img.value,
            imgs: this.refs.imgs.getData(),
            // types: this.refs.types.value,
            status: this.refs.status.value - 0,
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
                <button type="button" className="btn btn-link text-primary" onClick={this.onSubmit}>保存</button>
                <button type="button" className="btn btn-link text-secondary" onClick={this.close}>取消</button>
            </span>;
    };
    renderBody = () => {
        const area = this.state.area || {};
        const cityList = this.props.cityList || [];
        return <div>
            <table className="table">
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
                        <select ref="city" className="form-control">
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
                    <th>高峰时段</th>
                    <td>
                        <input ref="rushHours" type="text" className="form-control"/>
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
                    <th>状态</th>
                    <td>
                        <select ref="status" className="form-control">
                            <option value="0">正常</option>
                            <option value="-1">已下架</option>
                        </select>
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
                </tbody>
            </table>
        </div>;
    };

    componentDidMount() {
        const {area} = this.state;
        if (area) {
            area.status = area.status || 0;
            this.refs.area_id.value = area.area_id;
            this.refs.title.value = area.title;
            this.refs.city.value = area.city;
            this.refs.address.value = area.address;
            this.refs.status.value = area.status;
            this.refs.rushHours.value = area.rushHours ? JSON.stringify(area.rushHours) : null;
            this.refs.contact.value = area.contact;
            this.refs.notification.value = area.notification;

            this.refs.area_img.value = area.area_img;
            this.refs.minute_start.value = area.minute_start;
            this.refs.imgs.setData(area.imgs);

            if (area.location) {
                this.refs.longitude.value = area.location.longitude;
                this.refs.latitude.value = area.location.latitude;
            }
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
                    return <button type="button" className="btn btn-link btn-sm"
                                   onClick={this.showArea.bind(this, value)}>{value}</button>;
                }
            },
            {field: 'title', title: '标题'},
            {field: 'city', title: '城市'},
            {field: 'address', title: '地址'},
            {
                field: 'status', title: '状态',
                render: (value, row, index) => {
                    return value == -1 ? <span className="text-danger">已下线</span> : '正常';
                }
            },
        ];
        this.state = {columns};
    }

    newArea = () => {
        ModalContainer.modal.open(<AreaModal cityList={this.state.cityList}></AreaModal>);
    };

    showArea = (area_id) => {
        request({
            url: '/api/area/' + area_id, loading: true,
            success: (resp) => {
                if (resp.code == 0) {
                    ModalContainer.modal.open(
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
            },
            success: (resp) => {
                if (resp.code == 0) {
                    this.state.data = resp.data.list;
                    grid.state.data = resp.data.list;
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
                <select ref="city" className="form-control d-inline-block mx-3 w-auto">
                    <option value="">-- 全部城市 --</option>
                    {cityList ? cityList.map((city) => {
                        return <option value={city.city}>{city.city}</option>
                    }) : null}
                </select>
                状态：
                <select ref="status" className="form-control d-inline-block mx-3 w-auto">
                    <option value="">-- 全部 --</option>
                    <option value="0">正常</option>
                    <option value="-1">已下线</option>
                </select>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="button" className="btn btn-sm btn-success ml-1 float-right" onClick={this.newArea}>添加场地
                </button>
            </div>

            <Datagrid ref="grid" columns={columns}></Datagrid>
            查询结果条数：{data ? data.length : null}
            <ModalContainer id="modal"></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.search();
        reqwest({
            url: '/api/cityList',
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

