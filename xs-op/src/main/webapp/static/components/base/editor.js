class Editor extends React.Component {
    constructor(props) {
        super(props);
        this.state = {id: UUID.get()};
    }

    setValue = (value) => {
        this.editor.txt.html(value);
    };

    getValue = () => {
        return this.editor.txt.html();
    };


    clear = () => {
        this.editor.txt.clear();
    };

    render() {
        return <Fixed>
            <div>
                <div>
                    <button className="btn btn-sm btn-primary m-1" onClick={() => {
                        Modal.open(<AlertModal>
                            {this.editor.txt.html()}
                        </AlertModal>);
                    }}>get
                    </button>
                    <button className="btn btn-sm btn-primary m-1" onClick={() => {
                        this.editor.txt.clear()
                    }}>clean
                    </button>
                </div>
                <div id={this.state.id}>
                    <p>欢迎使用 <b>wangEditor</b> 富文本编辑器</p>
                </div>
            </div>
        </Fixed>
    }

    componentDidMount() {
        this.editor = new window.wangEditor(document.getElementById(this.state.id));
        this.editor.create()
    }
}