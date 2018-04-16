package com.xiangshui.tj.server.task;

public class TaskEntry<R> {
    private AbstractTask task;
    private R result;

    public AbstractTask getTask() {
        return task;
    }

    public void setTask(AbstractTask task) {
        this.task = task;
    }

    public R getResult() {
        return result;
    }

    public void setResult(R result) {
        this.result = result;
    }
}
