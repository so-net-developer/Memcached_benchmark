package com.example.app.memcached_benchmark.task;

import java.util.List;

import com.example.lib.memcached.MemcachedAdapter;
import com.example.lib.util.Counter;
import com.example.lib.workload.WorkloadManager;
import com.example.lib.workload.WorkloadTask;

/**
 * memcachedを使う負荷試験タスクのベースクラス.
 */
public abstract class MemcachedWorkloadTask extends WorkloadTask {

    protected final MemcachedAdapter memcachedAdapter;

    public final Counter counter = new Counter();

    protected MemcachedWorkloadTask(
            WorkloadManager manager,
            MemcachedAdapter memcachedAdapter)
    {
        super(manager);
        this.memcachedAdapter = memcachedAdapter;
    }

}
