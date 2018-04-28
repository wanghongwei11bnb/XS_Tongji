class Modal extends React.Component {
    constructor(props) {
        super(props);
        Modal.instance = this;
        this.state = {
            zIndex: 100,
            items: [],
        };
    }

    render() {
        const {items} = this.state;
        return <div className="modals">
            {items.map((item) => {
                return <div key={item.key} className="modal-item">
                    <div className="modal-layer"></div>
                    <div className="window">
                        <div className="window-header px-3 py-1 border-bottom">{item.header}</div>
                        <div className="window-body p-3">{item.body}</div>
                        <div className="window-footer px-3 py-1 border-top">{item.footer}</div>
                    </div>
                </div>
            })}
        </div>;
    }

    remove = (key) => {
        const {items} = this.state;
        for (let i = 0; i < items.length; i++) {
            let item = items[i];
            if (key == item.key) {
                items.splice(i, 1);
                this.setState({});
                return;
            }
        }

    };

    panel = (properties) => {
        let key = UUID.get();
        this.state.items.push({
            key,
            body: <div>{properties.content}</div>,
            header: <div className="clearfix">
                {properties.title}
                <button type="button" className="btn btn-link btn-sm float-right text-secondary"
                        onClick={this.remove.bind(this, key)}>
                    关闭
                </button>
                <button type="button" className="btn btn-link btn-sm float-right text-primary"
                        onClick={this.remove.bind(this, key)}>
                    保存
                </button>
            </div>,
        });
        this.setState({});
    };
}

Modal.panel = function (properties) {
    Modal.instance.panel(properties);
};
