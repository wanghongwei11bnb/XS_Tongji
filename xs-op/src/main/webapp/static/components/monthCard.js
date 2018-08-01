class MonthCardRecodeGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'uin', title: '用户uin'},
                {field: 'card_no', title: '卡号／手机号'},
                {field: 'city', title: '城市'},
                {
                    field: 'date_time', title: '购买时间', render: value => {
                        if (type(value) === 'Number') {
                            return new Date(value * 1000).format('yyyy-MM-dd hh:mm');
                        }
                    }
                },
                {
                    field: 'left_seconds', title: '当天剩余月卡时长', render: (value, row) => {
                        if (row.end_time * 1000 - Date.now() <= 0) {
                            return <span className="badge badge-danger m-1">已过期</span>;
                        }
                        if (!dateUtils.isSameDay(new Date(), new Date(row.update_time * 1000))) {
                            value = 60 * 60;
                        }
                        return `${Math.ceil(value / 60)}分钟`;
                    }
                },
                {
                    field: 'end_time', title: '月卡状态', render: value => {
                        return [
                            new Date(value * 1000).format('yyyy-MM-dd'),
                            value * 1000 - Date.now() > 0 ?
                                <span className="badge badge-success m-1">生效中</span>
                                : <span className="badge badge-danger m-1">已过期</span>
                        ]
                    }
                },
            ],
        };
    }

    load = (queryParams) => {
        if (queryParams) {
            this.state.queryParams = queryParams;
        }
        request({
            url: '/api/month_card_recode/search', loading: true,
            data: this.state.queryParams,
            success: resp => {
                this.setState({
                    data: resp.data.monthCardRecodeList,
                });
            }
        });

    };

    render() {
        return <Table columns={this.state.columns} data={this.state.data}></Table>
    }


}

class MonthCardRecodeGridModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            queryParams: props.queryParams,
        };
    }

    renderBody = () => {
        return <MonthCardRecodeGrid ref="grid"></MonthCardRecodeGrid>
    };

    componentDidMount() {
        super.componentDidMount();
        this.refs.grid.load(this.state.queryParams);
    }
}