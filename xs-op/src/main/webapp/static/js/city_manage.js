class CityModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            create: props.create || false,
            update: props.update || false,
            onSuccess: props.onSuccess,
        };
    }

    submit = () => {
        let code = this.refs.code.value;
        let city = this.refs.city.value;
        let province = this.refs.province.value;
        let region = this.refs.region.value;
        if (!code) return Message.msg('请输入城市编号');
        if (!city) return Message.msg('请输入城市名称');
        if (!province) return Message.msg('请输入省份');
        if (!region) return Message.msg('请选择区域');
        if (!/^\d{4}$/.test(code)) return Message.msg('城市编号格式错误，城市编号在1000到4200之间');
        request({
            url: '/api/city/create', method: 'post', contentType: 'application/json',
            data: JSON.stringify({code, city, province, region}, nullStringReplacer),
            success: (resp) => {
                if (resp.code == 0) {
                    Message.msg('保存成功');
                    this.close();
                    if (this.state.onSuccess) this.state.onSuccess();
                }
            }
        });

    };

    renderBody = () => {
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th>城市编号</th>
                <td>
                    <input ref="code" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>城市名称</th>
                <td>
                    <input ref="city" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>省份</th>
                <td>
                    <input ref="province" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>省份</th>
                <td>
                    <select ref="region" className="form-control">
                        <option value=""></option>
                        {CityRegionOption.map(item => <option value={item.value}>{item.text}</option>)}
                    </select>
                </td>
            </tr>
            </tbody>
        </table>
    };
    renderFooter = () => {
        return [
            <button className="btn btn-link text-primary float-right" onClick={this.submit}>保存</button>,
            <button className="btn btn-link text-secondary float-right" onClick={this.close}>取消</button>,
        ];
    };
}

class CityRegionUpdateModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            city: props.city,
            onSuccess: props.onSuccess,
        };
    }

    submit = (region) => {
        request({
            url: `/api/city/${this.state.city}/update/region`, method: 'post',
            data: {region},
            success: resp => {
                Message.msg('保存成功');
                this.close();
                if (this.state.onSuccess) this.state.onSuccess();
            }
        });

    };

    renderBody = () => {
        return <div>
            华东：上海、江苏、浙江、安徽、江西<br/>
            华南：广东、福建、广西、海南<br/>
            华北：北京、天津、河北、山东、河南、山西、内蒙古<br/>
            华中：湖北、湖南<br/>
            西南：四川、重庆、贵州、云南、西藏<br/>
            东北：辽宁、吉林、黑龙江<br/>
            西北：陕西、甘肃、宁夏、青海、新疆<br/>
            <table className="table table-bordered">
                <tbody>
                <tr>
                    <td className="text-center"><A onClick={this.submit.bind(this, '华北')}>西北</A></td>
                    <td className="text-center"><A onClick={this.submit.bind(this, '华北')}>华北</A></td>
                    <td className="text-center"><A onClick={this.submit.bind(this, '华北')}>东北</A></td>
                </tr>
                <tr>
                    <td className="text-center"></td>
                    <td className="text-center"><A onClick={this.submit.bind(this, '华北')}>华中</A></td>
                    <td className="text-center"><A onClick={this.submit.bind(this, '华北')}>华东</A></td>
                </tr>
                <tr>
                    <td className="text-center"><A onClick={this.submit.bind(this, '华北')}>西南</A></td>
                    <td className="text-center"><A onClick={this.submit.bind(this, '华北')}>华南</A></td>
                    <td className="text-center"></td>
                </tr>
                </tbody>
            </table>
        </div>
    };
}

class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'code', title: '城市编号'},
                {field: 'city', title: '城市名称'},
                {field: 'province', title: '省份'},
                {field: 'region', title: '区域'},
                {
                    field: 'city', title: '操作', render: value => {
                        return [
                            <button className="btn btn-sm btn-primary m-1"
                                    onClick={this.updateRegion.bind(this, value)}>修改区域</button>,
                        ];
                    }
                },
            ]
        };
    }

    updateRegion = (city) => {
        Modal.open(<CityRegionUpdateModal city={city} onSuccess={this.load}></CityRegionUpdateModal>);
    };

    load = () => {
        request({
            url: '/api/cityList', loading: true,
            success: (resp) => {
                if (resp.code == 0) {
                    this.setState({cityList: resp.data.cityList});
                }
            }
        });
    };

    createCity = () => {
        Modal.open(<CityModal create onSuccess={this.load}></CityModal>);
    };

    render() {
        const {cityList, columns} = this.state;
        return <div className="container-fluid my-3">
            <button className="btn btn-sm btn-success" onClick={this.createCity}>添加城市</button>
            <Table columns={columns} data={cityList}></Table>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.load();
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));