class SwiperItemModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            swiperItemId: props.swiperItemId,
            create: !!props.create,
            update: !!props.update,
        };
    }


    renderBody = () => {
        const {swiperItemId, create, update} = this.state;
        return <table className="table table-sm table-bordered">
            <tbody>
            <tr>
                <th>ID</th>
                <td>
                    <input ref="id" type="text" className="form-control" readOnly={true} disabled={true} value={swiperItemId}/>
                </td>
            </tr>
            <tr>
                <th>标题</th>
                <td>
                    <input ref="title" type="text" className="form-control" readOnly={!(create || update)} disabled={!(create || update)}/>
                </td>
            </tr>
            <tr>
                <th>副标题</th>
                <td>
                    <input ref="sub_title" type="text" className="form-control" readOnly={!(create || update)} disabled={!(create || update)}/>
                </td>
            </tr>
            <tr>
                <th>图片</th>
                <td>
                    <input ref="img" type="text" className="form-control" readOnly={!(create || update)} disabled={!(create || update)}/>
                </td>
            </tr>
            <tr>
                <th>链接</th>
                <td>
                    <input ref="link" type="text" className="form-control" readOnly={!(create || update)} disabled={!(create || update)}/>
                </td>
            </tr>
            <tr>
                <th>平台</th>
                <td>
                    <select ref="app" className="form-control" disabled={!(create || update)}>
                        <option value={null}></option>
                        <option value="wx">微信小程序</option>
                        <option value="ali">支付宝小程序</option>
                        <option value="h5">H5</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>状态</th>
                <td>
                    <select ref="status" className="form-control" disabled={!(create || update)}>
                        <option value=""></option>
                        <option value="1">已上线</option>
                        <option value="0">已下线</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>排序编码</th>
                <td>
                    <input ref="sort_num" type="text" className="form-control" readOnly={!(create || update)} disabled={!(create || update)}/>
                </td>
            </tr>
            </tbody>
        </table>
    };

    renderFooter = () => {
        const {swiperItemId, create, update} = this.state;
        return [
            <FileUploadButton className="btn btn-sm mx-1 btn-success">上传图片</FileUploadButton>,
            create || update ? <span className="btn btn-sm mx-1 btn-primary" onClick={this.submit}>提交</span> : null,
            <span className="btn btn-sm mx-1" onClick={this.close}>取消</span>,
        ]
    };

    setView = (swiperItem) => {
        if (swiperItem) {
            this.refs.id.value = swiperItem.id || null;
            this.refs.title.value = swiperItem.title || null;
            this.refs.sub_title.value = swiperItem.sub_title || null;
            this.refs.img.value = swiperItem.img || null;
            this.refs.link.value = swiperItem.link || null;
            this.refs.status.value = swiperItem.status || null;
            this.refs.sort_num.value = swiperItem.sort_num || null;
            this.refs.app.value = swiperItem.app || null;
        }
    };

    componentDidMount() {
        super.componentDidMount();
        const {swiperItemId, create, update} = this.state;
        if (swiperItemId) {
            request({
                url: `/api/swiperItem/${swiperItemId}`, loading: true,
                success: resp => {
                    this.setView(resp.data.swiperItem);
                }
            })
        }
    }

    submit = () => {
        const {swiperItemId, create, update} = this.state;
        const data = {
            title: this.refs.title.value,
            sub_title: this.refs.sub_title.value,
            img: this.refs.img.value,
            link: this.refs.link.value,
            status: this.refs.status.value,
            sort_num: this.refs.sort_num.value,
            app: this.refs.app.value,
        };
        if (create) {
            request({
                url: '/api/swiperItem/create', method: 'post', loading: true,
                data,
                success: resp => {
                    Message.msg("保存成功");
                    this.close();
                    if (this.props.onSuccess) this.props.onSuccess();
                }
            })
        } else if (update) {
            request({
                url: `/api/swiperItem/${swiperItemId}/update`, method: 'post', loading: true,
                data,
                success: resp => {
                    Message.msg("保存成功");
                    this.close();
                    if (this.props.onSuccess) this.props.onSuccess();
                }
            })
        }
    };

}

class SwiperItemGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'id'},
                {field: 'title'},
                {field: 'sub_title'},
                {field: 'img', render: value => <img style={{maxWidth: '300px'}} src={value} alt=""/>},
                {field: 'link'},
                {field: 'status'},
                {field: 'app'},
                {field: 'create_time'},
                {
                    field: 'id', title: <span className="btn btn-sm btn-success mx-1" onClick={this.create}>新建</span>, render: value => [
                        <span className="btn btn-sm m-1 btn-primary" onClick={this.update.bind(this, value)}>编辑</span>,
                        <span className="btn btn-sm m-1 btn-danger" onClick={this.delete.bind(this, value)}>删除</span>,
                        <span className="btn btn-sm m-1 btn-success">上移</span>,
                        <span className="btn btn-sm m-1 btn-warning">下移</span>,
                    ]
                },
            ],
        };
    }

    create = () => {
        Modal.open(<SwiperItemModal create onSuccess={this.load}></SwiperItemModal>);
    };
    update = (swiperItemId) => {
        Modal.open(<SwiperItemModal swiperItemId={swiperItemId} update onSuccess={this.load}></SwiperItemModal>);
    };
    delete = (swiperItemId) => {
        Modal.open(<ConfirmModal ok={() => {
            request({
                url: `/api/swiperItem/${swiperItemId}/delete`, method: 'post', loading: true,
                success: resp => {
                    Message.msg("删除成功");
                    this.load();
                }
            })
        }}>确定删除？</ConfirmModal>)
    };


    load = (queryParams) => {
        if (queryParams) {
            this.state.queryParams = queryParams;
        }
        request({
            url: this.props.url || '/api/swiperItem/search', loading: true,
            data: this.state.queryParams,
            success: (resp) => {
                this.setState({
                    data: resp.data.swiperItemList,
                });
            }
        });
    };

    render() {
        return <Table columns={this.state.columns} data={this.state.data}></Table>
    }

}


class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }


    getQueryParams = () => {
        return {
            id: this.refs.id.value,
            app: this.refs.app.value,
            status: this.refs.status.value,
        };
    };

    search = () => {
        this.refs.grid.load(this.getQueryParams());
    };
    download = () => {
        let queryParams = this.getQueryParams();
        queryParams.download = true;
        queryParams.payMonth = this.refs.payMonth.value;
        window.open(`/api/booking/search?${queryString(queryParams)}`)
    };


    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                ID：
                <input ref="id" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                平台：
                <select ref="app" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value={null}></option>
                    <option value="wx">微信小程序</option>
                    <option value="ali">支付宝小程序</option>
                    <option value="h5">H5</option>
                </select>
                状态：
                <select ref="status" className="form-control form-control-sm d-inline-block mx-3 w-auto">
                    <option value=""></option>
                    <option value="1">已上线</option>
                    <option value="0">已下线</option>
                </select>
                订单编号：
                <input ref="booking_id" type="text"
                       className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
            </div>
            <SwiperItemGrid ref="grid"></SwiperItemGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.search();
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));

