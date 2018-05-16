class PartnerModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            partner_id: props.partner_id,
            partner: props.partner,
            onSuccess: props.onSuccess,
        };
    }

    onSubmit = () => {
        const {partner_id, onSuccess} = this.state;
        if (!this.refs.phone) return Message.msg('手机号不能为空');
        let data = {
            phone: this.refs.phone.value,
            city: this.refs.city.value,
            email: this.refs.email.value,
            address: this.refs.address.value,
            theme: this.refs.theme.value,
            remark: this.refs.remark.value,
            passwd: this.refs.passwd.value,
        };
        if (partner_id) {
            data.id = partner_id;
            request({
                url: `/api/partner/${partner_id}/update`,
                method: 'post',
                contentType: 'application/json',
                loading: true,
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
                url: `/api/partner/add`, method: 'post', contentType: 'application/json', loading: true,
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

    initForm = (partner) => {
        this.refs.id.value = partner.id;
        this.refs.phone.value = partner.phone;
        this.refs.city.value = partner.city;
        this.refs.email.value = partner.email;
        this.refs.passwd.value = partner.passwd;
        this.refs.remark.value = partner.remark;
        this.refs.address.value = partner.address;
        this.refs.theme.value = partner.theme;
        this.refs.createTime.value = partner.createTime;
        this.refs.updateTime.value = partner.updateTime;
        this.refs.lastLoginTime.value = partner.lastLoginTime;
    };

    renderHeader = () => {
        return '用户信息';
    };

    renderBody = () => {
        const {cityList} = this.props;
        return <div>
            <table className="table ">
                <tbody>
                <tr>
                    <th width="150">用户ID</th>
                    <td>
                        <input ref="id" type="text" readOnly={true} disabled={true} className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>手机号</th>
                    <td>
                        <input ref="phone" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>邮箱</th>
                    <td>
                        <input ref="email" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>密码</th>
                    <td>
                        <input ref="passwd" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>城市</th>
                    <td>
                        <select ref="city" className="form-control">
                            <option value=""></option>
                            {cityList ? cityList.map((city) => {
                                return <option value={city.city}>{city.city}</option>
                            }) : null}
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
                    <th>创建时间</th>
                    <td>
                        <input ref="createTime" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>更新时间</th>
                    <td>
                        <input ref="updateTime" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>最后登录时间</th>
                    <td>
                        <input ref="lastLoginTime" type="text" className="form-control"/>
                    </td>
                </tr>

                <tr>
                    <th>主题</th>
                    <td>
                        <input ref="theme" type="text" className="form-control"/>
                    </td>
                </tr>

                <tr>
                    <th>备注</th>
                    <td>
                        <textarea ref="remark" className="form-control"/>
                    </td>
                </tr>
                </tbody>
            </table>

        </div>;
    };

    renderFooter = () => {
        return [
            <span className="float-right">
                <button type="button" className="btn btn-link text-primary" onClick={this.onSubmit}>保存</button>
                <button type="button" className="btn btn-link text-secondary" onClick={this.close}>取消</button>
            </span>
        ];
    };

    componentDidMount() {
        super.componentDidMount();
        const {partner_id, partner} = this.state;
        if (partner) {
            initForm(partner);
        } else if (partner_id) {
            request({
                url: `/api/partner/${partner_id}`, loading: true,
                success: (resp) => {
                    if (resp.code == 0) {
                        this.initForm(resp.data.partner);
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
                field: 'id', title: '用户编号', width: 100,
                render: (value, row, index) => {
                    return <a href="javascript:void(0);" onClick={this.openUpdateModal.bind(this, value)}>{value}</a>;
                }
            },
            {field: 'phone', title: '手机号', width: 100},
            {field: 'email', title: '邮箱', width: 100},
            {field: 'passwd', title: '密码', width: 100},
            {field: 'city', title: '城市', width: 100},
            {field: 'address', title: '地址', width: 100},
            {
                field: 'createTime', title: '创建时间', width: 100,
                render: (value, row, index) => {
                    if (value) {
                        return new Date(value).format('yyyy-MM-dd');
                    }
                }
            },
            {
                field: 'lastLoginTime', title: '最后登录时间', width: 100,
                render: (value, row, index) => {
                    if (value) {
                        return new Date(value).format('yyyy-MM-dd');
                    }
                }
            },
            {field: 'theme', title: '主题', width: 100},
            {field: 'remark', title: '备注', width: 100},
        ];

        this.state = {columns};
    }

    openAddModal = () => {
        ModalContainer.modal.open(<PartnerModal cityList={this.state.cityList} onSuccess={this.load}></PartnerModal>);
    };


    openUpdateModal = (partner_id) => {
        Modal.open(<PartnerModal cityList={this.state.cityList} partner_id={partner_id}
                                 onSuccess={this.load}></PartnerModal>);
    };


    search = () => {

        this.state.queryParams = {
            city: this.refs.city.value,
            phone: this.refs.phone.value,
        };
        this.load();
    };

    load = () => {
        const {grid} = this.refs;
        request({
            url: '/api/partner/search', loading: true,
            data: {
                city: this.refs.city.value,
                phone: this.refs.phone.value,
            },
            success: (resp) => {
                if (resp.code == 0) {
                    grid.state.data = resp.data.partnerList;
                    grid.setState({});
                } else {
                }
            }
        });
    };

    render() {
        const {cityList, columns} = this.state;
        return <div className="container-fluid my-3">
            <div className="m-1">
                城市：
                <select ref="city" className="form-control d-inline-block mx-3 w-auto">
                    <option value=""></option>
                    {cityList ? cityList.map((city) => {
                        return <option value={city.city}>{city.city}</option>
                    }) : null}
                </select>
                手机号：
                <input ref="phone" type="text" className="form-control d-inline-block mx-3 w-auto"/>
                <button type="submit" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="submit" className="btn btn-sm btn-success float-right" onClick={this.openAddModal}>新增用户
                </button>
            </div>


            <div className="table-responsive">
                <Datagrid ref="grid" columns={columns}></Datagrid>
            </div>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.search();
        request({
            url: '/api/activeCityList', loading: true,
            success: (resp) => {
                if (resp.code == 0) {
                    this.setState({cityList: resp.data.cityList});
                }
            }
        });
    }
}


ReactDOM.render(<Page/>, document.getElementById('root'));

