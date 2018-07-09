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
        return <div className="position-relative d-block h-100">
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
                            className={`tab-content position-relative w-100 h-100 ${activeIndex == index ? 'show' : 'hide'}`}>
                    {tab.content}
                </div>
            }) : null}
        </div>;
    }

    componentDidUpdate() {
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
        if (window.innerWidth < 900) {
            location.assign(url);
            return;
        }
        if (this.refs.tabs.getTabByTitle(title)) {
            this.refs.tabs.checkTitle(title);
        } else {
            this.refs.tabs.addTab({
                title, content: <iframe src={url} className="w-100 border-0"></iframe>
            }, true);
        }
    };


    render() {
        const {showMenu, menuWidth} = this.state;
        return <div className="position-fixed w-100 h-100">
            <div className="position-absolute top-0 bottom-0 left-0 border-right"
                 style={{overflow: 'auto', width: showMenu ? menuWidth : '50px'}}>
                <A className="d-block text-center" onClick={this.toggleMenu}>{showMenu ? '<<' : '>>'}</A>
                {showMenu ? null : <A className="d-block text-center" onClick={this.logout}>退出</A>}
                <ul className={`nav flex-column ${showMenu ? '' : 'hide'}`}>
                    {webMenuList.map((menu) => {
                        return <li className="nav-item">
                            <A className="nav-link"
                               onClick={this.checkTab.bind(this, menu.title, menu.path)}>{menu.title}</A>
                        </li>
                    })}
                    <li className="nav-item float-right">
                        <A className="nav-link" onClick={this.logout}>退出</A>
                    </li>
                    <li className="nav-item float-right">
                        {op_username}
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
loadOpInfo();

ReactDOM.render(<Page/>, document.getElementById('root'));

