package com.xiangshui.op.count;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public abstract class CountProcessor<T> {

    public CountResult count(List<T> data) {
        CountResult countResult = createCountResult();
        handStart(data, countResult);
        if (data != null) {
            for (T t : data) {
                reduce(t, countResult);
            }
        }
        handEnd(data, countResult);
        return countResult;
    }

    protected CountResult createCountResult() {
        return new CountResult();
    }

    protected abstract void reduce(T t, CountResult countResult);

    protected abstract void handStart(List<T> data, CountResult countResult);

    protected abstract void handEnd(List<T> data, CountResult countResult);


    public abstract void countForDownload(List<T> data, HttpServletResponse response) throws IOException;


}
