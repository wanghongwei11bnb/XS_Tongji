class MainAreaModal extends AreaModal {
    constructor(props) {
        super(props);
    }

    submit = () => {
        const {onSuccess} = this.state;
        let data = this.getData();
        request({
            url: `/api/main_area/${data.area_id}/update`,
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
    };

}

class MainAreaManageGrid extends AreaGrid {
    constructor(props) {
        super(props);
        this.state.columns.push({
            title: [
                <button type="button" className="btn btn-sm btn-primary m-1"
                        onClick={this.load}>刷新</button>,
            ],
            render: (value, row, index) => {
                return [
                    <button type="button" className="btn btn-sm btn-primary m-1"
                            onClick={this.showBooking.bind(this, row.area_id)}>30日内订单</button>,
                    <button type="button" className="btn btn-sm btn-primary m-1"
                            onClick={this.downloadBill.bind(this, row.area_id)}>下载上月订单</button>,
                    <button type="button" className="btn btn-sm btn-primary m-1"
                            onClick={this.downloadBillRange.bind(this, row.area_id)}>指定日期下载订单</button>,
                ];
            }
        });
    }

    downloadBill = (area_id) => {
        let date = new Date();
        date.setMonth(date.getMonth() - 1);
        let queryParams = {year: date.getFullYear(), month: date.getMonth() + 1};
        queryParams.download = true;
        window.open(`/api/main_area/${area_id}/reckon/download?${queryString(queryParams)}`);
    };

    downloadBillRange = (area_id) => {
        Modal.open(<DateRangeModal ok={(date_start, date_end) => {
            let queryParams = {create_date_start: date_start, create_date_end: date_end, download: true};
            window.open(`/api/main_area/${area_id}/reckon/download/range?${queryString(queryParams)}`);
        }}></DateRangeModal>);
    };

    showBooking = (area_id) => {
        Modal.open(<BookingGridViewModal url={`/api/area/${area_id}/booking/search`}
                                         queryParams={{
                                             area_id,
                                             create_date_start: new Date(Date.now() - 1000 * 60 * 60 * 24 * 30).format('yyyy-MM-dd')
                                         }}
        ></BookingGridViewModal>);
    };
    load = () => {
        request({
            url: `/api/main_area/search`, loading: true,
            success: (resp) => {
                this.setState({
                    data: resp.data.areaList,
                    countGroupArea: resp.data.countGroupArea,
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


    showArea = (area_id) => {
        Modal.open(<MainAreaModal area_id={area_id} update
                                  onSuccess={this.load}></MainAreaModal>);
    };

    componentDidMount() {
        this.load();
    }
}

ReactDOM.render(
    <div className="container-fluid my-3">
        <MainAreaManageGrid></MainAreaManageGrid>
        <ModalContainer></ModalContainer>
    </div>
    , document.getElementById('root'));