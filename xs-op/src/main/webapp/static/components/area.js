class AreaModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            area_id: props.area_id,
            area: props.area,
            onSuccess: props.onSuccess,
            create: props.create || false,
            update: props.update || false,
        };
    }

    submit = () => {
        const {onSuccess, create, update} = this.state;
        let data = {
            area_id: this.refs.area_id.value,
            title: this.refs.title.value,
            city: this.refs.city.value,
            address: this.refs.address.value,
            contact: this.refs.contact.value,
            is_time_limit: this.refs.is_time_limit.value,
            notification: this.refs.notification.value,
            minute_start: this.refs.minute_start.value,
            rushHours: this.refs.rushHours.getData(),
            imgs: this.refs.imgs.getData(),
            status: this.refs.status.value,
            is_external: this.refs.is_external.value,
        };
        if (create) {
            request({
                url: `/api/area/create`, method: 'post', contentType: 'application/json', loading: true,
                data: JSON.stringify(data, nullStringReplacer),
                success: (resp) => {
                    if (resp.code == 0) {
                        Message.msg('保存成功');
                        this.close();
                        if (onSuccess) onSuccess();
                    }
                }
            });
        } else if (update) {
            request({
                url: `/api/area/${data.area_id}/update`, method: 'post', contentType: 'application/json', loading: true,
                data: JSON.stringify(data, nullStringReplacer),
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
        const {create, update} = this.state;
        if (create || update) {
            return [
                <button type="button" className="btn btn-link text-primary" onClick={this.submit}>保存</button>,
                <button type="button" className="btn btn-link text-secondary" onClick={this.close}>取消</button>,
            ];
        } else {
            return <button type="button" className="btn btn-link text-secondary" onClick={this.close}>关闭</button>;
        }
    };
    renderBody = () => {
        const {create, update, show} = this.state;
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
                        <input ref="title" type="text" disabled={show} readOnly={show}
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>城市</th>
                    <td>
                        <input ref="city" type="text" readOnly={true} disabled={true} className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>地址</th>
                    <td>
                        <input ref="address" type="text" disabled={show} readOnly={show}
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>最少时长（分钟）</th>
                    <td>
                        <input ref="minute_start" type="text" disabled={show} readOnly={show}
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>联系方式</th>
                    <td>
                        <input ref="contact" type="text" disabled={show} readOnly={show}
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>提醒文案</th>
                    <td>
                        <textarea ref="notification" disabled={show} readOnly={show}
                                  className="form-control"></textarea>
                    </td>
                </tr>
                <tr>
                    <th>限时标记</th>
                    <td>
                        <select ref="is_time_limit" disabled={show} readOnly={show}
                                className="form-control">
                            {TimeLimitOption.map((option) => {
                                return <option value={option.value}>{option.text}</option>
                            })}
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>图片URL</th>
                    <td>
                        <ListEditor ref="imgs" disabled={show} readOnly={show}
                                    itemRender={(item, index, itemUpdate) => {
                                        return [<img src={`${item}_227`} alt=""/>,
                                            <input type="text" className="form-control" value={item} onChange={(e) => {
                                                itemUpdate(e.target.value);
                                            }}/>,
                                            <FileUploadButton onSuccess={(url) => {
                                                itemUpdate(url);
                                            }}>上传</FileUploadButton>
                                        ];
                                    }}></ListEditor>
                    </td>
                </tr>
                <tr>
                    <th>高峰时段</th>
                    <td>
                        <ListEditor ref="rushHours" disabled={show} readOnly={show}
                                    itemRender={(item, index, itemUpdate) => {
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
                        <select ref="is_external" disabled={show} readOnly={show}
                                className="form-control">
                            <option value="0">否</option>
                            <option value="1">是</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>状态</th>
                    <td>
                        <select ref="status" disabled={show} readOnly={show}
                                className="form-control">
                            {AreaStatusOption.map((option) => {
                                return <option value={option.value}>{option.text}</option>;
                            })}
                        </select>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>;
    };
    setView = (area) => {
        if (area) {
            if (type(area.area_id) == 'Number') this.refs.area_id.value = area.area_id;
            if (type(area.title) == 'String') this.refs.title.value = area.title;
            if (type(area.city) == 'String') this.refs.city.value = area.city;
            if (type(area.address) == 'String') this.refs.address.value = area.address;
            if (type(area.contact) == 'String') this.refs.contact.value = area.contact;
            if (type(area.notification) == 'String') this.refs.notification.value = area.notification;
            if (type(area.status) == 'Number') this.refs.status.value = area.status;
            if (type(area.is_external) == 'Number') this.refs.is_external.value = area.is_external;
            if (type(area.is_time_limit) == 'Number') this.refs.is_time_limit.value = area.is_time_limit;
            if (type(area.minute_start) == 'Number') this.refs.minute_start.value = area.minute_start;
            this.refs.imgs.setData(area.imgs);
            this.refs.rushHours.setData(area.rushHours);
        }
    };

    componentWillMount() {
        const {create, update} = this.state;
        this.state.show = !(create || update);
    }

    componentDidMount() {
        super.componentDidMount();
        const {area_id, area} = this.state;
        if (area) {
            this.setView(area);
        } else if (area_id) {
            request({
                url: '/api/area/' + area_id, loading: true,
                success: (resp) => {
                    this.setView(resp.data.area);
                }
            });
        }
    }
}

AreaModal.show = function (area_id) {
    Modal.open(<AreaModal area_id={area_id}></AreaModal>);
};

class AreaIdCreateModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {};
    }

    renderHeader = () => {
        return '创建场地';
    };

    renderBody = () => {
        const {cityList} = this.props;
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th>城市</th>
                <td>
                    <div className="row">
                        <div className="col-sm-6">
                            <input ref="city" readOnly={true} disabled={true} type="text" className="form-control"/>
                        </div>
                        <div className="col-sm-6">
                            <button type="button" className="btn btn-sm btn-primary m-1"
                                    onClick={this.selectCity.bind(this, false)}>选择城市
                            </button>
                            <button type="button" className="btn btn-sm btn-primary m-1"
                                    onClick={this.selectCity.bind(this, true)}>更多城市
                            </button>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <th>场地编号前四位</th>
                <td>
                    <input ref="area_id_4" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>场地编号后三位</th>
                <td>
                    <input ref="area_id_3" type="text" className="form-control"/>
                </td>
            </tr>
            </tbody>
        </table>
    };

    renderFooter = () => {
        return [
            <button type="button" className="btn btn-link text-primary float-right" onClick={this.submit}>下一步</button>,
            <button type="button" className="btn btn-link text-secondary float-right" onClick={this.close}>取消</button>,
        ];
    };

    selectCity = (all) => {
        Modal.open(<SelectCityModal all={all} onSuccess={this.setCity}></SelectCityModal>);
    };

    setCity = (city) => {
        this.refs.city.value = city.city;
        this.refs.area_id_4.value = city.code;
    };

    submit = () => {

        if (!this.refs.city.value) return Message.msg('请选择城市');
        if (!this.refs.area_id_4.value) return Message.msg('请选择城市');
        if (!this.refs.area_id_3.value) return Message.msg('请输入场地编号后三位');
        if (!/^\d{3}$/.test(this.refs.area_id_3.value)) return Message.msg('地编号后三位输入有误');

        let area_id = `${this.refs.area_id_4.value}${this.refs.area_id_3.value}` - 0;
        request({
            url: `/api/area/${area_id}/validateForCreate`, loading: true,
            success: () => {
                let area = {
                    area_id,
                    city: this.refs.city.value,
                };
                this.close();
                if (this.props.onSuccess) {
                    this.props.onSuccess(area);
                }
            }
        });
    };

}

class AreaGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {
                    field: 'area_id', title: '场地编号',
                    render: (value, row, index) => {
                        return <A onClick={AreaModal.show.bind(this, value)}>{value}</A>;
                    }
                },
                {field: 'title', title: '标题'},
                {field: 'city', title: '城市'},
                {field: 'address', title: '地址'},
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
                {field: 'contact', title: '联系方式'},
                {field: 'minute_start', title: '最少时长（分钟）'},
                {
                    field: 'is_time_limit', title: '限时标记', render: (value, row, index) => {
                        value = value || 0;
                        for (let i = 0; i < TimeLimitOption.length; i++) {
                            if (TimeLimitOption[i].value == value) {
                                return TimeLimitOption[i].text;
                            }
                        }
                        return value;
                    }
                },
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
                    field: 'is_external', title: '是否对外开放', render: (value, row, index) => {
                        return value == 1 ? <span className="text-success">是</span> :
                            <span className="text-danger">否</span>;
                    }
                },
            ],
            queryParams: props.queryParams,
        };
    }

    load = (queryParams) => {
        if (type(queryParams) == 'Object') this.state.queryParams = queryParams;
        request({
            url: '/api/area/search', loading: true,
            data: this.state.queryParams,
            success: (resp) => {
                if (resp.code == 0) {
                    this.state.data = resp.data.areaList;
                    this.setState({});
                } else {
                }
            }
        });
    };

    render() {
        const {columns, data} = this.state;
        return <Table columns={columns} data={data}></Table>;
    }

    componentDidMount() {
        this.load();
    }
}