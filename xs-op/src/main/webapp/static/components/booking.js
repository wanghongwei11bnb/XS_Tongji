class BookingUpdateModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            booking_id: props.booking_id,
        };
    }

    submit = () => {
        request({
            url: `/api/booking/${this.state.booking_id}/update/op`, loading: true, method: 'post',
            data: {
                final_price: this.refs.final_price.value ? Math.floor(this.refs.final_price.value * 100) : null,
                status: this.refs.status.value,
            },
            success: (resp) => {
                Message.msg('保存成功');
                this.close();
                if (this.props.onSuccess) this.props.onSuccess();
            }
        });
    };

    renderBody = () => {
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th>订单金额</th>
                <td>
                    <input ref="final_price" type="text" className="form-control"/>
                </td>
            </tr>
            <tr>
                <th>订单状态</th>
                <td>
                    <select ref="status" className="form-control">
                        <option value="2">待支付</option>
                    </select>
                </td>
            </tr>
            </tbody>
        </table>
    };

    renderFooter = () => {
        return [
            <A className="btn btn-link text-primary float-right" onClick={this.submit}>保存</A>,
            <A className="btn btn-link text-secondary float-right" onClick={this.close}>取消</A>,
        ];
    };

    componentDidMount() {
        super.componentDidMount();
        this.refs.status.value = 2;
        request({
            url: `/api/booking/${this.state.booking_id}`, loading: true,
            success: (resp) => {
                if (resp.code == 0) {
                    if (resp.data.booking) {
                        this.refs.final_price.value = resp.data.booking.final_price ? resp.data.booking.final_price / 100 : null;

                    }
                }
            }
        });
    }

}