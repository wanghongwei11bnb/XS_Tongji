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

class SummaryGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: '', title: ''},
            ],
        };
    }
}


class MainAreaManageGrid extends AreaGrid {
    constructor(props) {
        super(props);
        this.state.columns.push({
            title: [
                <button type="button" className="btn btn-sm btn-success m-1" onClick={this.load}>刷新</button>,
                authMapOptions.get(finalAuthMap.auth_my_area_summary) ?
                    <button type="button" className="btn btn-sm btn-success m-1" onClick={this.summary}>单日汇总</button>
                    : null,
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

    summary = () => {
        Modal.open(<CalendarModal onDateClick={(ymd) => {
            // request({
            //     url: '/api/main_area/summary', method: 'post', loading: true,
            //     data: {
            //         date: ymd.format('yyyy-MM-dd'),
            //     },
            //     success: resp => {
            //         let areaMapOptions = new AreaMapOptions(resp.data.areaList);
            //         Modal.open(<AlertModal>
            //             <Table columns={[
            //                 {field: 'area_id', title: '场地', render: value => areaMapOptions.get(value) ? areaMapOptions.get(value).address : value},
            //                 {field: 'booking_count', title: '订单数量'},
            //                 {field: 'pay_price', title: '订单收入',render:value=>type(value,'Number')?value/100:null},
            //             ]} data={resp.data.areaBillResultList}></Table>
            //         </AlertModal>);
            //     }
            // });
            request({
                url: '/api/main_area/report', method: 'post', loading: true,
                data: {
                    date: ymd.format('yyyy-MM-dd'),
                },
                success: resp => {
                    let reportResult = resp.data.reportResult;
                    let areaMapOptions = new AreaMapOptions(reportResult.areaList);
                    Modal.open(<AlertModal>
                        <Table columns={[
                            {field: 'address', title: '场地'},
                            {field: 'area_id', title: `订单数量total(${reportResult.countBooking})`, render: value => reportResult.reportResultMapGroupForArea[value] && reportResult.reportResultMapGroupForArea[value].countBooking},
                            {field: 'area_id', title: `订单收入total(${reportResult.totalAmount / 100})`, render: value => reportResult.reportResultMapGroupForArea[value] && reportResult.reportResultMapGroupForArea[value].totalAmount / 100},
                            {field: 'area_id', title: `绿动社区下单数量total(${reportResult.countBookingMapForReqFrom['ldwork'] || 0})`, render: value => reportResult.reportResultMapGroupForArea[value] && reportResult.reportResultMapGroupForArea[value].countBookingMapForReqFrom['ldwork'] || 0},
                        ]} data={reportResult.areaList}></Table>
                    </AlertModal>);
                }
            });
        }}></CalendarModal>);
    };

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