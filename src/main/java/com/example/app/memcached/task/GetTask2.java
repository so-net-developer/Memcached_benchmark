package com.example.app.memcached.task;

import com.example.lib.benchmark.AbstractedBenchmarkTask;
import com.example.lib.benchmark.BenchmarkParam;
import com.example.lib.memcached.MemcachedAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GetTask2 extends GetTask {

    private static final Log LOG = LogFactory.getLog(GetTask2.class);

    public static class Factory extends GetTask.Factory {

        private int hitPercent;

        public Factory(int hitPercent) {
            this.hitPercent = hitPercent;
        }

        @Override
        public AbstractedBenchmarkTask createBenchmarkTask(
                int index,
                BenchmarkParam benchmarkParam,
                TaskParam taskParam,
                MemcachedAdapter adapter)
        {
            return new GetTask2(adapter, taskParam, this.hitPercent);
        }

    }

    private int hitPercent;

    private GetTask2(
            MemcachedAdapter adapter,
            TaskParam taskParam,
            int hitPercent)
    {
        super(adapter, taskParam);
        this.hitPercent = hitPercent;
    }

    @Override
    public Log getLogger() {
        return LOG;
    }

    @Override
    protected String getKey(int subIndex) {
        if ((subIndex % 100) < this.hitPercent) {
            return Generator.getValidKey(this.index, subIndex);
        } else {
            return Generator.getInvalidKey(this.index, subIndex);
        }
    }

}
