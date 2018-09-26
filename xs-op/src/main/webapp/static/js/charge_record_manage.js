class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }


    getQueryParams = () => {
        return {
            subject: this.refs.subject.value,
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
        window.open(`/api/charge_record/search?${queryString(queryParams)}`)
    };


    render() {
        const {subjectSet} = this.state;
        return <div className="container-fluid my-3">
            <div className="m-1">
                交易时间：
                <DateInput ref="create_date_start"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <DateInput ref="create_date_end"
                           className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                交易编号：
                <input ref="booking_id" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                交易类型：
                <select ref="subject" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value=""></option>
                    {subjectSet ? subjectSet.map(subject => <option value={subject}>{subject}</option>) : null}
                </select>

                用户编号：
                <input ref="uin" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                手机号：
                <input ref="phone" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="button" className="btn btn-sm btn-success ml-1" onClick={this.download}>下载</button>
            </div>
            <ChargeRecordGrid ref="grid"></ChargeRecordGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        request({
            url: '/api/charge_record/subjectSet',
            success: resp => {
                this.setState({
                    subjectSet: resp.data.subjectSet,
                });
            }
        });
        let now = new Date();
        this.refs.create_date_start.setValue(now.format('yyyy-MM-dd'));
        this.refs.create_date_end.setValue(now.format('yyyy-MM-dd'));
        this.search();
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));

