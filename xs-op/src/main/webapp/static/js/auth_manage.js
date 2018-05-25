class AuthsModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            onSuccess: props.onSuccess,
            username: props.username,
        };
    }

    submit = () => {
        let auths = [];
        authList.map((auth) => {
            if (this.refs[auth] && this.refs[auth].checked) {
                auths.push(auth);
            }
        });
        request({
            url: '/api/op/update/auths', method: 'post', loading: true,
            data: {
                username: this.state.username,
                auths: auths.join(','),
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
            url: '/api/op/auths', loading: true,
            data: {username: this.state.username},
            success: resp => {
                resp.data.auths.map((auth) => {
                    if (this.refs[auth]) {
                        this.refs[auth].checked = true;
                    }
                });
            }
        });
    }

}

class CitysModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            onSuccess: props.onSuccess,
            username: props.username,
        };
    }

    submit = () => {
        let citys = [];
        cityList.map((city) => {
            if (this.refs[city.city] && this.refs[city.city].checked) {
                citys.push(city.city);
            }
        });
        request({
            url: '/api/op/update/citys', method: 'post', loading: true,
            data: {
                username: this.state.username,
                citys: citys.join(','),
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
            {cityList.map((city) => {
                return <div className="col-sm-12 col-md-6 col-lg-4 form-check">
                    <input ref={city.city} id={city.city} className="form-check-input" type="checkbox"/>
                    <label className="form-check-label" htmlFor={city.city}>{city.city}</label>
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
            url: '/api/op/citys', loading: true,
            data: {username: this.state.username},
            success: resp => {
                resp.data.citys.map((city) => {
                    if (this.refs[city]) {
                        this.refs[city].checked = true;
                    }
                });
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
                    field: 'citys', title: '城市', render: (value) => {
                        if (type(value) == 'String') {
                            let citys = value.split(",");
                            if (citys && citys.length > 0) {
                                return citys.map((city) => {
                                    return <span className="badge badge-primary m-1">{city}</span>;
                                });
                            }
                        }
                    }
                },
                {
                    field: 'username', title: '',
                    render: (value) => {
                        return [
                            <button className="btn btn-sm btn-primary m-1"
                                    onClick={this.updateAuths.bind(this, value)}>更改权限</button>,
                            <button className="btn btn-sm btn-primary m-1"
                                    onClick={this.updateCitys.bind(this, value)}>分配城市</button>,
                        ];
                    }
                },
            ],
        };
    }

    updateAuths = (username) => {
        Modal.open(<AuthsModal username={username} onSuccess={this.load}></AuthsModal>);
    };
    updateCitys = (username) => {
        Modal.open(<CitysModal username={username} onSuccess={this.load}></CitysModal>);
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

request({
    url: '/api/cityList',
    success: (resp) => {
        window.cityList = resp.data.cityList;
    }
});

ReactDOM.render(<Page/>, document.getElementById('root'));