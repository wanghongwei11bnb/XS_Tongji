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

class NumberInput extends React.Component {
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
        this.setState({value: type(value) == 'Number' ? value : null});
    };

    getValue = () => {
        const {value} = this.state;
        return value;
    };

    onChange = (e) => {
        this.setValue(e.target.value);
    };

    render() {
        const {value} = this.state;
        return <input ref="input" type="number" value={value} onChange={this.onChange}/>
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


class PriceInput extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            value: null,
        };
    }

    setValue = (value) => {
        if (type(value) == 'Number') {
            this.refs.input.value = Math.floor(value) / 100;
        } else if (type(value) == 'String') {
            this.setValue(value - 0);
        } else {
            this.refs.input.value = 0;
        }
    };

    getValue = () => {
        let value = this.refs.input.value;
        if (type(value) == 'Number') {
            return Math.floor(value * 100);
        } else if (type(value) == 'String' && type(value - 0) == 'Number') {
            return Math.floor((value - 0) * 100);
        } else {
            return 0;
        }
    };

    onChange = (e) => {
        let value = e.target.value;
        if (/^\d*\.?\d*$/.test(value)) {
            this.setState({value});
        } else {
            this.setState({});
        }
    };

    render() {
        const {value} = this.state;
        return <input ref="input" type="text"
                      className={this.props.className}
                      placeholder={this.props.placeholder}
                      onChange={this.onChange}
                      value={value}/>
    }

    componentDidUpdate() {
        this.value = this.getValue();
    }
}

