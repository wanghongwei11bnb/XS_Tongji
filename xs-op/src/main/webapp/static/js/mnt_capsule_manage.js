class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    search = () => {
        this.refs.grid.load();
    };

    render() {
        return <div className="container-fluid my-3">
            <MinitouCapsuleGrid ref="grid"></MinitouCapsuleGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.search();
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));