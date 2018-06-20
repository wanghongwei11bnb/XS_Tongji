class MonthCardRecodeGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'uin', title: '用户uin'},
                {field: 'card_no', title: '卡号／手机号'},
                {field: 'city', title: '城市'},
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


class Page extends React.Component {
    constructor(props) {
        super(props);
    }

    getQueryParams = () => {
        return {
            uin: this.refs.uin.value,
            card_no: this.refs.phone.value,
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
        return <div className="container-fluid my-3">
            <div className="m-1">
                uin：
                <input ref="uin" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                手机号：
                <input ref="phone" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                {auth_month_card_download ? <button type="button" className="btn btn-sm btn-success ml-1"
                                                    onClick={this.download}>下载</button> : null}
            </div>
            <div className="text-danger">最多返回{maxResultSize}条数据</div>
            <MonthCardRecodeGrid ref="grid"></MonthCardRecodeGrid>

        </div>;
    }

    componentDidMount() {
        this.search();
    }
}

window.areaMapOptions = null;
window.areaList = [];


ReactDOM.render(<div><Page/><ModalContainer/></div>, document.getElementById('root'));