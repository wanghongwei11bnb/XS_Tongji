class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            payType: {5: '公众号支付', 7: '支付宝移动页面支付', 9: '微信小程序支付', 30: '新用户注册赠送', 1: '微信支付', 2: '支付宝支付', 20: '钱包余额支付'},
        };
    }

    getQueryParams = () => {
        return {
            processor: this.refs.processor.value,
            city: this.refs.city.value,
            area_id: this.refs.area_id.value,
            capsule_id: this.refs.capsule_id.value,
            uin: this.refs.uin.value,
            phone: this.refs.phone.value,
            create_date_start: this.refs.create_date_start.value,
            create_date_end: this.refs.create_date_end.value,
        };
    };

    count = () => {
        let queryParams = this.getQueryParams();
        request({
            url: '/api/booking/count', method: 'post', loading: true,
            data: queryParams,
            success: resp => {
                this.refs.canvasBox.innerHTML = null;
                let canvas = document.createElement('canvas');
                this.refs.canvasBox.appendChild(canvas);
                let ctx = canvas.getContext('2d');
                new Chart(ctx, {...resp.data.countResult});
            }
        });
    };

    render() {
        const {cityList} = this.state;
        return <div className="container-fluid my-3">
            <div className="m-1">
                订单创建时间：
                <DateInput ref="create_date_start"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <DateInput ref="create_date_end"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                城市：
                <select ref="city" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value=""></option>
                    {cityList ? cityList.map((city) => {
                        return <option value={city.city}>{city.city}</option>
                    }) : null}
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
                用户编号：
                <input ref="uin" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                手机号：
                <input ref="phone" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                统计类型：
                <select ref="processor" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value=""></option>
                    <option value="CountBookingForCountAtHourUnit">分时段统计下单数量</option>
                    <option value="CountBookingForCountAtDayUnit">统计每天下单数量</option>
                    <option value="CountBookingForCountMonthCardAtDayUnit">统计每天月卡下单数量</option>
                    <option value="CountBookingForCountPriceAtDayUnit">统计每天订单金额</option>
                </select>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.count}>统计</button>
            </div>
            <Fixed>
                <div ref="canvasBox"></div>
            </Fixed>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
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

