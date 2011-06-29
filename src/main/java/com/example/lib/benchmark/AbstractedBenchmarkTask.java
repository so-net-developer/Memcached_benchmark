package com.example.lib.benchmark;

import java.util.concurrent.CyclicBarrier;

import org.apache.commons.logging.Log;

public abstract class AbstractedBenchmarkTask implements Runnable {

    private BenchmarkManager manager = null;

    private int index = -1;

    public abstract Log getLogger();

    public abstract void setupTask(int index, BenchmarkParam param)
        throws Exception;

    public abstract void mainTask() throws Exception;

    public abstract void clearTask() throws Exception;

    public abstract long getScore();

    public int getIndex() {
        return this.index;
    }

    public BenchmarkManager getBenchmarkManager() {
        return this.manager;
    }

    @Override
    public final void run() {
        Log LOG = getLogger();
        boolean started = false;
        try {
            if (LOG.isTraceEnabled()) {
                LOG.trace("setupTask");
            }
            setupTask(this.index, this.manager.getBenchmarkParam());

            this.manager.getStartBarrier().await();
            started = true;

            if (LOG.isTraceEnabled()) {
                LOG.trace("mainTask");
            }
            long start = System.nanoTime();
            mainTask();
            long end = System.nanoTime();

            long elapsed = end - start;
            this.manager.notifyFinishTask(this, elapsed);

            if (LOG.isTraceEnabled()) {
                LOG.trace("clearTask");
            }
            clearTask();
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(String.format("task #%1$d failed", this.index), e);
            }
        } finally {
            if (!started) {
                try {
                    this.manager.getStartBarrier().await();
                } catch (Exception e) {
                }
            }
            try {
                this.manager.getEndBarrier().await();
            } catch (Exception e) {
            }
        }
    }

    public final void setupManager(
            BenchmarkManager manager,
            int index)
    {
        this.manager = manager;
        this.index = index;
    }

}
