class AllVerifyModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            verify_code: props.verify_code,
            create: props.create || false,
            update: props.update || false,
        };
    }


    renderBody = () => {
        const {verify_code, create, update} = this.state;
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th>兑换码</th>
                <td>
                    <StringInput ref="verify_code" type="text"
                                 disabled={!create}
                                 readOnly={!create}
                                 className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>优惠券类型</th>
                <td>
                    <select ref="type" className="form-control">
                        <option value=""></option>
                        <option value="0">现金红包</option>
                        <option value="1">满减券</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>红包金额</th>
                <td>
                    <PriceInput ref="red_envelope" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>最低消费金额</th>
                <td>
                    <PriceInput ref="min_price" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>减免金额</th>
                <td>
                    <PriceInput ref="cash" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>有效期</th>
                <td>
                    <div className="row">
                        <div className="col-6">
                            <DateInput ref="start_time_date" className="form-control"></DateInput>
                        </div>
                        <div className="col-6">
                            <DateInput ref="end_time_date" className="form-control"></DateInput>
                        </div>
                    </div>
                </td>
            </tr>
            </tbody>
        </table>
    };

    submit = () => {
        const {verify_code, create, update} = this.state;
        let data = {
            verify_code: this.refs.verify_code.getValue(),
            type: this.refs.type.value,
            red_envelope: this.refs.red_envelope.getValue(),
            min_price: this.refs.min_price.getValue(),
            cash: this.refs.cash.getValue(),
            start_time_date: this.refs.start_time_date.getValue(),
            end_time_date: this.refs.end_time_date.getValue(),
        };

        if (create) {
            request({
                url: '/api/all_verify/create', method: 'post', loading: true,
                data: data,
                success: resp => {
                    Message.msg('保存成功');
                    this.close();
                    if (this.props.success) this.props.success();
                }
            });
        } else if (update) {
            request({
                url: `/api/all_verify/${verify_code}/update`, method: 'post', loading: true,
                data: data,
                success: resp => {
                    Message.msg('保存成功');
                    this.close();
                    if (this.props.success) this.props.success();
                }
            });
        }
    };


    renderFooter = () => {
        return [
            <A className="btn btn-link mx-1 text-primary" onClick={this.submit}>保存</A>,
            <A className="btn btn-link mx-1 text-secondary" onClick={this.close}>取消</A>,
        ];
    };


    setView = (allVerify) => {
        if (allVerify) {
            this.refs.verify_code.setValue(allVerify.verify_code);
            this.refs.type.value = allVerify.type || 0;
            this.refs.red_envelope.setValue(allVerify.red_envelope);
            this.refs.min_price.setValue(allVerify.min_price);
            this.refs.cash.setValue(allVerify.cash);
            this.refs.start_time_date.setValue(allVerify.start_time ? allVerify.start_time * 1000 : null);
            this.refs.end_time_date.setValue(allVerify.end_time ? allVerify.end_time * 1000 : null);
        }

    };

    componentDidMount() {
        const {verify_code, create, update} = this.state;
        if (verify_code && update) {
            request({
                url: `/api/all_verify/${verify_code}`, loading: true,
                success: resp => {
                    this.setView(resp.data.allVerify);
                }
            });
        }
    }
}


class AllVerifyGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            queryParams: props.queryParams,
            columns: [
                {field: 'verify_code', title: '兑换码'},
                {
                    field: 'type', title: '优惠券类型', render: value => {
                        if (!value) {
                            return '现金红包';
                        } else if (value === 1) {
                            return '满减券';
                        }
                    }
                },
                {field: 'red_envelope', title: '红包金额', render: value => type(value, 'Number') ? value / 100 : null},
                {field: 'min_price', title: '最低消费金额', render: value => type(value, 'Number') ? value / 100 : null},
                {field: 'cash', title: '减免金额', render: value => type(value, 'Number') ? value / 100 : null},
                {
                    title: '有效期', render: (value, row) =>
                        <div>
                            {type(row.start_time, 'Number') ? new Date(row.start_time * 1000).format('yyyy-MM-dd') : null}
                            ～
                            {type(row.end_time, 'Number') ? new Date(row.end_time * 1000).format('yyyy-MM-dd') : null}
                            {!(type(row.end_time, 'Number') && row.end_time * 1000 >= Date.now()) ? <span className="text-danger">已过期</span> : null}
                        </div>
                },
                {
                    field: 'verify_code',
                    title: <button className="btn btn-sm btn-success" onClick={this.create}>创建</button>,
                    render: value => [
                        <button className="btn btn-sm m-1 btn-primary" onClick={this.update.bind(this, value)}>编辑</button>,
                        <button className="btn btn-sm m-1 btn-danger" onClick={this.delete.bind(this, value)}>删除</button>,
                    ]
                },
            ]
        };
    }


    load = (queryParams) => {
        if (queryParams) {
            this.state.queryParams = queryParams;
        }
        request({
            url: '/api/all_verify/search', loading: true,
            data: this.state.queryParams,
            success: resp => {
                this.setState({
                    allVerifyList: resp.data.allVerifyList
                });
            }
        });

    };

    create = () => {
        Modal.open(<AllVerifyModal create success={this.load}></AllVerifyModal>)
    };


    update = (verify_code) => {
        Modal.open(<AllVerifyModal verify_code={verify_code} update success={this.load}></AllVerifyModal>)
    };


    delete = (verify_code) => {
        Modal.open(<ConfirmModal ok={() => {
            request({
                url: `/api/all_verify/${verify_code}/delete`, method: 'post', loading: true,
                success: resp => {
                    Message.msg('删除成功！');
                    this.load();
                }
            });
        }}>
            确定删除？
        </ConfirmModal>)
    };


    render() {
        return <Table columns={this.state.columns} data={this.state.allVerifyList}></Table>
    }

}


class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    search = () => {
        this.refs.grid.load();
    };


    render() {
        const {cityList, columns, data} = this.state;
        return <div className="container-fluid my-3">
            <div className="m-1">
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
            </div>
            <AllVerifyGrid ref="grid"></AllVerifyGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.search();
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));