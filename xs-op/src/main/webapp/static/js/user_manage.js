class UserGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'uin', title: '用户ID'},
                {field: 'phone', title: '手机号'},
                {field: 'nick_name', title: '用户昵称'},
                {
                    field: 'create_time', title: '注册日期', render: (value) => {
                        if (type(value) == 'Number') return new Date(value * 1000).format('yyyy-MM-dd');
                    }
                },
            ],
            queryParams: props.queryParams,
        };
    }

    load = (queryParams) => {
        if (type(queryParams) == 'Object') this.state.queryParams = queryParams;
        request({
            url: '/api/user/search', loading: true,
            data: this.state.queryParams,
            success: (resp) => {
                if (resp.data) {
                    this.setState({data: resp.data.userInfoList});
                }
            }
        });
    };

    render() {
        const {columns, data} = this.state;
        return <Table columns={columns} data={data}></Table>;
    }

    componentDidMount() {
        this.load();
    }

}


class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {columns: []};
    }

    search = () => {
        this.refs.userGrid.load({
            create_date_start: this.refs.create_date_start.value,
            create_date_end: this.refs.create_date_end.value,
        });
    };

    render() {
        const {data} = this.state;
        return <div className="container-fluid my-3">
            <div className="m-1">
                注册日期：
                <DateInput ref="create_date_start"
                           className="form-control form-control-sm d-inline-block w-auto mx-1"/>
                <DateInput ref="create_date_end"
                           className="form-control form-control-sm d-inline-block w-auto mx-1"/>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
            </div>
            <div className="text-danger">最多返回{maxResultSize}条数据</div>
            <UserGrid ref="userGrid"></UserGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.search();
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));