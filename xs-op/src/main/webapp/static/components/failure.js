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
            url: `/api/capsule/${this.refs.capsule_id.value}`, loading: true,
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

    showMonthCardModal = () => {
        if (!this.refs.uin.value) {
            return Message.msg('请输用户uin');
        }
        Modal.open(<MonthCardRecodeGridModal queryParams={{uin: this.refs.uin.value}}></MonthCardRecodeGridModal>);
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

    updateBooking = () => {
        let booking_id = this.refs.booking_id.value;
        if (!booking_id) return Message.msg('请输入订单编号');
        if (!/^\d+$/.test(booking_id)) return Message.msg('订单编号输入有误');
        Modal.open(<BookingUpdateModal booking_id={booking_id}></BookingUpdateModal>);
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
                    <td colSpan={2} className="text-center text-danger">头等舱信息</td>
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
                    <td colSpan={2} className="text-center text-danger">用户信息</td>
                </tr>
                <tr>
                    <th>用户uin</th>
                    <td>
                        <div className="row">
                            <div className="col-sm-8">
                                <input ref="uin" readOnly={readOnly} disabled={disabled} type="text"
                                       className="form-control"/>
                            </div>
                            <div className="col-sm-4">
                                <button type="button" className="btn btn-sm btn-success m-1"
                                        onClick={this.showMonthCardModal}>查看月卡纪录
                                </button>
                            </div>
                        </div>

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
                        <div className="row">
                            <div className="col-sm-8">
                                <input ref="booking_id" readOnly={readOnly} disabled={disabled} type="text"
                                       className="form-control"/>
                            </div>
                            <div className="col-sm-4">
                                <button type="button" className="btn btn-sm btn-success m-1"
                                        onClick={this.updateBooking}>更改订单
                                </button>
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td colSpan={2} className="text-center text-danger">报修信息</td>
                </tr>
                <tr>
                    <th>报修时间</th>
                    <td>
                        <input ref="create_time" readOnly={true} disabled={true} type="datetime"
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>订单来源</th>
                    <td>
                        <input ref="req_from" readOnly={readOnly} disabled={disabled} type="text"
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>App版本号</th>
                    <td>
                        <input ref="app_version" readOnly={readOnly} disabled={disabled} type="text"
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>手机类型</th>
                    <td>
                        <input ref="client_type" readOnly={readOnly} disabled={disabled} type="text"
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>手机型号</th>
                    <td>
                        <input ref="client_version" readOnly={readOnly} disabled={disabled} type="text"
                               className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>问题标签</th>
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
                    <td colSpan={2} className="text-center text-danger">处理结果</td>
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
            this.refs.capsule_id.value = typeValue(failure.capsule_id, 'Number');
            this.refs.area_id.value = typeValue(failure.area_id, 'Number');
            if (failure._area) {
                this.refs.area_title.value = typeValue(failure.title, 'String');
                this.refs.area_city.value = typeValue(failure.city, 'String');
                this.refs.area_address.value = typeValue(failure.address, 'String');
            }
            this.refs.uin.value = typeValue(failure.uin, 'Number');
            this.refs.phone.value = typeValue(failure.phone, 'String');
            this.refs.booking_id.value = typeValue(failure.booking_id, 'Number');

            this.refs.req_from.value = typeValue(failure.req_from, 'String');
            this.refs.app_version.value = typeValue(failure.app_version, 'String');
            this.refs.client_type.value = typeValue(failure.client_type, 'String');
            this.refs.client_version.value = typeValue(failure.client_version, 'String');

            this.refs.create_time.value = type(failure.create_time, 'Number') ? new Date(failure.create_time * 1000).format('yyyy-MM-dd hh:mm:ss') : failure.create_time;
            this.refs.tags.setData(failure.tags);
            this.refs.description.value = typeValue(failure.description, 'String');


            this.refs.op_description.value = typeValue(failure.op_description, 'String');
            this.refs.op_status.value = typeValue(failure.op_status, 'Number');
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
