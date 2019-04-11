

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


    download = () => {
        let queryParams = this.getQueryParams();
        queryParams.download = true;
        window.open(`/api/cash_info/search?${queryString(queryParams)}`)
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
                <button type="button" className="btn btn-sm btn-success ml-1" onClick={this.download}>下载</button>
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

