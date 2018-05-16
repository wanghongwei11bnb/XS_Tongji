class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    onSubmit = () => {
        if (!this.refs.username.value) {
            Message.msg('用户名不能为空');
            return;
        }
        if (!this.refs.password.value) {
            Message.msg('密码不能为空');
            return;
        }
        request({
            url: '/api/login', method: 'post', loading: true,
            data: {
                username: this.refs.username.value,
                password: this.refs.password.value,
            },
            success: (resp) => {
                if (resp.code == 0) {
                    Message.msg('登录成功');
                    if (location.pathname == '/login') {
                        location.replace('/');
                    } else {
                        location.reload();
                    }
                }
            }
        });
    };

    render() {
        const {cityList, columns, data} = this.state;
        return <div className="cxy position-fixed">
            <input ref="username" type="text" className="form-control my-3" placeholder="用户名"/>
            <input ref="password" type="password" className="form-control my-3" placeholder="密码"/>
            <div className="text-right my-3">
                <button type="button" className="btn btn-primary" onClick={this.onSubmit}>登录</button>
            </div>
        </div>;
    }

}


ReactDOM.render(
    <Page/>
    , document.getElementById('root'));

