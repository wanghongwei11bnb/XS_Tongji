class AreaContractModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            create: props.create || false,
            update: props.update || false,
            verify: props.verify || false,
            area_id: props.area_id,
        };
    }


    submit = () => {
        const {create, update, verify, area_id} = this.state;
        if (!this.refs.area_id.value) return Message.msg('请选择场地');

        let data = {
            area_id: this.refs.area_id.value,
            saler: this.refs.saler.value,
            saler_city: this.refs.saler_city.value,
            customer: this.refs.customer.value,
            customer_email: this.refs.customer_email.value,
            customer_contact: this.refs.customer_contact.value,
            account_ratio: this.refs.account_ratio.value,
            bank_account_name: this.refs.bank_account_name.value,
            bank_account: this.refs.bank_account.value,
            bank_branch: this.refs.bank_branch.value,
            remark: this.refs.remark.value,
            status: this.refs.status.value,
        };
        if (create) {
            request({
                url: '/api/area_contract/create/forVerify',
                contentType: 'application/json', method: 'post', loading: true,
                data: JSON.stringify(data, nullStringReplacer),
                success: resp => {
                    Message.msg('保存成功');
                    this.close();
                    if (this.props.onSuccess) this.props.onSuccess();
                }
            });
        } else if (update) {
            request({
                url: `/api/area_contract/${this.refs.area_id.value}/update/forSaler`,
                contentType: 'application/json', method: 'post', loading: true,
                data: JSON.stringify(data, nullStringReplacer),
                success: resp => {
                    Message.msg('保存成功');
                    this.close();
                    if (this.props.onSuccess) this.props.onSuccess();
                }
            });
        } else if (verify) {
            request({
                url: `/api/area_contract/${this.refs.area_id.value}/update/verify`,
                contentType: 'application/json', method: 'post', loading: true,
                data: JSON.stringify(data, nullStringReplacer),
                success: resp => {
                    Message.msg('保存成功');
                    this.close();
                    if (this.props.onSuccess) this.props.onSuccess();
                }
            });
        }

    };

    selectArea = () => {
        Modal.open(<AreaSelectModal onSuccess={area => {
            this.refs.area_id.value = area.area_id;
            this.refs.area_title.value = area.title;
            this.refs.area_city.value = area.city;
            this.refs.area_address.value = area.address;
        }}></AreaSelectModal>);
    };

    selectSaler = () => {
        Modal.open(<SelectSalerModal onSuccess={op => {
            this.refs.saler.value = op.fullname;
            this.refs.saler_city.value = op.city;
        }}></SelectSalerModal>);
    };

    renderBody = () => {
        const {create, update, verify, area_id} = this.state;
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <td colSpan={2} className="text-center text-danger">场地信息</td>
            </tr>
            <tr>
                <th>场地编号</th>
                <td>
                    <div className="row">
                        <div className="col-sm-6">
                            <input ref="area_id" readOnly={true} disabled={true} type="text" className="form-control"/>
                        </div>
                        <div className="col-sm-6">
                            <button disabled={!create} className="btn btn-success btn-sm m-1"
                                    onClick={this.selectArea}>选择场地
                            </button>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <th>场地名称</th>
                <td>
                    <input ref="area_title" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>投放城市</th>
                <td>
                    <input ref="area_city" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>投放地址</th>
                <td>
                    <input ref="area_address" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <td colSpan={2} className="text-center text-danger">销售人员</td>
            </tr>
            <tr>
                <th>销售人员</th>
                <td>
                    <div className="row">
                        <div className="col-sm-6">
                            <input ref="saler" type="text" readOnly={true} disabled={true} className="form-control"/>
                        </div>
                        <div className="col-sm-6">
                            <button disabled={!(create || verify)} className="btn btn-sm btn-success m-1"
                                    onClick={this.selectSaler}>选择
                            </button>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <th>所属公司</th>
                <td>
                    <input ref="saler_city" type="text" readOnly={true} disabled={true} className="form-control"/>
                </td>
            </tr>
            <tr>
                <td colSpan={2} className="text-center text-danger">客户信息</td>
            </tr>
            <tr>
                <th>客户公司名称</th>
                <td>
                    <input ref="customer" type="text" readOnly={!(create || update || verify)}
                           disabled={!(create || update || verify)}
                           className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>客户公司邮箱</th>
                <td>
                    <input ref="customer_email" type="text" readOnly={!(create || update || verify)}
                           disabled={!(create || update || verify)} className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>客户公司联系方式</th>
                <td>
                    <input ref="customer_contact" type="text" readOnly={!(create || update || verify)}
                           disabled={!(create || update || verify)} className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>客户银行付款账户</th>
                <td>
                    <input ref="bank_account_name" type="text" readOnly={!(create || update || verify)}
                           disabled={!(create || update || verify)}
                           className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>客户银行付款帐号</th>
                <td>
                    <input ref="bank_account" type="text" readOnly={!(create || update || verify)}
                           disabled={!(create || update || verify)}
                           className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>客户银行支行信息</th>
                <td>
                    <input ref="bank_branch" type="text" readOnly={!(create || update || verify)}
                           disabled={!(create || update || verify)}
                           className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>分账比例</th>
                <td>
                    <div className="row">
                        <div className="col-sm-6">
                            <input ref="account_ratio" type="text" readOnly={!(create || update || verify)}
                                   disabled={!(create || update || verify)} className="form-control"/>
                        </div>
                        <div className="col-sm-6 pt-1 pl-0">
                            %
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <td colSpan={2} className="text-center text-danger">审核状态</td>
            </tr>
            <tr>
                <th>创建日期</th>
                <td>
                    <input ref="create_time" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>备注</th>
                <td>
                    <textarea ref="remark" readOnly={!(create || update || verify)}
                              disabled={!(create || update || verify)}
                              className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>状态</th>
                <td>
                    <select ref="status" disabled={!verify} className="form-control">
                        {AreaContractStatusOption.map(option => {
                            return <option value={option.value}>{option.text}</option>
                        })}
                    </select>
                </td>
            </tr>
            </tbody>
        </table>
    };

    renderFooter = () => {
        const {create, update, verify, area_id} = this.state;
        if (create || update || verify) {
            return [
                <A className="btn btn-link text-primary float-right" onClick={this.submit}>保存</A>,
                <A className="btn btn-link text-secondary float-right" onClick={this.close}>取消</A>,
            ];
        } else {
            return <A className="btn btn-link text-secondary float-right" onClick={this.close}>关闭</A>;
        }

    };

    setView = (areaContract, area) => {
        if (areaContract) {
            if (type(areaContract.area_id) === 'Number') this.refs.area_id.value = areaContract.area_id;
            if (type(areaContract.customer) === 'String') this.refs.customer.value = areaContract.customer;
            if (type(areaContract.customer_email) === 'String') this.refs.customer_email.value = areaContract.customer_email;
            if (type(areaContract.customer_contact) === 'String') this.refs.customer_contact.value = areaContract.customer_contact;
            if (type(areaContract.bank_account_name) === 'String') this.refs.bank_account_name.value = areaContract.bank_account_name;
            if (type(areaContract.bank_account) === 'String') this.refs.bank_account.value = areaContract.bank_account;
            if (type(areaContract.bank_branch) === 'String') this.refs.bank_branch.value = areaContract.bank_branch;
            if (type(areaContract.account_ratio) === 'Number') this.refs.account_ratio.value = areaContract.account_ratio;
            if (type(areaContract.saler) === 'String') this.refs.saler.value = areaContract.saler;
            if (type(areaContract.saler_city) === 'String') this.refs.saler_city.value = areaContract.saler_city;
            if (type(areaContract.remark) === 'String') this.refs.remark.value = areaContract.remark;
            if (type(areaContract.status) === 'Number') this.refs.status.value = areaContract.status;
            if (type(areaContract.create_time) === 'Number')
                this.refs.create_time.value = new Date(areaContract.create_time * 1000).format('yyyy-MM-dd');
        }
        if (area) {
            if (type(area.title) === 'String') this.refs.area_title.value = area.title;
            if (type(area.city) === 'String') this.refs.area_city.value = area.city;
            if (type(area.address) === 'String') this.refs.area_address.value = area.address;
        }
    };


    componentDidMount() {
        super.componentDidMount();
        const {create, update, verify, area_id} = this.state;
        if (area_id) {
            request({
                url: `/api/area_contract/${area_id}`, loading: true,
                success: resp => {
                    this.setView(resp.data.areaContract, resp.data.area);
                }
            });
        }
    }
}

