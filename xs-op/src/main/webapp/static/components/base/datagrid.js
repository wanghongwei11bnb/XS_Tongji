class Table extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        const {columns, data} = this.props;
        const theadHtml = <thead>
        <tr>
            <th colSpan={columns.length} className="text-danger">
                {data ? `${data.length}条数据` : null}
            </th>
        </tr>
        <tr>{columns.map((column) => {
            return <th width={column.width}>{column.title || column.field}</th>
        })}</tr>
        </thead>;
        const tbodyHtml = <tbody>
        {data ? data.map((row, index) => {
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
        }) : null}
        </tbody>;
        return (<table className="table table-hover table-bordered">
            {theadHtml}{tbodyHtml}
        </table>);
    }
}

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
            return <th width={column.width}>{column.title || column.field}</th>
        })}</tr>
        </thead>;
        const tbodyHtml = <tbody>
        {data ? data.map((row, index) => {
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
        }) : null}
        </tbody>;


        return (<table className="table table-hover table-bordered">
            {theadHtml}{tbodyHtml}
        </table>);
    }
}
