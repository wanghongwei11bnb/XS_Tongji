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
        return <div className="position-relative d-block ">
            <ul ref="nav" className="nav nav-tabs p-1">
                {tabs ? tabs.map((tab, index) => {
                    return <li key={`tab-bar-${index}`} className="nav-item">
                        <div
                            className={`nav-link d-inline-block ${activeIndex == index ? "active" : ""}`}>
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

    componentDidMount() {
        // this.interval = setInterval(() => {
        this.refs.iframe.height = window.innerHeight - 150;
        // }, 100);
    }

    componentWillUnmount() {
        clearInterval(this.interval);
    }
}

class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }


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
        const {} = this.state;
        return <div className="container-fluid">
            <ul className="nav nav-pills  mb-3 bg-secondary">
                <li className="nav-item">
                    <a className="nav-link text-white" href="javascript:void(0);"
                       onClick={this.checkTab.bind(this, '城市列表', '/city_manage')}>城市列表</a>
                </li>
                <li className="nav-item">
                    <a className="nav-link text-white" href="javascript:void(0);"
                       onClick={this.checkTab.bind(this, '场地管理', '/area_manage')}>场地管理</a>
                </li>
                <li className="nav-item">
                    <a className="nav-link text-white" href="javascript:void(0);"
                       onClick={this.checkTab.bind(this, '故障报修', '/failure_manage')}>故障报修</a>
                </li>
                <li className="nav-item">
                    <a className="nav-link text-white" href="javascript:void(0);"
                       onClick={this.checkTab.bind(this, '场地方用户管理', '/partner_manage')}>场地方用户管理</a>
                </li>
            </ul>

            <Tabs ref="tabs"></Tabs>
        </div>;
    }

    componentDidMount() {
    }
}


ReactDOM.render(<Page/>, document.getElementById('root'));
