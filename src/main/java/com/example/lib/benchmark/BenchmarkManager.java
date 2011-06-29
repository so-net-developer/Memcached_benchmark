package com.example.lib.benchmark;

import java.util.concurrent.CyclicBarrier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BenchmarkManager {

    private static final Log LOG = LogFactory.getLog(BenchmarkManager.class);

    private final BenchmarkParam param;

    private final BenchmarkTaskFactory taskFactory;

    private final CyclicBarrier startBarrier;

    private final CyclicBarrier endBarrier;

    private long score;

    public BenchmarkManager(
            BenchmarkParam param,
            BenchmarkTaskFactory taskFactory)
    {
        this.param = param;
        this.taskFactory = taskFactory;

        int barrierCount = this.param.parallelCount + 1;
        this.startBarrier = new CyclicBarrier(barrierCount);
        this.endBarrier = new CyclicBarrier(barrierCount);
    }

    public BenchmarkParam getBenchmarkParam() {
        return this.param;
    }

    public CyclicBarrier getStartBarrier() {
        return this.startBarrier;
    }

    public CyclicBarrier getEndBarrier() {
        return this.endBarrier;
    }

    public long getScore() {
        return this.score;
    }

    public synchronized void notifyFinishTask(
            AbstractedBenchmarkTask task,
            long elapsed)
    {
        this.score += task.getScore();
        this.taskFactory.disposeBenchmarkTask(task);
    }

    public long executeBenchmark() throws Exception {
        this.score = 0;

        int count = this.param.parallelCount;

        if (LOG.isInfoEnabled()) {
            LOG.info(String.format("creating %1$d tasks", count));
        }
        for (int i = 0; i < count; ++i) {
            AbstractedBenchmarkTask task =
                this.taskFactory.createBenchmarkTask(i, this.param);
            task.setupManager(this, i);
            Thread thread = new Thread(task);
            thread.start();
        }

        long start = System.nanoTime();
        this.startBarrier.await();
        if (LOG.isInfoEnabled()) {
            LOG.info(String.format("waiting to finish %1$d tasks", count));
        }
        this.endBarrier.await();
        long end = System.nanoTime();
        long elapsed = end - start;

        return elapsed;
    }

}
