class ModalContainer extends React.Component {
    constructor(props) {
        super(props);
        if (props.id) {
            ModalContainer[props.id] = this;
        } else {
            ModalContainer.default = this;
        }
        this.state = {
            modalMap: {},
            zIndex: 1000,
        };
    }

    render() {
        const {modalMap} = this.state;
        const modals = [];
        for (let id in modalMap) {
            let modal = modalMap[id];
            modals.push(modal);
        }
        return <div className="modal-container">{modals}</div>;
    }

    open = (modal) => {
        let id = UUID.get();
        this.state.modalMap[id] = React.cloneElement(modal, {id, zIndex: ++this.state.zIndex, modalContainer: this});
        this.setState({});
    };
    close = (id) => {
        delete this.state.modalMap[id];
        this.setState({});
    };
}


class Modal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    renderHeader = () => {
        return this.state.header || null;
    };
    renderBody = () => {
        return this.state.body || null;
    };
    renderFooter = () => {
        return <span className="float-right">
                <button type="button" className="btn btn-link text-secondary" onClick={this.close}>关闭</button>
            </span>;
    };

    close = () => {
        const {modalContainer} = this.props;
        if (modalContainer) {
            modalContainer.close(this.props.id);
        }
    };

    reViewSize = () => {
        this.refs.body.style.maxHeight = window.innerHeight - this.refs.header.offsetHeight - this.refs.footer.offsetHeight - 50 + 'px';
    };

    render() {
        const header = this.renderHeader();
        const body = this.renderBody();
        const footer = this.renderFooter();
        return <div ref="modal" className="modal show clearfix" style={{zIndex: this.props.zIndex}}>
            <div ref="layer" className="modal-layer"/>
            <div ref="dialog" className="modal-dialog m-0">
                <div ref="dialog" className="modal-content">
                    <div ref="header" className="modal-header ">{header}</div>
                    <div ref="body" className="modal-body">{body}</div>
                    <div ref="footer" className="modal-footer ">{footer}</div>
                </div>
            </div>
        </div>
    }

    componentDidMount() {
        this.reViewSize();
        eventUtil.addHandler(window, 'resize', this.reViewSize);
    }

    componentWillUnmount() {
        eventUtil.removeHandler(window, 'resize', this.reViewSize);
    }
}

Modal.open = function (modal) {
    ModalContainer.default.open(modal);
};

class AlertModal extends Modal {

    constructor(props) {
        super(props);
        this.state = {
            message: props.message,
            title: props.title,
        };
    }

    renderHeader = () => {
        return this.state.title || null;
    };


    renderBody = () => {
        return this.state.message || null;
    };

    renderFooter = () => {
        return <button type="button" className="btn btn-link text-secondary float-right"
                       onClick={this.close}>确定</button>;
    };

}


class ShowAreaModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            area: props.area,
            area_id: props.area_id,
            cityList: props.cityList || [],
        };
    }

    renderHeader = () => {
        return '场地信息';
    };
    renderFooter = () => {
        return <span className="float-right">
                <button type="button" className="btn btn-link text-secondary" onClick={this.close}>取消</button>
            </span>;
    };
    renderBody = () => {
        const area = this.state.area || {};
        const cityList = this.props.cityList || [];
        return <div>
            <table className="table">
                <tbody>
                <tr>
                    <th>场地编号</th>
                    <td>
                        <input ref="area_id" type="text" disabled={true} readOnly={true} className="form-control"
                               value={area.area_id}/>
                    </td>
                </tr>
                <tr>
                    <th>标题</th>
                    <td>
                        <input ref="title" type="text" className="form-control" value={area.title}/>
                    </td>
                </tr>
                <tr>
                    <th>城市</th>
                    <td>
                        <select ref="city" className="form-control" value={area.city}>
                            <option value=""></option>
                            {cityList.map((city) => {
                                return <option value={city.city}>{city.city}</option>
                            })}
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>地址</th>
                    <td>
                        <input ref="address" type="text" className="form-control" value={area.address}/>
                    </td>
                </tr>
                <tr>
                    <th>最少时长</th>
                    <td>
                        <input ref="minute_start" type="text" className="form-control" value={area.minute_start}/>
                    </td>
                </tr>
                <tr>
                    <th>高峰时段</th>
                    <td>
                        <input ref="rushHours" type="text" className="form-control" value={area.rushHours}/>
                    </td>
                </tr>
                <tr>
                    <th>经纬度</th>
                    <td>
                        <input ref="location" type="text" className="form-control" value={area.location}/>
                    </td>
                </tr>
                <tr>
                    <th>联系方式</th>
                    <td>
                        <input ref="contact" type="text" className="form-control" value={area.contact}/>
                    </td>
                </tr>
                <tr>
                    <th>提醒文案</th>
                    <td>
                        <textarea ref="notification" className="form-control" value={area.notification}></textarea>
                    </td>
                </tr>
                <tr>
                    <th>状态</th>
                    <td>
                        <select ref="status" className="form-control" value={area.status}>
                            <option value="0">正常</option>
                            <option value="-1">已下架</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>地图URL</th>
                    <td>
                        <input ref="area_img" type="text" className="form-control" value={area.area_img}/>
                    </td>
                </tr>
                <tr>
                    <th>图片URL</th>
                    <td>
                        <ListEditor data={area.imgs} ref="imgs" itemRender={(item, index, itemUpdate) => {
                            return [<img src={`${item}_227`} alt=""/>,
                                <input type="text" className="form-control" value={item} onChange={(e) => {
                                    itemUpdate(e.target.value)
                                }}/>];
                        }}></ListEditor>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>;
    };

    componentDidMount() {
        const {area, area_id} = this.state;
        if (area_id) {
            request({
                url: `/api/area/${area_id}`,
                success: (resp) => {
                    this.setState({area: resp.data.area});
                }
            });
        }
    }
}










