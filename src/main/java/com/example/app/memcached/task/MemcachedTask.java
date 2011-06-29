package com.example.app.memcached.task;

import com.example.lib.benchmark.AbstractedBenchmarkTask;
import com.example.lib.benchmark.BenchmarkParam;
import com.example.lib.memcached.MemcachedAdapter;
import com.example.lib.util.LatencyPeriod;

public abstract class MemcachedTask extends AbstractedBenchmarkTask {

    protected final MemcachedAdapter adapter;

    protected final LatencyPeriod latencyPeriod = new LatencyPeriod();

    protected long score = 0;

    protected MemcachedTask(MemcachedAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void setupTask(int index, BenchmarkParam param) throws Exception {
        this.score = 0;
        this.latencyPeriod.reset();
    }

    @Override
    public void clearTask() throws Exception {
        // nothing to do default.
    }

    @Override
    public long getScore() {
        return this.score;
    }

    public LatencyPeriod getLatencyPeriod() {
        return this.latencyPeriod;
    }

}
