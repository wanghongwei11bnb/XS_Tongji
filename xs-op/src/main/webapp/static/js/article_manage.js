class ArticleModal extends Modal {
    constructor(props) {
        super(props);
        this.state = {
            article_id: props.article_id,
            create: !!props.create,
            update: !!props.update,
        };
    }

    submit = () => {
        const {create, update, article_id} = this.state;
        let data = {
            type: 0,
            title: this.refs.title.getValue(),
            summary: this.refs.summary.getValue(),
            author: this.refs.author.getValue(),
            remark: this.refs.remark.getValue(),
            content: this.refs.content.getValue(),
            head_img: this.refs.head_img.getValue(),
            release_time: this.refs.release_time.getValue(),
        };
        request({
            url: create ? '/api/article/create' : `/api/article/${article_id}/update`,
            method: 'post', contentType: 'application/json', loading: true,
            data: JSON.stringify(data),
            success: resp => {
                Message.msg('保存成功！');
                this.close();
                if (this.props.onSuccess) {
                    this.props.onSuccess(create ? resp.data.article_id : undefined);
                }
            }
        });
    };


    renderBody = () => {
        const {create, update, article_id} = this.state;
        return <table className="table table-bordered">
            <tbody>
            <tr>
                <th>ID</th>
                <td>
                    <NumberInput ref="id" readOnly={true} disabled={true} className="form-control"></NumberInput>
                </td>
            </tr>
            <tr>
                <th>标题</th>
                <td>
                    <StringInput ref="title" className="form-control"></StringInput>
                </td>
            </tr>
            <tr>
                <th>头图Url</th>
                <td>
                    <StringInput ref="head_img" className="form-control"></StringInput>
                </td>
            </tr>
            <tr>
                <th>作者</th>
                <td>
                    <StringInput ref="author" className="form-control"></StringInput>
                </td>
            </tr>
            <tr>
                <th>摘要</th>
                <td>
                    <StringInput ref="summary" className="form-control"></StringInput>
                </td>
            </tr>
            <tr>
                <th>备注</th>
                <td>
                    <StringInput ref="remark" className="form-control"></StringInput>
                </td>
            </tr>
            <tr>
                <th>发布日期</th>
                <td>
                    <DateInput ref="release_time" className="form-control"></DateInput>
                </td>
            </tr>
            <tr>
                <td colSpan={2}>
                    <Editor ref="content"></Editor>
                </td>
            </tr>
            </tbody>
        </table>

    };


    renderFooter = () => {
        const {create, update, article_id} = this.state;

        if (create || update) {
            return [
                <button className="btn btn-link mx-1 text-primary" onClick={this.submit}>保存</button>,
                <button className="btn btn-link mx-1 text-secondary" onClick={this.close}>取消</button>,
            ];
        } else {
            return <button className="btn btn-link mx-1 text-secondary" onClick={this.close}>关闭</button>;
        }

    };

    review = (article) => {
        if (article) {
            this.refs.id.setValue(article.id);
            this.refs.title.setValue(article.title);
            this.refs.author.setValue(article.author);
            this.refs.summary.setValue(article.summary);
            this.refs.head_img.setValue(article.head_img);
            this.refs.remark.setValue(article.remark);
            this.refs.release_time.setValue(article.release_time);
            this.refs.content.setValue(article.content);
        }
    };

    componentDidMount() {
        const {create, update, article_id} = this.state;
        if (!create && article_id) {
            request({
                url: `/api/article/${article_id}`, loading: true,
                success: resp => {
                    this.review(resp.data.article);
                }
            });
        }
    }
}


class ArticleGrid extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            queryParams: props.queryParams,
            total: 0,
            pageSize: props.pageSize || 100,
            pageNum: 1,
            columns: [
                {field: 'id', title: 'ID'},
                {field: 'title', title: '标题'},
                {
                    field: 'head_img', title: '头图', render: value => {
                        if (type(value, 'String')) {
                            return <img src={value} alt={value} style={{maxWidth: '10em'}}/>
                        }
                    }
                },
                {field: 'author', title: '作者'},
                {field: 'summary', title: '摘要'},
                {field: 'release_time', title: '发布日期', render: value => type(value, 'Number') ? new Date(value).format('yyyy-MM-dd') : null},
                {
                    field: 'id', title: <button className="btn btn-sm btn-success" onClick={this.create}>新建</button>, render: value => {
                        return [
                            <button className="btn btn-sm m-1 btn-primary" onClick={this.update.bind(this, value)}>编辑</button>,
                            <button className="btn btn-sm m-1 btn-danger" onClick={this.delete.bind(this, value)}>删除</button>,
                        ];
                    }
                },
            ],
        };
    }

    load = (queryParams) => {
        if (queryParams) {
            this.state.queryParams = queryParams;
            this.state.pageNum = 1;
        }
        request({
            url: '/api/article/search', loading: true,
            data: {
                ...this.state.queryParams,
                ...{pageSize: this.state.pageSize, pageNum: this.state.pageNum},
            },
            success: resp => {
                this.setState({
                    articleList: resp.data.articleList,
                    total: resp.data.total,
                });
            }
        });
    };

    create = () => {
        Modal.open(<ArticleModal create onSuccess={this.load}></ArticleModal>);
    };

    update = (article_id) => {
        Modal.open(<ArticleModal article_id={article_id} update onSuccess={this.load}></ArticleModal>);
    };

    delete = (article_id) => {
        Modal.open(<ConfirmModal ok={() => {
            request({
                url: `/api/article/${article_id}/delete`, method: 'post', loading: true,
                success: resp => {
                    Message.msg('操作成功！');
                    this.load();
                }
            });
        }}>
            确定要删除？
        </ConfirmModal>);
    };

    render() {
        return <div>
            <Table columns={this.state.columns} data={this.state.articleList}></Table>
        </div>
    }


}

class Page extends React.Component {
    constructor(props) {
        super(props);
        this.state = {};
    }


    search = () => {
        this.refs.grid.load({
            title: this.refs.title.value,
        });
    };


    render() {
        return <div className="container-fluid my-3">
            <div className="m-1">
                标题：
                <input ref="title" type="text" className="form-control form-control-sm d-inline-block mx-3 w-auto"/>
                <button type="button" className="btn btn-sm btn-primary ml-1" onClick={this.search}>搜索</button>
            </div>
            <ArticleGrid ref="grid"></ArticleGrid>
            <ModalContainer></ModalContainer>
        </div>;
    }

    componentDidMount() {
        this.search();
    }
}

ReactDOM.render(<Page/>, document.getElementById('root'));