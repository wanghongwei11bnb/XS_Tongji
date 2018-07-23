class Editor extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            editorId: UUID.get(),
        };
    }

    render() {
        return <Fixed>
            <div id={this.state.editorId}></div>
        </Fixed>
    }

    componentDidMount() {
        this.editor = new wangEditor(`#${this.state.editorId}`);
        this.editor.create();
    }

    setValue = (value) => {
        this.editor.txt.html(value);
    };

    getValue = () => {
        return this.editor.txt.html();
    };

}