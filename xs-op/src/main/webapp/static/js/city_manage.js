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
        if (!code) return Message.msg('请输入城市编号');
        if (!city) return Message.msg('请输入城市名称');
        if (!province) return Message.msg('请输入省份');
        if (!/^\d{4}$/.test(code)) return Message.msg('城市编号格式错误，城市编号在1000到4200之间');
        request({
            url: '/api/city/create', method: 'post', contentType: 'application/json',
            data: JSON.stringify({code, city, province}, nullStringReplacer),
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

class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'code', title: '城市编号'},
                {field: 'city', title: '城市名称'},
                {field: 'province', title: '省份'},
            ]
        };
    }

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