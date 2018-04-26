const columns = [
    {
        field: 'id', title: '用户编号', width: 100,
        render: function (value, row, index) {
            return <a href="javascript:void(0);">{value}</a>;
        }
    },
    {field: 'phone', title: '手机号', width: 100},
    {
        field: 'create_time', title: '创建时间', width: 100,
        render: function (value, row, index) {
            if (value) {
                return new Date(value).format('yyyy-MM-dd');
            }
        }
    },
    {field: 'last_login_time', title: '上次登录时间', width: 100},
    {
        field: 'area_list', title: '合作场地', width: 100,
        render: function (value, row, index) {
            if (value && value.length > 0) {
                return value.map(function (item, i) {
                    return item.title
                }).join(",");
            }
        }
    },
    {
        field: '操作', title: '操作', width: 200,
        render: function (value, row, index) {
            return <a href="javascript:void(0);">场地管理</a>;
        }
    },
];

class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }


    onSubmit = (event) => {
        event = event || window.event;
        event.preventDefault(); // 兼容标准浏览器
        window.event.returnValue = false; // 兼容IE6~8
        this.search();
    };

    search = () => {
        const {grid} = this.refs;
        reqwest({
            url: '/api/partner/search', method: 'get',
            data: {
                city: this.refs.city.value,
                phone: this.refs.phone.value,
            },
            success: (resp) => {
                if (resp.code == 0) {
                    grid.state.data = resp.data.list;
                    grid.setState({});
                } else {
                }
            }
        });
    };

    render() {
        const {cityList} = this.state;
        return <div className="container-fluid">
            <div className="m-1">
                <form onSubmit={this.onSubmit} className="form-inline">
                    <div className="form-group">
                        <label>城市：</label>
                        <select ref="city" className="form-control">
                            <option value=""></option>
                            {cityList ? cityList.map((city) => {
                                return <option value={city.city}>{city.city}</option>
                            }) : null}
                        </select>
                    </div>
                    <div className="form-group">
                        <label>手机号：</label>
                        <input ref="phone" type="text" className="form-control"/>
                    </div>
                    <button type="submit" className="btn btn-sm btn-primary ml-1">搜索</button>
                </form>
            </div>

            <Datagrid ref="grid" columns={columns}></Datagrid>
            <Messager></Messager>
            <Modal></Modal>
        </div>;
    }

    componentDidMount() {
        this.search();
        reqwest({
            url: '/api/cityList',
            success: (resp) => {
                if (resp.code == 0) {
                    this.setState({cityList: resp.data.cityList});
                }
            }
        });
    }
}


ReactDOM.render(
    <Page/>
    , document.getElementById('root'));

