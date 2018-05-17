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


