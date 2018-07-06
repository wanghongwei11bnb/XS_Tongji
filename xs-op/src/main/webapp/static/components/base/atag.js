class A extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return <a {...this.props} href="javascript:void(0);"></a>
    }
}
