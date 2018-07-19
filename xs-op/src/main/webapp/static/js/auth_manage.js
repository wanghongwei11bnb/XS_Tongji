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


    region = (region) => {
        let provinceMap = {};
        for (let i = 0; i < cityList.length; i++) {
            let city = cityList[i];
            if (region == city.region) {
                provinceMap[city.province] = city.province;
            }
        }
        let provinceListHtml = [];
        for (let province in provinceMap) {
            provinceListHtml.push(<tr>
                <td>{province}</td>
                <td>{cityList.map(city => {
                    if (city.region == region && city.province == province) {
                        return <div>
                            <input ref={city.city} type="checkbox" className="form-control d-inline-block w-auto"/>
                            {city.city}
                        </div>
                    } else {
                        return null;
                    }

                })}</td>
            </tr>);
        }

        return <div>
            {region}
            <button className="btn btn-sm btn-primary m-1" onClick={this.selectOrClean.bind(this, region, null)}>全选／清空
            </button>
            <table className="table table-bordered table-sm">
                <tbody>{provinceListHtml}</tbody>
            </table>
        </div>
    };

    selectOrClean = (region, province) => {
        let selected = false;
        let cityMap = {};
        for (let i = 0; i < cityList.length; i++) {
            let city = cityList[i];
            if (region == city.region && (!province || province == city.province)) {
                cityMap[city.city] = city.city;
                if (this.refs[city.city] && this.refs[city.city].checked) {
                    selected = true;
                }
            }
        }
        for (let city in cityMap) {
            this.refs[city].checked = !selected;
        }
    };

    renderBody = () => {
        // return <div className="row px-3">
        //     {cityList.map((city) => {
        //         return <div className="col-sm-12 col-md-6 col-lg-4 form-check">
        //             <input ref={city.city} id={city.city} className="form-check-input" type="checkbox"/>
        //             <label className="form-check-label" htmlFor={city.city}>{city.city}</label>
        //         </div>
        //     })}
        // </div>;
        return <table className="table table-bordered table-sm">
            <tbody>
            <tr>
                <td style={{width: '33%'}}>{this.region('西北')}</td>
                <td style={{width: '33%'}}>{this.region('华北')}</td>
                <td style={{width: '33%'}}>{this.region('东北')}</td>
            </tr>
            <tr>
                <td></td>
                <td>{this.region('华中')}</td>
                <td>{this.region('华东')}</td>
            </tr>
            <tr>
                <td>{this.region('西南')}</td>
                <td>{this.region('华南')}</td>
                <td></td>
            </tr>
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

    clean = () => {
        areaList.map((area) => {
            if (this.refs[area.area_id] && this.refs[area.area_id].checked) {
                this.refs[area.area_id].checked = false;
            }
        });
    };

    renderBody = () => {
        return <table className="table table-bordered table-hover">
            <tbody>
            {areaList.map((area) => {
                return <tr>
                    <td className="text-right">
                        <input ref={area.area_id} className="form-check-input" type="checkbox"/>
                    </td>
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
            <A className="btn btn-link text-danger float-right" onClick={this.clean}>清空</A>,
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


class OpCreateModal extends Modal {
    constructor(props) {
        super(props);
    }

    renderBody = () => {
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th>帐号</th>
                <td>
                    <input ref="username" type="text" className="form-control" placeholder="邮箱或手机号"/>
                </td>
            </tr>
            <tr>
                <th>密码</th>
                <td>
                    <input ref="password" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th colSpan={2} className="text-center text-danger">如是销售，请填写</th>
            </tr>
            <tr>
                <th>姓名</th>
                <td>
                    <input ref="fullname" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>城市</th>
                <td>
                    <select ref="city" className="form-control">
                        <option value=""></option>
                        {cityList.map(city => {
                            return <option value={city.city}>{city.city}</option>
                        })}
                    </select>
                </td>
            </tr>
            </tbody>
        </table>
    };

    submit = () => {
        request({
            url: '/api/op/create', method: 'post', contentType: 'application/json', loading: true,
            data: JSON.stringify({
                username: this.refs.username.value,
                password: this.refs.password.value,
                fullname: this.refs.fullname.value,
                city: this.refs.city.value,
            }, nullStringReplacer),
            success: resp => {
                Message.msg('操作成功');
                this.close();
                if (this.props.onSuccess) this.props.onSuccess();
            }
        });


    };

    renderFooter = () => {
        return [
            <A className="btn btn-link text-primary float-right" onClick={this.submit}>提交</A>,
            <A className="btn btn-link text-secondary float-right" onClick={this.close}>取消</A>,
        ];
    };
}

class OpGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {
                    field: 'username', title: '帐号', render: (value, row) => {
                        return <div>
                            {value}
                            <br/>
                            {row.fullname || row.city ? <div className="alert alert-primary">
                                {row.fullname}
                                <br/>
                                {row.city}
                            </div> : null}

                        </div>
                    }
                },
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
                    field: 'username',
                    title: <button className="btn btn-sm btn-success m-1" onClick={this.create}>创建帐号</button>,
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

    create = () => {
        Modal.open(<OpCreateModal onSuccess={this.load}></OpCreateModal>);
    };

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

    componentDidMount() {
        request({
            url: '/api/op/areas/options', success: resp => {
                window.areaList = resp.data.areaList;
                window.areaMapOptions = new AreaMapOptions(resp.data.areaList);
                this.setState({});
            }
        });
        request({
            url: '/api/cityList',
            success: (resp) => {
                window.cityList = resp.data.cityList;
                this.setState({});
            }
        });
    }
}

window.areaMapOptions = null;
window.areaList = [];


ReactDOM.render(<Page/>, document.getElementById('root'));