package com.xiangshui.server.bean;

import org.joda.time.LocalTime;

public class PeakTime {
    private LocalTime start_time;
    private LocalTime end_time;

    public LocalTime getStart_time() {
        return start_time;
    }

    public PeakTime setStart_time(LocalTime start_time) {
        this.start_time = start_time;
        return this;
    }

    public LocalTime getEnd_time() {
        return end_time;
    }

    public PeakTime setEnd_time(LocalTime end_time) {
        this.end_time = end_time;
        return this;
    }
}
