const GridUtils = {
    mkBaseColumn: function (field, title, render) {
        return {field: field, title: title || field, render};
    },
    mkDateColumn: function (field, title, format) {
        return this.mkBaseColumn(field, title, value => {
            return value ? new Date(value * 1000).format(format || 'yyyy-MM-dd hh:mm') : value;
        });
    },
    mkOptionColumn: function (field, title, options) {
        return this.mkBaseColumn(field, title, value => {
            if (options == null || options.length === 0) return value || null;
            for (let i = 0; i < options.length; i++) {
                if (options[i].value == value) {
                    switch (options[i].color) {
                        case "success":
                            return <span className="text-success">{options[i].text}</span>;
                        case "primary":
                            return <span className="text-primary">{options[i].text}</span>;
                        case "danger":
                            return <span className="text-danger">{options[i].text}</span>;
                        default:
                            return options[i].text;
                    }
                }
            }
            return value || null;
        });
    },
};


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

        const tbody = <tbody ref="tbody">
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
        const thead = <thead ref="thead">
        <tr>
            <th colSpan={columns.length} className="text-danger">
                {
                    this.props.renderHeader ?
                        this.props.renderHeader(data ? `${data.length}条数据` : null, data, columns)
                        : (data ? `${data.length}条数据` : null)
                }
            </th>
        </tr>
        <tr>{columns.map((column) => {
            let style = {};
            if (column.width) {
                let width = type(column.width) === 'Number' ? `${column.width}px` : (type(column.width) === 'String' ? column.width : undefined);
                style.minWidth = width;
                style.maxWidth = width;
            } else if (column.minWidth) {
                style.minWidth = type(column.minWidth) === 'Number' ? `${column.minWidth}px` : (type(column.minWidth) === 'String' ? column.minWidth : undefined);
            } else if (column.maxWidth) {
                style.maxWidth = type(column.maxWidth) === 'Number' ? `${column.maxWidth}px` : (type(column.maxWidth) === 'String' ? column.maxWidth : undefined);
            }
            return <th style={style}>
                {column.title || column.field}
                {column.totalHandle ?
                    [<br/>, <span className="text-danger">{column.totalName || 'Total'}（{column.total}）</span>]
                    : null}
            </th>
        })}</tr>
        </thead>;
        return <table className="table table-hover table-bordered">{thead}{tbody}</table>;
    }

    fixedThead = (top) => {
        this.cleanFixedThead();
        this.handScroll = () => {
            let scrollTop = domUtil.scrollTop();
            let y = domUtil.getY(this.refs.thead);
            if (scrollTop + top - y > 0) {
                this.refs.thead.style.transform = `translateY(${scrollTop + top - y}px)`;
            } else {
                this.refs.thead.style.transform = 'none';
            }
        };
        eventUtil.addHandler(document, 'scroll', this.handScroll);
    };

    cleanFixedThead = () => {
        if (this.handScroll) eventUtil.removeHandler(document, 'scroll', this.handScroll);
    };

    componentWillUnmount() {
        this.cleanFixedThead();
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

