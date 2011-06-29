package com.example.lib.workload;

import com.example.lib.util.LatencyPeriod;

public abstract class WorkloadTask {

    protected final WorkloadManager manager;

    protected final LatencyPeriod latencyPeriod = new LatencyPeriod();

    protected WorkloadTask(WorkloadManager manager) {
        this.manager = manager;
    }

    public abstract void executeTask() throws Exception;

}
