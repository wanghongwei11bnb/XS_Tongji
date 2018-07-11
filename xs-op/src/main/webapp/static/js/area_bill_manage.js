

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


    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                年份：
                <select ref="year" className="form-control d-inline-block w-auto m-1">
                    <option value=""></option>
                    {(() => {
                        let os = [];
                        for (let i = 2017; i <= 2018; i++) {
                            os.push(<option value={i}>{i}</option>);
                        }
                        return os;
                    })()}
                </select>
                月份：
                <select ref="month" className="form-control d-inline-block w-auto m-1">
                    <option value=""></option>
                    {(() => {
                        let os = [];
                        for (let i = 1; i <= 12; i++) {
                            os.push(<option value={i}>{i}</option>);
                        }
                        return os;
                    })()}
                </select>
                场地编号：
                <input ref="area_id" type="text" className="form-control d-inline-block w-auto m-1"/>

                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
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