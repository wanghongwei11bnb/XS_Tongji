class Table extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        const {columns, data} = this.props;

        columns.map((column) => {
            column.total = null;
        });

        const tbody = <tbody>
        {data ? data.map((row, index) => {
            return <tr>
                {columns.map((column, cIndex) => {
                    let value = column.field ? row[column.field] : null;

                    if (column.totalHandle) {
                        column.total = column.totalHandle(column.total, value);
                    }

                    if (column.render) {
                        return <td>{column.render(value, row, index)}</td>
                    } else {
                        return <td>{value}</td>;
                    }
                })}
            </tr>
        }) : null}
        </tbody>;
        const thead = <thead>
        <tr>
            <th colSpan={columns.length} className="text-danger">
                {data ? `${data.length}条数据` : null}
            </th>
        </tr>
        <tr>{columns.map((column) => {
            return <th width={column.width}>{column.title || column.field}{column.totalHandle ?
                [<br/>, <span className="text-danger">{column.totalName || 'Total'}（{column.total}）</span>] : null}</th>
        })}</tr>
        </thead>;
        return (<table className="table table-hover table-bordered">
            {thead}{tbody}
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


class Grid extends React.Component {
    constructor(props) {
        super(props);
    }

    componentWillMount() {
        if (this.props.handleColumns) {
            this.props.handleColumns(this.state.columns, this);
        }
    }

    render() {
        return <Table columns={this.state.columns} data={this.state.data}></Table>
    }

}

