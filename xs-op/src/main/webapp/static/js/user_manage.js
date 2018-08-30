
class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    search = () => {
        this.refs.userGrid.load({
            create_date_start: this.refs.create_date_start.value,
            create_date_end: this.refs.create_date_end.value,
            uin: this.refs.uin.value,
            phone: this.refs.phone.value,
            fial_verifie: this.refs.fial_verifie.checked,
        });
    };

    download = () => {
        let queryParams = {
            create_date_start: this.refs.create_date_start.value,
            create_date_end: this.refs.create_date_end.value,
            uin: this.refs.uin.value,
            phone: this.refs.phone.value,
            fial_verifie: this.refs.fial_verifie.checked,
        };
        queryParams.download = true;
        window.open(`/api/user/search?${queryString(queryParams)}`)
    };

    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                注册日期：
                <DateInput ref="create_date_start"
                           className="form-control form-control-sm d-inline-block w-auto mx-1"/>
                <DateInput ref="create_date_end"
                           className="form-control form-control-sm d-inline-block w-auto mx-1"/>
                uin：
                <input ref="uin" type="text" className="form-control form-control-sm d-inline-block w-auto mx-1"/>
                手机号：
                <input ref="phone" type="text" className="form-control form-control-sm d-inline-block w-auto mx-1"/>
                三次认证失败：
                <input ref="fial_verifie" type="checkbox"
                       className="form-control form-control-sm d-inline-block w-auto mx-1"/>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="button" className="btn btn-sm btn-success ml-1" onClick={this.download}>下载</button>
                <button type="button" className="btn btn-sm btn-success ml-1" onClick={this.uin_to_phone}>UIN转手机号
                </button>
            </div>
            <div className="text-danger">最多返回{maxResultSize}条数据</div>
            <UserGrid ref="userGrid"></UserGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    uin_to_phone = () => {
        Modal.open(<TextareaModal ok={text => {
            let data = text.split("\n");
            request({
                url: '/api/user/uin_to_phone', method: 'post', loading: true, contentType: 'application/json',
                data: JSON.stringify(data),
                success: resp => {
                    Modal.open(<AlertModal>
                        <pre>{resp.data.phoneList.join('\n')}</pre>
                    </AlertModal>);
                }
            });


        }}></TextareaModal>);


    };

    componentDidMount() {
        // this.search();
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));