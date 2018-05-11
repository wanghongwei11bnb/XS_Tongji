class FailureModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            isNew: props.isNew || false,
            failure: props.failure,
            onSuccess: props.onSuccess,
            uin: props.uin,
            phone: props.phone,
            area_id: props.area_id,
            capsule_id: props.capsule_id,
            booking_id: props.booking_id,
        };

    }

    fillByCapsuleId = () => {
        if (!this.refs.capsule_id.value) {
            return Message.msg('请输入头等舱编号');
        }
        request({
            url: `/api/capsule/${this.refs.capsule_id.value}`,
            success: (resp) => {
                if (resp.data.capsule) {
                    let capsule = resp.data.capsule;
                    this.refs.area_id.value = capsule.area_id;
                    if (capsule._area) {
                        let area = capsule._area;
                        this.refs.area_title.value = area.title;
                        this.refs.area_city.value = area.city;
                        this.refs.area_address.value = area.address;
                    }
                }
            }
        });
    };

    renderHeader = () => {
        return '故障报修';
    };
    renderFooter = () => {
        return <span className="float-right">
                <button type="button" className="btn btn-link text-primary" onClick={this.onSubmit}>保存</button>
                <button type="button" className="btn btn-link text-secondary" onClick={this.close}>取消</button>
            </span>;
    };

    onSubmit = () => {
        const {isNew, failure, onSuccess} = this.state;
        if (isNew) {
            request({
                url: `/api/failure/create`, method: 'post', loading: true, contentType: "application/json",
                data: JSON.stringify({
                    capsule_id: this.refs.capsule_id.value,
                    area_id: this.refs.area_id.value,
                    uin: this.refs.uin.value,
                    phone: this.refs.phone.value,
                    booking_id: this.refs.booking_id.value,
                    req_from: this.refs.req_from.value,
                    app_version: this.refs.app_version.value,
                    client_type: this.refs.client_type.value,
                    tags: this.refs.tags.getData(),
                    description: this.refs.description.value,
                    op_description: this.refs.op_description.value,
                    op_status: this.refs.op_status.value,
                }, nullStringReplacer),
                success: (resp) => {
                    Message.msg('保存成功');
                    this.close();
                    if (onSuccess) onSuccess();
                }
            });
        } else {
            request({
                url: `/api/failure/${failure.capsule_id}/${failure.create_time}/update/review`,
                method: 'post', loading: true, contentType: "application/json",
                data: JSON.stringify({
                    op_description: this.refs.op_description.value,
                    op_status: this.refs.op_status.value,
                }, nullStringReplacer),
                success: (resp) => {
                    Message.msg('保存成功');
                    this.close();
                    if (onSuccess) onSuccess();
                }
            });
        }
    };

    renderBody = () => {
        const {isNew, failure} = this.state;
        const readOnly = !isNew;
        const disabled = !isNew;
        return <div>
            <table className="table table-bordered">
                <tbody>
                <tr>
                    <td colSpan={2} className="text-center">头等舱信息</td>
                </tr>
                <tr>
                    <th>头等舱编号</th>
                    <td>
                        <div className="row">
                            <div className="col-sm-8">
                                <input ref="capsule_id" readOnly={readOnly} disabled={disabled} type="text"
                                       className="form-control"/>
                            </div>
                            <div className="col-sm-4">
                                <button disabled={disabled} type="button" className="btn btn-sm btn-success m-1 hide">选择
                                </button>
                                <button disabled={disabled} type="button" className="btn btn-sm btn-success m-1"
                                        onClick={this.fillByCapsuleId}>补全信息
                                </button>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>场地编号</th>
                    <td>
                        <input ref="area_id" readOnly={readOnly} disabled={disabled} type="text"
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>场地名称</th>
                    <td>
                        <input ref="area_title" readOnly={true} disabled={true} type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>城市</th>
                    <td>
                        <input ref="area_city" readOnly={true} disabled={true} type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>地址</th>
                    <td>
                        <input ref="area_address" readOnly={true} disabled={true} type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <td colSpan={2} className="text-center">用户信息</td>
                </tr>
                <tr>
                    <th>用户uin</th>
                    <td>
                        <input ref="uin" readOnly={readOnly} disabled={disabled} type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>用户手机号</th>
                    <td>
                        <input ref="phone" readOnly={readOnly} disabled={disabled} type="text"
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>订单编号</th>
                    <td>
                        <input ref="booking_id" readOnly={readOnly} disabled={disabled} type="text"
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <td colSpan={2} className="text-center">报修信息</td>
                </tr>
                <tr>
                    <th>报修时间</th>
                    <td>
                        <input ref="create_time" readOnly={true} disabled={true} type="datetime"
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>req_from</th>
                    <td>
                        <input ref="req_from" readOnly={readOnly} disabled={disabled} type="text"
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>app_version</th>
                    <td>
                        <input ref="app_version" readOnly={readOnly} disabled={disabled} type="text"
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>client_type</th>
                    <td>
                        <input ref="client_type" readOnly={readOnly} disabled={disabled} type="text"
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>client_version</th>
                    <td>
                        <input ref="client_version" readOnly={readOnly} disabled={disabled} type="text"
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>tags</th>
                    <td>
                        <ListEditor ref="tags" readOnly={readOnly}
                                    itemRender={(item, index, itemUpdate) => {
                                        return readOnly ?
                                            <span className="badge badge-pill badge-primary">{item}</span> :
                                            <input type="text" className="form-control" onChange={(e) => {
                                                itemUpdate(e.target.value)
                                            }}/>;
                                    }}></ListEditor>
                    </td>
                </tr>
                <tr>
                    <th>用户描述</th>
                    <td>
                        <textarea ref="description" readOnly={readOnly} disabled={disabled}
                                  className="form-control"></textarea>
                    </td>
                </tr>
                <tr>
                    <th>处理结果</th>
                    <td>
                        <textarea ref="op_description" className="form-control"></textarea>
                    </td>
                </tr>
                <tr>
                    <th>处理状态</th>
                    <td>
                        <select ref="op_status" className="form-control">
                            <option value="0">未处理</option>
                            <option value="1">处理中</option>
                            <option value="2">已解决</option>
                            <option value="-1">未解决</option>
                        </select>
                    </td>
                </tr>

                </tbody>
            </table>
        </div>;
    };

    reViewData = (failure) => {
        if (failure) {
            this.refs.capsule_id.value = failure.capsule_id;
            this.refs.area_id.value = failure.area_id;

            if (failure._area) {

                this.refs.area_title.value = failure._area.title;
                this.refs.area_city.value = failure._area.city;
                this.refs.area_address.value = failure._area.address;
            }
            this.refs.uin.value = failure.uin;
            this.refs.phone.value = failure.phone;
            this.refs.booking_id.value = failure.booking_id;

            this.refs.req_from.value = failure.req_from;
            this.refs.app_version.value = failure.app_version;
            this.refs.client_type.value = failure.client_type;
            this.refs.client_version.value = failure.client_version;


            this.refs.create_time.value = failure.create_time ? new Date(failure.create_time * 1000).format('yyyy-MM-dd hh:mm:ss') : failure.create_time;
            this.refs.tags.setData(failure.tags);
            this.refs.description.value = failure.description;


            this.refs.op_description.value = failure.op_description;
            this.refs.op_status.value = failure.op_status;
        }
    };

    componentDidMount() {
        super.componentDidMount();
        const {isNew, failure, uin, phone, area_id, capsule_id, booking_id} = this.state;
        if (failure) {
            this.reViewData(failure);
        } else if (isNew) {
            let url;
            if (booking_id) {
                url = `/api/failure/makeByBooking/${booking_id}`;
            } else if (capsule_id) {
                url = `/api/failure/makeByCapsule/${capsule_id}`;
            } else if (area_id) {
                url = `/api/failure/makeByArea/${area_id}`;
            } else if (uin) {
                url = `/api/failure/makeByUin/${uin}`;
            } else if (phone) {
                url = `/api/failure/makeByPhone/${phone}`;
            }
            if (url) {
                request({
                    url, loading: true, success: (resp) => {
                        this.reViewData(resp.data.failure);
                    }
                });
            }
        }
    }
}
