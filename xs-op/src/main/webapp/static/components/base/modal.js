class ModalContainer extends React.Component {
    constructor(props) {
        super(props);
        if (props.id) {
            ModalContainer[props.id] = this;
        } else {
            ModalContainer.default = this;
        }
        this.state = {
            modalMap: {},
            zIndex: 1000,
        };
    }

    render() {
        const {modalMap} = this.state;
        const modals = [];
        for (let id in modalMap) {
            let modal = modalMap[id];
            modals.push(modal);
        }
        return <div className="modal-container">{modals}</div>;
    }

    checkScroll = () => {
        for (let id in this.state.modalMap) {
            document.body.style.overflow = 'hidden';
            return;
        }
        document.body.style.overflow = 'auto';
    };

    open = (modal) => {
        let id = UUID.get();
        this.state.modalMap[id] = React.cloneElement(modal, {id, zIndex: ++this.state.zIndex, modalContainer: this});
        this.setState({});
        this.checkScroll();
    };
    close = (id) => {
        delete this.state.modalMap[id];
        this.setState({});
        this.checkScroll();
    };
}


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
        return <A className="btn btn-link text-secondary float-right" onClick={this.close}>关闭</A>;
    };

    close = () => {
        const {modalContainer} = this.props;
        if (modalContainer) {
            modalContainer.close(this.props.id);
        }
    };

    reViewSize = () => {
        this.refs.body.style.maxHeight = window.innerHeight - this.refs.header.offsetHeight - this.refs.footer.offsetHeight - 50 + 'px';
    };

    render() {
        const header = this.renderHeader();
        const body = this.renderBody();
        const footer = this.renderFooter();
        return <div ref="modal" className="modal show clearfix" style={{zIndex: this.props.zIndex}}>
            <div ref="layer" className="modal-layer"/>
            <div ref="dialog" className="modal-dialog m-0">
                <div ref="dialog" className="modal-content">
                    <div ref="header" className="modal-header ">{header}</div>
                    <div ref="body" className="modal-body">{body}</div>
                    <div ref="footer" className="modal-footer ">{footer}</div>
                </div>
            </div>
        </div>
    }

    componentDidMount() {
        this.reViewSize();
        eventUtil.addHandler(window, 'resize', this.reViewSize);
    }

    componentWillUnmount() {
        eventUtil.removeHandler(window, 'resize', this.reViewSize);
    }
}

Modal.open = function (modal) {
    ModalContainer.default.open(modal);
};

class AlertModal extends Modal {

    constructor(props) {
        super(props);
        this.state = {
            message: props.message,
            title: props.title,
        };
    }

    renderHeader = () => {
        return this.props.title || null;
    };


    renderBody = () => {
        return this.props.children;
    };

    renderFooter = () => {
        return <A className="btn btn-link text-secondary float-right" onClick={this.close}>确定</A>;
    };

}

class ConfirmModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {};
    }

    renderHeader = () => {
        return this.props.title || null;
    };


    renderBody = () => {
        return this.props.children;
    };

    renderFooter = () => {
        return [
            <A className="btn btn-link text-primary float-right" onClick={this.ok}>确定</A>,
            <A className="btn btn-link text-secondary float-right" onClick={this.cancel}>取消</A>,
        ];
    };

    ok = () => {
        this.close();
        if (this.props.ok) this.props.ok();
    };
    cancel = () => {
        this.close();
        if (this.props.cancel) this.props.cancel();
    };

}


class PromptModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            dataType: props.dataType || 'String',
        };
    }

    renderHeader = () => {
        return this.props.title || null;
    };

    renderBody = () => {
        return <input ref="input" type="text" className="form-control"/>;
    };

    renderFooter = () => {
        return [
            <A className="btn btn-link text-primary float-right" onClick={this.ok}>确定</A>,
            <A className="btn btn-link text-secondary float-right" onClick={this.cancel}>取消</A>,
        ];
    };

    ok = () => {
        let value = this.refs.input.value;
        if (type(value) == 'Undefined' || type(value) == 'Null' || !value) {
            return Message.msg('请输入');
        }
        this.close();
        if (this.props.ok) this.props.ok(value);
    };
    cancel = () => {
        this.close();
        if (this.props.cancel) this.props.cancel();
    };


}



