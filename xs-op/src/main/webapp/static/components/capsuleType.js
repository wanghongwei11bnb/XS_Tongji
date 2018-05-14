class CapsuleTypeModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            capsuleType: props.capsuleType,
            onSuccess: props.onSuccess,
        };
    }

    onSubmit = () => {
        const {onSuccess} = this.state;
        if (!this.refs.type_id.value) return Message.msg('类型id 不能为空');
        if (!this.refs.price.value) return Message.msg('价格 不能为空');
        if (!this.refs.day_max_price.value) return Message.msg('每日最高费用 不能为空');
        if (!this.refs.price_rule_text.value) return Message.msg('价格文案 不能为空');
        if (!this.refs.typeTitle.value) return Message.msg('标题 不能为空');
        if (!this.refs.typeDesc.value) return Message.msg('描述 不能为空');
        let data = {
            type_id: this.refs.type_id.value,
            size: this.refs.size.value,
            day_max_price: this.refs.day_max_price.value,
            price: this.refs.price.value,
            rush_hour_price: this.refs.rush_hour_price.value,
            price_rule_text: this.refs.price_rule_text.value,
            typeDesc: this.refs.typeDesc.value,
            typeTitle: this.refs.typeTitle.value,

        };
        if (onSuccess) onSuccess(data);
        this.close();
    };
    renderHeader = () => {
        return '头等舱类型信息';
    };
    renderFooter = () => {
        return <span className="float-right">
                <button type="button" className="btn btn-link text-primary" onClick={this.onSubmit}>确定</button>
                <button type="button" className="btn btn-link text-secondary" onClick={this.close}>取消</button>
            </span>;
    };
    renderBody = () => {
        return <div>
            <table className="table table-bordered">
                <tbody>
                <tr>
                    <th>类型id</th>
                    <td>
                        <input ref="type_id" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>价格</th>
                    <td>
                        <input ref="price" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>高峰期价格</th>
                    <td>
                        <input ref="rush_hour_price" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>每日最高费用</th>
                    <td>
                        <input ref="day_max_price" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>价格文案</th>
                    <td>
                        <input ref="price_rule_text" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>面积</th>
                    <td>
                        <input ref="size" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>标题</th>
                    <td>
                        <input ref="typeTitle" type="text" className="form-control"/>
                    </td>
                </tr>
                <tr>
                    <th>描述</th>
                    <td>
                        <input ref="typeDesc" type="text" className="form-control"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>;
    };

    componentDidMount() {
        super.componentDidMount();
        const {capsuleType} = this.state;
        if (capsuleType) {
            this.refs.type_id.value = capsuleType.type_id;
            this.refs.size.value = capsuleType.size;
            this.refs.day_max_price.value = capsuleType.day_max_price;
            this.refs.price.value = capsuleType.price;
            this.refs.rush_hour_price.value = capsuleType.rush_hour_price;
            this.refs.price_rule_text.value = capsuleType.price_rule_text;
            this.refs.typeDesc.value = capsuleType.typeDesc;
            this.refs.typeTitle.value = capsuleType.typeTitle;
        }
    }
}


class CapsuleTypeGridModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            columns: [
                {field: 'type_id', title: '类型id'},
                {field: 'size', title: '面积'},
                {field: 'day_max_price', title: '每日最高费用'},
                {field: 'price', title: '价格'},
                {field: 'rush_hour_price', title: '高峰期价格'},
                {field: 'price_rule_text', title: '价格文案'},
                {field: 'typeDesc', title: '描述'},
                {field: 'typeTitle', title: '标题'},
                {
                    title: <button type="button" className="btn btn-sm btn-success"
                                   onClick={this.addNew}>新建类型</button>,
                    render: (value, row, index) => {
                        return [
                            <button type="button" className="btn btn-sm btn-primary m-1"
                                    onClick={this.editRow.bind(this, index, row)}>修改</button>,
                            <button type="button" className="btn btn-sm btn-danger m-1"
                                    onClick={this.removeRow.bind(this, index, row)}>删除</button>,
                        ]
                    }
                },
            ],
            area_id: props.area_id,
            types: props.types,
        };
    }


    setData = (types) => {
        this.refs.grid.state.data = types;
        this.refs.grid.setState({});
    };

    getData = () => {
        return this.refs.grid.state.data;
    };


    editRow = (index, row) => {
        Modal.open(<CapsuleTypeModal capsuleType={row} onSuccess={(data) => {
            this.refs.grid.state.data[index] = data;
            this.refs.grid.setState({});
        }}></CapsuleTypeModal>);
    };

    onSubmit = () => {
        const {area_id, onSuccess} = this.state;
        request({
            url: `/api/area/${area_id}/update/types`, method: 'post', contentType: 'application/json', loading: true,
            data: JSON.stringify({
                types: this.refs.grid.state.data
            }, nullStringReplacer),
            success: (resp) => {
                if (resp.code == 0) {
                    Message.msg('保存成功');
                    this.close();
                    if (onSuccess) onSuccess();
                }
            }
        });
    };

    removeRow = (index, row) => {
        this.refs.grid.state.data.splice(index, 1);
        this.refs.grid.setState({});
    };

    addNew = (index, row) => {
        Modal.open(<CapsuleTypeModal onSuccess={(data) => {
            this.refs.grid.state.data.push(data);
            this.refs.grid.setState({});
        }}></CapsuleTypeModal>);
    };

    renderHeader = () => {
        return '头等舱类型信息';
    };
    renderFooter = () => {
        return <span className="float-right">
                <button type="button" className="btn btn-link text-primary" onClick={this.onSubmit}>保存</button>
                <button type="button" className="btn btn-link text-secondary" onClick={this.close}>取消</button>
            </span>;
    };

    renderBody = () => {
        const {columns} = this.state;
        return <div>
            <Datagrid ref="grid" columns={columns}></Datagrid>
        </div>
    };

    componentDidMount() {
        super.componentDidMount();
        const {area_id, types} = this.state;
        this.setData(types);

        if (!types && area_id) {
            request({
                url: `/api/area/${area_id}/types`, loading: true,
                success: (resp) => {
                    if (resp.code == 0) {
                        this.setData(resp.data.types);
                    }
                }
            });

        }

    }
}
