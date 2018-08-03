


class Page extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return <div className="container-fluid my-3">

            <Editor></Editor>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));