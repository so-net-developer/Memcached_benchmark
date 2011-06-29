package com.example.app.memcached.task;

import com.example.lib.benchmark.AbstractedBenchmarkTask;
import com.example.lib.benchmark.BenchmarkParam;
import com.example.lib.memcached.MemcachedAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SetTask extends MemcachedTask {

    private static final Log LOG = LogFactory.getLog(SetTask.class);

    public static class Factory extends TaskFactory {

        @Override
        public AbstractedBenchmarkTask createBenchmarkTask(
                int index,
                BenchmarkParam benchmarkParam,
                TaskParam taskParam,
                MemcachedAdapter adapter)
        {
            return new SetTask(adapter, taskParam);
        }

    }

    private int iteration;

    private int revision;

    private int index;

    private SetTask(MemcachedAdapter adapter, TaskParam taskParam) {
        super(adapter);
        this.iteration = taskParam.iteration;
        this.revision = taskParam.revision;
    }

    @Override
    public Log getLogger() {
        return LOG;
    }

    @Override
    public void setupTask(int index, BenchmarkParam param) throws Exception {
        this.index = index;
    }

    @Override
    public void mainTask() throws Exception {
        for (int i = 0; i < this.iteration; ++i) {
            this.latencyPeriod.start();
            this.adapter.set(Generator.getKey(this.index, i),
                    Generator.getValue(this.index, i, this.revision));
            this.latencyPeriod.end();
            ++this.score;
        }
    }

}
