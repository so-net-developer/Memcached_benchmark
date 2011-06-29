package com.example.app.memcached_benchmark.task;

import java.util.HashMap;
import java.util.List;

import com.example.lib.memcached.MemcachedAdapter;
import com.example.lib.memcached.MemcachedAdapterFactory;
import com.example.lib.util.Counter;
import com.example.lib.util.ReportWriter;
import com.example.lib.workload.WorkloadManager;
import com.example.lib.workload.WorkloadParam;
import com.example.lib.workload.WorkloadTask;
import com.example.lib.workload.WorkloadTaskFactory;

/**
 * memcachedを使う負荷試験タスクのファクトリ.
 */
public abstract class MemcachedWorkloadTaskFactory
    implements WorkloadTaskFactory
{

    protected MemcachedAdapterFactory memcachedAdapterFactory = null;

    private HashMap<Integer, MemcachedAdapter> adapterTable =
        new HashMap<Integer, MemcachedAdapter>();

    public void setMemcachedAdapterFactory(MemcachedAdapterFactory factory) {
        this.memcachedAdapterFactory = factory;
    }

    @Override
    public void setupTaskArgs(List<String> args) {
        // nothing to do as default.
    }

    @Override
    public final WorkloadTask createWorkloadTask(
                WorkloadManager manager,
                WorkloadParam param,
                long index,
                int parallelIndex)
    {
        MemcachedAdapter adapter = getMemcachedAdapter(parallelIndex);
        return createMemcachedWorkloadTask(manager, param, index,
                parallelIndex, adapter);
    }

    public abstract MemcachedWorkloadTask createMemcachedWorkloadTask(
                WorkloadManager manager,
                WorkloadParam param,
                long index,
                int parallelIndex,
                MemcachedAdapter memcachedAdapter);

    @Override
    public Object disposeWorkloadTask(WorkloadTask task) {
        MemcachedWorkloadTask myTask = (MemcachedWorkloadTask)task;
        return myTask != null ? myTask.counter : null;
    }

    @Override
    public Object createReportObject() {
        return new Counter();
    }

    @Override
    public void updateReportObject(Object reportObject, Object newReport) {
        // ヒット率を更新する.
        Counter c = (Counter)reportObject;
        Counter c2 = (Counter)newReport;
        if (c != null && c2 != null) {
            c.add(c2);
        }
    }

    private static final String REPORT_FORMAT =
        "  Hit-rate (%%): key-found=%1$.2f value-matched=%2$.2f\n";

    @Override
    public void outputReportObject(
            Object reportObject,
            ReportWriter writer)
    {
        // ヒット率を出力する.
        Counter c = (Counter)reportObject;
        if (c != null) {
            writer.writeReport(REPORT_FORMAT, c.getKeyFoundPercent(),
                    c.getValueMatchPercent());
        }
    }

    @Override
    public void resetReportObject(Object reportObject) {
        Counter c = (Counter)reportObject;
        if (c != null) {
            c.reset();
        }
    }

    private MemcachedAdapter getMemcachedAdapter(int index) {
        if (this.adapterTable.containsKey(index)) {
            return this.adapterTable.get(index);
        } else {
            MemcachedAdapter adapter =
                this.memcachedAdapterFactory.createMemcachedAdapter();
            this.adapterTable.put(index, adapter);
            return adapter;
        }
    }

}
