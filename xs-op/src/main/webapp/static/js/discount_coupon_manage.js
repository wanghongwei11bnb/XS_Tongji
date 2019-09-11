
class DiscountCouponGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'coupon_id',},
                {
                    field: 'type', title: '活动名称', render: value => {
                        if (value === 1) return '单单返现金';
                        if (value === 2) return '幸运大转盘';
                        return value;
                    }
                },
                {field: 'uin', title: '用户编号'},
                {field: 'uin', title: '用户手机号', render: value => this.state.userInfoMapOptions.get(value) ? this.state.userInfoMapOptions.get(value).phone : null},
                {field: 'price_title', title: '奖品'},
                {
                    field: 'id', title: '奖品', render: (value, row, index) => {
                        if (row.price_title === '现金红包') return `${row.price / 100}元现金红包`;
                        if (row.price_title === '雨露均沾奖') return `满${row.min_price / 100}减${row.cash / 100}优惠券`;
                        return value;
                    }
                },
                {
                    field: 'status', title: '领取状态', render: value => {
                        if (value === 1) return '已领取';
                        return <span className="text-danger">未领取</span>;
                    }
                },
                {field: 'receive_time', title: '领取时间', render: value => type(value, 'Number') ? new Date(value * 1000).format() : null},
                {field: 'booking_id', title: '订单编号'},
                {
                    field: 'id', title: <span className="btn btn-sm m-1 btn-success hide" onClick={this.create}>新建</span>, render: (value, row, index) => [
                        <span className="btn btn-sm m-1 btn-danger hide" onClick={this.delete.bind(this, value)}>删除</span>,
                        <span className="btn btn-sm m-1 btn-primary hide" onClick={this.update.bind(this, value)}>编辑</span>,
                    ]
                },
            ],
            queryParams: props.queryParams,
        };
    }

    load = (queryParams) => {
        if (type(queryParams) == 'Object') this.state.queryParams = queryParams;
        request({
            url: '/api/red_bag/search', loading: true,
            data: this.state.queryParams,
            success: (resp) => {
                this.setState({
                    data: resp.data.redBagList,
                    userInfoMapOptions: new UserInfoMapOptions(resp.data.userInfoList),
                });
            }
        });
    };

    create = () => {


    };
    delete = () => {


    };
    update = () => {


    };


    render() {
        const {columns, data} = this.state;
        return <Table columns={columns} data={data}></Table>;
    }

    componentDidMount() {
    }
}





class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    load = () => {

    };

    download = () => {
        window.open(`/api/device_status/search?${queryString({download: true})}`)
    };



    render() {
        const {columns, data} = this.state;
        return <div className="container-fluid my-3">
            <button className="btn btn-success btn-sm m-1" type="button" onClick={this.load}>刷新</button>
            <button className="btn btn-success btn-sm m-1" type="button" onClick={this.download}>下载</button>
            <Table columns={columns} data={data}></Table>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.load();
    }

}

ReactDOM.render(<Page/>, document.getElementById('root'));