class ChargeRecordGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'out_trade_no', title: '交易编号'},
                {field: 'subject', title: '业务类型'},
                {field: 'uin', title: '用户编号'},
                {field: 'phone', title: '用户手机号'},
                {field: 'booking_id', title: '订单编号'},
                {field: 'bill_area_id', title: '分账场地编号'},
                {field: 'bill_booking_id', title: '分账订单编号'},
                {field: 'city', title: '城市'},
                {field: 'price', title: '交易金额', render: value => type(value) === 'Number' ? value / 100 : null},
                {field: 'status', title: '状态'},
                {
                    field: 'create_time', title: '交易时间', render: value => {
                        if (type(value) === 'Number') return new Date(value * 1000).format('yyyy-MM-dd hh:mm');
                    }
                },
                {
                    field: 'update_time', title: '更新时间', render: value => {
                        if (type(value) === 'Number') return new Date(value * 1000).format('yyyy-MM-dd hh:mm');
                    }
                },
            ],
            queryParams: props.queryParams
        };
    }

    load = (queryParams) => {
        if (queryParams) {
            this.state.queryParams = queryParams;
        }
        request({
            url: '/api/charge_record/search', loading: true,
            data: this.state.queryParams,
            success: resp => {
                this.setState({
                    data: resp.data.chargeRecordList,
                });
            }
        });
    };

    render() {
        return <Table columns={this.state.columns} data={this.state.data}></Table>
    }


}


class ChargeRecordGridModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            queryParams: props.queryParams,
        };
    }

    renderBody = () => {
        return <ChargeRecordGrid ref="grid"></ChargeRecordGrid>
    };

    componentDidMount() {
        super.componentDidMount();
        this.refs.grid.load(this.state.queryParams);
    }
}
