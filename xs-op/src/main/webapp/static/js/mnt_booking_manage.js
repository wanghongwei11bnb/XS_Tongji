class MinitouBookingGrid extends BookingGrid {
    constructor(props) {
        super(props);
    }

    load = (queryParams) => {
        if (queryParams) this.state.queryParams = queryParams;
        request({
            url: '/api/mnt/booking/search', loading: true,
            data: this.state.queryParams,
            success: resp => {
                this.state.bookingList = resp.data.bookingList;
                this.setState({
                    data: resp.data.bookingList,
                    areaMapOptions: new AreaMapOptions(resp.data.areaList),
                    userInfoMapOptions: new UserInfoMapOptions(resp.data.userInfoList),
                    cityMapOptions: new CityMapOptions(resp.data.cityList),
                });
            }
        });
    };
}


class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    search = () => {
        this.refs.bookingGrid.load({
            date: this.refs.date.value,
        });
    };

    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                <DateInput ref="date" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
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