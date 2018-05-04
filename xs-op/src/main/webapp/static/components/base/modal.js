class ModalContainer extends React.Component {
    constructor(props) {
        super(props);
        ModalContainer[props.id] = this;
        this.state = {
            zIndex: 100,
        };
    }

    render() {
        const {modal} = this.state;
        return <div
            className="modal-container">{modal ? React.cloneElement(modal, {modalContainer: this}) : null}</div>;
    }

    open = (modal) => {
        this.state.modal = modal;
        this.setState({});
    };
    close = () => {
        this.state.modal = null;
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
        return this.state.footer || null;
    };

    close = () => {
        const {modalContainer} = this.props;
        if (modalContainer) {
            modalContainer.close();
        }
    };

    render() {
        const header = this.renderHeader();
        const body = this.renderBody();
        const footer = this.renderFooter();
        return <div className="modal show clearfix">
            <div className="modal-layer"/>
            <div className="modal-dialog">
                <div className="modal-content">
                    {header ? <div className="modal-header ">{header}</div> : null}
                    {body ? <div className="modal-body">{body}</div> : null}
                    {footer ? <div className="modal-footer ">{footer}</div> : null}
                </div>
            </div>
        </div>
    }
}

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










