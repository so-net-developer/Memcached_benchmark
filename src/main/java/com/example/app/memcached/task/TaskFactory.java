package com.example.app.memcached.task;

import java.util.ArrayList;
import java.util.List;

import com.example.lib.benchmark.AbstractedBenchmarkTask;
import com.example.lib.benchmark.BenchmarkParam;
import com.example.lib.benchmark.BenchmarkTaskFactory;
import com.example.lib.memcached.MemcachedAdapter;
import com.example.lib.util.LatencyPeriod;

public abstract class TaskFactory implements BenchmarkTaskFactory {

    private TaskParam taskParam = null;

    private MemcachedAdapter memcachedAdapter = null;

    private final LatencyPeriod latencyPeriod = new LatencyPeriod();
    
    protected TaskFactory() {
    }

    public void setTaskParam(TaskParam taskParam) {
        this.taskParam = taskParam;
    }

    public void setMemcachedAdapter(MemcachedAdapter adapter) {
        this.memcachedAdapter = adapter;
    }

    @Override
    public final AbstractedBenchmarkTask createBenchmarkTask(
            int index,
            BenchmarkParam param)
    {
        return createBenchmarkTask(index, param, this.taskParam,
                this.memcachedAdapter);
    }

    public abstract AbstractedBenchmarkTask createBenchmarkTask(
            int index,
            BenchmarkParam benchmarkParam,
            TaskParam taskParam,
            MemcachedAdapter memcachedAdapter);

    @Override
    public void disposeBenchmarkTask(AbstractedBenchmarkTask task) {
        // Sum latencyPeriod when the task has.
        MemcachedTask memcachedTask = (MemcachedTask)task;
        if (memcachedTask != null) {
            this.latencyPeriod.add(memcachedTask.getLatencyPeriod());
        }
    }

    @Override
    public List<String> getTaskResultsString() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(String.format(
                    "latency (nanosec): min=%2$,d max=%3$,d avg=%4$,d",
                    this.getClass().getSimpleName(),
                    this.latencyPeriod.getMin(),
                    this.latencyPeriod.getMax(),
                    this.latencyPeriod.getAverage()));
        return list;
    }

    @Override
    public void resetTaskResults() {
        this.latencyPeriod.reset();
    }

}
