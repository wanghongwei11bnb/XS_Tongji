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

class AreasModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            onSuccess: props.onSuccess,
            username: props.username,
        };
    }

    submit = () => {
        let areas = [];
        areaList.map((area) => {
            if (this.refs[area.area_id] && this.refs[area.area_id].checked) {
                areas.push(area.area_id);
            }
        });
        request({
            url: '/api/op/update/areas', method: 'post', loading: true,
            data: {
                username: this.state.username,
                areas: areas.join(','),
            },
            success: resp => {
                Message.msg('保存成功');
                this.close();
                if (this.state.onSuccess) this.state.onSuccess();
            }
        });
    };

    renderBody = () => {
        return <table className="table table-bordered">
            <tbody>
            {areaList.map((area) => {
                return <tr>
                    <td><input ref={area.area_id} id={area.area_id} className="form-check-input float-right"
                               type="checkbox"/></td>
                    <td>{area.title}</td>
                    <td>{area.city}</td>
                    <td>{area.address}</td>
                </tr>
            })}
            </tbody>
        </table>;
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
            url: '/api/op/areas', loading: true,
            data: {username: this.state.username},
            success: resp => {
                resp.data.areas.map((area) => {
                    if (this.refs[area]) {
                        this.refs[area].checked = true;
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
                    field: 'areas', title: '场地', render: (value) => {
                        if (type(value) == 'String') {
                            let areas = value.split(",");
                            if (areas && areas.length > 0) {
                                return areas.map((area) => {
                                    return <span
                                        className="badge badge-primary m-1">{areaMapOptions && areaMapOptions.get(area) ? areaMapOptions.get(area).title : area}</span>;
                                });
                            }
                        }
                    }
                },
                {
                    field: 'username', title: '操作',
                    render: (value) => {
                        return [
                            <button className="btn btn-sm btn-primary m-1"
                                    onClick={this.updateAuths.bind(this, value)}>更改权限</button>,
                            <button className="btn btn-sm btn-primary m-1"
                                    onClick={this.updateCitys.bind(this, value)}>分配城市</button>,
                            <button className="btn btn-sm btn-primary m-1"
                                    onClick={this.updateAreas.bind(this, value)}>分配场地</button>,
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
    updateAreas = (username) => {
        Modal.open(<AreasModal username={username} onSuccess={this.load}></AreasModal>);
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

window.areaMapOptions = null;
window.areaList = [];
request({
    url: '/api/op/areas/options', success: resp => {
        window.areaList = resp.data.areaList;
        window.areaMapOptions = new AreaMapOptions(resp.data.areaList);
    }
});
request({
    url: '/api/cityList',
    success: (resp) => {
        window.cityList = resp.data.cityList;
    }
});

ReactDOM.render(<Page/>, document.getElementById('root'));