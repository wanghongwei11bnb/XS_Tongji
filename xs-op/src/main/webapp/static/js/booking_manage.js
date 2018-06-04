class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            payType: {5: '公众号支付', 7: '支付宝移动页面支付', 9: '微信小程序支付', 30: '新用户注册赠送', 1: '微信支付', 2: '支付宝支付', 20: '钱包余额支付'},

        };
    }


    getQueryParams = () => {
        return {
            status: this.refs.status.value,
            booking_id: this.refs.booking_id.value,
            area_id: this.refs.area_id.value,
            capsule_id: this.refs.capsule_id.value,
            uin: this.refs.uin.value,
            phone: this.refs.phone.value,
            create_date_start: this.refs.create_date_start.value,
            create_date_end: this.refs.create_date_end.value,
        };
    };

    search = () => {
        this.refs.bookingGrid.load(this.getQueryParams());
    };
    download = () => {
        let queryParams = this.getQueryParams();
        queryParams.download = true;
        window.open(`/api/booking/search?${queryString(queryParams)}`)
    };


    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                订单创建时间：
                <DateInput ref="create_date_start"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <DateInput ref="create_date_end"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                状态：
                <select ref="status" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value=""></option>
                    <option value="1">进行中</option>
                    <option value="2">待支付</option>
                    <option value="3">待支付（支付中）</option>
                    <option value="4">已支付</option>
                </select>
                订单编号：
                <input ref="booking_id" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                场地编号：
                <input ref="area_id" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                头等舱编号：
                <input ref="capsule_id" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>

                uin：
                <input ref="uin" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>

                手机号：
                <input ref="phone" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>


                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="button" className="btn btn-sm btn-success ml-1" onClick={this.download}>下载</button>

            </div>
            <div className="text-danger">最多返回{maxResultSize}条数据</div>
            <BookingGrid ref="bookingGrid"></BookingGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.refs.create_date_start.setValue(new Date().format('yyyy-MM-dd'));
        this.refs.create_date_end.setValue(new Date().format('yyyy-MM-dd'));
        this.search();
        request({
            url: '/api/activeCityList',
            success: (resp) => {
                if (resp.code == 0) {
                    this.setState({cityList: resp.data.cityList});
                }
            }
        });
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));

