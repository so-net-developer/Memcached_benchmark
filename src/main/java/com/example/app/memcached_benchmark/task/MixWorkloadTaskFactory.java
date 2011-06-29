package com.example.app.memcached_benchmark.task;

import java.util.List;

import com.example.app.memcached.task.Generator;
import com.example.lib.memcached.MemcachedAdapter;
import com.example.lib.util.ReportWriter;
import com.example.lib.workload.WorkloadManager;
import com.example.lib.workload.WorkloadParam;
import com.example.lib.workload.WorkloadTask;

/**
 * getとsetを混合する負荷試験.
 */
public class MixWorkloadTaskFactory extends MemcachedWorkloadTaskFactory
{

    private int valueRevision = 0;

    private long maxGetKey = 10000;

    private long maxSetKey = 3000;

    private long setKeyInterval = 36000000;

    private long nextSetTime = 0;

    private int setKeyIndex = -1;
    private long countGetTask = 0;
    private long countSetTask = 0;

    @Override
    public void setupTaskArgs(List<String> args) {
        for (int i = 0, I = args.size(); i < I; ++i) {
            String first = args.get(i);
            String second = i + 1 < I ? args.get(i + 1) : null;
            if ("-mix-gk".equals(first) && second != null) {
                this.maxGetKey = Long.parseLong(second);
            } else if ("-mix-sk".equals(first) && second != null) {
                this.maxSetKey = Long.parseLong(second);
            } else if ("-mix-si".equals(first) && second != null) {
                this.setKeyInterval = Long.parseLong(second) * 1000;
            }
        }

        this.setKeyIndex = -1;
        this.countGetTask = 0;
        this.countSetTask = 0;
    }

    @Override
    public MemcachedWorkloadTask createMemcachedWorkloadTask(
            WorkloadManager manager,
            WorkloadParam param,
            long index,
            int parallelIndex,
            MemcachedAdapter memcachedAdapter)
    {
        long curr = System.currentTimeMillis();
        MemcachedWorkloadTask task = null;
        if (parallelIndex == 0 &&
                (this.setKeyIndex > 0 || this.nextSetTime < curr)) {
            // 一連のSetタスクを定期的に実行するためのブロック.

            //   * Setタスク群開始時に、リビジョンを更新する.
            if (this.setKeyIndex < 0) {
                ++this.valueRevision;
            }

            //   * Setタスク群終了をチェックする.
            ++this.setKeyIndex;
            if (this.setKeyIndex + 1 >= this.maxSetKey) {
                this.setKeyIndex = -1;
                this.nextSetTime = curr + this.setKeyInterval;
            }

            //   * Setタスクを作成する.
            task = new SetTask(manager, memcachedAdapter, this.setKeyIndex);
        } else {
            int keyIndex = (int)(index % this.maxGetKey);
            task = new GetTask(manager, memcachedAdapter, keyIndex);
        }
        return task;
    }

    public Object disposeWorkloadTask(WorkloadTask task) {
        if (task instanceof SetTask) {
            ++this.countSetTask;
        } else if (task instanceof GetTask) {
            ++this.countGetTask;
        }
        return super.disposeWorkloadTask(task);
    }

    @Override
    public void outputReportObject(
            Object reportObject,
            ReportWriter writer)
    {
        super.outputReportObject(reportObject, writer);
        writer.writeReport("  Task detail: get=%1$,d set=%2$,d\n",
                this.countGetTask, this.countSetTask);
    }

    @Override
    public void resetReportObject(Object reportObject) {
        super.resetReportObject(reportObject);
        this.countGetTask = 0;
        this.countSetTask = 0;
    }

    private class SetTask extends MemcachedWorkloadTask
    {

        private int keyIndex;

        private SetTask(
                WorkloadManager manager,
                MemcachedAdapter memcachedAdapter,
                int keyIndex)
        {
            super(manager, memcachedAdapter);
            this.keyIndex = keyIndex;
        }

        @Override
        public void executeTask() throws Exception {
            String key = Generator.getKey(0, this.keyIndex);
            String value = Generator.getValue(0, this.keyIndex,
                    MixWorkloadTaskFactory.this.valueRevision);
            this.latencyPeriod.start();
            this.memcachedAdapter.set(key, value);
            this.latencyPeriod.end();
        }

    }

    private class GetTask extends MemcachedWorkloadTask
    {
        private int keyIndex;

        private GetTask(
                WorkloadManager manager,
                MemcachedAdapter memcachedAdapter,
                int keyIndex)
        {
            super(manager, memcachedAdapter);
            this.keyIndex = keyIndex;
        }

        @Override
        public void executeTask() throws Exception {
            String key = Generator.getKey(0, this.keyIndex);
            String estVal = Generator.getValue(0, this.keyIndex,
                    MixWorkloadTaskFactory.this.valueRevision);

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

}
