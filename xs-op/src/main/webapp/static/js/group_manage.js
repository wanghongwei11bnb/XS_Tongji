class GroupGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'group_id', title: '编号'},
                {
                    field: 'create_time', title: '开团时间', render: value => {
                        return value ? new Date(value * 1000).format('yyyy-MM-dd hh:mm') : null;
                    }
                },
                {field: 'group_amount', title: '参团人数'},
                {
                    field: 'group_status', title: '拼团状态', render: value => {
                        switch (value) {
                            case 0:
                                return <span className="text-secondary">初始化</span>;
                            case 1:
                                return <span className="text-warning">拼团中</span>;
                            case 3:
                                return <span className="text-danger">拼团失败</span>;
                            case 2:
                                return <span className="text-success">拼团成功</span>;
                            default:
                                return value;
                        }
                    }
                },
                {field: 'group_master', title: '开团用户uin'},
                {
                    field: 'group_id', title: '参团用户uin', render: value => {
                        const groupBookingList = this.state.groupBookingList;
                        const trs = [];
                        for (let i = 0; i < groupBookingList.length; i++) {
                            let groupBooking = groupBookingList[i];
                            if (value == groupBooking.group_id) {
                                let status = groupBooking.status;
                                trs.push(<tr>
                                    <td>{groupBooking.uin}</td>
                                    <td>{status == 1 ? '支付成功'
                                        : status == 2 ? '支付失败'
                                            : status == 4 ? '退款成功'
                                                : status == 3 ? '开始退款'
                                                    : status == 0 ? '开始支付'
                                                        : status}</td>
                                    <td>{(groupBooking.price || 0) / 100}元</td>
                                </tr>);
                            }
                        }
                        return <table className="table table-sm">
                            <thead>
                            <tr>
                                <th>用户编号</th>
                                <th>支付状态</th>
                                <th>支付金额</th>
                            </tr>
                            </thead>
                            <tbody>{trs}</tbody>
                        </table>;
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
            url: '/api/group/search', loading: true,
            data: this.state.queryParams,
            success: resp => {
                this.setState({
                    data: resp.data.groupInfoList,
                    groupBookingList: resp.data.groupBookingList || [],
                    groupInfoMapOptions: resp.data.groupInfoList ? new GroupInfoMapOptions(resp.data.groupInfoList) : null,
                });
            }
        });
    };

    render() {
        const {columns, data, groupBookingList, groupInfoMapOptions} = this.state;
        let countMap = {};
        if (data && data.length > 0) {
            for (let i = 0; i < data.length; i++) {
                let groupInfo = data[i];
                if (!countMap[groupInfo.group_amount]) {
                    countMap[groupInfo.group_amount] = {
                        count: 0,
                        success: 0,
                    };
                }
                countMap[groupInfo.group_amount].count++;
                if (groupInfo.group_status === 2) {
                    countMap[groupInfo.group_amount].success++;
                }
            }
        }
        return <div>
            <table className="table table-sm table-bordered table-hover">
                <thead>
                <tr>
                    <th>参团人数</th>
                    <th>拼团数量</th>
                    <th>成功数量</th>
                </tr>
                </thead>
                <tbody>
                {(() => {
                    let trs = [];
                    for (let group_amount in countMap) {

                        trs.push(<tr>
                            <td>{group_amount}</td>
                            <td>{countMap[group_amount].count}</td>
                            <td>{countMap[group_amount].success}</td>
                        </tr>);
                    }
                    return trs;
                })()}
                </tbody>
            </table>
            <Table columns={columns} data={data}></Table>
        </div>
    }
}


class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    search = () => {
        this.refs.grid.load({
            create_date_start: this.refs.create_date_start.value,
            create_date_end: this.refs.create_date_end.value,
        });
    };

    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                开团日期：
                <DateInput ref="create_date_start"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <DateInput ref="create_date_end"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <button className="btn btn-sm btn-primary mx-1" onClick={this.search}>搜索</button>
            </div>
            <GroupGrid ref="grid"></GroupGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));