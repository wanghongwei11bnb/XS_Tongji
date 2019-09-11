class WalletRecordGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'uin', title: 'uin'},
                {field: 'phone', title: '手机号'},
                {
                    field: 'price', title: '余额变动', render: value => {
                        if (type(value) === 'Number') {
                            if (value >= 0) {
                                return value / 100;
                            } else {
                                return <span className="text-danger">{value / 100}</span>
                            }
                        } else {
                            return value;
                        }
                    }
                },
                {field: 'subject', title: '业务类型'},
                {field: 'operator', title: '操作人'},
                {
                    field: 'create_time', title: '时间', render: value => {
                        return type(value) === 'Number' ? new Date(value * 1000).format('yyyy-MM-dd hh:mm') : value;
                    }
                },
            ],
        };
    }

    render() {
        const {columns, data} = this.state;
        return <Table columns={columns} data={data}></Table>
    }

    load = (queryParams) => {
        if (type(queryParams) == 'Object') this.state.queryParams = queryParams;
        request({
            url: '/api/user_wallet/search', loading: true,
            data: this.state.queryParams,
            success: (resp) => {
                if (resp.data) {
                    this.setState({data: resp.data.walletRecordList});
                }
            }
        });
    };
}

class UserWalletUpdateModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            uin: props.uin,
        };
    }

    renderHeader = () => {
        return '修改钱包金额'
    };

    renderBody = () => {
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th>变化金额(￥)</th>
                <td>
                    <input ref="disparity" type="text" className="form-control"/>
                    <div className="text-danger">
                        注意: 负数代表扣除钱包(例:-8) 正数代表添加钱包(例:8)
                    </div>
                </td>
            </tr>
            <tr>
                <th>原因</th>
                <td>
                    <select ref="subject" className="form-control">
                        <option value=""></option>
                        <option value="奖励赠送">奖励赠送</option>
                        <option value="回访奖励">回访奖励</option>
                        <option value="用户补偿">用户补偿</option>
                        <option value="体验金">体验金</option>
                        <option value="订单扣款">订单扣款</option>
                        <option value="包月赠送">包月赠送</option>
                        <option value="测试">测试</option>
                        <option value="损坏赔偿">损坏赔偿</option>
                        <option value="押金退款">押金退款</option>
                    </select>
                </td>
            </tr>
            </tbody>
        </table>
    };

    submit = () => {
        if (type(this.refs.disparity.value - 0) !== 'Number' || type(this.refs.disparity.value - 0) === 0) return Message.msg('变化金额输入有误');
        if (!this.refs.subject.value) return Message.msg('请选择原因');
        if (this.refs.disparity.value - 0 <= 0) return Message.msg('暂不支持扣款');
        request({
            url: `/api/user/${this.state.uin}/wallet/update/balance`,
            method: 'post', loading: true,
            data: {
                disparity: Math.floor((this.refs.disparity.value - 0) * 100),
                subject: this.refs.subject.value,
            },
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
        ]
    };


}

class UserWalletModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            uin: props.uin,
        };
    }

    renderHeader = () => '用户钱包信息';


    renderBody = () => {
        const {userWallet} = this.state;
        return userWallet ? <table className="table table-bordered">
            <tbody>
            <tr>
                <th>押金</th>
                <td width="50%">{type(userWallet.deposit) === 'Number' ? userWallet.deposit / 100 : 0}</td>
            </tr>
            <tr>
                <th>钱包总余额</th>
                <td>{type(userWallet.balance) === 'Number' ? userWallet.balance / 100 : 0}</td>
            </tr>
            <tr>
                <th>钱包充值余额</th>
                <td>{type(userWallet.charge) === 'Number' ? userWallet.charge / 100 : 0}</td>
            </tr>
            <tr>
                <th>钱包赠送余额</th>
                <td>{type(userWallet.bonus) === 'Number' ? userWallet.bonus / 100 : 0}</td>
            </tr>
            <tr>
                <th></th>
                <td>
                    <button className="btn btn-sm btn-primary m-1" onClick={this.updateBalance}>钱包充值／扣款</button>
                </td>
            </tr>
            </tbody>
        </table> : null;
    };

    updateBalance = () => {
        Modal.open(<UserWalletUpdateModal uin={this.state.uin} onSuccess={this.load}></UserWalletUpdateModal>);
    };

    load = () => {
        request({
            url: `/api/user/${this.state.uin}/wallet`, loading: true,
            success: resp => {
                this.setState({userWallet: resp.data.userWallet});
            }
        });
    };

    componentDidMount() {
        super.componentDidMount();
        this.load();
    }

}

class VerifyModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            uin: props.uin,
        };
    }

    renderBody = () => {
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th>用户编号</th>
                <td>
                    <input ref="uin" type="text" readOnly={true} disabled={true} className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>姓名</th>
                <td>
                    <input ref="name" type="text" readOnly={true} disabled={true} className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>身份证</th>
                <td>
                    <input ref="id_number" type="text" readOnly={true} disabled={true} className="form-control"/>
                </td>
            </tr>
            </tbody>
        </table>
    };


}

class UserGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'uin', title: '用户uin'},
                {field: 'phone', title: '手机号'},
                {
                    field: 'create_time', title: '注册日期', render: (value) => {
                        if (type(value) == 'Number') return new Date(value * 1000).format('yyyy-MM-dd');
                    }
                },
                {
                    field: 'id_verified', title: '是否已认证', render: value => {
                        if (value == 1) return <span className="text-success">已通过认证</span>;
                        else return <span className="text-danger">未通过认证</span>;
                    }
                },
                {field: 'fail_count', title: '认证失败次数'},
                {
                    field: 'fail_data', title: '认证失败数据', render: value => {
                        if (type(value) === 'Array') {
                            return value.map(item => <div>{item}</div>);
                        }
                    }
                },
                {field: 'invited_by', title: '是否被邀请', render: value => type(value, 'Number') && value > 0 ? '是：' + value : '否'},
                {
                    field: 'block', title: '黑名单', render: value => {
                        return value === 1 ? <span className="text-danger">已拉黑</span> : '';
                    }
                },
                {
                    field: 'uin', title: '操作', render: (value, row) => {
                        return [
                            <button className="btn btn-sm btn-primary m-1"
                                    onClick={this.showBookingList.bind(this, value)}>30日内订单</button>,
                            <button className="btn btn-sm btn-primary m-1"
                                    onClick={this.showUserWallet.bind(this, value)}>钱包／押金查看</button>,
                            <button className="btn btn-sm btn-primary m-1"
                                    onClick={this.showChargeRecordGridModal.bind(this, value)}>查看充值记录</button>,
                            <button className="btn btn-sm btn-primary m-1"
                                    onClick={this.showMonthCardModal.bind(this, value)}>查看月卡纪录</button>,
                            <button className="btn btn-sm btn-primary m-1"
                                    onClick={this.update_id_verified.bind(this, value)}>标记为认证成功</button>,
                            <button className="btn btn-sm btn-warning m-1"
                                    onClick={this.month_card_delete.bind(this, value)}>删除月卡</button>,
                            <button className="btn btn-sm btn-warning m-1"
                                    onClick={this.month_card_update.bind(this, value)}>修改月卡</button>,
                            <button className="btn btn-sm btn-warning m-1"
                                    onClick={this.wallet_clean.bind(this, value)}>清空钱包</button>,
                            <button className="btn btn-sm btn-danger m-1"
                                    onClick={this.updateBlock.bind(this, value, row.block === 1 ? 0 : 1)}>{row.block === 1 ? '移除黑名单' : '加入黑名单'}</button>,
                        ];
                    }
                }
            ],
            queryParams: props.queryParams,
        };
    }

    updateBlock = (uin, block) => {
        request({
            url: `/api/user/${uin}/update/block`, method: 'post', loading: true,
            data: {block},
            success: resp => {
                Message.msg('操作成功');
                this.load();
            }
        });
    };

    update_id_verified = (uin) => {
        Modal.open(<ConfirmModal ok={() => {
            request({
                url: `/api/user/${uin}/update/id_verified`, method: 'post', loading: true,
                data: {id_verified: 1},
                success: resp => {
                    Message.msg('操作成功');
                    this.load();
                }
            });
        }}>将此用户标记为身份认证通过？</ConfirmModal>);
    };

    month_card_delete = (uin) => {
        Modal.open(<ConfirmModal ok={() => {
            request({
                url: `/api/user/${uin}/monthCard/delete`, method: 'post', loading: true,
                success: resp => {
                    Message.msg('操作成功');
                    this.load();
                }
            });
        }}>将此用户的月卡删除？</ConfirmModal>);
    };

    wallet_clean = (uin) => {
        Modal.open(<ConfirmModal ok={() => {
            request({
                url: `/api/user/${uin}/wallet/clean`, method: 'post', loading: true,
                success: resp => {
                    Message.msg('操作成功');
                    this.load();
                }
            });
        }}>将此用户的钱包清空？</ConfirmModal>);
    };

    month_card_update = (uin) => {
        Modal.open(<CalendarModal onDateClick={(ymd) => {
            Modal.open(<ConfirmModal ok={() => {
                request({
                    url: `/api/user/${uin}/monthCard/appendTo`, method: 'post', loading: true,
                    data: {date: ymd.toDate().format('yyyy-MM-dd')},
                    success: resp => {
                        Message.msg('操作成功');
                        this.load();
                    }
                });
            }}>将此用户的月卡过期时间改为{ymd.toDate().format('yyyy-MM-dd')}？</ConfirmModal>);
        }}></CalendarModal>);
    };

    showChargeRecordGridModal = (uin) => {
        Modal.open(<ChargeRecordGridModal queryParams={{uin}}></ChargeRecordGridModal>);
    };

    showMonthCardModal = (uin) => {
        Modal.open(<MonthCardRecodeGridModal queryParams={{uin}}></MonthCardRecodeGridModal>);
    };


    showUserWallet = (uin) => {
        Modal.open(<UserWalletModal uin={uin}></UserWalletModal>);
    };

    showBookingList = (uin) => {
        Modal.open(<BookingGridModal queryParams={{
            uin,
            create_date_start: new Date(Date.now() - 1000 * 60 * 60 * 24 * 30).format('yyyy-MM-dd')
        }}></BookingGridModal>)

    };

    load = (queryParams) => {
        if (type(queryParams) == 'Object') this.state.queryParams = queryParams;
        request({
            url: '/api/user/search', loading: true,
            data: this.state.queryParams,
            success: (resp) => {
                if (resp.data) {
                    this.setState({data: resp.data.userInfoList});
                }
            }
        });
    };

    render() {
        const {columns, data} = this.state;
        return <Table columns={columns} data={data}></Table>;
    }

}

class UserGridModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            queryParams: props.queryParams,
        };
    }

    renderBody = () => {
        return <UserGrid ref="grid"></UserGrid>
    };

    componentDidMount() {
        super.componentDidMount();
        this.refs.grid.load(this.state.queryParams);
    }
}

function showUser(uin) {
    Modal.open(<UserGridModal queryParams={{uin}}></UserGridModal>)
}