class AreaModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            action: props.action,
            area_id: props.area_id,
            area: props.area,
            onSuccess: props.onSuccess,
            cityList: props.cityList || [],
        };
    }

    onSubmit = () => {
        const {action, area_id, onSuccess} = this.state;
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
        const {action} = this.state;
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
                                                itemUpdate({
                                                    start_time: e.target.value,
                                                    end_time: (item || {}).end_time
                                                })
                                            }}/>
                                结束时间：<input type="text" className="form-control d-inline-block w-auto"
                                            value={item ? item.end_time : null}
                                            onChange={(e) => {
                                                itemUpdate({
                                                    end_time: e.target.value,
                                                    start_time: (item || {}).start_time
                                                })
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

class AreaCreateModal extends Modal {
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
        const { area_id, onSuccess} = this.state;
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
        return '新建场地';
    };
    renderFooter = () => {
        return <span className="float-right">
                <button type="button" className="btn btn-link text-primary"
                        onClick={this.onSubmit}>保存</button>
                <button type="button" className="btn btn-link text-secondary" onClick={this.close}>取消</button>
            </span>;
    };
    renderBody = () => {
        const {action} = this.state;
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
                                                itemUpdate({
                                                    start_time: e.target.value,
                                                    end_time: (item || {}).end_time
                                                })
                                            }}/>
                                结束时间：<input type="text" className="form-control d-inline-block w-auto"
                                            value={item ? item.end_time : null}
                                            onChange={(e) => {
                                                itemUpdate({
                                                    end_time: e.target.value,
                                                    start_time: (item || {}).start_time
                                                })
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
