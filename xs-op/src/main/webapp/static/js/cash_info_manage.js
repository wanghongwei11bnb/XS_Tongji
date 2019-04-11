class CashInfoGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'cash_id', title: 'ID',},
                {
                    field: 'type', title: '业务类型', render: value => {
                        switch (value) {
                            case 1:
                                return '发放';
                            case 2:
                                return <span className="text-danger">提现</span>;
                            default:
                                return null;
                        }
                    }
                },
                {field: 'create_time', title: '时间', render: value => type(value, 'Number') ? new Date(value * 1000).format() : null},
                {field: 'cash_num', title: '金额', render: value => type(value, 'Number') ? value / 100 : null},
                {field: 'uin', title: '用户编号uin'},
                {field: 'uin', title: '用户手机号', render: value => this.state.userInfoMapOptions.get(value) && this.state.userInfoMapOptions.get(value).phone},
                {field: 'booking_id', title: '订单编号'},
            ],
            queryParams: props.queryParams,
        };
    }

    load = (queryParams) => {
        if (type(queryParams) == 'Object') this.state.queryParams = queryParams;
        request({
            url: '/api/cash_info/search', loading: true,
            data: this.state.queryParams,
            success: (resp) => {
                this.setState({
                    data: resp.data.cashInfoList,
                    userInfoMapOptions: new UserInfoMapOptions(resp.data.userInfoList),
                });
            }
        });
    };

    render() {
        const {columns, data} = this.state;
        return <Table columns={columns} data={data}></Table>;
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
            uin: this.refs.uin.value,
            phone: this.refs.phone.value,
            create_date_start: this.refs.create_date_start.value,
            create_date_end: this.refs.create_date_end.value,
        };
    };

    search = () => {
        this.refs.grid.load(this.getQueryParams());
    };


    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                时间：
                <DateInput ref="create_date_start" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <DateInput ref="create_date_end" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                订单编号：
                <input ref="booking_id" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                uin：
                <input ref="uin" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                手机号：
                <input ref="phone" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
            </div>
            <div className="text-danger">最多返回{maxResultSize}条数据</div>
            <CashInfoGrid ref="grid"></CashInfoGrid>
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

