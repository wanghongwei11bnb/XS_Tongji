function showDeviceStatus(device_id) {
    request({
        url: `/api/device/${device_id}/status`, method: 'post', loading: true,
        success: resp => {
            resp = resp.data.resp;
            if (resp.ret == 0) {
                Modal.open(<AlertModal>
                    目前状态：{(resp.status & 1) == 0 ? '关闭' : '打开'}<br/>
                    wifi链接情况：{resp.wifi_flag == 1 ? '链接成功' : '链接失败'}<br/>
                    最后链接时间：{resp.localtime}<br/>
                </AlertModal>)
            } else {
                Message.msg(resp.err || '未知错误');
            }
        }
    });
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
                    device_version: this.refs.device_version.value,
                    is_downline: this.refs.is_downline.value,
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
                    device_version: this.refs.device_version.value,
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
                    <input ref="capsule_id" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>场地编号</th>
                <td>
                    <input ref="area_id" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>硬件设备ID</th>
                <td>
                    <input ref="device_id" readOnly={show} disabled={show} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>硬件设备版本</th>
                <td>
                    <select ref="device_version" className="form-control">
                        {DeviceVersionOption.map((option) => {
                            return <option value={option.value}>{option.text}</option>
                        })}
                    </select>
                </td>
            </tr>
            <tr>
                <th>设备状态</th>
                <td>
                    <select ref="status" disabled={show} className="form-control">
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
            this.refs.capsule_id.value = capsule.capsule_id || null;
            this.refs.area_id.value = capsule.area_id;
            this.refs.create_time.value = capsule.create_time ? new Date(capsule.create_time * 1000).format('yyyy-MM-dd') : null;
            this.refs.update_time.value = capsule.update_time ? new Date(capsule.update_time * 1000).format('yyyy-MM-dd') : null;
            this.refs.status.value = capsule.status;
            this.refs.device_id.value = capsule.device_id || null;
            this.refs.device_version.value = capsule.device_version;
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


class CapsuleManageModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'capsule_id', title: '头等舱编号'},
                {field: 'device_id', title: '硬件设备ID'},
                {
                    field: 'device_version', title: '硬件设备版本', render: (value) => {
                        for (let i = 0; i < DeviceVersionOption.length; i++) {
                            if (DeviceVersionOption[i].value == value) {
                                return DeviceVersionOption[i].text;
                            }
                        }
                    }
                },
                {field: 'area_id', title: '店铺id'},
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
                    field: 'is_downline', title: '是否标记下线', render: (value) => {
                        return value == 1 ? <span className="text-danger">已下线</span> : null;
                    }
                },
                {
                    title: <A className="btn btn-sm m-1 btn-success" onClick={this.openCreateModal}>创建头等舱</A>,
                    render: (value, row, index) => {
                        return [
                            <button type="button" className="btn btn-sm btn-primary m-1"
                                    onClick={this.showQrcode.bind(this, row.capsule_id)}>查看二维码</button>,
                            <button type="button" className="btn btn-sm m-1 btn-primary"
                                    onClick={this.openUpdateModal.bind(this, row.capsule_id)}>编辑</button>,
                            <button type="button" className="btn btn-sm m-1 btn-primary"
                                    onClick={showDeviceStatus.bind(this, row.device_id)}>查看硬件设备状态</button>,
                            <button type="button" className="btn btn-sm m-1 btn-success"
                                    onClick={this.makeFailureByCapsule.bind(this, row.capsule_id)}>创建报修</button>,

                        ];
                    }
                },
            ],
            area_id: props.area_id,
            data: props.data || [],
        };
    }


    showQrcode = (capsule_id) => {
        let qrid = UUID.get();
        Modal.open(<AlertModal>
            <Fixed onDidMount={() => {
                new QRCode(document.getElementById(qrid), {
                    text: `https://www.xiangshuispace.com/www/index.html?id=${capsule_id}`,
                    width: 300,
                    height: 300,
                    colorDark: "#000000",
                    colorLight: "#ffffff",
                    correctLevel: QRCode.CorrectLevel.H
                });
            }}>
                <div id={qrid}></div>
            </Fixed>
        </AlertModal>);

    };
    makeFailureByCapsule = (capsule_id) => {
        Modal.open(<FailureModal isNew={true} capsule_id={capsule_id}></FailureModal>);
    };


    openCreateModal = () => {
        const {area_id} = this.state;
        Modal.open(<CapsuleIdCreateModal area_id={area_id} onSuccess={(capsule) => {
            Modal.open(<CapsuleModal create capsule={capsule} onSuccess={this.load}></CapsuleModal>);
        }}></CapsuleIdCreateModal>);
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
                    this.setState({data: resp.data.capsuleList || []});
                }
            }
        });
    };

    componentDidMount() {
        super.componentDidMount();
        this.load();
    }
}


class CapsuleIdCreateModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            area_id: props.area_id,
        };
    }

    renderHeader = () => {
        return '创建头等舱';
    };

    renderBody = () => {
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th>场地编号</th>
                <td>
                    <input readOnly={true} disabled={true} type="text" className="form-control"
                           value={this.state.area_id}/>
                </td>
            </tr>
            <tr>
                <th>头等舱编号后三位</th>
                <td>
                    <input ref="capsule_id_3" type="text" className="form-control"/>
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

    submit = () => {

        if (!this.refs.capsule_id_3.value) return Message.msg('请输入头等舱编号后三位');
        if (!/^\d{3}$/.test(this.refs.capsule_id_3.value)) return Message.msg('头等舱编号后三位输入有误');

        let capsule_id = `${this.state.area_id}${this.refs.capsule_id_3.value}` - 0;
        request({
            url: `/api/capsule/${capsule_id}/validateForCreate`, loading: true,
            success: () => {
                let capsule = {
                    area_id: this.state.area_id,
                    capsule_id,
                };
                this.close();
                if (this.props.onSuccess) {
                    this.props.onSuccess(capsule);
                }
            }
        });
    };

}