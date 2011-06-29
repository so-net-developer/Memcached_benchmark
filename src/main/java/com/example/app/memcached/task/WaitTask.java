package com.example.app.memcached.task;

import com.example.lib.benchmark.AbstractedBenchmarkTask;
import com.example.lib.benchmark.BenchmarkParam;
import com.example.lib.memcached.MemcachedAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WaitTask extends AbstractedBenchmarkTask {

    private static final Log LOG = LogFactory.getLog(WaitTask.class);

    public static class Factory extends TaskFactory {

        @Override
        public AbstractedBenchmarkTask createBenchmarkTask(
                int index,
                BenchmarkParam benchmarkParam,
                TaskParam taskParam,
                MemcachedAdapter adapter)
        {
            return new WaitTask();
        }

    }

    private WaitTask() {
    }

    @Override
    public Log getLogger() {
        return LOG;
    }

    @Override
    public void setupTask(int index, BenchmarkParam param) throws Exception {
        // nothing to do.
    }

    @Override
    public void mainTask() throws Exception {
        Thread.sleep(5000);
    }

    @Override
    public void clearTask() throws Exception {
        // nothing to do.
    }

    @Override
    public long getScore() {
        return 1;
    }

}
