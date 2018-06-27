class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            cityList: []
        };
    }

    getQueryParams = () => {
        return {
            uin: this.refs.uin.value,
            card_no: this.refs.phone.value,
            city: this.refs.city.value,
            create_date_start: this.refs.create_date_start.value,
            create_date_end: this.refs.create_date_end.value,
        };
    };

    search = () => {
        this.refs.grid.load(this.getQueryParams());
    };

    download = () => {
        let queryParams = this.getQueryParams();
        queryParams.download = true;
        window.open(`/api/month_card_recode/search?${queryString(queryParams)}`)
    };

    render() {
        const {cityList} = this.state;
        return <div className="container-fluid my-3">
            <div className="m-1">
                购买时间：
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
                uin：
                <input ref="uin" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                手机号：
                <input ref="phone" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                {authMapOptions.get(finalAuthMap.auth_month_card_download) ?
                    <button type="button" className="btn btn-sm btn-success ml-1"
                            onClick={this.download}>下载</button> : null}
            </div>
            <div className="text-danger">最多返回{maxResultSize}条数据</div>
            <MonthCardRecodeGrid ref="grid"></MonthCardRecodeGrid>

        </div>;
    }

    componentDidMount() {
        this.refs.create_date_start.setValue(new Date().format('yyyy-MM-dd'));
        request({
            url: '/api/cityList',
            success: (resp) => {
                if (resp.code == 0) {
                    this.setState({cityList: resp.data.cityList});
                }
            }
        });
        this.search();
    }
}

ReactDOM.render(<div><Page/><ModalContainer/></div>, document.getElementById('root'));