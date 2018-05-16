class Binary extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            columns: props.columns,
            value: props.initialValue || 0,
        };
    }

    setValue = (value) => {
        this.setState({value});
    };

    getValue = () => {
        return this.state.value;
    };

    checkIndex = (index) => {
        const {value} = this.state;
        if ((value & Math.pow(2, index)) == Math.pow(2, index)) {
            this.setState({value: value ^ Math.pow(2, index)});
        } else {
            this.setState({value: value | Math.pow(2, index)});
        }
    };

    render() {
        const {columns, value} = this.state;
        return <div>
            {columns.map((column, index) => {
                if (column.render) {
                    return column.render((value & Math.pow(2, index)) == Math.pow(2, index), value, index);
                } else {
                    return [<input type="checkbox"
                                   checked={(value & Math.pow(2, index)) == Math.pow(2, index)}
                                   onChange={this.checkIndex.bind(this, index)}/>, column.title]
                }
            })}
        </div>;
    }
}