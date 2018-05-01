class AreaForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            area: props.area,
            cityList: props.cityList || [],
            modal: !!props.modal,
        };
    }

    validate = () => {
        let formData = this.getFormData();
    };

    getFormData = () => {
        return {
            area_id: this.refs.area_id.value,
            title: this.refs.title.value,
            city: this.refs.city.value,
            address: this.refs.address.value,
            status: this.refs.status.value,
            minute_start: this.refs.minute_start.value,
            rushHours: this.refs.rushHours.value,
            location: this.refs.location.value,
            contact: this.refs.contact.value,
            notification: this.refs.notification.value,
            area_img: this.refs.area_img.value,
            imgs: this.refs.imgs.value,
        };
    };

    render() {
        const {area, cityList, modal} = this.state;
        return <div>
            <table className="table table-hover">
                <tbody>
                <tr>
                    <th>场地编号</th>
                    <td>
                        <input type="text" disabled={true} readOnly={true} className="form-control"
                               value={area.area_id}/>
                    </td>
                </tr>
                <tr>
                    <th>标题</th>
                    <td>
                        <input type="text" className="form-control" value={area.title}/>
                    </td>
                </tr>
                <tr>
                    <th>城市</th>
                    <td>
                        <select className="form-control">
                            {cityList.map((city) => {
                                return <option selected={city == area.city} value={city.city}>{city.city}</option>
                            })}
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>地址</th>
                    <td>
                        <input type="text" className="form-control" value={area.address}/>
                    </td>
                </tr>
                <tr>
                    <th>最少时长</th>
                    <td>
                        <input type="text" className="form-control" value={area.minute_start}/>
                    </td>
                </tr>
                <tr>
                    <th>高峰时段</th>
                    <td>
                        <input type="text" className="form-control" value={JSON.stringify(area.rushHours)}/>
                    </td>
                </tr>
                <tr>
                    <th>经纬度</th>
                    <td>
                        <input type="text" className="form-control" value={JSON.stringify(area.location)}/>
                    </td>
                </tr>
                <tr>
                    <th>联系方式</th>
                    <td>
                        <input type="text" className="form-control" value={area.contact}/>
                    </td>
                </tr>
                <tr>
                    <th>提醒文案</th>
                    <td>
                        <textarea className="form-control">{area.notification}</textarea>
                    </td>
                </tr>
                <tr>
                    <th>状态</th>
                    <td>
                        <select type="text" className="form-control" value={area.status}>
                            <option selected={area.status != -1} value="0">正常</option>
                            <option selected={area.status == -1} value="-1">已下架</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>地图</th>
                    <td>
                        {area.area_img ? <img src={`${area.area_img}_227`} alt=""/> : null}
                    </td>
                </tr>
                <tr>
                    <th>图片</th>
                    <td>
                        {area.imgs ? area.imgs.map((img) => {
                            return <img src={`${img}_227`} alt=""/>
                        }) : null}
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    }
}


class AreaModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            area: props.area,
            cityList: props.cityList || [],
        };
    }


    renderHeader = () => {
        return [
            '场地信息',
            <span className="cy right-0">
                <button type="button" className="btn btn-link text-primary">保存</button>
                <button type="button" className="btn btn-link text-secondary" onClick={this.close}>取消</button>
            </span>,
        ];
    };
    renderBody = () => {
        const area = this.state.area || {};
        const cityList = this.props.cityList || [];
        return <div>
            <table className="table table-hover">
                <tbody>
                <tr>
                    <th>场地编号</th>
                    <td>
                        <input type="text" disabled={true} readOnly={true} className="form-control"
                               value={area.area_id}/>
                    </td>
                </tr>
                <tr>
                    <th>标题</th>
                    <td>
                        <input type="text" className="form-control" value={area.title}/>
                    </td>
                </tr>
                <tr>
                    <th>城市</th>
                    <td>
                        <select className="form-control">
                            {cityList.map((city) => {
                                return <option selected={city == area.city} value={city.city}>{city.city}</option>
                            })}
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>地址</th>
                    <td>
                        <input type="text" className="form-control" value={area.address}/>
                    </td>
                </tr>
                <tr>
                    <th>最少时长</th>
                    <td>
                        <input type="text" className="form-control" value={area.minute_start}/>
                    </td>
                </tr>
                <tr>
                    <th>高峰时段</th>
                    <td>
                        <input type="text" className="form-control" value={JSON.stringify(area.rushHours)}/>
                    </td>
                </tr>
                <tr>
                    <th>经纬度</th>
                    <td>
                        <input type="text" className="form-control" value={JSON.stringify(area.location)}/>
                    </td>
                </tr>
                <tr>
                    <th>联系方式</th>
                    <td>
                        <input type="text" className="form-control" value={area.contact}/>
                    </td>
                </tr>
                <tr>
                    <th>提醒文案</th>
                    <td>
                        <textarea className="form-control">{area.notification}</textarea>
                    </td>
                </tr>
                <tr>
                    <th>状态</th>
                    <td>
                        <select type="text" className="form-control" value={area.status}>
                            <option selected={area.status != -1} value="0">正常</option>
                            <option selected={area.status == -1} value="-1">已下架</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>地图</th>
                    <td>
                        {area.area_img ? <img src={`${area.area_img}_227`} alt=""/> : null}
                    </td>
                </tr>
                <tr>
                    <th>图片</th>
                    <td>
                        {area.imgs ? area.imgs.map((img) => {
                            return <img src={`${img}_227`} alt=""/>
                        }) : null}
                    </td>
                </tr>
                </tbody>
            </table>
        </div>;
    };
}


class Page extends React.Component {
    constructor(props) {
        super(props);
        const columns = [
            {
                field: 'area_id', title: '场地编号',
                render: (value, row, index) => {
                    return <button type="button" className="btn btn-link btn-sm"
                                   onClick={this.showArea.bind(this, value)}>{value}</button>;
                }
            },
            {field: 'title', title: '标题'},
            {field: 'city', title: '城市'},
            {field: 'address', title: '地址'},
            {
                field: 'status', title: '状态',
                render: (value, row, index) => {
                    return value == -1 ? <span className="text-danger">已下线</span> : '正常';
                }
            },
        ];
        this.state = {columns};
    }

    newArea = () => {
        ModalContainer.open(<AreaModal cityList={this.state.cityList}></AreaModal>);
    };

    showArea = (area_id) => {
        reqwest({
            url: '/api/area/' + area_id,
            success: (resp) => {
                if (resp.code == 0) {
                    Modal.panel({
                        title: '场地信息',
                        content: <AreaForm area={resp.data.area} cityList={this.state.cityList}></AreaForm>
                    });
                } else {
                }
            }
        });
    };


    search = () => {
        const {grid} = this.refs;
        reqwest({
            url: '/api/area/search', method: 'get',
            data: {
                city: this.refs.city.value,
                status: this.refs.status.value,
            },
            success: (resp) => {
                if (resp.code == 0) {
                    grid.state.data = resp.data.list;
                    grid.setState({});
                } else {
                }
            }
        });
    };

    render() {
        const {cityList, columns} = this.state;
        return <div className="container-fluid">
            <div className="m-1">
                城市：
                <select ref="city" className="form-control d-inline-block mx-3 w-auto">
                    <option value="">-- 全部城市 --</option>
                    {cityList ? cityList.map((city) => {
                        return <option value={city.city}>{city.city}</option>
                    }) : null}
                </select>
                状态：
                <select ref="status" className="form-control d-inline-block mx-3 w-auto">
                    <option value="">-- 全部 --</option>
                    <option value="0">正常</option>
                    <option value="-1">已下线</option>
                </select>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="button" className="btn btn-sm btn-success ml-1" onClick={this.newArea}>添加场地</button>
            </div>

            <Datagrid ref="grid" columns={columns}></Datagrid>
            <Messager></Messager>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.search();
        reqwest({
            url: '/api/cityList',
            success: (resp) => {
                if (resp.code == 0) {
                    this.setState({cityList: resp.data.cityList});
                }
            }
        });
    }
}


ReactDOM.render(
    <Page/>
    , document.getElementById('root'));

