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
                {field: 'uin', title: '用户编号uin', render: value => [value, testUinMapOptions.get(value) && <span className="text-danger mx-1">测试</span>]},
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
