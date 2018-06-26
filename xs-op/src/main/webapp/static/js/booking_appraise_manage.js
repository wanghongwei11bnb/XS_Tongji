class BookingAppraiseGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'booking_id', title: '订单编号'},
                {field: 'area_id', title: '场地编号'},
                {
                    field: 'area_id', title: '场地名称', render: value => {
                        const areaMapOptions = this.state.areaMapOptions;
                        if (areaMapOptions && areaMapOptions.get(value)) {
                            return areaMapOptions.get(value).title;
                        }
                    }
                },
                {
                    field: 'area_id', title: '场地城市', render: value => {
                        const areaMapOptions = this.state.areaMapOptions;
                        if (areaMapOptions && areaMapOptions.get(value)) {
                            return areaMapOptions.get(value).city;
                        }
                    }
                },
                {
                    field: 'area_id', title: '场地详细地址', render: value => {
                        const areaMapOptions = this.state.areaMapOptions;
                        if (areaMapOptions && areaMapOptions.get(value)) {
                            return areaMapOptions.get(value).address;
                        }
                    }
                },
                {field: 'uin', title: '用户编号'},
                {
                    field: 'uin', title: '用户手机号', render: value => {
                        const userInfoMapOptions = this.state.userInfoMapOptions;
                        if (userInfoMapOptions && userInfoMapOptions.get(value)) {
                            return userInfoMapOptions.get(value).phone;
                        }
                    }
                },
                {
                    field: 'createtime', title: '评论时间', render: value => {
                        if (type(value) === 'Number') return new Date(value * 1000).format('yyyy-MM-dd hh:mm');
                    }
                },
                {
                    field: 'appraise', title: '描述标签', render: value => {
                        if (type(value) === 'String') {
                            return value;
                        } else if (type(value) === 'Array') {
                            return value.map(tag => <span className="badge badge-primary m-1">{tag}</span>);
                        }
                    }
                },
                {field: 'suggest', title: '用户描述'},
            ],
        };
    }

    load = (queryParams) => {
        if (queryParams) this.state.queryParams = queryParams;
        request({
            url: '/api/booking_appraise/search', loading: true,
            data: this.state.queryParams,
            success: resp => {
                this.setState({
                    data: resp.data.bookingAppraiseList,
                    areaMapOptions: resp.data.areaList ? new AreaMapOptions(resp.data.areaList) : null,
                    userInfoMapOptions: resp.data.userInfoList ? new UserInfoMapOptions(resp.data.userInfoList) : null,
                });
            }
        });
    };

    render() {
        return <Table columns={this.state.columns} data={this.state.data}></Table>
    }
}


class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }


    getQueryParams = () => {
        return {
            booking_id: this.refs.booking_id.value,
            area_id: this.refs.area_id.value,
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
                评论时间：
                <DateInput ref="create_date_start"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <DateInput ref="create_date_end"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                订单编号：
                <input ref="booking_id" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                场地编号：
                <input ref="area_id" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                uin：
                <input ref="uin" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                手机号：
                <input ref="phone" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>

                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
            </div>
            <div className="text-danger">最多返回{maxResultSize}条数据</div>
            <BookingAppraiseGrid ref="bookingGrid"></BookingAppraiseGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.refs.create_date_start.setValue(new Date().format('yyyy-MM-dd'));
        this.refs.create_date_end.setValue(new Date().format('yyyy-MM-dd'));
        this.search();
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));

