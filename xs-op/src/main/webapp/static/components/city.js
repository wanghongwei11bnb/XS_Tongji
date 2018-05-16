class SelectCityModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            cityList: [], columns: [
                {field: 'code', title: 'code'},
                {field: 'city', title: '城市'},
                {field: 'province', title: '省份'},
                {
                    render: (value, row, index) => {
                        return <button type="button" className="btn btn-sm btn-primary"
                                       onClick={this.submit.bind(this, row)}>选择</button>
                    }
                },
            ]
        };
    }

    submit = (city) => {
        this.close();
        if (this.props.onSuccess) {
            this.props.onSuccess(city);
        }
    };


    renderBody = () => {
        const {columns, cityList} = this.state;
        return <Table columns={columns} data={cityList}></Table>;
    };

    componentDidMount() {
        super.componentDidMount();
        request({
            url: this.props.all ? '/api/cityList' : '/api/activeCityList', loading: true,
            success: (resp) => {
                if (resp.code == 0) {
                    this.setState({cityList: resp.data.cityList});
                }
            }
        });
    }
}
