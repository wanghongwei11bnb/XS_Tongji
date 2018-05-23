class Tabs extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            tabs: props.tabs || [],
            activeIndex: props.activeIndex || 0,
            height: props.height,
        };
    }

    height = (value) => {
        if (value) {
            this.setState({height});
        } else {
            return this.state.height;
        }
    };

    getTabByTitle = (title) => {
        const {tabs} = this.state;
        for (let i = 0; i < tabs.length; i++) {
            let tab = tabs[i];
            if (tab.title == title) {
                return tab;
            }
        }
    };

    addTab = (tab, active) => {
        const {tabs, activeIndex} = this.state;
        tabs.push(tab);
        if (active) {
            this.setState({activeIndex: tabs.length - 1});
        } else {
            this.setState({});
        }
    };

    removeTab = (index) => {
        const {tabs, activeIndex} = this.state;
        tabs.splice(index, 1);
        if (activeIndex >= tabs.length) {
            this.setState({activeIndex: tabs.length - 1});
        } else {
            this.setState({});
        }
    };

    checkTitle = (title) => {
        const {tabs} = this.state;
        for (let i = 0; i < tabs.length; i++) {
            let tab = tabs[i];
            if (tab.title == title) {
                this.checkIndex(i);
                return;
            }
        }
    };

    checkIndex = (index) => {
        const {tabs, activeIndex} = this.state;
        if (tabs.length > index) {
            this.setState({activeIndex: index});
        }
    };

    render() {
        const {tabs, activeIndex, height} = this.state;
        return <div className="position-relative d-block">
            <ul ref="nav" className="nav nav-tabs p-1">
                {tabs ? tabs.map((tab, index) => {
                    return <li key={`tab-bar-${index}`} className="nav-item">
                        <div
                            className={`nav-link d-inline-block p-1 ${activeIndex == index ? "active btn-primary text-white" : ""}`}>
                        <span className="hm d-inline-block" onClick={this.checkIndex.bind(this, index)}>
                            {tab.title}
                        </span>
                            <span className="hm d-inline-block ml-3"
                                  onClick={this.removeTab.bind(this, index)}>x</span>
                        </div>
                    </li>
                }) : null}
            </ul>
            {tabs ? tabs.map((tab, index) => {
                return <div key={`tab-content-${tab.title}`}
                            className={`position-relative w-100 h-100 ${activeIndex == index ? 'show' : 'hide'}`}>
                    {tab.content}
                </div>
            }) : null}
        </div>;
    }

    componentDidUpdate() {
    }

}


class Iframe extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        return <iframe ref="iframe"   {...this.props}></iframe>
    }

    resize = () => {
        this.refs.iframe.height = window.innerHeight - 50;
    };

    componentDidMount() {
        this.resize();
        eventUtil.addHandler(window, 'resize', this.resize);
    }

    componentWillUnmount() {
        eventUtil.removeHandler(window, 'resize', this.resize);
    }
}

class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showMenu: true,
            menuWidth: '150px',

        };
    }

    toggleMenu = () => {
        this.setState({showMenu: !this.state.showMenu});
    };


    checkTab = (title, url) => {
        if (this.refs.tabs.getTabByTitle(title)) {
            this.refs.tabs.checkTitle(title);
        } else {
            this.refs.tabs.addTab({
                title, content: <Iframe src={url} className="w-100 border-0"></Iframe>
            }, true);
        }
    };


    render() {
        const {showMenu, menuWidth} = this.state;
        return <div className="position-fixed w-100 h-100">
            <div className="position-absolute top-0 bottom-0 left-0 border-right"
                 style={{width: showMenu ? menuWidth : '50px'}}>
                <A className="d-block text-center" onClick={this.toggleMenu}>{showMenu ? '<<' : '>>'}</A>
                {showMenu ? null : <A className="d-block text-center" onClick={this.logout}>退出</A>}
                <ul className={`nav flex-column ${showMenu ? '' : 'hide'}`}>
                    {debug ? <li className="nav-item">
                        <A className="nav-link"
                           onClick={this.checkTab.bind(this, '时时监控平台', 'http://tj.xiangshuispace.com/index.html')}>时时监控平台</A>
                    </li> : null}
                    {debug ? <li className="nav-item">
                        <A className="nav-link"
                           onClick={this.checkTab.bind(this, '数据汇总', 'http://tj.xiangshuispace.com/tj/home')}>数据汇总</A>
                    </li> : null}
                    {debug ? <li className="nav-item">
                        <A className="nav-link"
                           onClick={this.checkTab.bind(this, '城市列表', '/city_manage')}>城市列表</A>
                    </li> : null}
                    <li className="nav-item">
                        <A className="nav-link"
                           onClick={this.checkTab.bind(this, '用户管理', '/user_manage')}>用户管理</A>
                    </li>
                    <li className="nav-item">
                        <A className="nav-link"
                           onClick={this.checkTab.bind(this, '场地管理', '/area_manage')}>场地管理</A>
                    </li>
                    {debug || (op_username && op_username.indexOf("zhangqun@") > -1) ? <li className="nav-item">
                        <A className="nav-link"
                           onClick={this.checkTab.bind(this, '订单管理', '/booking_manage')}>订单管理</A>
                    </li> : null}
                    <li className="nav-item hide">
                        <A className="nav-link"
                           onClick={this.checkTab.bind(this, '用户评论', '/appraise_manage')}>用户评论</A>
                    </li>
                    <li className="nav-item">
                        <A className="nav-link"
                           onClick={this.checkTab.bind(this, '故障报修', '/failure_manage')}>故障报修</A>
                    </li>
                    {debug ? <li className="nav-item">
                        <A className="nav-link"
                           onClick={this.checkTab.bind(this, '场地方用户管理', '/partner_manage')}>场地方用户管理</A>
                    </li> : null}
                    <li className="nav-item float-right">
                        <A className="nav-link"
                           onClick={this.logout}>退出</A>
                    </li>
                    <li className="nav-item float-right px-2">
                        <FileUploadButton className="btn btn-sm btn-success">头等舱图片上传</FileUploadButton>
                    </li>
                </ul>
            </div>
            <div className="position-absolute top-0 bottom-0 right-0" style={{left: showMenu ? menuWidth : '50px'}}>
                <Tabs ref="tabs"></Tabs>
            </div>
            <ModalContainer></ModalContainer>
        </div>;
    }

    logout = () => {
        Modal.open(<ConfirmModal ok={() => {
            request({
                url: '/api/logout', method: 'post', success: () => {
                    location.reload();
                }
            });
        }}>是否退出？</ConfirmModal>);
    };

    componentDidMount() {
    }
}


ReactDOM.render(<Page/>, document.getElementById('root'));

