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
                    }}>查看源码
                    </button>
                    <button className="btn btn-sm btn-primary m-1" onClick={() => {
                        this.editor.txt.clear()
                    }}>清楚
                    </button>
                </div>
                <div id={this.state.id}>
                </div>
            </div>
        </Fixed>
    }

    componentDidMount() {
        this.editor = new window.wangEditor(document.getElementById(this.state.id));
        this.editor.customConfig.uploadImgServer = '/api/article/img/upload';
        this.editor.customConfig.uploadImgMaxSize = 3 * 1024 * 1024;
        this.editor.customConfig.uploadImgMaxLength = 1;
        this.editor.customConfig.uploadFileName = 'uploadFile';
        this.editor.customConfig.uploadImgHooks = {
            customInsert: function (insertImg, result, editor) {
                if (result.code === 0) {
                    insertImg(result.data.url)
                } else {
                    Message.error(result.msg);
                }
            }
        };
        this.editor.create();
    }
}