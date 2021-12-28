class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }


    search = () => {
        this.refs.grid.load({
            area_id: this.refs.area_id.value,
            year: this.refs.year.value,
            month: this.refs.month.value,
            // status: this.refs.status.value,
        });
    };
    download = () => {
        let queryParams = {
            area_id: this.refs.area_id.value,
            year: this.refs.year.value,
            month: this.refs.month.value,
        };
        queryParams.download = true;
        window.open(`/api/area_bill/search?${queryString(queryParams)}`)
    };


    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                年份：
                <select ref="year" className="form-control d-inline-block w-auto m-1">
                    <option value=""></option>
                    {[2019, 2020, 2021, 2022, 2023 , 2024 , 2025 ].map(year => <option value={year}>{year}</option>)}
                </select>
                月份：
                <select ref="month" className="form-control d-inline-block w-auto m-1">
                    <option value=""></option>
                    {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12].map(month => <option value={month}>{month}</option>)}
                </select>
                场地编号：
                <input ref="area_id" type="text" className="form-control d-inline-block w-auto m-1"/>

                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
                <button type="button" className="btn btn-sm btn-success ml-1" onClick={this.download}>下载</button>
            </div>
            <AreaBillGrid ref="grid"></AreaBillGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.search();
    }
}

loadOpInfo();

request({
    url: '/api/activeCityList',
    success: (resp) => {
        window.activeCityList = resp.data.cityList;
    }
});

ReactDOM.render(<Page/>, document.getElementById('root'));
