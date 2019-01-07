class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    search = () => {
        this.refs.grid.load({
            year: this.refs.year.value,
            month: this.refs.month.value,
        });
    };

    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                <select ref="year" className="form-control d-inline-block w-auto mx-1" onChange={this.setState.bind(this, {})}>
                    <option></option>
                    <option value={2018}>2018</option>
                    <option value={2019}>2019</option>
                </select>
                <select ref="month" className="form-control d-inline-block w-auto mx-1">
                    {(() => {
                        if (this.refs && this.refs.year && this.refs.year.value) {
                            if (this.refs.year.value == 2018) {
                                return <option value={12}>12</option>;
                            } else if (this.refs.year.value == 2019) {
                                let options = [];
                                for (let i = 1; i <= 12; i++) {
                                    options.push(<option value={i}>{i}</option>);
                                }
                                return options;
                            }
                        }
                    })()}
                </select>
                <button className="btn btn-sm btn-primary mx-1" onClick={this.search}>查看报表</button>
            </div>
            <MinitouBillGrid renderHeader={(headerHtml, data, columns) => {
                return [
                    headerHtml,
                    <span className="text-danger pl-3">本月净利润{(columns[3].total - columns[4].total - 22000).toFixed(2)}元（扣除固定成本2.2万元）</span>
                ];
            }} ref="grid"></MinitouBillGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));