package com.example.app.memcached_benchmark.task;

import java.util.List;

import com.example.app.memcached.task.Generator;
import com.example.lib.memcached.MemcachedAdapter;
import com.example.lib.util.Counter;
import com.example.lib.util.ReportWriter;
import com.example.lib.workload.WorkloadManager;
import com.example.lib.workload.WorkloadParam;

public class GetWorkloadTask extends MemcachedWorkloadTask
{

    public static class Factory extends MemcachedWorkloadTaskFactory {

        @Override
        public MemcachedWorkloadTask createMemcachedWorkloadTask(
                WorkloadManager manager,
                WorkloadParam param,
                long index,
                int parallelIndex,
                MemcachedAdapter memcachedAdapter)
        {
            GetWorkloadTask task = new GetWorkloadTask(manager,
                    memcachedAdapter, index);
            return task;
        }

    }

    private long index;

    private GetWorkloadTask(
            WorkloadManager manager,
            MemcachedAdapter memcachedAdapter,
            long index)
    {
        super(manager, memcachedAdapter);
        this.index = index;
    }

    @Override
    public void executeTask() throws Exception {
        String key = Generator.getKey(0, (int)this.index);
        String estVal = Generator.getValue(0, (int)this.index, 0);

        // memcachedクエリーを発行.
        this.latencyPeriod.start();
        String value = this.memcachedAdapter.get(key);
        this.latencyPeriod.end();

        // 成績更新.
        if (value == null) {
            ++this.counter.notfound;
        } else if (value.equals(estVal)) {
            ++this.counter.match;
        } else {
            ++this.counter.unmatch;
        }
    }

}
