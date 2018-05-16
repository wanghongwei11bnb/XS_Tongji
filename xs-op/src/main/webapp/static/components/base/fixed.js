class Fixed extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return this.props.children;
    }

    componentDidMount() {
        if (this.props.onDidMount) {
            this.props.onDidMount();
        }
    }

    shouldComponentUpdate() {
        return false;
    }
}