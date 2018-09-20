class CityAreaModal extends AreaModal {
    constructor(props) {
        super(props);
        this.state.auth_city = props.auth_city;
    }

    submit = () => {
        const {onSuccess, create, update, auth_city} = this.state;
        let data = this.getData();
        if (create) {
            request({
                url: auth_city ? `/api/city/${auth_city.city}/area/create` : `/api/area/create`,
                method: 'post', contentType: 'application/json', loading: true,
                data: JSON.stringify(data, nullStringReplacer),
                success: (resp) => {
                    if (resp.code == 0) {
                        Message.msg('保存成功');
                        this.close();
                        if (onSuccess) onSuccess();
                    }
                }
            });
        } else if (update) {
            request({
                url: auth_city ? `/api/city/${auth_city.city}/area/${data.area_id}/update` : `/api/area/${data.area_id}/update`,
                method: 'post', contentType: 'application/json', loading: true,
                data: JSON.stringify(data, nullStringReplacer),
                success: (resp) => {
                    if (resp.code == 0) {
                        Message.msg('保存成功');
                        this.close();
                        if (onSuccess) onSuccess();
                    }
                }
            });
        }
    };

}

class CityAreaManageGrid extends AreaGrid {
    constructor(props) {
        super(props);
        this.state.columns.push({
            title: [
                <button type="button" className="btn btn-sm btn-success m-1"
                        onClick={this.newArea}>新建场地</button>,
                <button type="button" className="btn btn-sm btn-primary m-1"
                        onClick={this.load}>刷新</button>,
            ],
            render: (value, row, index) => {
                return [
                    <button type="button" className="btn btn-sm btn-primary m-1"
                            onClick={this.showArea.bind(this, row.area_id)}>编辑场地</button>,
                    <button type="button" className="btn btn-sm btn-primary m-1"
                            onClick={this.showCapsuleModal.bind(this, row.area_id)}>管理头等舱</button>,
                    <button type="button" className="btn btn-sm btn-primary m-1"
                            onClick={this.editTypes.bind(this, row)}>编辑类型</button>,
                    <button type="button" className="btn btn-sm btn-primary m-1"
                            onClick={this.showBooking.bind(this, row.area_id)}>30日内订单</button>,
                ];
            }
        });
        this.state.city = props.city;
    }

    showBooking = (area_id) => {
        Modal.open(<BookingGridModal url={`/api/area/${area_id}/booking/search`}
                                     queryParams={{
                                         area_id,
                                         create_date_start: new Date(Date.now() - 1000 * 60 * 60 * 24 * 30).format('yyyy-MM-dd')
                                     }}
        ></BookingGridModal>);
    };
    load = () => {
        request({
            url: `/api/city/${this.state.city}/area/search`, loading: true,
            success: (resp) => {
                this.setState({
                    data: resp.data.areaList,
                    countGroupArea: resp.data.countGroupArea,
                    cityMapOptions: new CityMapOptions(resp.data.cityList),
                });

            }
        });
    };


    showCapsuleModal = (area_id) => {
        Modal.open(<CapsuleManageModal area_id={area_id}></CapsuleManageModal>);
    };


    editTypes = (area) => {
        Modal.open(<CapsuleTypeGridModal area_id={area.area_id} onSuccess={this.load}></CapsuleTypeGridModal>);
    };

    newArea = () => {
        Modal.open(<AreaIdCreateModal city={{city, code: city_code}} onSuccess={(area) => {
            Modal.open(<CityAreaModal auth_city={{city, code: city_code}} area={area} create
                                      onSuccess={this.load}></CityAreaModal>);
        }}></AreaIdCreateModal>);
    };

    showArea = (area_id) => {
        Modal.open(<CityAreaModal auth_city={{city, code: city_code}} area_id={area_id} update
                                  onSuccess={this.load}></CityAreaModal>);
    };

    componentDidMount() {
        this.load();
    }
}

ReactDOM.render(
    <div className="container-fluid my-3">
        <div className="text-danger">最多返回{maxResultSize}条数据</div>
        <CityAreaManageGrid city={city}></CityAreaManageGrid>
        <ModalContainer></ModalContainer>
    </div>
    , document.getElementById('root'));