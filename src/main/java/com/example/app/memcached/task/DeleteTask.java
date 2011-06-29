package com.example.app.memcached.task;

import com.example.lib.benchmark.AbstractedBenchmarkTask;
import com.example.lib.benchmark.BenchmarkParam;
import com.example.lib.memcached.MemcachedAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DeleteTask extends MemcachedTask {

    private static final Log LOG = LogFactory.getLog(DeleteTask.class);

    public static class Factory extends TaskFactory {

        @Override
        public AbstractedBenchmarkTask createBenchmarkTask(
                int index,
                BenchmarkParam benchmarkParam,
                TaskParam taskParam,
                MemcachedAdapter adapter)
        {
            return new DeleteTask(adapter, taskParam);
        }

    }

    private int iteration;

    private int index;

    private DeleteTask(MemcachedAdapter adapter, TaskParam taskParam) {
        super(adapter);
        this.iteration = taskParam.iteration;
    }

    @Override
    public Log getLogger() {
        return LOG;
    }

    @Override
    public void setupTask(int index, BenchmarkParam param) throws Exception {
        super.setupTask(index, param);
        this.index = index;
    }

    @Override
    public void mainTask() throws Exception {
        for (int i = 0; i < this.iteration; ++i) {
            this.latencyPeriod.start();
            this.adapter.delete(Generator.getKey(this.index, i));
            this.latencyPeriod.end();
            ++this.score;
        }
    }

}
