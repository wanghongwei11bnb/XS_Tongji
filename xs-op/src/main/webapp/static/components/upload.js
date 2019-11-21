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
            startXHR: () => {
                Loading.open();
            },
            startNonXHR: () => {
                Loading.open();
            },
            endXHR: () => {
                Loading.close();
            },
            endNonXHR: () => {
                Loading.close();
            },
            onComplete: (filename, resp) => {
                if (!resp) {
                    alert(filename + '上传失败');
                    return false;
                }
                if (resp.code == 0) {
                    if (this.props.onSuccess) this.props.onSuccess(resp.data.url);
                    else Modal.open(<AlertModal>{resp.data.url}</AlertModal>);
                } else {
                    Message.msg(resp.msg);
                }
            }
        });
    }
}




// class UploadFileButton extends React.Component {
//     constructor(props) {
//         super(props);
//         this.state = {};
//     }
//
//     render() {
//
//         return <div className={this.props.className} style={this.props.style} onClick={this.onClick}>
//             {this.props.children}
//             <input ref="input" type="file" accept="image/x-png,image/gif,image/jpeg,image/bmp" className="hide" onChange={this.onChange}/>
//         </div>
//     }
//
//     onClick = () => {
//         this.refs.input.click();
//     };
//
//
//     onChange = () => {
//         const formdata = new FormData();
//         formdata.append('file', this.refs.input.files[0]);
//         request({
//             url: '/api/v1/getUploadToken', method: 'post',
//             success: resp => {
//                 reqwest({
//                     url: "http://upload.qiniup.com?token=" + resp.data.token,
//                     method: 'post',
//                     data: formdata,
//                     dataType: 'json',
//                     processData: false,
//                     contentType: false,
//                     success: resp => {
//                         if (!resp) {
//                             return Message.error(filename + '上传失败');
//                         }
//                         if (resp.key) {
//                             let url = 'http://pyfqnfyt6.bkt.clouddn.com/' + resp.key;
//                             this.props.onSuccess(url);
//                         } else {
//                             Message.msg(resp.msg);
//                         }
//                     }
//                 });
//             }
//         });
//
//
//     }
// }