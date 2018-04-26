class Datagrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            title: this.props.title,
            columns: this.props.columns || [],
            url: this.props.url,
            data: this.props.data || [],
            method: this.props.method,
            queryParams: this.props.queryParams,
            rowStyler: this.props.rowStyler,
        };
    }

    render() {
        const {title, columns, data} = this.state;
        const theadHtml = <thead>
        <tr>{columns.map((column) => {
            return <th>{column.title || column.field}</th>
        })}</tr>
        </thead>;
        const tbodyHtml = <tbody>
        {data.map((row, index) => {
            return <tr>
                {columns.map((column, cIndex) => {
                    let value = column.field ? row[column.field] : null;
                    if (column.render) {
                        return <td>{column.render(value, row, index)}</td>
                    } else {
                        return <td>{value}</td>;
                    }
                })}
            </tr>
        })}
        </tbody>;


        return (<table className="table">
            {theadHtml}{tbodyHtml}
        </table>);
    }
}

class Messager extends React.Component {
    constructor(props) {
        super(props);
        Messager.instance = this;
        this.state = {
            zIndex: 100,
            items: [],
        };
    }

    render() {
        const {items} = this.state;
        return <div>
            {items.map((item, index) => {
                return <div>{index}</div>
            })}
        </div>;
    }


    alert(properties) {
        this.state.items.push(
            <div className="message">
                <div className="message-layer"></div>
                <div className="message-box"></div>
            </div>
        );
        this.setState({});
    }
}

Messager.alert = function (properties) {
    Messager.instance.alert(properties);
};


class Modal extends React.Component {
    constructor(props) {
        super(props);
        Modal.instance = this;
        this.state = {
            zIndex: 100,
            items: [],
        };
    }

    render() {
        const {items} = this.state;
        return <div className="modals">
            <div className="modal-layer"></div>
            {items.map((item) => {
                return <div key={item.key} className="window">
                    <div className="window-header">{item.header}</div>
                    <div className="window-body">{item.body}</div>
                    <div className="window-footer">{item.footer}</div>
                </div>
            })}
        </div>;
    }

    panel = (properties) => {
        this.state.items.push({
            key: UUID.get(),
            body: <div>body</div>
        });
        this.setState({});
    };
}

Modal.panel = function (properties) {
    Modal.instance.panel(properties);
};


class TextInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            initialValue: props.initialValue || '',
            value: props.initialValue || '',
        };
    }

    setValue = (value) => {
        this.setState({value});
    };

    getValue = () => {
        return this.state.value;
    };

    onChange = (event) => {
        this.setState({value: event.target.value});
    };

    render() {
        const {value} = this.state;
        return <input ref="input" className="form-control" type="text" value={value} onChange={this.onChange}/>
    }
}


class NumberInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            initialValue: props.initialValue || '',
            value: props.initialValue || '',
        };
    }

    setValue = (value) => {
        this.setState({value});
    };

    getValue = () => {
        return this.state.value;
    };

    onChange = (event) => {
        this.setState({value: event.target.value});
    };

    render() {
        const {value} = this.state;
        return <input ref="input" className="form-control" type="number" value={value} onChange={this.onChange}/>
    }
}

class EmailInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            initialValue: props.initialValue || '',
            value: props.initialValue || '',
        };
    }

    setValue = (value) => {
        this.setState({value});
    };

    getValue = () => {
        return this.state.value;
    };

    onChange = (event) => {
        this.setState({value: event.target.value});
    };

    render() {
        const {value} = this.state;
        return <input ref="input" className="form-control" type="text" value={value} onChange={this.onChange}/>
    }
}

class SelectInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            initialValue: props.initialValue || '',
            value: props.initialValue || '',
            options: props.options,
            valueField: props.valueField || 'id',
            textField: props.textField || 'text',
        };
    }

    setValue = (value) => {
        this.setState({value});
    };

    getValue = () => {
        return this.state.value;
    };

    onChange = (event) => {
        this.setState({value: event.target.value});
    };

    render() {
        const {value, options, valueField, textField} = this.state;
        return <select ref="select" className="form-control" value={value} onChange={this.onChange}>
            <option></option>
            {options.map((option) => {
                return <option value={option[valueField]}>{option[textField]}</option>
            })}
        </select>
    }
}

