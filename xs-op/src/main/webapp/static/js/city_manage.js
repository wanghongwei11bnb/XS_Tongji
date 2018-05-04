class Page extends React.Component {
    constructor(props) {
        super(props);
        const columns = [
            {field: 'code', title: '城市编号'},
            {field: 'city', title: '城市名称'},
            {field: 'province', title: '省份'},
            {field: 'lng', title: '经度（lng）'},
            {field: 'lat', title: '纬度（lng）'},
        ];
        this.state = {columns};
    }


    render() {
        const {cityList, columns} = this.state;
        return <div className="container-fluid my-3">
            <Datagrid ref="grid" columns={columns}></Datagrid>
            <ModalContainer id="modal"></ModalContainer>
        </div>;
    }

    componentDidMount() {
        request({
            url: '/api/cityList',loading: true,
            success: (resp) => {
                if (resp.code == 0) {
                    this.refs.grid.state.data=resp.data.cityList;
                    this.setState({});
                }
            }
        });
    }
}


ReactDOM.render(
    <Page/>
    , document.getElementById('root'));

