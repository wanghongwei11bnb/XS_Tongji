class ListEditor extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            itemType: props.itemType,
            itemRender: props.itemRender,
            data: props.initialData || [],
            modalId: UUID.get(),
        };
    }

    add = () => {
        this.state.data.push(null);
        this.setState({});
    };

    del = (index) => {
        this.state.data.splice(index, 1);
        this.setState({});
    };

    itemUpdate = (index, value, update) => {
        if (value) {
            this.state.data[index] = value;
        } else if (update) {
            update(this.state.data);
        }
        this.setState({});
    };

    show = () => {
        let message = <pre><code>{JSON.stringify(this.state.data, 2, 2)}</code></pre>;
        ModalContainer[this.state.modalId].open(<AlertModal message={message}></AlertModal>);
    };

    getData = () => {
        return this.state.data;
    };

    setData = (data) => {
        this.state.data = data || [];
        this.setState({});
    };

    render() {
        const {data, itemType, itemRender, modalId} = this.state;
        return <div>
            {data.map((item, index) => {
                return <div className="row m-1 p-1 border">
                    <div className="col">{itemRender(item, index, this.itemUpdate.bind(this, index))}</div>
                    <span><button type="button" className="btn btn-sm btn-danger mx-3"
                                  onClick={this.del.bind(this, index)}>删除</button></span>
                </div>;
            })}
            <button type="button" className="btn btn-sm btn-primary mx-3" onClick={this.add}>添加</button>
            <button type="button" className="btn btn-sm btn-success mx-3" onClick={this.show}>查看</button>
            <ModalContainer id={modalId}></ModalContainer>
        </div>
    }
}




