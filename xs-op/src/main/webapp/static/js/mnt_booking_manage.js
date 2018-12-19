class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    search = () => {
        this.refs.bookingGrid.load({
            create_date_start: this.refs.create_date_start.value,
            create_date_end: this.refs.create_date_end.value,
            capsule_id: this.refs.capsule_id.value,
        });
    };

    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                订单创建日期：
                <DateInput ref="create_date_start" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <DateInput ref="create_date_end" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                设备编号：
                <input ref="capsule_id" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <button className="btn btn-sm btn-primary mx-1" onClick={this.search}>搜索</button>
            </div>
            <MinitouBookingGrid ref="bookingGrid"></MinitouBookingGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));