class SalerGrid extends Grid {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'fullname', title: '姓名'},
                {field: 'city', title: '城市'},
            ],
        };
    }

    componentDidMount() {
        request({
            url: '/api/saler/list', loading: true,
            success: resp => {
                this.setState({data: resp.data.salerList});
            }
        });
    }
}


class SelectSalerModal extends Modal {
    constructor(props) {
        super(props);
    }

    onSelect = (op) => {
        this.close();
        if (this.props.onSuccess) {
            this.props.onSuccess(op);
        }
    };

    renderBody = () => {
        return <SalerGrid handleColumns={(columns, grid) => {
            columns.push({
                title: '操作', render: (value, row) => {
                    return <button className="btn btn-sm btn-primary m-1"
                                   onClick={this.onSelect.bind(this, row)}>选择</button>
                }
            });
        }}></SalerGrid>
    };
}


class OperateAreaContractModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            create: props.create || false,
            update: props.update || false,
            verify: props.verify || false,
            area_id: props.area_id,
        };
    }

    renderBody = () => {
        const {create, update, verify, area_id} = this.state;
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <td colSpan={2} className="text-center text-danger">场地信息</td>
            </tr>
            <tr>
                <th>场地编号</th>
                <td>
                    <input ref="area_id" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>场地名称</th>
                <td>
                    <input ref="area_title" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>投放城市</th>
                <td>
                    <input ref="area_city" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>投放地址</th>
                <td>
                    <input ref="area_address" readOnly={true} disabled={true} type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <td colSpan={2} className="text-center text-danger">销售人员</td>
            </tr>
            <tr>
                <th>销售人员</th>
                <td>
                    <div className="row">
                        <div className="col-sm-6">
                            <input ref="saler" type="text" readOnly={true} disabled={true} className="form-control"/>
                        </div>
                        <div className="col-sm-6">
                            <button className="btn btn-sm btn-success m-1" onClick={this.selectSaler}>选择</button>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <th>所属公司</th>
                <td>
                    <input ref="saler_city" type="text" readOnly={true} disabled={true} className="form-control"/>
                </td>
            </tr>
            </tbody>
        </table>
    };

    submit = () => {
        if (!this.refs.area_id.value) return Message.msg('场地编号不能为空');
        if (!this.refs.saler.value) return Message.msg('销售不能为空');
        if (!this.refs.saler_city.value) return Message.msg('销售不能为空');
        request({
            url: '/api/area_contract/create/forOperate',
            contentType: 'application/json', method: 'post', loading: true,
            data: JSON.stringify({
                area_id: this.refs.area_id.value,
                saler: this.refs.saler.value,
                saler_city: this.refs.saler_city.value,
            }, nullStringReplacer),
            success: resp => {
                Message.msg('保存成功');
                this.close();
                if (this.props.onSuccess) this.props.onSuccess();
            }
        });
    };

    renderFooter = () => {
        return [
            <A className="btn btn-link text-primary float-right" onClick={this.submit}>保存</A>,
            <A className="btn btn-link text-secondary float-right" onClick={this.close}>取消</A>,
        ];
    };


    selectSaler = () => {
        Modal.open(<SelectSalerModal onSuccess={op => {
            this.refs.saler.value = op.fullname;
            this.refs.saler_city.value = op.city;
        }}></SelectSalerModal>);
    };

    componentDidMount() {
        super.componentDidMount();
        const {area_id} = this.state;
        if (area_id) {
            request({
                url: `/api/area/${area_id}`, loading: true,
                success: resp => {
                    let area = resp.data.area;
                    this.refs.area_id.value = area.area_id;
                    this.refs.area_title.value = area.title;
                    this.refs.area_city.value = area.city;
                    this.refs.area_address.value = area.address;
                }
            });
        }
    }
}