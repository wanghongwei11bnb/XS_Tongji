class TextInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            initialValue: props.initialValue || null,
            required: props.required || false,
            validType: props.validType || null,
            validator: props.validator || null,
            validateOnBlur: props.validateOnBlur || false,
            validateOnCreate: props.validateOnCreate || false,
            value: null,
        };
    }

    validate = () => {
        return true;
    };

    setValue = (value) => {

        this.setState({value});
    };

    getValue = () => {
        return this.state.value;
    };

    onChange = (e) => {
        this.setValue(e.target.value);
    };

    render() {
        const {value} = this.state;
        return <input ref="input" type="text" value={value} onChange={this.onChange}/>
    }

    componentDidMount() {
        const {initialValue} = this.state;
        if (initialValue) this.setValue(initialValue);
    }

}


class IntInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            value: props.initialValue || null,
        };
    }

    setValue = (value) => {
        if (value) {
            if (/^\d+$/.test(value) && type(value - 0) == 'Number') {
                this.setState({value: value - 0});
            } else {
                this.setState({});
            }
        } else {
            this.setState({value: null});
        }
    };

    onChange = (e) => {
        let newValue = e.target.value;
        this.setValue(newValue);
    };

    render() {
        const {value} = this.state;
        return <input ref="input" type="text"
                      className={this.props.className}
                      placeholder={this.props.placeholder}
                      onChange={this.onChange}
                      value={value}/>
    }
}

class StringInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }

    setValue = (value) => {
        this.refs.input.value = type(value) === 'String' ? value : null;
    };

    getValue = () => {
        return this.refs.input.value;
    };

    render() {
        return <input ref="input"
                      type="text"
                      disabled={this.props.disabled}
                      readOnly={this.props.readOnly}
                      className={this.props.className}
                      onChange={this.props.onChange}
        />
    }

    componentDidMount() {
        this.setValue(this.props.initialValue);
    }

}

class NumberInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }


    render() {
        return <input ref="input"
                      type="number"
                      disabled={this.props.disabled}
                      readOnly={this.props.readOnly}
                      className={this.props.className}
                      onChange={this.props.onChange}
        />
    }

    componentDidMount() {
        this.setValue(this.props.initialValue);
    }

    setValue = (value) => {
        this.refs.input.value = type(value) === 'Number' ? value : null;
    };

    getValue = () => {
        let value = this.refs.input.value;
        if (type(value, 'String')) {
            value = new Number(value);
        }
        if (type(value, 'Number')) {
            return value;
        } else {
            return null;
        }
    };
}


class PriceInput extends NumberInput {
    constructor(props) {
        super(props);
    }

    setValue = (value) => {
        this.refs.input.value = type(value, 'Number') ? value / 100 : null;
    };

    getValue = () => {
        let value = this.refs.input.value;
        if (type(value, 'String')) {
            value = new Number(value);
        }
        if (type(value, 'Number')) {
            return value * 100;
        } else {
            return null;
        }
    };
}