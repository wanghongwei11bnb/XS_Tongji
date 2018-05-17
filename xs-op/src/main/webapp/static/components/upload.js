class FileUploadButton extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            id: UUID.get(),
        };
    }

    render() {
        return <Fixed>
            <button ref="button" id={this.state.id} disabled={this.props.disabled}
                    className={this.props.className}>{this.props.children}</button>
        </Fixed>
    }

    componentDidMount() {
        new ss.SimpleUpload({
            button: this.state.id,
            url: '/api/upload/area_img',
            name: 'uploadFile', responseType: 'json',
            onComplete: function (filename, resp) {
                if (!resp) {
                    alert(filename + '上传失败');
                    return false;
                }
                if (resp.code == 0) {
                    Modal.open(<AlertModal>{resp.data.url}</AlertModal>);
                } else {
                    Message.msg(resp.msg);
                }
            }
        });
    }
}
