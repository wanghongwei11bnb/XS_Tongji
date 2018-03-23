package com.xiangshui.tj.server.task;

public class TaskEntry<R> {
    private Task task;
    private R result;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public R getResult() {
        return result;
    }

    public void setResult(R result) {
        this.result = result;
    }
}
