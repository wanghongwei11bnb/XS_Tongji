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


        return (<table className="table table-hover">
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


