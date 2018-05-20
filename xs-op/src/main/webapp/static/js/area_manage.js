class AreaManageGrid extends AreaGrid {
    constructor(props) {
        super(props);
        this.state.columns.push({
            title: <button type="button" className="btn btn-sm btn-success m-1"
                           onClick={this.newArea}>新建场地</button>,
            render: (value, row, index) => {
                return [
                    <button type="button" className="btn btn-sm btn-primary m-1"
                            onClick={this.showArea.bind(this, row.area_id)}>编辑场地</button>,
                    <button type="button" className="btn btn-sm btn-primary m-1"
                            onClick={this.showCapsuleModal.bind(this, row.area_id)}>管理头等舱</button>,
                    <button type="button" className="btn btn-sm btn-primary m-1"
                            onClick={this.editTypes.bind(this, row)}>编辑类型</button>,
                ];
            }
        });
    }

    showCapsuleModal = (area_id) => {
        Modal.open(<CapsuleManageModal area_id={area_id}></CapsuleManageModal>);
    };


    editTypes = (area) => {
        Modal.open(<CapsuleTypeGridModal area_id={area.area_id} onSuccess={this.load}></CapsuleTypeGridModal>);
    };

    newArea = () => {
        Modal.open(<AreaIdCreateModal onSuccess={(area) => {
            Modal.open(<AreaModal area={area} create onSuccess={this.load}></AreaModal>);
        }}></AreaIdCreateModal>);
    };

    showArea = (area_id) => {
        Modal.open(<AreaModal area_id={area_id} update onSuccess={this.load}></AreaModal>);
    };


}

class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }


    search = () => {
        this.state.queryParams = {
            city: this.refs.city.value,
            title: this.refs.title.value,
            address: this.refs.address.value,
            area_id: this.refs.area_id.value,
            capsule_id: this.refs.capsule_id.value,
            status: this.refs.status.value,
        };
        this.load();
    };


    render() {
        const {cityList, columns, data} = this.state;
        return <div className="container-fluid my-3">
            <div className="m-1">
                城市：
                <select ref="city" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value=""></option>
                    {cityList ? cityList.map((city) => {
                        return <option value={city.city}>{city.city}</option>
                    }) : null}
                </select>
                标题：
                <input ref="title" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                地址：
                <input ref="address" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                状态：
                <select ref="status" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value=""></option>
                    <option value="-1">已下线</option>
                    <option value="-2">待运营</option>
                </select>
                场地编号：
                <input ref="area_id" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                头等舱编号：
                <input ref="capsule_id" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
            </div>
            <div className="text-danger">查询结果条数：{data ? data.length : null}（最多返回{maxResultSize}条数据）</div>
            <AreaManageGrid data={data}></AreaManageGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        reqwest({
            url: '/api/activeCityList',
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

