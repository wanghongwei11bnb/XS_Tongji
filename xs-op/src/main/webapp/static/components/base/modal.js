class ModalContainer extends React.Component {
    constructor(props) {
        super(props);
        ModalContainer.instance = this;
        this.state = {
            zIndex: 100,
            modals: [],
        };
    }

    render() {
        const {modals, zIndex} = this.state;
        return <div className="modal-container">
            {modals.map((modal, index) => {
                modal.zIndex = zIndex + index;
                return React.cloneElement(modal, {key: modal.key});
            })}
        </div>;
    }

    open = (modal) => {
        if (modal) {
            this.close(modal.key);
            this.state.modals.push(modal);
            this.setState({});
        }
    };
    close = (key) => {
        const {modals} = this.state;
        for (let i = 0; i < modals.length; i++) {
            let modal = modals[i];
            if (key == modals[i].key) {
                modals.splice(i, 1);
                this.setState({});
                return;
            }
        }
    };


}

ModalContainer.open = function (modal) {
    ModalContainer.instance.open(modal);
};

ModalContainer.close = function (key) {
    ModalContainer.instance.close(key);
};

class Modal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    renderHeader = () => {
        return this.state.header || null;
    };
    renderBody = () => {
        return this.state.body || null;
    };
    renderFooter = () => {
        return this.state.footer || null;
    };

    open = () => {
        ModalContainer.open(this);
    };
    close = () => {
        ModalContainer.close(this.props.key);
    };

    render() {
        const header = this.renderHeader();
        const body = this.renderBody();
        const footer = this.renderFooter();
        return <div className="modal show clearfix">
            <div className="modal-layer"/>
            <div className="modal-dialog">
                <div className="modal-content">
                    {header ? <div className="modal-header">{header}</div> : null}
                    {body ? <div className="modal-body">{body}</div> : null}
                    {footer ? <div className="modal-footer">{footer}</div> : null}
                </div>
            </div>
        </div>
    }
}




