package com.example.app.memcached.task;

import java.util.List;

import com.example.lib.benchmark.AbstractedBenchmarkTask;
import com.example.lib.benchmark.BenchmarkParam;
import com.example.lib.memcached.MemcachedAdapter;
import com.example.lib.util.Counter;
import com.example.lib.util.LatencyPeriod;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GetTask extends MemcachedTask {

    private static final Log LOG = LogFactory.getLog(GetTask.class);

    public static class Factory extends TaskFactory {

        private final Counter counter = new Counter();

        @Override
        public AbstractedBenchmarkTask createBenchmarkTask(
                int index,
                BenchmarkParam benchmarkParam,
                TaskParam taskParam,
                MemcachedAdapter adapter)
        {
            return new GetTask(adapter, taskParam);
        }

        @Override
        public void disposeBenchmarkTask(AbstractedBenchmarkTask task) {
            super.disposeBenchmarkTask(task);

            GetTask getTask = (GetTask)task;
            if (getTask != null) {
                this.counter.add(getTask.counter);
            }
        }

        @Override
        public List<String> getTaskResultsString() {
            List<String> list = super.getTaskResultsString();
            list.add(String.format(
                        "hit-rate (percent): key-found=%1$.2f%% value-matched=%2$.2f%%",
                        (this.counter.getFound() * 100f /
                            this.counter.getTotal()),
                        (this.counter.match * 100f /
                         this.counter.getFound())));
            return list;
        }

        @Override
        public void resetTaskResults() {
            super.resetTaskResults();
            this.counter.reset();
        }

    }

    private int iteration;

    private int revision;

    protected int index;

    private final Counter counter = new Counter();

    protected GetTask(MemcachedAdapter adapter, TaskParam taskParam) {
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
        this.counter.reset();
    }

    protected String getKey(int subIndex) {
        return Generator.getKey(this.index, subIndex);
    }

    @Override
    public void mainTask() throws Exception {
        for (int i = 0; i < this.iteration; ++i) {
            String key = getKey(i);
            this.latencyPeriod.start();
            String value = this.adapter.get(key);
            this.latencyPeriod.end();
            if (value == null) {
                ++this.counter.notfound;
            } else if (value.equals(Generator.getValue(this.index, i,
                            this.revision))) {
                ++this.counter.match;
            } else {
                ++this.counter.unmatch;
            }
            ++this.score;
        }
    }

    @Override
    public void clearTask() throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("notfound=%1$d match=%2$d unmatch=%3$d",
                        this.counter.notfound, this.counter.match,
                        this.counter.unmatch));
        }
    }

}
