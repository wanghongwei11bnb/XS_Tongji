class AuthsModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            onSuccess: props.onSuccess,
            username: props.username,
            auths: props.auths,
            authActive: [],
        };
    }

    submit = () => {
        let authArr = [];
        authList.map((auth) => {
            if (this.refs[auth] && this.refs[auth].checked) {
                authArr.push(auth);
            }
        });
        request({
            url: '/api/op/update/auths', method: 'post', loading: true,
            data: {
                username: this.state.username,
                auths: authArr.join(','),
            },
            success: resp => {
                Message.msg('保存成功');
                this.close();
                if (this.state.onSuccess) this.state.onSuccess();
            }
        });
    };

    renderBody = () => {
        return <div className="row px-3">
            {authList.map((auth) => {
                return <div className="col-sm-12 col-md-6 col-lg-4 form-check">
                    <input ref={auth} id={auth} className="form-check-input" type="checkbox"/>
                    <label className="form-check-label" htmlFor={auth}>{auth}</label>
                </div>

            })}
        </div>;
    };

    renderFooter = () => {
        return [
            <A className="btn btn-link text-primary float-right" onClick={this.submit}>保存</A>,
            <A className="btn btn-link text-secondary float-right" onClick={this.close}>取消</A>,
        ];
    };

    componentDidMount() {
        super.componentDidMount();
        request({
            url: '/api/op/get', loading: true,
            data: {username: this.state.username},
            success: resp => {
                let auths = resp.data.op.auths;
                if (auths) {
                    let authArr = auths.split(',');
                    if (authArr != null && authArr.length > 0) {
                        authArr.map((auth) => {
                            if (this.refs[auth]) {
                                this.refs[auth].checked = true;
                            }
                        });
                    }

                }
            }
        });
    }

}


class OpGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'username', title: '帐号'},
                {
                    field: 'auths', title: '权限', render: (value) => {
                        if (type(value) == 'String') {
                            let auths = value.split(",");
                            if (auths && auths.length > 0) {
                                return auths.map((auth) => {
                                    return <span className="badge badge-primary m-1">{auth}</span>;
                                });
                            }
                        }
                    }
                },
                {
                    field: 'username',
                    title: <button className="btn btn-sm btn-success" onClick={this.createOp}>创建用户</button>,
                    render: (value) => {
                        return [
                            <button className="btn btn-sm btn-primary m-1"
                                    onClick={this.updateAuths.bind(this, value)}>更改权限</button>,
                        ];
                    }
                },
            ],
        };
    }

    createOp = () => {
    };
    updateAuths = (username) => {
        Modal.open(<AuthsModal username={username} onSuccess={this.load}></AuthsModal>);
    };

    load = () => {
        request({
            url: '/api/op/list', loading: true,
            success: (resp) => {
                this.setState({data: resp.data.opList});
            }
        });
    };

    render() {
        const {columns, data} = this.state;
        return <Table columns={columns} data={data}></Table>
    }

    componentDidMount() {
        this.load();
    }
}

class Page extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return <div className="container-fluid my-3">
            <OpGrid></OpGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));